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

import com.h3xstream.findsecbugs.FindSecBugsGlobalConfig;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.DepthFirstSearch;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.IAnalysisCache;
import edu.umd.cs.findbugs.classfile.IMethodAnalysisEngine;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.apache.bcel.generic.MethodGen;

/**
 * Requests or creates needed objects and execute taint analysis,
 * extends taint summaries with analyzed methods
 * 
 * @author David Formanek (Y Soft Corporation, a.s.)
 */
public class TaintDataflowEngine implements IMethodAnalysisEngine<TaintDataflow> {

    private static final FindSecBugsGlobalConfig CONFIG = FindSecBugsGlobalConfig.getInstance();
    private static final Logger LOGGER = Logger.getLogger(TaintDataflowEngine.class.getName());
    private static final String TAINT_CONFIG_PATH = "taint-config/";
    private static final String[] TAINT_CONFIG_FILENAMES = {
        "android-taint-sql.txt",
        "collections.txt",
        "dropwizard.txt",
        "guava.txt",
        "java-ee.txt",
        "java-lang.txt",
        "java-net.txt",
        "jetty.txt",
        "kotlin.txt",
        "logging.txt",
        "other.txt",
        "portlet.txt",
        "scala.txt",
        "sonarqube.txt",
        "spring.txt",
        "struts2-taint.txt",
        "wicket.txt",
    };
    private static final String SAFE_ENCODERS_PATH = "safe-encoders/";
    private static final String[] SAFE_ENCODERS_FILENAMES = {
        "owasp.txt",
        "apache-commons.txt",
        "other.txt"
    };
    private static TaintDataflowEngine instance;
    private final TaintConfig taintConfig = new TaintConfig();
    private int excludedKeyCount;
    private boolean derivedConfigImported;
    private boolean prepassStarted;
    private Map<String, Taint> staticFieldsTaint;
    private static boolean prepass;
    /**
     * Whether {@link #startPrepass()} skips the prepass, set only by the tests of the summaries pending
     * without it
     */
    static boolean prepassSkipped;
    @SuppressFBWarnings(value="MS_SHOULD_BE_REFACTORED_TO_BE_FINAL", justification="Can't be final because FileOutputStream needs a try-catch.")
    protected static Writer writer = null;
    private static List<TaintFrameAdditionalVisitor> visitors = new ArrayList<TaintFrameAdditionalVisitor>();
    private static boolean configUsageShutdownHook;
    private static final Set<String> distrustedSummaries = new HashSet<String>();
    private static String distrustFileName;
    private static Writer distrustWriter;


