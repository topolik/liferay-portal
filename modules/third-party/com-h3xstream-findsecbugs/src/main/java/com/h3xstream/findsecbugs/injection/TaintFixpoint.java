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
package com.h3xstream.findsecbugs.injection;

import com.h3xstream.findsecbugs.taintanalysis.TaintDataflow;
import com.h3xstream.findsecbugs.taintanalysis.TaintDataflowEngine;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.bcel.BCELUtil;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.IAnalysisCache;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;
import edu.umd.cs.findbugs.util.TopologicalSort;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.INVOKEDYNAMIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUTSTATIC;

/**
 * Computes the taint dataflow of all methods of a class until the derived summaries and the static
 * field taints are stable.
 * <p>
 * A method depends on its same-class callees (summaries), on the lambda bodies it creates and on the
 * methods writing the static fields it reads (static field taint). The strongly connected components of
 * this graph are analyzed with the dependencies first, the cyclic ones repeatedly until nothing
 * changes or the round limit is reached. The resulting order is also used to look for the sinks once,
 * so the sinks of the callees are known at the call sites.
 * <p>
 * With the prepass enabled, the first analysis derives the summaries of all application classes in
 * the order SpotBugs uses, callees first, before the detectors analyze the first class. A call site
 * then knows the summaries of the classes analyzed after its class, such as the overriding methods.
 */
class TaintFixpoint {

    private static final Logger LOGGER = Logger.getLogger(TaintFixpoint.class.getName());
    static final int MAX_ROUNDS = 5;

    private static ClassContext lastClassContext;
    private static List<Method> lastOrder;

    private final ClassContext classContext;
    private final String slashedClassName;
    private final List<Method> methods = new ArrayList<Method>();
    private final Map<Method, Set<Method>> dependencies = new IdentityHashMap<Method, Set<Method>>();
    private final Map<Method, Set<String>> writtenFields = new IdentityHashMap<Method, Set<String>>();

    private TaintFixpoint(ClassContext classContext) {
        this.classContext = classContext;
        this.slashedClassName = com.h3xstream.findsecbugs.BCELUtil.getSlashedClassName(classContext.getJavaClass());
    }

    /**
     * Analyzes the class, unless already done for the class context, and returns the methods in
     * the analysis order
     */
    static synchronized List<Method> analyze(ClassContext classContext) {
        if (TaintDataflowEngine.startPrepass()) {
            try {
                prepass();
            } finally {
                TaintDataflowEngine.finishPrepass();
            }
        }
        if (classContext != lastClassContext) {
            TaintFixpoint fixpoint = new TaintFixpoint(classContext);
            fixpoint.buildDependencies();
            List<List<Method>> components = fixpoint.getComponents();
            fixpoint.computeDataflows(components, TaintDataflowEngine.isPrepassed());
            List<Method> order = new ArrayList<Method>();
            for (List<Method> component : components) {
                order.addAll(component);
            }
            lastClassContext = classContext;
            lastOrder = Collections.unmodifiableList(order);
        }
        return lastOrder;
    }

    private static void prepass() {
        AnalysisContext context = AnalysisContext.currentAnalysisContext();
        final IAnalysisCache cache = Global.getAnalysisCache();
        List<ClassDescriptor> classes = new ArrayList<ClassDescriptor>();
        for (XClass xClass : context.getSubtypes2().getXClassCollection()) {
            if (context.isApplicationClass(xClass.getClassDescriptor())) {
                classes.add(xClass.getClassDescriptor());
            }
        }
        Collections.sort(classes);
        classes = TopologicalSort.sortByCallGraph(classes, new TopologicalSort.OutEdges<ClassDescriptor>() {
            @Override
            public Collection<ClassDescriptor> getOutEdges(ClassDescriptor descriptor) {
                try {
                    return cache.getClassAnalysis(XClass.class, descriptor).getCalledClassDescriptors();
                } catch (CheckedAnalysisException e) {
                    return Collections.emptyList();
                }
            }
        });
        ClassDescriptor classBeingAnalyzed = context.getClassBeingAnalyzed();
        try {
            for (ClassDescriptor descriptor : classes) {
                try {
                    context.setClassBeingAnalyzed(descriptor);
                    TaintFixpoint fixpoint = new TaintFixpoint(cache.getClassAnalysis(ClassContext.class, descriptor));
                    fixpoint.buildDependencies();
                    fixpoint.computeDataflows(fixpoint.getComponents(), false);
                } catch (CheckedAnalysisException e) {
                    // the detectors report it when they analyze the class
                } catch (RuntimeException e) {
                    // the same
                }
            }
        } finally {
            if (classBeingAnalyzed != null) {
                context.setClassBeingAnalyzed(classBeingAnalyzed);
            }
        }
    }

