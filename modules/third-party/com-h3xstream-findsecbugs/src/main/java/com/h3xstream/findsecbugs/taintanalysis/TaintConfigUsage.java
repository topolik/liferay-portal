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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Records which custom taint config entries the analysis consults and what the
 * call sites would get without them, so that redundant entries can be found
 *
 * <p>Every loaded key is attributed to its source, overrides are audited at load
 * time. Each call site resolved to a custom method entry records the matched key
 * and the facts needed to replay the lookup without that entry. The summary the
 * analysis derives for a method shadowed by a custom entry is kept as well. The
 * verdicts are resolved against the final summaries when the file is written.</p>
 */
public class TaintConfigUsage {

    private static final Logger LOGGER = Logger.getLogger(TaintConfigUsage.class.getName());
    private static final int MAX_FALLBACKS = 5;
    private static final int MAX_FAILURES = 10;
    private static final Set<String> MARKER_TAGS = new HashSet<String>(Arrays.asList(
            Taint.Tag.SENSITIVE_DATA.name(), Taint.Tag.CREDIT_CARD_VARIABLE.name(),
            Taint.Tag.PASSWORD_VARIABLE.name(), Taint.Tag.HASH_VARIABLE.name(), Taint.Tag.XML_VALUE.name()));

    enum FallbackKind {
        CONFIG_BUILTIN("config-builtin"), CONFIG_CUSTOM("config-custom"), TYPE("type"), ANNOTATION("annotation"),
        RECEIVER("receiver"), DERIVED("derived"), CONSTRUCTOR("ctor"), NONE("none");

        private final String label;

        FallbackKind(String label) {
            this.label = label;
        }
    }

    /**
     * Call site of a custom method entry with the facts the lookup fallback depends on
     */
    static final class Site {
        private final String matchedKey;
        private final String methodKey;
        private final String methodId;
        private final boolean location;
        private final List<String> parentClassNames;
        private final boolean safeReturnType;
        private final boolean annotationElement;
        private final int receiverIndex;
        private final int constructorStackSize;
        private final List<Integer> signatureMutables;
        private final boolean voidReturn;

        /**
         * @param matchedKey key of the matched entry, location entries end with @caller
         * @param className slashed name of the invoked class
         * @param methodId method name with signature, e.g. .getName()Ljava/lang/String;
         * @param location whether the matched entry is a location entry
         * @param parentClassNames supertypes of the invoked class in the lookup order
         * @param safeReturnType whether the return type is a safe class
         * @param annotationElement whether the call reads an annotation element
         * @param receiverIndex stack index of the receiver for receiver transfer methods, -1 otherwise
         * @param constructorStackSize stack size of a defaulted constructor call, 0 otherwise
         * @param signatureMutables stack indices of the mutable parameters
         */
        Site(String matchedKey, String className, String methodId, boolean location, List<String> parentClassNames,
                boolean safeReturnType, boolean annotationElement, int receiverIndex, int constructorStackSize,
                Collection<Integer> signatureMutables) {
            this.matchedKey = matchedKey;
            this.methodKey = className.concat(methodId);
            this.methodId = methodId;
            this.location = location;
            this.parentClassNames = parentClassNames;
            this.safeReturnType = safeReturnType;
            this.annotationElement = annotationElement;
            this.receiverIndex = receiverIndex;
            this.constructorStackSize = constructorStackSize;
            this.signatureMutables = new ArrayList<Integer>(new TreeSet<Integer>(signatureMutables));
            this.voidReturn = methodId.endsWith(")V");
        }
    }

    private static final class Usage {
        private final TaintMethodConfig config;
        private final Set<Site> sites = new LinkedHashSet<Site>();
        private long hits;

        private Usage(TaintMethodConfig config) {
            this.config = config;
        }
    }

    private static final class Shadow {
        private final TaintMethodConfig config;
        private final boolean informative;

        private Shadow(TaintMethodConfig config, boolean informative) {
            this.config = config;
            this.informative = informative;
        }
    }

    private static final class Fallback {
        private final FallbackKind kind;
        private final String summary;
        private final String category;

        private Fallback(FallbackKind kind, String summary, String category) {
            this.kind = kind;
            this.summary = summary;
            this.category = category;
        }
    }

