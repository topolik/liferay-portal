/**
 * Find Security Bugs
 * Copyright (c) Philippe Arteau, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.h3xstream.findsecbugs.taintanalysis;

import com.h3xstream.findsecbugs.BCELUtil;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.SignatureParser;
import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.ba.XFactory;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Map of taint summaries for all known methods and classes
 *
 * This class extends HashMap:
 * <ul>
 *  <li>The key is the method signature (ie :
 org/hibernate/Session.createQuery(Ljava/lang/String;)Lorg/hibernate/Query;)</li>
 *  <li>The value is the behavior of the method
 *  ("0" for param index 0 is tainted,
 *  "UNKNOWN" if the method does not become tainted base on the value,
 *  "TAINTED" if the result must be consider unsafe)</li>
 * </ul>
 *
 * @author David Formanek (Y Soft Corporation, a.s.)
 */
public class TaintConfig extends HashMap<String, TaintMethodConfig> {

    private static final long serialVersionUID = 1L;
    private final Map<String, TaintClassConfig> taintClassConfigMap = new HashMap<String, TaintClassConfig>();
    private final Map<String, TaintFieldConfig> taintFieldConfigMap = new HashMap<String, TaintFieldConfig>();
    private final Map<String, TaintMethodConfigWithArgumentsAndLocation> taintMethodConfigWithArgumentsAndLocationMap =
            new HashMap<String, TaintMethodConfigWithArgumentsAndLocation>();

    private final Map<String, Taint> staticFieldsTaint = new HashMap<String, Taint>();

    /**
     * Annotation type signatures (e.g. Lcom/liferay/portal/kernel/bean/AutoEscape;)
     * whose unconfigured instance methods return the receiver's taint, tags
     * included. Configured with a line {@code @com/liferay/portal/kernel/bean/AutoEscape:RECEIVER}.
     */
    private final Set<String> receiverTransferAnnotations = new HashSet<String>();
    private final Map<String, Boolean> receiverTransferMethodCache = new ConcurrentHashMap<String, Boolean>();

    /**
     * Known subtypes declaring the method, keyed by the receiver class name and method id
     */
    private final Map<String, Set<String>> overridersCache = new ConcurrentHashMap<String, Set<String>>();
    private static final Set<String> UNRESOLVED_OVERRIDERS = Collections.unmodifiableSet(new HashSet<String>());
    private static final Set<String> NOT_OVERRIDABLE = Collections.unmodifiableSet(new HashSet<String>());
    /**
     * Classes declaring the method invoked at runtime known by a class hierarchy analysis of the classes
     * outside the analyzed code, keyed by the receiver class name and method id
     */
    private final Map<String, String[]> externalOverriders = new HashMap<String, String[]>();
    private static final String[] UNRESOLVED_EXTERNAL_OVERRIDERS = new String[0];
    private static final Set<String> OBJECT_METHOD_IDS = new HashSet<String>(Arrays.asList(
            ".equals(Ljava/lang/Object;)Z", ".hashCode()I", ".toString()Ljava/lang/String;"));
    private final Map<String, Boolean> functionalMethodCache = new ConcurrentHashMap<String, Boolean>();
    private final Map<String, Byte> abstractMethodCache = new ConcurrentHashMap<String, Byte>();
    private static final byte UNKNOWN_ABSTRACT = -1;
    private boolean mergingOverriders;

    private transient TaintConfigUsage usage;
    private transient Set<String> excludedKeys = Collections.emptySet();
    private final Set<String> excludedMatches = new HashSet<String>();
    private int excludedEntries;

    private static final Pattern DERIVED_PARAMETER_TAGS = Pattern.compile("(\\d+)\\[([^\\]]*)\\]");
    private int importedEntries;
    private int importSkippedEntries;
    private int importPresentEntries;
    private int importBadEntries;

    /**
     * Dumps all the summaries for debugging
     * 
     * @param output stream where to output the summaries
     */
    public void dump(PrintStream output) {
        TreeSet<String> keys = new TreeSet<String>(keySet());
        for (String key : keys) {
            output.println(key + ":" + get(key));
        }
    }

    /**
     * Loads summaries from stream checking the format
     * 
     * @param input input stream of configured summaries
     * @param checkRewrite whether to check duplicit summaries
     * @throws IOException if cannot read the stream or the format is bad
     * @throws IllegalArgumentException for bad method format
     * @throws IllegalStateException if there are duplicit configurations
     */
    public void load(InputStream input, final boolean checkRewrite) throws IOException {
        load(input, checkRewrite, null, false);
    }