    private void buildDependencies() {
        JavaClass javaClass = classContext.getJavaClass();
        ConstantPoolGen cpg = classContext.getConstantPoolGen();
        Map<Method, Set<String>> readFields = new IdentityHashMap<Method, Set<String>>();
        Map<String, List<Method>> fieldWriters = new HashMap<String, List<Method>>();
        for (Method method : javaClass.getMethods()) {
            MethodGen methodGen = classContext.getMethodGen(method);
            if (methodGen == null || methodGen.getInstructionList() == null) {
                continue;
            }
            methods.add(method);
            Set<Method> callees = new LinkedHashSet<Method>();
            Set<String> reads = new LinkedHashSet<String>();
            Set<String> writes = new LinkedHashSet<String>();
            for (InstructionHandle handle : methodGen.getInstructionList().getInstructionHandles()) {
                Instruction instruction = handle.getInstruction();
                if (instruction instanceof INVOKEDYNAMIC) {
                    addIfNotNull(callees, AbstractTaintDetector.getLambdaImplementation(
                            javaClass, cpg, (INVOKEDYNAMIC) instruction));
                } else if (instruction instanceof InvokeInstruction) {
                    addIfNotNull(callees, AbstractTaintDetector.getSameClassMethod(
                            javaClass, cpg, (InvokeInstruction) instruction));
                } else if (instruction instanceof GETSTATIC) {
                    reads.add(getFieldSignature(cpg, (FieldInstruction) instruction));
                } else if (instruction instanceof PUTSTATIC) {
                    writes.add(getFieldSignature(cpg, (FieldInstruction) instruction));
                }
            }
            dependencies.put(method, callees);
            readFields.put(method, reads);
            writtenFields.put(method, writes);
            for (String field : writes) {
                List<Method> writers = fieldWriters.get(field);
                if (writers == null) {
                    writers = new ArrayList<Method>();
                    fieldWriters.put(field, writers);
                }
                writers.add(method);
            }
        }
        for (Method method : methods) {
            if (isStaticInitializer(method)) {
                // runs before the other methods, cannot read what they write
                continue;
            }
            for (String field : readFields.get(method)) {
                List<Method> writers = fieldWriters.get(field);
                if (writers != null) {
                    dependencies.get(method).addAll(writers);
                }
            }
        }
    }

    private static boolean isStaticInitializer(Method method) {
        return "<clinit>".equals(method.getName());
    }

    private static void addIfNotNull(Set<Method> methods, Method method) {
        if (method != null) {
            methods.add(method);
        }
    }

    private static String getFieldSignature(ConstantPoolGen cpg, FieldInstruction instruction) {
        // the same key as in TaintFrameModelingVisitor
        return com.h3xstream.findsecbugs.BCELUtil.getSlashedClassName(cpg, instruction) + "." + instruction.getName(cpg);
    }