    static {
        if (CONFIG.isDebugOutputTaintConfigs()) {
            try {
                final String fileName = "derived-config.txt";
                writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(fileName), "utf-8"));
                // note: writer is not closed until the end
                LOGGER.info("Derived method configs will be output to " + fileName);
            } catch (UnsupportedEncodingException ex) {
                assert false : ex.getMessage();
            } catch (FileNotFoundException ex) {
                AnalysisContext.logError("File for derived configs cannot be created or opened", ex);
            }
        }
    }

    /**
     * Constructs the engine and loads all configured method summaries
     */
    public TaintDataflowEngine() {
        if (CONFIG.isTaintConfigUsage()) {
            taintConfig.setUsage(new TaintConfigUsage(CONFIG.isTaintConfigUsageAll()));
            registerConfigUsageShutdownHook();
            LOGGER.log(Level.INFO, "Taint config usage accounting enabled (mode {0}), output to {1}", new Object[] {
                CONFIG.isTaintConfigUsageAll() ? "all" : "used",
                new File(CONFIG.getTaintConfigUsageFile()).getAbsolutePath()});
        }
        for (String path : TAINT_CONFIG_FILENAMES) {
            loadTaintConfig(TAINT_CONFIG_PATH.concat(path), true);
        }
        for (String path : SAFE_ENCODERS_FILENAMES) {
            loadTaintConfig(SAFE_ENCODERS_PATH.concat(path), true);
        }

        // Override the sensitive data taints
        loadTaintConfig(TAINT_CONFIG_PATH.concat("taint-sensitive-data.txt"), false);

        if (CONFIG.isTaintedSystemVariables()) {
            loadTaintConfig(TAINT_CONFIG_PATH.concat("tainted-system-variables.txt"), false);
            LOGGER.info("System variables are considered to be tainted");
        }
        String excludeConfigKeys = CONFIG.getTaintExcludeConfigKeys();
        if (excludeConfigKeys != null && !excludeConfigKeys.isEmpty()) {
            taintConfig.setExcludedKeys(loadExcludedKeys(excludeConfigKeys));
        }
        String customConfigFile = CONFIG.getCustomConfigFile();
        if (customConfigFile != null && !customConfigFile.isEmpty()) {
            for (String configFile : customConfigFile.split(File.pathSeparator)) {
                addCustomConfig(configFile);
            }
        }
        if (excludeConfigKeys != null && !excludeConfigKeys.isEmpty()) {
            LOGGER.log(Level.INFO, "Excluded {0} entries ({1} distinct keys) from custom taint config, {2} keys listed in {3}",
                    new Object[] {String.valueOf(taintConfig.getExcludedEntries()),
                    String.valueOf(taintConfig.getExcludedMatches()), String.valueOf(excludedKeyCount),
                    excludeConfigKeys});
        }
        if (!CONFIG.isTaintedMainArgument()) {
            LOGGER.info("The argument of the main method is not considered tainted");
        }
        if (CONFIG.isTaintOverriders()) {
            taintConfig.setMergingOverriders(true);
            LOGGER.info("The derived summaries are merged with the summaries of the overriding methods");
            String overridersFile = CONFIG.getTaintOverridersFile();
            if (isSet(overridersFile)) {
                loadOverriders(overridersFile);
            }
        } else if (isSet(CONFIG.getTaintOverridersFile()) || isSet(CONFIG.getTaintDistrustDerivedFile())) {
            LOGGER.warning("The derived summaries settings are ignored, findsecbugs.taint.overriders is not enabled");
        }
    }

    private static boolean isSet(String value) {
        return value != null && !value.isEmpty();
    }

    private void loadOverriders(String overridersFile) {
        for (String path : overridersFile.split(File.pathSeparator)) {
            try (InputStream stream = path.endsWith(".gz")
                    ? new GZIPInputStream(new FileInputStream(path))
                    : new FileInputStream(path)) {
                taintConfig.loadOverriders(stream);
            } catch (IOException ex) {
                throw new RuntimeException("Cannot load overriders from " + path, ex);
            }
        }
        LOGGER.log(Level.INFO, "Loaded the overriders of {0} methods from {1}",
                new Object[] {String.valueOf(taintConfig.getExternalOverriderCount()), overridersFile});
    }

    public static void registerAdditionalVisitor(TaintFrameAdditionalVisitor visitor) {
        visitors.add(visitor);
    }

    private void loadTaintConfig(String path, boolean checkRewrite) {
        assert path != null && !path.isEmpty();

        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
            taintConfig.load(stream, checkRewrite, path, false);
        } catch (IOException ex) {
            assert false : ex.getMessage();
        }
    }
    
    private Set<String> loadExcludedKeys(String path) {
        Set<String> keys = new HashSet<String>();
        try {
            for (String line : Files.readAllLines(new File(path).toPath(), StandardCharsets.UTF_8)) {
                String key = line.trim();
                if (!key.isEmpty() && !key.startsWith("#")) {
                    keys.add(key);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Cannot load excluded taint config keys from " + path, ex);
        }
        excludedKeyCount = keys.size();
        return keys;
    }

    private void addCustomConfig(String path) {
        File file = new File(path);
        try (InputStream stream = file.exists()
                ? new FileInputStream(file)
                : getClass().getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                String message = String.format("Could not add custom config. "
                        + "Neither file %s nor resource matching %s found.",
                        file.getAbsolutePath(), path);
                throw new IllegalArgumentException(message);
            }
            taintConfig.load(stream, false, path, true);
            LOGGER.log(Level.INFO, "Custom taint config loaded from {0}", path);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot load custom taint config from " + path, ex);
        }
    }
    
    @Override
    public TaintDataflow analyze(IAnalysisCache cache, MethodDescriptor descriptor)
            throws CheckedAnalysisException {
        if (!derivedConfigImported) {
            importDerivedConfigs();
        }
        if(FindSecBugsGlobalConfig.getInstance().isDebugPrintInstructionVisited() || FindSecBugsGlobalConfig.getInstance().isDebugPrintInvocationVisited()) {
            System.out.println("==[ Method: "+descriptor.getName()+" ]==");
        }
        CFG cfg = cache.getMethodAnalysis(CFG.class, descriptor);
        DepthFirstSearch dfs = cache.getMethodAnalysis(DepthFirstSearch.class, descriptor);
        MethodGen methodGen = cache.getMethodAnalysis(MethodGen.class, descriptor);
        TaintAnalysis analysis = new TaintAnalysis(methodGen, dfs, descriptor, taintConfig, visitors);
        TaintDataflow flow = new TaintDataflow(cfg, analysis);
        flow.execute();
        analysis.finishAnalysis();
        return flow;
    }

    /**
     * Imports the summaries derived by other scans, the application classes are known only once the analysis
     * starts and their summaries are skipped, they are derived again by this scan
     */
    private void importDerivedConfigs() {
        derivedConfigImported = true;
        String importConfigFile = CONFIG.getTaintImportConfigFile();
        if (importConfigFile == null || importConfigFile.isEmpty()) {
            return;
        }
        //Looking the classes up in the class hierarchy would report the classes of the other scans as missing
        final Set<String> applicationResources = Global.getAnalysisCache().getClassPath()
                .getApplicationCodebaseEntries().keySet();
        for (String path : importConfigFile.split(File.pathSeparator)) {
            try (InputStream stream = path.endsWith(".gz")
                    ? new GZIPInputStream(new FileInputStream(path))
                    : new FileInputStream(path)) {
                taintConfig.loadDerived(stream, className -> applicationResources.contains(className + ".class"));
            } catch (IOException ex) {
                throw new RuntimeException("Cannot import derived taint config from " + path, ex);
            }
        }
        LOGGER.log(Level.INFO, "Imported {0} derived taint summaries from {1}, skipped {2} of the analyzed classes, "
                + "{3} already set, {4} bad", new Object[] {String.valueOf(taintConfig.getImportedEntries()),
                importConfigFile, String.valueOf(taintConfig.getImportSkippedEntries()),
                String.valueOf(taintConfig.getImportPresentEntries()), String.valueOf(taintConfig.getImportBadEntries())});
    }

    /**
     * Analyzes the method again and replaces the cached dataflow, the summaries and the static field
     * taints of the other methods may have changed since the previous analysis
     */
    public static TaintDataflow reanalyze(IAnalysisCache cache, MethodDescriptor descriptor)
            throws CheckedAnalysisException {
        TaintDataflow flow = instance.analyze(cache, descriptor);
        cache.eagerlyPutMethodAnalysis(TaintDataflow.class, descriptor, flow);
        return flow;
    }

    /**
     * Starts the prepass deriving the summaries of all analyzed classes, once per analysis. Until
     * {@link #finishPrepass()}, the derived summaries and the distrusted summaries are not written and
     * the config usage is not recorded. The prepass leaves only the derived summaries behind.
     *
     * @return false if the overriders are disabled or the prepass already started
     */
    public static boolean startPrepass() {
        if (!CONFIG.isTaintOverriders() || prepassSkipped || instance.prepassStarted) {
            return false;
        }
        instance.prepassStarted = true;
        // the static initializers must not read the static fields written by the prepass
        instance.staticFieldsTaint = instance.taintConfig.copyStaticFieldsTaint();
        prepass = true;
        return true;
    }

    public static void finishPrepass() {
        prepass = false;
        if (instance.staticFieldsTaint != null) {
            instance.taintConfig.restoreStaticFieldsTaint(instance.staticFieldsTaint);
            instance.staticFieldsTaint = null;
        }
    }

    /**
     * @return true while the prepass derives the summaries
     */
    public static boolean isPrepassing() {
        return prepass;
    }

    /**
     * @return true if the prepass of the analysis has derived the summaries, so the dataflows cached by
     * the prepass are outdated
     */
    public static boolean isPrepassed() {
        return instance.prepassStarted && !prepass;
    }

    /**
     * Describes the derived summary of the method, used to detect a change between two analyses
     */
    public static String getSummaryKey(String slashedMethodName) {
        TaintMethodConfig config = instance.taintConfig.get(slashedMethodName);
        if (config == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        appendTaintKey(sb, config.getOutputTaint());
        Map<Integer, Taint> parametersOutputTaints = new TreeMap<Integer, Taint>(config.getParametersOutputTaints());
        for (Map.Entry<Integer, Taint> parameterTaint : parametersOutputTaints.entrySet()) {
            sb.append('^').append(parameterTaint.getKey()).append(':');
            appendTaintKey(sb, parameterTaint.getValue());
        }
        return sb.toString();
    }

    /**
     * Describes the taint of the static field, used to detect a change between two analyses
     */
    public static String getStaticFieldKey(String fieldSignature) {
        StringBuilder sb = new StringBuilder();
        appendTaintKey(sb, instance.taintConfig.getStaticFieldTaint(fieldSignature, null));
        return sb.toString();
    }

    private static void appendTaintKey(StringBuilder sb, Taint taint) {
        if (taint == null) {
            sb.append('-');
            return;
        }
        sb.append(taint.getState()).append(new TreeSet<Integer>(taint.getParameters()));
        sb.append(taint.getNonParametricState());
        sb.append('+').append(new TreeSet<TaintTag>(taint.getTags()));
        sb.append('-').append(new TreeSet<TaintTag>(taint.getTagsToRemove()));
        sb.append('@').append(taint.getRealInstanceClassName());
    }

    /**
     * Writes the derived summary of the method, if enabled
     */
    public static void writeDerivedConfig(String slashedMethodName) {
        if (!CONFIG.isDebugOutputTaintConfigs() || writer == null || prepass) {
            return;
        }
        TaintMethodConfig derivedConfig = instance.taintConfig.get(slashedMethodName);
        if (derivedConfig == null) {
            return;
        }
        try {
            writer.append(slashedMethodName);

            Taint outputTaint = derivedConfig.getOutputTaint();
            if (outputTaint != null) {
                writer.append(':');
                writeTaint(outputTaint);
            }

            Map<Integer, Taint> parametersOutputTaints = derivedConfig.getParametersOutputTaints();
            if (!parametersOutputTaints.isEmpty()) {
                for (Map.Entry<Integer, Taint> parameterTaint : parametersOutputTaints.entrySet()) {
                    writer.append('^');
                    writer.append(Integer.toString(parameterTaint.getKey()));
                    writer.append(':');
                    writeTaint(parameterTaint.getValue());
                }
            }

            writer.append('\n');
            writer.flush();
        } catch (IOException ex) {
            AnalysisContext.logError("Cannot write derived configs", ex);
        }
    }

    /**
     * Writes the derived summary not applied to a call once, if enabled
     *
     * @param kind override or constructor
     * @param methodName slashed class name with the method id
     * @param overriders known subtypes declaring the method, null if not relevant
     * @param resolved false if the subtypes are not known
     */
    public static void writeDistrustedSummary(String kind, String methodName, Set<String> overriders,
            boolean resolved) {
        StringBuilder line = new StringBuilder(kind).append('\t').append(methodName);
        if (!resolved) {
            line.append("\t?");
        } else if (overriders != null) {
            line.append('\t').append(overriders.size()).append('\t')
                    .append(overriders.stream().limit(10).collect(Collectors.joining(",")));
        }
        writeDistrustLine(line.toString());
    }

    /**
     * Writes the outcome of merging the summaries of the overriding methods once, if enabled
     *
     * @param kind merge for a derived summary merged with the overriding methods, implementors for
     * an abstract method
     * @param methodName slashed class name with the method id
     * @param outcome merged, or the reason the summaries were not merged
     */
    public static void writeMergedSummary(String kind, String methodName, String outcome) {
        writeDistrustLine(kind + '\t' + methodName + '\t' + outcome);
    }

    private static synchronized void writeDistrustLine(String line) {
        String fileName = CONFIG.getTaintDistrustDerivedFile();
        if (fileName == null || fileName.isEmpty() || prepass) {
            return;
        }
        try {
            if (!fileName.equals(distrustFileName)) {
                if (distrustWriter != null) {
                    distrustWriter.close();
                }
                distrustedSummaries.clear();
                distrustFileName = fileName;
                distrustWriter = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(fileName), StandardCharsets.UTF_8));
            }
            if (!distrustedSummaries.add(line)) {
                return;
            }
            distrustWriter.append(line).append('\n');
            distrustWriter.flush();
        } catch (IOException ex) {
            AnalysisContext.logError("Cannot write distrusted derived summaries", ex);
        }
    }

    private static void writeTaint(Taint taint) throws IOException {
        writer.append(formatDerivedTaint(taint));
    }

    /**
     * Formats the taint in the summary format, an argument whose tags differ from the tags of the taint is
     * followed by its tags in brackets, e.g. {@code 0[XSS_SAFE],1,UNKNOWN|+SQL_INJECTION_SAFE,-URL_ENCODED}
     */
    static String formatDerivedTaint(Taint taint) {
        StringBuilder sb = new StringBuilder();
        if (taint.isUnknown() && taint.hasParameters()) {
            String separator = "";
            for (Integer parameter : new TreeSet<Integer>(taint.getParameters())) {
                sb.append(separator).append(parameter);
                separator = ",";

                Set<TaintTag> parameterTags = taint.getParameterTags(parameter);
                if (!parameterTags.equals(taint.getTags())) {
                    sb.append('[');
                    sb.append(parameterTags.stream().map(TaintTag::name).sorted().collect(Collectors.joining(",")));
                    sb.append(']');
                }
            }

            Taint.State nonParametricState = taint.getNonParametricState();
            if (nonParametricState != Taint.State.INVALID) {
                sb.append(',');
                sb.append(nonParametricState.name());
            }
        }
        else {
            sb.append(taint.getState().name());
        }

        if (taint.hasTags() || taint.isRemovingTags()) {
            sb.append('|').append(Stream.concat(
                    taint.getTags().stream().map(tag -> "+" + tag.name()).sorted(),
                    taint.getTagsToRemove().stream().map(tag -> "-" + tag.name()).sorted())
                    .collect(Collectors.joining(",")));
        }
        return sb.toString();
    }

    /**
     * Writes the taint config usage of the current engine, if enabled and changed since the last write
     */
    public static void writeConfigUsage() {
        TaintDataflowEngine engine = instance;
        if (engine == null) {
            return;
        }
        TaintConfigUsage usage = engine.taintConfig.getUsage();
        if (usage != null) {
            synchronized (usage) {
                usage.write(engine.taintConfig, CONFIG.getTaintConfigUsageFile());
            }
        }
    }

    private static synchronized void registerConfigUsageShutdownHook() {
        if (configUsageShutdownHook) {
            return;
        }
        configUsageShutdownHook = true;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                writeConfigUsage();
            }
        }, "taint-config-usage"));
    }

    @Override
    public void registerWith(IAnalysisCache iac) {
        iac.registerMethodAnalysisEngine(TaintDataflow.class, this);
        instance = this;
    }
}