    private final boolean all;
    private final List<String> sourceLabels = new ArrayList<String>();
    private final List<Boolean> sourceCustoms = new ArrayList<Boolean>();
    private final List<Integer> sourceKeyCounts = new ArrayList<Integer>();
    private final Map<String, Integer> keySources = new HashMap<String, Integer>();
    private final Map<String, Set<Integer>> customKeySources = new TreeMap<String, Set<Integer>>();
    private final Map<String, Character> customKeyKinds = new HashMap<String, Character>();
    private final Map<String, String> otherConfigs = new HashMap<String, String>();
    private final Map<String, TaintMethodConfig> customMethodConfigs = new HashMap<String, TaintMethodConfig>();
    private final Map<String, TaintMethodConfig> builtinOverridden = new HashMap<String, TaintMethodConfig>();
    private final List<String> overrides = new ArrayList<String>();
    private final Set<TaintMethodConfig> customConfigs =
            Collections.newSetFromMap(new IdentityHashMap<TaintMethodConfig, Boolean>());
    private final Map<String, Site> sites = new HashMap<String, Site>();
    private final Map<String, Usage> usages = new TreeMap<String, Usage>();
    private final Map<String, Shadow> shadows = new HashMap<String, Shadow>();
    private final List<String> failureMessages = new ArrayList<String>();
    private int failures;
    private long version;
    private long writtenVersion = -1;

    /**
     * @param all whether to write every custom entry, not only the used ones
     */
    public TaintConfigUsage(boolean all) {
        this.all = all;
    }

    /**
     * Registers the source of the following keys
     *
     * @param label resource name or path of the config
     * @param custom whether the config is custom
     * @return id of the source
     */
    public int addSource(String label, boolean custom) {
        sourceLabels.add(label == null ? "?" : label);
        sourceCustoms.add(custom);
        sourceKeyCounts.add(0);
        version++;
        return sourceLabels.size() - 1;
    }

    /**
     * Records a loaded method or location entry
     *
     * @param key method key, location keys end with @caller
     * @param kind 'M' for method, 'L' for location entries
     * @param source id of the source
     * @param previous entry replaced by this one, null if none
     * @param config the loaded entry
     */
    public void recordMethod(String key, char kind, int source, TaintMethodConfig previous, TaintMethodConfig config) {
        Integer previousSource = recordKey(key, kind, source);
        boolean custom = sourceCustoms.get(source);
        if (previous != null) {
            boolean previousCustom = customConfigs.contains(previous);
            if (!previousCustom && custom && !builtinOverridden.containsKey(key)) {
                builtinOverridden.put(key, previous);
            }
            String returnType = getReturnType(key);
            String previousSummary = summarize(previous, returnType, (List<Integer>) null);
            String summary = summarize(config, returnType, (List<Integer>) null);
            String bucket;
            if (!previousCustom && !custom) {
                bucket = "builtin-builtin";
            } else if (previousSummary.equals(summary)) {
                bucket = "same";
            } else if (previousCustom) {
                bucket = "custom-custom";
            } else if (isWeakening(previous, config)) {
                bucket = "weaken-builtin";
            } else {
                bucket = "builtin-other";
            }
            addOverride(key, kind, previousSource, previousSummary, source, summary, bucket);
        }
        if (custom) {
            customConfigs.add(config);
            customMethodConfigs.put(key, config);
        }
    }

    /**
     * Records a loaded class, field or annotation entry
     *
     * @param key class signature, field or @annotation
     * @param kind 'C' for class, 'F' for field, 'A' for annotation entries
     * @param source id of the source
     * @param config the loaded config
     */
    public void recordOther(String key, char kind, int source, String config) {
        Integer previousSource = recordKey(key, kind, source);
        String previous = otherConfigs.put(key, config);
        if (previous != null) {
            boolean previousCustom = sourceCustoms.get(previousSource);
            boolean custom = sourceCustoms.get(source);
            String bucket;
            if (!previousCustom && !custom) {
                bucket = "builtin-builtin";
            } else if (previous.equals(config)) {
                bucket = "same";
            } else if (previousCustom) {
                bucket = "custom-custom";
            } else if ((config.startsWith("SAFE") && !previous.startsWith("SAFE"))
                    || (config.endsWith("#IMMUTABLE") && !previous.endsWith("#IMMUTABLE"))) {
                bucket = "weaken-builtin";
            } else {
                bucket = "builtin-other";
            }
            addOverride(key, kind, previousSource, previous, source, config, bucket);
        }
    }