    /**
     * Tarjan's algorithm without recursion, the components come out with their dependencies first
     */
    private List<List<Method>> getComponents() {
        List<List<Method>> components = new ArrayList<List<Method>>();
        Map<Method, Integer> indexes = new IdentityHashMap<Method, Integer>();
        Map<Method, Integer> lowLinks = new IdentityHashMap<Method, Integer>();
        Set<Method> onStack = Collections.newSetFromMap(new IdentityHashMap<Method, Boolean>());
        List<Method> stack = new ArrayList<Method>();
        List<Method> callStack = new ArrayList<Method>();
        List<Iterator<Method>> iteratorStack = new ArrayList<Iterator<Method>>();
        int index = 0;
        List<Method> roots = new ArrayList<Method>();
        for (Method method : methods) {
            if (isStaticInitializer(method)) {
                roots.add(0, method);
            } else {
                roots.add(method);
            }
        }
        for (Method root : roots) {
            if (indexes.containsKey(root)) {
                continue;
            }
            indexes.put(root, index);
            lowLinks.put(root, index);
            index++;
            stack.add(root);
            onStack.add(root);
            callStack.add(root);
            iteratorStack.add(dependencies.get(root).iterator());
            while (!callStack.isEmpty()) {
                int top = callStack.size() - 1;
                Method method = callStack.get(top);
                Iterator<Method> iterator = iteratorStack.get(top);
                if (iterator.hasNext()) {
                    Method dependency = iterator.next();
                    if (!dependencies.containsKey(dependency)) {
                        continue;
                    }
                    if (!indexes.containsKey(dependency)) {
                        indexes.put(dependency, index);
                        lowLinks.put(dependency, index);
                        index++;
                        stack.add(dependency);
                        onStack.add(dependency);
                        callStack.add(dependency);
                        iteratorStack.add(dependencies.get(dependency).iterator());
                    } else if (onStack.contains(dependency)) {
                        lowLinks.put(method, Math.min(lowLinks.get(method), indexes.get(dependency)));
                    }
                    continue;
                }
                callStack.remove(top);
                iteratorStack.remove(top);
                if (top > 0) {
                    Method caller = callStack.get(top - 1);
                    lowLinks.put(caller, Math.min(lowLinks.get(caller), lowLinks.get(method)));
                }
                if (lowLinks.get(method).equals(indexes.get(method))) {
                    List<Method> component = new ArrayList<Method>();
                    Method member;
                    do {
                        member = stack.remove(stack.size() - 1);
                        onStack.remove(member);
                        component.add(member);
                    } while (member != method);
                    // the members discovered last come first, closer to the callees first order
                    components.add(component);
                }
            }
        }
        return components;
    }

    /**
     * @param reanalyze true to replace the cached dataflows, derived by the prepass
     */
    private void computeDataflows(List<List<Method>> components, boolean reanalyze) {
        IAnalysisCache cache = Global.getAnalysisCache();
        for (List<Method> component : components) {
            for (Method method : component) {
                try {
                    if (reanalyze) {
                        TaintDataflowEngine.reanalyze(cache, getDescriptor(method));
                    } else {
                        cache.getMethodAnalysis(TaintDataflow.class, getDescriptor(method));
                    }
                } catch (CheckedAnalysisException e) {
                    // cached by SpotBugs, the detectors report it when they request the dataflow
                } catch (RuntimeException e) {
                    // the same
                }
            }
            if (!isCyclic(component)) {
                continue;
            }
            Map<String, String> state = getState(component);
            int round = 1;
            while (true) {
                if (round == MAX_ROUNDS) {
                    LOGGER.fine("Taint analysis of " + component.size() + " methods in " + slashedClassName
                            + " did not stabilize in " + MAX_ROUNDS + " rounds");
                    break;
                }
                round++;
                for (Method method : component) {
                    try {
                        TaintDataflowEngine.reanalyze(cache, getDescriptor(method));
                    } catch (CheckedAnalysisException e) {
                        // keeps the previous dataflow
                    } catch (RuntimeException e) {
                        // the same
                    }
                }
                Map<String, String> newState = getState(component);
                if (newState.equals(state)) {
                    break;
                }
                state = newState;
            }
        }
        for (List<Method> component : components) {
            for (Method method : component) {
                TaintDataflowEngine.writeDerivedConfig(getSlashedMethodName(method));
            }
        }
    }

    private boolean isCyclic(List<Method> component) {
        if (component.size() > 1) {
            return true;
        }
        Method method = component.get(0);
        return dependencies.get(method).contains(method);
    }

    /**
     * Collects the summaries of the methods and the taints of the static fields they write
     */
    private Map<String, String> getState(List<Method> component) {
        Map<String, String> state = new LinkedHashMap<String, String>();
        for (Method method : component) {
            String slashedMethodName = getSlashedMethodName(method);
            state.put(slashedMethodName, TaintDataflowEngine.getSummaryKey(slashedMethodName));
            for (String field : writtenFields.get(method)) {
                state.put(field, TaintDataflowEngine.getStaticFieldKey(field));
            }
        }
        return state;
    }

    private String getSlashedMethodName(Method method) {
        return slashedClassName + "." + method.getName() + method.getSignature();
    }

    private MethodDescriptor getDescriptor(Method method) {
        return BCELUtil.getMethodDescriptor(classContext.getJavaClass(), method);
    }
}