    /**
     * Loads summaries from stream checking the format
     *
     * @param input input stream of configured summaries
     * @param checkRewrite whether to check duplicit summaries
     * @param source resource name or path of the config, for the usage accounting
     * @param custom whether the config is custom, for the usage accounting
     * @throws IOException if cannot read the stream or the format is bad
     * @throws IllegalArgumentException for bad method format
     * @throws IllegalStateException if there are duplicit configurations
     */
    public void load(InputStream input, final boolean checkRewrite, String source, boolean custom) throws IOException {
        final int sourceId = usage == null ? -1 : usage.addSource(source, custom);
        new TaintConfigLoader().load(input, new TaintConfigLoader.TaintConfigReceiver() {
            @Override
            public void receiveTaintConfig(String typeSignature, String config) throws IOException {
                if (custom && isExcluded(typeSignature)) {
                    return;
                }
                if (typeSignature.startsWith("@")) {
                    if (!"RECEIVER".equals(config.trim())) {
                        throw new IllegalArgumentException("Invalid annotation config " + typeSignature + ":" + config);
                    }
                    receiverTransferAnnotations.add("L" + typeSignature.substring(1) + ";");
                    receiverTransferMethodCache.clear();
                    if (usage != null) {
                        usage.recordOther(typeSignature, 'A', sourceId, config.trim());
                    }
                    return;
                }

                if (TaintMethodConfig.accepts(typeSignature, config)) {
                    if (checkRewrite && containsKey(typeSignature)) {
                        throw new IllegalStateException("Config for " + typeSignature + " already loaded");
                    }
                    TaintMethodConfig taintMethodConfig = new TaintMethodConfig(true).load(config);
                    taintMethodConfig.setTypeSignature(typeSignature);
                    TaintMethodConfig previous = put(typeSignature, taintMethodConfig);
                    if (usage != null) {
                        usage.recordMethod(typeSignature, 'M', sourceId, previous, taintMethodConfig);
                    }
                    return;
                }

                if (TaintClassConfig.accepts(typeSignature, config)) {
                    if (checkRewrite && taintClassConfigMap.containsKey(typeSignature)) {
                        throw new IllegalStateException("Config for " + typeSignature + " already loaded");
                    }
                    TaintClassConfig taintClassConfig = new TaintClassConfig().load(config);
                    taintClassConfig.setTypeSignature(typeSignature);
                    taintClassConfigMap.put(typeSignature, taintClassConfig);
                    if (usage != null) {
                        usage.recordOther(typeSignature, 'C', sourceId, config);
                    }
                    return;
                }

                if (TaintFieldConfig.accepts(typeSignature, config)) {
                    if (checkRewrite && taintFieldConfigMap.containsKey(typeSignature)) {
                        throw new IllegalStateException("Config for " + typeSignature + " already loaded");
                    }
                    TaintFieldConfig taintFieldConfig = new TaintFieldConfig().load(config);
                    taintFieldConfig.setTypeSignature(typeSignature);
                    taintFieldConfigMap.put(typeSignature, taintFieldConfig);
                    if (usage != null) {
                        usage.recordOther(typeSignature, 'F', sourceId, config);
                    }
                    return;
                }

                if (TaintMethodConfigWithArgumentsAndLocation.accepts(typeSignature, config)) {
                    if (checkRewrite && taintMethodConfigWithArgumentsAndLocationMap.containsKey(typeSignature)) {
                        throw new IllegalStateException("Config for " + typeSignature + " already loaded");
                    }

                    TaintMethodConfigWithArgumentsAndLocation methodConfig =
                            new TaintMethodConfigWithArgumentsAndLocation().load(config);

                    methodConfig.setTypeSignature(typeSignature);

                    String key = typeSignature + '@' + methodConfig.getLocation();
                    if (custom && isExcluded(key)) {
                        return;
                    }
                    TaintMethodConfig previous = taintMethodConfigWithArgumentsAndLocationMap.put(key, methodConfig);
                    if (usage != null) {
                        usage.recordMethod(key, 'L', sourceId, previous, methodConfig);
                    }
                    return;
                }

                throw new IllegalArgumentException("Invalid signature " + typeSignature + " configured");
            }
        });
    }