    private Integer recordKey(String key, char kind, int source) {
        Integer previousSource = keySources.put(key, source);
        sourceKeyCounts.set(source, sourceKeyCounts.get(source) + 1);
        if (sourceCustoms.get(source)) {
            Set<Integer> sources = customKeySources.get(key);
            if (sources == null) {
                sources = new TreeSet<Integer>();
                customKeySources.put(key, sources);
            }
            sources.add(source);
            customKeyKinds.put(key, kind);
        }
        version++;
        return previousSource;
    }

    private void addOverride(String key, char kind, Integer previousSource, String previousSummary,
            int source, String summary, String bucket) {
        overrides.add("override\t" + key + '\t' + kind + '\t' + previousSource + '\t' + previousSummary
                + '\t' + source + '\t' + summary + '\t' + bucket);
    }

    /**
     * Whether the custom entry reports less than the built-in one it replaces
     */
    private static boolean isWeakening(TaintMethodConfig previous, TaintMethodConfig config) {
        Taint previousTaint = previous.getOutputTaint();
        Taint taint = config.getOutputTaint();
        if (previousTaint == null || taint == null) {
            return previousTaint != null;
        }
        if (rank(taint) < rank(previousTaint)) {
            return true;
        }
        if (previousTaint.hasParameters() && taint.hasParameters()
                && !taint.getParameters().containsAll(previousTaint.getParameters())) {
            return true;
        }
        for (TaintTag tag : taint.getTags()) {
            if (!previousTaint.getTags().contains(tag) && !MARKER_TAGS.contains(tag.name())) {
                // a sanitizer tag
                return true;
            }
        }
        for (TaintTag tag : previousTaint.getTags()) {
            if (MARKER_TAGS.contains(tag.name()) && !taint.getTags().contains(tag)) {
                return true;
            }
        }
        if (!taint.getTagsToRemove().containsAll(previousTaint.getTagsToRemove())) {
            return true;
        }
        if (previous.hasMutableStackIndices()
                && !(config.hasMutableStackIndices()
                    && config.getMutableStackIndices().containsAll(previous.getMutableStackIndices()))) {
            return true;
        }
        return false;
    }

    private static int rank(Taint taint) {
        if (taint.isTainted()) {
            return 3;
        }
        if (taint.isUnknown() && taint.hasParameters()) {
            Taint.State nonParametricState = taint.getNonParametricState();
            if (nonParametricState == Taint.State.TAINTED) {
                return 3;
            }
            return nonParametricState == Taint.State.UNKNOWN ? 2 : 1;
        }
        if (taint.isUnknown()) {
            return 2;
        }
        return 0;
    }

    /**
     * Whether the entry was loaded from a custom config
     */
    public boolean isCustom(TaintMethodConfig config) {
        return customConfigs.contains(config);
    }

    public Site getSite(String siteKey) {
        return sites.get(siteKey);
    }

    public void addSite(String siteKey, Site site) {
        sites.put(siteKey, site);
    }

    /**
     * Records that the call site was resolved to the custom entry
     */
    public void recordHit(Site site, TaintMethodConfig config) {
        Usage usage = usages.get(site.matchedKey);
        if (usage == null) {
            usage = new Usage(config);
            usages.put(site.matchedKey, usage);
        }
        usage.hits++;
        usage.sites.add(site);
        version++;
    }

    /**
     * Records the summary derived for a method shadowed by a custom entry,
     * replacing the one of the previous analysis
     *
     * @param key method key
     * @param config the derived summary
     * @param informative whether the summary would be stored without the entry
     */
    public void recordShadow(String key, TaintMethodConfig config, boolean informative) {
        shadows.put(key, new Shadow(config, informative));
        version++;
    }

    /**
     * Records an exception thrown by the accounting, the analysis continues
     */
    public void recordFailure(Throwable t) {
        failures++;
        if (failureMessages.size() < MAX_FAILURES) {
            failureMessages.add(String.valueOf(t));
            LOGGER.log(Level.WARNING, "Taint config usage accounting failed", t);
        }
        version++;
    }