    /**
     * Imports the summaries derived by the analysis of other classes, the lines of derived-config.txt
     * {@code signature[:output taint](^stack index:argument output taint)*}. A summary is added as not configured,
     * only if no summary is set for the method and its class is not skipped.
     *
     * @param input stream of the derived summaries
     * @param skipClass tells whether to skip the summaries of the slashed class name, e.g. of the analyzed classes
     * @throws IOException if cannot read the stream
     */
    public void loadDerived(InputStream input, Predicate<String> skipClass) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\^");
            int index = parts[0].indexOf(':');
            String key = index < 0 ? parts[0] : parts[0].substring(0, index);
            int parenthesis = key.indexOf('(');
            int dot = parenthesis < 0 ? -1 : key.lastIndexOf('.', parenthesis);
            if (dot <= 0) {
                importBadEntries++;
                continue;
            }
            if (skipClass.test(key.substring(0, dot))) {
                importSkippedEntries++;
                continue;
            }
            if (containsKey(key)) {
                importPresentEntries++;
                continue;
            }
            try {
                TaintMethodConfig config = new TaintMethodConfig(false);
                if (index >= 0) {
                    config.setOuputTaint(loadDerivedTaint(parts[0].substring(index + 1)));
                }
                for (int i = 1; i < parts.length; i++) {
                    String[] tuple = parts[i].split(":", 2);
                    if (tuple.length != 2) {
                        throw new IOException("Bad format: argument output taint " + parts[i]);
                    }
                    config.setParameterOutputTaint(Integer.parseInt(tuple[0]), loadDerivedTaint(tuple[1]));
                }
                config.setParametersOutputTaintsProcessed(true);
                config.setTypeSignature(key);
                put(key, config);
                importedEntries++;
            } catch (IOException | RuntimeException ex) {
                importBadEntries++;
            }
        }
    }

    private static Taint loadDerivedTaint(String str) throws IOException {
        Map<Integer, Set<TaintTag>> parameterTags = new HashMap<Integer, Set<TaintTag>>();
        Matcher matcher = DERIVED_PARAMETER_TAGS.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            Set<TaintTag> tags = new HashSet<TaintTag>();
            for (String tagName : matcher.group(2).split(",")) {
                if (!tagName.isEmpty()) {
                    TaintTag tag = TaintTag.get(tagName);
                    tags.add(tag == null ? TaintTag.create(tagName) : tag);
                }
            }
            parameterTags.put(Integer.valueOf(matcher.group(1)), tags);
            matcher.appendReplacement(sb, matcher.group(1));
        }
        matcher.appendTail(sb);
        // the tags of the taint apply to its arguments too, the arguments with other tags are set afterwards
        Taint taint = new TaintMethodConfig(false).load(sb.toString()).getOutputTaint();
        for (Map.Entry<Integer, Set<TaintTag>> entry : parameterTags.entrySet()) {
            taint.setParameterTags(entry.getKey(), entry.getValue());
        }
        return taint;
    }

    /**
     * @return number of imported derived summaries
     */
    public int getImportedEntries() {
        return importedEntries;
    }

    /**
     * @return number of derived summaries not imported because their class is skipped
     */
    public int getImportSkippedEntries() {
        return importSkippedEntries;
    }

    /**
     * @return number of derived summaries not imported because a summary is already set
     */
    public int getImportPresentEntries() {
        return importPresentEntries;
    }

    /**
     * @return number of derived summaries not imported because of a bad format
     */
    public int getImportBadEntries() {
        return importBadEntries;
    }

    private boolean isExcluded(String key) {
        if (!excludedKeys.contains(key)) {
            return false;
        }
        excludedEntries++;
        excludedMatches.add(key);
        return true;
    }

    /**
     * Drops the given keys from the custom configs loaded afterwards, location entries are matched
     * by the signature followed by @ and the location
     */
    public void setExcludedKeys(Set<String> excludedKeys) {
        this.excludedKeys = excludedKeys;
    }

    /**
     * @return number of custom entries dropped by the excluded keys
     */
    public int getExcludedEntries() {
        return excludedEntries;
    }

    /**
     * @return number of excluded keys that matched a custom entry
     */
    public int getExcludedMatches() {
        return excludedMatches.size();
    }

    public TaintConfigUsage getUsage() {
        return usage;
    }

    /**
     * Enables the usage accounting of the summaries loaded afterwards
     */
    public void setUsage(TaintConfigUsage usage) {
        this.usage = usage;
    }

    public boolean isClassImmutable(String typeSignature) {
        if (!isClassType(typeSignature)) {
            return false;
        }

        TaintClassConfig classConfig = taintClassConfigMap.get(typeSignature);
        if (classConfig == null) {
            return false;
        }

        return classConfig.isImmutable();
    }

    public boolean isClassTaintSafe(String typeSignature) {
        if (typeSignature != null && typeSignature.startsWith("[")) {
            // an array can only hold instances of its element type
            return isClassTaintSafe(typeSignature.substring(1));
        }
        if (!isClassType(typeSignature)) {
            return false;
        }

        TaintClassConfig taintClassConfig = getTaintClassConfig(typeSignature);
        if (taintClassConfig == null) {
            return false;
        }

        return taintClassConfig.getTaintState().equals(Taint.State.SAFE);
    }

    public Taint.State getClassTaintState(String typeSignature, Taint.State defaultState) {
        if (!isClassType(typeSignature)) {
            return defaultState;
        }

        TaintClassConfig taintClassConfig = getTaintClassConfig(typeSignature);

        if (taintClassConfig == null) {
            return defaultState;
        }

        Taint.State taintClassConfigState = taintClassConfig.getTaintState();

        if (taintClassConfigState.equals(TaintClassConfig.DEFAULT_TAINT_STATE)) {
            return defaultState;
        }

        return taintClassConfigState;
    }

    public Taint.State getFieldTaintState(String fieldSignature, Taint.State defaultState) {
        if (!isFieldType(fieldSignature)) {
            return defaultState;
        }

        TaintFieldConfig taintFieldConfig = taintFieldConfigMap.get(fieldSignature);

        if (taintFieldConfig == null) {
            return defaultState;
        }

        Taint.State taintFieldConfigState = taintFieldConfig.getTaintState();

        if (taintFieldConfigState.equals(TaintClassConfig.DEFAULT_TAINT_STATE)) {
            return defaultState;
        }

        return taintFieldConfigState;
    }

    public TaintClassConfig getTaintClassConfig(String typeSignature) {
        if (!isClassType(typeSignature)) {
            return null;
        }

        return taintClassConfigMap.get(typeSignature);
    }

    private boolean isClassType(String typeSignature) {
        return typeSignature != null && typeSignature.length() > 2 && typeSignature.charAt(0) == 'L';
    }

    private boolean isFieldType(String typeSignature) {
        return typeSignature != null && typeSignature.length() > 2 && typeSignature.charAt(0) != 'L';
    }

    /**
     * Ignores safe primitive parameters except for char, which is not
     * inherently safe.
     */
    private static int[] getParameterSlots(SignatureParser p) {
        String[] parameters = p.getArguments();
        int[] pnums = new int[parameters.length];
        int count = 0;
        for (int i = 0; i < parameters.length; i++) {
            String parameter = parameters[i];
            if (SignatureParser.isReferenceType(parameter) || "C".equals(parameter)) {
                pnums[count++] = p.getSlotsFromTopOfStackForParameter(i);
            }
        }

        return (count == parameters.length) ? pnums : Arrays.copyOfRange(pnums, 0, count);
    }

    public TaintMethodConfig getMethodConfig(TaintFrame frame, MethodDescriptor methodDescriptor, String className, String methodId) {
        TaintMethodConfig taintMethodConfig = getTaintMethodConfigWithArgumentsAndLocation(frame, methodDescriptor, className, methodId);

        if (taintMethodConfig == null) {
            taintMethodConfig = get(className.concat(methodId));
        }

        if (taintMethodConfig == null) {
            taintMethodConfig = getSuperMethodConfig(className, methodId);
        }

        // In Java 9+, string concatenation is done with invokedynamic
        // that takes a String constant and substitues one or more
        // values into placeholders. There is no StringBuilder on the
        // stack with mutable taint state.
        if (taintMethodConfig == null && methodId.indexOf("makeConcatWithConstants") > 0) {
            taintMethodConfig = new TaintMethodConfig(true);
            Taint t = new Taint(Taint.State.UNKNOWN);
            String signature = methodId.substring(methodId.indexOf("("), methodId.length());
            SignatureParser p = new SignatureParser(signature);
            for (int paramNum : getParameterSlots(p)) {
                t.addParameter(paramNum);
            }
            // If we had only safe parameters (int, long, etc.), the combined String is SAFE.
            taintMethodConfig.setOuputTaint(t.hasParameters() ? t : new Taint(Taint.State.SAFE));
        }

        return taintMethodConfig;
    }

    public TaintMethodConfig getSuperMethodConfig(String className, String methodId) {
        try {
            if (className.endsWith("]")) {
                // not a real class
                return null;
            }

            JavaClass javaClass = Repository.lookupClass(className);
            assert javaClass != null;

            Set<String> parentClassNames = BCELUtil.getParentClassNames(javaClass);

            for (String parentClassName : parentClassNames) {
                TaintMethodConfig conf = get(parentClassName.concat(methodId));
                if (conf != null) {
                    return conf;
                }
            }

        } catch (ClassNotFoundException ex) {
            AnalysisContext.reportMissingClass(ex);
        }

        return null;
    }

    /**
     * Returns the supertypes in the order {@link #getSuperMethodConfig(String, String)} visits them,
     * without reporting missing classes
     *
     * @param className slashed name of the class
     * @return supertype names, empty for arrays and missing classes
     */
    public List<String> getParentClassNamesQuietly(String className) {
        if (className.endsWith("]")) {
            return Collections.emptyList();
        }
        try {
            return new ArrayList<String>(BCELUtil.getParentClassNamesQuietly(Repository.lookupClass(className)));
        } catch (ClassNotFoundException ex) {
            return Collections.emptyList();
        }
    }

    /**
     * Whether {@link #getSuperMethodConfig(String, String)} finds a summary, without reporting missing classes
     */
    public boolean hasSuperMethodConfigQuietly(String className, String methodId) {
        for (String parentClassName : getParentClassNamesQuietly(className)) {
            if (containsKey(parentClassName.concat(methodId))) {
                return true;
            }
        }
        return false;
    }

    public void setMergingOverriders(boolean mergingOverriders) {
        this.mergingOverriders = mergingOverriders;
    }

    /**
     * Whether the derived summary of a virtual method declared by a known subtype of the receiver class
     * is merged with the summaries of the subtypes, it may not describe the implementation invoked at
     * runtime, and the call of an abstract method without a summary gets the merged summaries of the
     * methods implementing it
     */
    public boolean isMergingOverriders() {
        return mergingOverriders;
    }

    /**
     * Loads the classes declaring the methods invoked at runtime known by a class hierarchy analysis
     * of the classes outside the analyzed code, the lines are {@code <receiver class><method id>\t<classes>}
     * with the slashed class names separated by commas, or {@code ?} if the classes are not known
     *
     * @param input stream of the lines
     */
    public void loadOverriders(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        Map<String, String> classNames = new HashMap<String, String>();
        String line;
        while ((line = reader.readLine()) != null) {
            int index = line.indexOf('\t');
            if (index < 0) {
                continue;
            }
            String value = line.substring(index + 1);
            String[] overriders = UNRESOLVED_EXTERNAL_OVERRIDERS;
            if (!value.equals("?")) {
                overriders = value.split(",");
                for (int i = 0; i < overriders.length; i++) {
                    String className = classNames.putIfAbsent(overriders[i], overriders[i]);
                    if (className != null) {
                        overriders[i] = className;
                    }
                }
            }
            externalOverriders.put(line.substring(0, index), overriders);
        }
        overridersCache.clear();
    }

    public int getExternalOverriderCount() {
        return externalOverriders.size();
    }

    /**
     * Returns the classes declaring the method invoked at runtime on the known subtypes of the class,
     * other than the method invoked on the class, without reporting missing classes
     *
     * @param className slashed name of the receiver class
     * @param methodId method name with signature, e.g. .getName()Ljava/lang/String;
     * @return slashed names of the classes, null if the subtypes of the class are not known
     */
    public Set<String> getOverriders(String className, String methodId) {
        String key = className.concat(methodId);
        Set<String> overriders = overridersCache.get(key);
        if (overriders == null) {
            overriders = findOverriders(className, methodId);
            String[] external = externalOverriders.get(key);
            if (external == UNRESOLVED_EXTERNAL_OVERRIDERS && overriders != NOT_OVERRIDABLE) {
                overriders = UNRESOLVED_OVERRIDERS;
            } else if (external != null && overriders != UNRESOLVED_OVERRIDERS && overriders != NOT_OVERRIDABLE) {
                // the subtypes outside the analyzed code are not known to the class hierarchy
                Set<String> union = new TreeSet<String>(overriders);
                union.addAll(Arrays.asList(external));
                union.remove(className);
                overriders = Collections.unmodifiableSet(union);
            }
            overridersCache.put(key, overriders);
        }
        return overriders == UNRESOLVED_OVERRIDERS ? null : overriders;
    }

    /**
     * Whether a method is abstract for the class, no class on the superclass chain and no interface
     * declares it non abstract, without reporting missing classes
     *
     * @return null if a class is not known
     */
    public Boolean isAbstractMethodQuietly(String className, String methodId) {
        if (AnalysisContext.currentAnalysisContext() == null || className.startsWith("[")) {
            return null;
        }
        String key = className.concat(methodId);
        Byte cached = abstractMethodCache.get(key);
        if (cached == null) {
            Boolean result = findAbstractMethod(className, methodId);
            cached = Byte.valueOf(result == null ? UNKNOWN_ABSTRACT : result.booleanValue() ? (byte) 1 : (byte) 0);
            abstractMethodCache.put(key, cached);
        }
        return cached.byteValue() == UNKNOWN_ABSTRACT ? null : Boolean.valueOf(cached.byteValue() == 1);
    }

    private static Boolean findAbstractMethod(String className, String methodId) {
        XFactory xFactory = AnalysisContext.currentXFactory();
        XClass receiverClass = xFactory.getXClass(DescriptorFactory.createClassDescriptor(className));
        if (receiverClass == null) {
            return null;
        }
        int signatureIndex = methodId.indexOf('(');
        String methodName = methodId.substring(1, signatureIndex);
        String signature = methodId.substring(signatureIndex);
        if (!receiverClass.isInterface() && !receiverClass.isAbstract()) {
            return Boolean.FALSE;
        }
        List<XClass> interfaces = new ArrayList<XClass>();
        for (XClass xClass = receiverClass; xClass != null; ) {
            XMethod method = xClass.findMethod(methodName, signature, false);
            if (method != null) {
                return Boolean.valueOf(method.isAbstract());
            }
            interfaces.add(xClass);
            ClassDescriptor superclass = xClass.getSuperclassDescriptor();
            if (superclass == null) {
                break;
            }
            xClass = xFactory.getXClass(superclass);
            if (xClass == null) {
                return null;
            }
        }
        // a default method
        Set<ClassDescriptor> visited = new HashSet<ClassDescriptor>();
        while (!interfaces.isEmpty()) {
            XClass xClass = interfaces.remove(interfaces.size() - 1);
            for (ClassDescriptor descriptor : xClass.getInterfaceDescriptorList()) {
                if (!visited.add(descriptor)) {
                    continue;
                }
                XClass xInterface = xFactory.getXClass(descriptor);
                if (xInterface == null) {
                    return null;
                }
                XMethod method = xInterface.findMethod(methodName, signature, false);
                if (method != null && !method.isAbstract()) {
                    return Boolean.FALSE;
                }
                interfaces.add(xInterface);
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Whether a lambda expression may implement the method, the class or a known subtype is a functional
     * interface with an abstract method of the same name, without reporting missing classes
     */
    public boolean isFunctionalMethodQuietly(String className, String methodId) {
        String key = className.concat(methodId);
        Boolean functional = functionalMethodCache.get(key);
        if (functional == null) {
            functional = Boolean.valueOf(findFunctionalMethod(className, methodId));
            functionalMethodCache.put(key, functional);
        }
        return functional.booleanValue();
    }

    private static boolean findFunctionalMethod(String className, String methodId) {
        AnalysisContext analysisContext = AnalysisContext.currentAnalysisContext();
        if (analysisContext == null) {
            return true;
        }
        XFactory xFactory = AnalysisContext.currentXFactory();
        ClassDescriptor classDescriptor = DescriptorFactory.createClassDescriptor(className);
        XClass receiverClass = xFactory.getXClass(classDescriptor);
        if (receiverClass == null) {
            return true;
        }
        if (!receiverClass.isInterface()) {
            return false;
        }
        String methodName = methodId.substring(1, methodId.indexOf('('));
        Set<ClassDescriptor> subtypes;
        try {
            subtypes = analysisContext.getSubtypes2().getSubtypes(classDescriptor);
        } catch (ClassNotFoundException ex) {
            return true;
        }
        for (ClassDescriptor subtype : subtypes) {
            XClass xClass = subtype.equals(classDescriptor) ? receiverClass : xFactory.getXClass(subtype);
            if (xClass != null && xClass.isInterface()) {
                String functionalMethodName = getFunctionalMethodName(xFactory, xClass);
                if (functionalMethodName == null || functionalMethodName.equals(methodName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the name of the abstract methods of a functional interface, the methods with the same name
     * are counted as one method, they may override each other in the generic interfaces
     *
     * @return name of the method, empty if the interface is not functional, null if not known
     */
    private static String getFunctionalMethodName(XFactory xFactory, XClass xInterface) {
        Set<String> abstractNames = new HashSet<String>();
        Set<String> defaultNames = new HashSet<String>();
        List<XClass> interfaces = new ArrayList<XClass>();
        interfaces.add(xInterface);
        Set<ClassDescriptor> visited = new HashSet<ClassDescriptor>();
        while (!interfaces.isEmpty()) {
            XClass xClass = interfaces.remove(interfaces.size() - 1);
            for (XMethod method : xClass.getXMethods()) {
                if (method.isStatic() || method.isPrivate()
                        || OBJECT_METHOD_IDS.contains("." + method.getName() + method.getSignature())) {
                    continue;
                }
                (method.isAbstract() ? abstractNames : defaultNames).add(method.getName());
            }
            for (ClassDescriptor descriptor : xClass.getInterfaceDescriptorList()) {
                if (visited.add(descriptor)) {
                    XClass xSuperInterface = xFactory.getXClass(descriptor);
                    if (xSuperInterface == null) {
                        return null;
                    }
                    interfaces.add(xSuperInterface);
                }
            }
        }
        // a default method may implement the abstract methods of the super interfaces
        abstractNames.removeAll(defaultNames);
        return abstractNames.size() == 1 ? abstractNames.iterator().next() : "";
    }

    private static Set<String> findOverriders(String className, String methodId) {
        AnalysisContext analysisContext = AnalysisContext.currentAnalysisContext();
        if (analysisContext == null || className.startsWith("[") || className.endsWith("]")) {
            return UNRESOLVED_OVERRIDERS;
        }
        XFactory xFactory = AnalysisContext.currentXFactory();
        ClassDescriptor classDescriptor = DescriptorFactory.createClassDescriptor(className);
        XClass receiverClass = xFactory.getXClass(classDescriptor);
        if (receiverClass == null) {
            // resolving the subtypes of a missing class would report it
            return UNRESOLVED_OVERRIDERS;
        }
        int signatureIndex = methodId.indexOf('(');
        String methodName = methodId.substring(1, signatureIndex);
        String signature = methodId.substring(signatureIndex);
        XMethod receiverMethod = receiverClass.findMethod(methodName, signature, false);
        if (receiverClass.isFinal() || receiverMethod != null && (receiverMethod.isFinal() || receiverMethod.isPrivate())) {
            return NOT_OVERRIDABLE;
        }
        Set<ClassDescriptor> subtypes;
        try {
            subtypes = analysisContext.getSubtypes2().getSubtypes(classDescriptor);
        } catch (ClassNotFoundException ex) {
            return UNRESOLVED_OVERRIDERS;
        }
        Set<String> overriders = new TreeSet<String>();
        for (ClassDescriptor subtype : subtypes) {
            if (subtype.equals(classDescriptor)) {
                continue;
            }
            XClass xClass = xFactory.getXClass(subtype);
            if (xClass == null) {
                continue;
            }
            // bridge methods included, a generic method is overridden under the erased signature by a bridge
            XMethod method = xClass.findMethod(methodName, signature, false);
            if (method != null) {
                if (!method.isAbstract() && !method.isPrivate()) {
                    overriders.add(subtype.getClassName());
                }
            } else if (!xClass.isInterface() && !xClass.isAbstract()) {
                // the method may be inherited from a superclass not being a subtype of the class
                String inheritingClassName = findInheritingClassName(xFactory, xClass, classDescriptor, methodName,
                        signature);
                if (inheritingClassName == null) {
                    return UNRESOLVED_OVERRIDERS;
                }
                if (!inheritingClassName.isEmpty()) {
                    overriders.add(inheritingClassName);
                }
            }
        }
        if (overriders.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(overriders);
    }

    /**
     * Returns the class declaring the method invoked on a class not declaring it, the first superclass
     * declaring it, else the maximally specific super interface declaring it (JLS 8.4.8.4)
     *
     * @return slashed name of the class, empty if the method is the method invoked on the receiver class,
     * abstract or ambiguous, null if a super type is not known
     */
    private static String findInheritingClassName(XFactory xFactory, XClass xClass, ClassDescriptor receiverClass,
            String methodName, String signature) {
        List<XClass> classes = new ArrayList<XClass>();
        classes.add(xClass);
        boolean inherited = false;
        ClassDescriptor superclass = xClass.getSuperclassDescriptor();
        while (superclass != null) {
            inherited |= superclass.equals(receiverClass);
            XClass xSuperclass = xFactory.getXClass(superclass);
            if (xSuperclass == null) {
                return null;
            }
            XMethod method = xSuperclass.findMethod(methodName, signature, false);
            if (method != null && !method.isPrivate()) {
                // the receiver class inherits the method too
                return inherited || method.isAbstract() ? "" : superclass.getClassName();
            }
            classes.add(xSuperclass);
            superclass = xSuperclass.getSuperclassDescriptor();
        }
        return findDefaultClassName(xFactory, classes, receiverClass, methodName, signature);
    }

    private static String findDefaultClassName(XFactory xFactory, List<XClass> classes, ClassDescriptor receiverClass,
            String methodName, String signature) {
        Map<ClassDescriptor, XClass> interfaces = new HashMap<ClassDescriptor, XClass>();
        Map<ClassDescriptor, XMethod> declarations = new HashMap<ClassDescriptor, XMethod>();
        while (!classes.isEmpty()) {
            XClass xClass = classes.remove(classes.size() - 1);
            for (ClassDescriptor descriptor : xClass.getInterfaceDescriptorList()) {
                if (interfaces.containsKey(descriptor)) {
                    continue;
                }
                XClass xInterface = xFactory.getXClass(descriptor);
                if (xInterface == null) {
                    return null;
                }
                interfaces.put(descriptor, xInterface);
                XMethod method = xInterface.findMethod(methodName, signature, false);
                if (method != null && !method.isPrivate()) {
                    declarations.put(descriptor, method);
                }
                classes.add(xInterface);
            }
        }
        // a declaration overrides the declarations of the super interfaces
        Set<ClassDescriptor> overridden = new HashSet<ClassDescriptor>();
        for (ClassDescriptor descriptor : declarations.keySet()) {
            classes.add(interfaces.get(descriptor));
            while (!classes.isEmpty()) {
                for (ClassDescriptor superInterface : classes.remove(classes.size() - 1).getInterfaceDescriptorList()) {
                    if (overridden.add(superInterface)) {
                        classes.add(interfaces.get(superInterface));
                    }
                }
            }
        }
        declarations.keySet().removeAll(overridden);
        if (declarations.size() != 1) {
            return "";
        }
        Map.Entry<ClassDescriptor, XMethod> declaration = declarations.entrySet().iterator().next();
        if (declaration.getValue().isAbstract() || declaration.getKey().equals(receiverClass)) {
            return "";
        }
        return declaration.getKey().getClassName();
    }

    /**
     * Same as {@link #isReceiverTransferMethod(String, String)} without reporting missing classes
     * and without caching the result
     */
    public boolean isReceiverTransferMethodQuietly(String className, String methodId) {
        if (receiverTransferAnnotations.isEmpty() || className.endsWith("]")) {
            return false;
        }

        Boolean result = receiverTransferMethodCache.get(className.concat(methodId));
        if (result != null) {
            return result;
        }
        int signatureIndex = methodId.indexOf('(');
        String methodName = methodId.substring(1, signatureIndex);
        String signature = methodId.substring(signatureIndex);
        try {
            JavaClass javaClass = Repository.lookupClass(className);
            if (hasReceiverTransferAnnotation(javaClass, methodName, signature)) {
                return true;
            }
            for (String parentClassName : BCELUtil.getParentClassNamesQuietly(javaClass)) {
                try {
                    if (hasReceiverTransferAnnotation(Repository.lookupClass(parentClassName), methodName, signature)) {
                        return true;
                    }
                } catch (ClassNotFoundException ex) {
                    // ignore
                }
            }
        } catch (ClassNotFoundException ex) {
            // ignore
        }
        return false;
    }

    /**
     * Whether the method, declared in the class or any of its supertypes, carries
     * one of the annotations configured with {@code @annotation/Type:RECEIVER}
     *
     * @param className slashed name of the invoked class
     * @param methodId method name with signature, e.g. .getName()Ljava/lang/String;
     */
    public boolean isReceiverTransferMethod(String className, String methodId) {
        if (receiverTransferAnnotations.isEmpty() || className.endsWith("]")) {
            return false;
        }

        String key = className.concat(methodId);
        Boolean result = receiverTransferMethodCache.get(key);
        if (result == null) {
            result = hasReceiverTransferAnnotation(className, methodId);
            receiverTransferMethodCache.put(key, result);
        }
        return result;
    }

    private boolean hasReceiverTransferAnnotation(String className, String methodId) {
        int signatureIndex = methodId.indexOf('(');
        String methodName = methodId.substring(1, signatureIndex);
        String signature = methodId.substring(signatureIndex);
        try {
            JavaClass javaClass = Repository.lookupClass(className);
            if (hasReceiverTransferAnnotation(javaClass, methodName, signature)) {
                return true;
            }
            for (String parentClassName : BCELUtil.getParentClassNames(javaClass)) {
                try {
                    if (hasReceiverTransferAnnotation(Repository.lookupClass(parentClassName), methodName, signature)) {
                        return true;
                    }
                } catch (ClassNotFoundException ex) {
                    AnalysisContext.reportMissingClass(ex);
                }
            }
        } catch (ClassNotFoundException ex) {
            AnalysisContext.reportMissingClass(ex);
        }
        return false;
    }

    private boolean hasReceiverTransferAnnotation(JavaClass javaClass, String methodName, String signature) {
        for (Method method : javaClass.getMethods()) {
            if (method.isStatic() || !method.getName().equals(methodName) || !method.getSignature().equals(signature)) {
                continue;
            }
            for (AnnotationEntry annotationEntry : method.getAnnotationEntries()) {
                if (receiverTransferAnnotations.contains(annotationEntry.getAnnotationType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private TaintMethodConfig getTaintMethodConfigWithArgumentsAndLocation(TaintFrame frame, MethodDescriptor methodDescriptor, String className, String methodId) {
        if (taintMethodConfigWithArgumentsAndLocationMap.isEmpty()) {
            return null;
        }

        String signature = methodId.substring(methodId.indexOf("("), methodId.length());
        int parameters = new SignatureParser(signature).getNumParameters();
        StringBuffer sb = null;
        if (parameters > 0 && frame.getStackDepth() >= parameters) {
            sb = new StringBuffer(parameters);
            for (int i = parameters - 1; i >= 0; i--) {
                try {
                    Taint taint = frame.getStackValue(i);
                    String value = taint.getConstantValue();
                    if (value != null) {
                        sb.append('"' + value + '"');
                    }
                    else {
                        sb.append(taint.getState().name());
                    }
                    if (i > 0) {
                        sb.append(',');
                    }
                }
                catch (DataflowAnalysisException e) {
                    assert false : e.getMessage();
                }
            }
        }

        String arguments = sb != null ? sb.toString() : "";
        String methodName = methodId.substring(1, methodId.indexOf('('));
        String methodDefinition = className + "." + methodName + "(" + arguments + ")";
        String key = methodDefinition + "@" + methodDescriptor.getSlashedClassName();
        return taintMethodConfigWithArgumentsAndLocationMap.get(key);
    }

    public Taint getStaticFieldTaint(String fieldSignature, Taint defaultValue) {
        if (!isFieldType(fieldSignature)) {
            return defaultValue;
        }

        return staticFieldsTaint.getOrDefault(fieldSignature, defaultValue);
    }

    public void putStaticFieldTaint(String fieldSignature, Taint t) {
        if (!isFieldType(fieldSignature)) {
            return;
        }

        staticFieldsTaint.put(fieldSignature, t);
    }

    /**
     * @return copy of the taint of the static fields, see {@link #restoreStaticFieldsTaint(Map)}
     */
    Map<String, Taint> copyStaticFieldsTaint() {
        Map<String, Taint> copy = new HashMap<String, Taint>();
        for (Map.Entry<String, Taint> entry : staticFieldsTaint.entrySet()) {
            copy.put(entry.getKey(), new Taint(entry.getValue()));
        }
        return copy;
    }

    /**
     * Replaces the taint of the static fields with a copy made before
     */
    void restoreStaticFieldsTaint(Map<String, Taint> copy) {
        staticFieldsTaint.clear();
        staticFieldsTaint.putAll(copy);
    }

}