    /**
     * Writes the usage file if anything changed since the last write
     *
     * @param taintConfig the final summaries
     * @param fileName output file
     */
    public void write(TaintConfig taintConfig, String fileName) {
        if (version == writtenVersion) {
            return;
        }
        File file = new File(fileName).getAbsoluteFile();
        Path tempPath = null;
        try {
            File directory = file.getParentFile();
            tempPath = Files.createTempFile(directory.toPath(), file.getName(), ".tmp");
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    Files.newOutputStream(tempPath), StandardCharsets.UTF_8))) {
                write(taintConfig, writer);
            }
            Files.move(tempPath, file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            tempPath = null;
            writtenVersion = version;
        } catch (IOException | RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Cannot write taint config usage to " + file, ex);
        } finally {
            if (tempPath != null) {
                try {
                    Files.deleteIfExists(tempPath);
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }

    void write(TaintConfig taintConfig, Writer writer) throws IOException {
        writer.append("# config-usage v1 mode=").append(all ? "all" : "used")
                .append(" failures=").append(Integer.toString(failures)).append('\n');
        for (String failureMessage : failureMessages) {
            writer.append("# failure ").append(failureMessage.replace('\n', ' ')).append('\n');
        }
        for (int i = 0; i < sourceLabels.size(); i++) {
            writer.append("source\t").append(Integer.toString(i)).append('\t').append(sourceLabels.get(i))
                    .append('\t').append(sourceCustoms.get(i) ? "custom" : "builtin")
                    .append('\t').append(Integer.toString(sourceKeyCounts.get(i))).append('\n');
        }
        for (String override : overrides) {
            writer.append(override).append('\n');
        }
        for (Map.Entry<String, Set<Integer>> entry : customKeySources.entrySet()) {
            String key = entry.getKey();
            Usage usage = usages.get(key);
            if (usage == null && !all) {
                continue;
            }
            char kind = customKeyKinds.get(key);
            StringBuilder sb = new StringBuilder("usage\t");
            sb.append(key).append('\t').append(kind).append('\t');
            appendJoined(sb, entry.getValue());
            if (kind != 'M' && kind != 'L') {
                sb.append("\t0\t0\t-\t-\tuntracked\t").append(otherConfigs.get(key)).append("\t-");
            } else if (usage == null) {
                TaintMethodConfig config = customMethodConfigs.get(key);
                sb.append("\t0\t0\t-\t").append(getShadowSummary(taintConfig, key)).append("\tunused\t")
                        .append(config == null ? "-" : summarize(config, getReturnType(key), (List<Integer>) null)).append("\t-");
            } else {
                appendUsage(sb, taintConfig, key, usage);
            }
            writer.append(sb).append('\n');
        }
    }

    private void appendUsage(StringBuilder sb, TaintConfig taintConfig, String key, Usage usage) {
        Map<String, Integer> categoryCounts = new TreeMap<String, Integer>();
        Set<String> fallbackSummaries = new TreeSet<String>();
        boolean plainSafe = isPlainSafe(usage.config);
        boolean unsafe = false, none = false, parametric = false, other = false, equalCustom = false;
        boolean typeOnly = true;
        String entrySummary = null;
        for (Site site : usage.sites) {
            String siteEntrySummary = summarize(usage.config, site.voidReturn ? "V" : null, site.signatureMutables);
            if (entrySummary == null) {
                entrySummary = siteEntrySummary;
            }
            Fallback fallback = resolve(taintConfig, site, siteEntrySummary);
            String kindCategory = fallback.kind.label + '/' + fallback.category;
            Integer count = categoryCounts.get(kindCategory);
            categoryCounts.put(kindCategory, count == null ? 1 : count + 1);
            if (fallback.summary != null) {
                fallbackSummaries.add(fallback.kind.label + '=' + fallback.summary);
            }
            switch (fallback.category) {
                case "unsafe":
                    unsafe = true;
                    break;
                case "none":
                    none = true;
                    break;
                case "parametric":
                    parametric = true;
                    break;
                case "other":
                    other = true;
                    break;
                case "equal-custom":
                    equalCustom = true;
                    break;
                default:
                    break;
            }
            if ((fallback.kind != FallbackKind.TYPE && fallback.kind != FallbackKind.ANNOTATION)
                    || !"equal".equals(fallback.category)) {
                typeOnly = false;
            }
        }
        String verdict;
        if (plainSafe) {
            if (unsafe) {
                verdict = "assertion";
            } else if (none) {
                verdict = "effective";
            } else if (parametric) {
                verdict = "conditional";
            } else if (other) {
                verdict = "differs";
            } else if (equalCustom) {
                verdict = "redundant-custom";
            } else if (typeOnly) {
                verdict = "redundant-type";
            } else {
                verdict = "redundant-derived";
            }
        } else if (none) {
            verdict = "effective";
        } else if (unsafe || parametric || other) {
            verdict = "differs";
        } else if (equalCustom) {
            verdict = "redundant-custom";
        } else {
            verdict = "redundant-derived";
        }

        sb.append('\t').append(usage.hits).append('\t').append(usage.sites.size()).append('\t');
        boolean first = true;
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(entry.getKey()).append(':').append(entry.getValue());
        }
        sb.append('\t').append(getShadowSummary(taintConfig, key));
        sb.append('\t').append(verdict);
        sb.append('\t').append(entrySummary);
        sb.append('\t');
        if (fallbackSummaries.isEmpty()) {
            sb.append('-');
        } else {
            int i = 0;
            for (String fallbackSummary : fallbackSummaries) {
                if (i == MAX_FALLBACKS) {
                    sb.append(" ...");
                    break;
                }
                if (i++ > 0) {
                    sb.append(' ');
                }
                sb.append(fallbackSummary);
            }
        }
    }

    private String getShadowSummary(TaintConfig taintConfig, String key) {
        Shadow shadow = shadows.get(key);
        if (shadow == null) {
            return "-";
        }
        String returnType = getReturnType(key);
        Collection<Integer> mutables = Collections.emptyList();
        int signatureIndex = key.indexOf('(');
        if (signatureIndex > 0) {
            mutables = TaintFrameModelingVisitor.getMutableStackIndices(taintConfig, key.substring(signatureIndex));
        }
        String summary = summarize(shadow.config, returnType, new ArrayList<Integer>(new TreeSet<Integer>(mutables)));
        return shadow.informative ? summary : "!" + summary;
    }

    /**
     * Replays the lookup of the call site without the matched custom entry
     */
    private Fallback resolve(TaintConfig taintConfig, Site site, String entrySummary) {
        TaintMethodConfig found = null;
        if (site.location) {
            found = builtinOverridden.get(site.matchedKey);
        }
        if (found == null) {
            List<String> chain = new ArrayList<String>(site.parentClassNames.size() + 1);
            chain.add(site.methodKey);
            for (String parentClassName : site.parentClassNames) {
                chain.add(parentClassName.concat(site.methodId));
            }
            for (String key : chain) {
                TaintMethodConfig config;
                if (!site.location && key.equals(site.matchedKey)) {
                    config = builtinOverridden.get(key);
                    if (config == null) {
                        Shadow shadow = shadows.get(key);
                        config = shadow != null && shadow.informative ? shadow.config : null;
                    }
                } else {
                    config = taintConfig.get(key);
                }
                if (config != null) {
                    found = config;
                    break;
                }
            }
        }

        FallbackKind kind;
        TaintMethodConfig fallback;
        if (found != null && found.isConfigured()) {
            kind = customConfigs.contains(found) ? FallbackKind.CONFIG_CUSTOM : FallbackKind.CONFIG_BUILTIN;
            fallback = found;
        } else if (site.safeReturnType) {
            kind = FallbackKind.TYPE;
            fallback = TaintMethodConfig.SAFE_CONFIG;
        } else if (site.annotationElement) {
            kind = FallbackKind.ANNOTATION;
            fallback = TaintMethodConfig.SAFE_CONFIG;
        } else if (site.receiverIndex >= 0) {
            kind = FallbackKind.RECEIVER;
            Taint receiverTransfer = new Taint(Taint.State.UNKNOWN);
            receiverTransfer.addParameter(site.receiverIndex);
            fallback = new TaintMethodConfig(false);
            fallback.setOuputTaint(receiverTransfer);
        } else if (found != null) {
            kind = FallbackKind.DERIVED;
            fallback = found;
        } else if (site.constructorStackSize > 0) {
            kind = FallbackKind.CONSTRUCTOR;
            fallback = TaintMethodConfig.getDefaultConstructorConfig(site.constructorStackSize);
        } else {
            return new Fallback(FallbackKind.NONE, null, "none");
        }

        String summary = summarize(fallback, site.voidReturn ? "V" : null, site.signatureMutables);
        String category;
        if (summary.equals(entrySummary)) {
            category = kind == FallbackKind.CONFIG_CUSTOM ? "equal-custom" : "equal";
        } else {
            Taint outputTaint = fallback.getOutputTaint();
            if (site.voidReturn) {
                category = "other";
            } else if (outputTaint == null || outputTaint.isTainted()
                    || (outputTaint.isUnknown() && !outputTaint.hasParameters())) {
                category = "unsafe";
            } else if (outputTaint.hasParameters()) {
                Taint.State nonParametricState = outputTaint.getNonParametricState();
                category = nonParametricState == Taint.State.UNKNOWN || nonParametricState == Taint.State.TAINTED
                        ? "unsafe" : "parametric";
            } else {
                category = "other";
            }
        }
        return new Fallback(kind, summary, category);
    }

    /**
     * SAFE output without tags, transfers or side effects
     */
    static boolean isPlainSafe(TaintMethodConfig config) {
        Taint outputTaint = config.getOutputTaint();
        return outputTaint != null && outputTaint.isSafe() && !outputTaint.hasParameters()
                && !outputTaint.hasTags() && !outputTaint.isRemovingTags()
                && outputTaint.getRealInstanceClassName() == null && !config.hasMutableStackIndices();
    }

    /**
     * Describes the effect of the summary at a call site, normalized so that equal
     * strings mean equal results: the output taint (NULL as SAFE, the non-parametric
     * state only with parameters), the indices receiving the output (#), the derived
     * parameter outputs (^) and, for unconfigured summaries without derived parameter
     * outputs, the mutable parameters merged with the default taint (~)
     *
     * @param config the summary
     * @param returnType "V" for void methods, the output is ignored then unless it goes to mutable indices
     * @param signatureMutables stack indices of the mutable parameters, null if unknown
     */
    static String summarize(TaintMethodConfig config, String returnType, List<Integer> signatureMutables) {
        StringBuilder sb = new StringBuilder();
        if ("V".equals(returnType)) {
            sb.append("void");
            if (config.hasMutableStackIndices()) {
                // e.g. the receiver of a constructor
                sb.append('=');
                appendTaintKey(sb, config.getOutputTaint());
            }
        } else {
            appendTaintKey(sb, config.getOutputTaint());
        }
        if (config.hasMutableStackIndices()) {
            sb.append('#');
            appendJoined(sb, new TreeSet<Integer>(config.getMutableStackIndices()));
        }
        if (config.isConfigured()) {
            return sb.toString();
        }
        if (config.isParametersOutputTaintsProcessed()) {
            Map<Integer, Taint> parametersOutputTaints = new TreeMap<Integer, Taint>(config.getParametersOutputTaints());
            for (Map.Entry<Integer, Taint> parameterTaint : parametersOutputTaints.entrySet()) {
                sb.append('^').append(parameterTaint.getKey()).append(':');
                appendTaintKey(sb, parameterTaint.getValue());
            }
        } else if (signatureMutables == null) {
            sb.append("~?");
        } else if (!signatureMutables.isEmpty()) {
            sb.append('~');
            appendJoined(sb, signatureMutables);
        }
        return sb.toString();
    }

    private static void appendTaintKey(StringBuilder sb, Taint taint) {
        if (taint == null) {
            sb.append('-');
            return;
        }
        Taint.State state = taint.getState();
        sb.append(state == Taint.State.NULL ? Taint.State.SAFE : state);
        if (taint.hasParameters()) {
            sb.append('[');
            boolean first = true;
            for (Integer parameter : new TreeSet<Integer>(taint.getParameters())) {
                if (!first) {
                    sb.append(',');
                }
                first = false;
                sb.append(parameter);
                Set<TaintTag> parameterTags = taint.getParameterTags(parameter);
                if (!parameterTags.isEmpty()) {
                    appendTags(sb, parameterTags);
                }
            }
            sb.append(']');
            if (taint.getNonParametricState() != Taint.State.INVALID) {
                sb.append(',').append(taint.getNonParametricState());
            }
        }
        if (taint.hasTags()) {
            sb.append('+');
            appendTags(sb, taint.getTags());
        }
        if (taint.isRemovingTags()) {
            sb.append('-');
            appendTags(sb, taint.getTagsToRemove());
        }
        if (taint.getRealInstanceClassName() != null) {
            sb.append('@').append(taint.getRealInstanceClassName());
        }
    }

    private static void appendTags(StringBuilder sb, Set<TaintTag> tags) {
        Set<String> names = new TreeSet<String>();
        for (TaintTag tag : tags) {
            names.add(tag.name());
        }
        sb.append(names);
    }

    private static void appendJoined(StringBuilder sb, Collection<Integer> values) {
        boolean first = true;
        for (Integer value : values) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(value);
        }
    }

    private static String getReturnType(String key) {
        int index = key.indexOf(')');
        if (index < 0 || key.indexOf('@') >= 0) {
            return null;
        }
        return key.substring(index + 1);
    }
}
