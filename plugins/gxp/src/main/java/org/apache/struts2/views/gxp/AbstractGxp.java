/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.views.gxp;

import com.google.common.annotations.VisibleForTesting;
import com.google.gxp.base.GxpContext;
import com.google.gxp.base.MarkupClosure;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Struts2 to GXP adapter. Can be used to write a GXP or create a
 * {@link MarkupClosure}. Pulls GXP parameters from Struts2 value stack.
 *
 * @author Bob Lee
 */
public abstract class AbstractGxp<T extends MarkupClosure> {

    ValueStackFactory valueStackFactory;
    Map defaultValues = new HashMap();
    List<Param> params;
    Class gxpClass;
    Method writeMethod;
    Method getGxpClosureMethod;
    boolean hasBodyParam;

    protected AbstractGxp(Class gxpClass) {
        this(gxpClass, lookupMethodByName(gxpClass, "write"), lookupMethodByName(gxpClass, "getGxpClosure"));
    }

    protected AbstractGxp(Class gxpClass, Method writeMethod, Method getGxpClosureMethod) {
        this.gxpClass = gxpClass;
        this.writeMethod = writeMethod;
        this.getGxpClosureMethod = getGxpClosureMethod;
        this.params = lookupParams();
    }

    /**
     * Writes GXP. Pulls GXP parameters from Struts2's value stack.
     */
    public void write(Appendable out, GxpContext gxpContext) {
        write(out, gxpContext, null);
    }

    /**
     * Writes GXP. Pulls GXP parameters from Struts2's value stack.
     *
     * @param overrides parameter map pushed onto the value stack
     */
    protected void write(Appendable out, GxpContext gxpContext, Map overrides) {
        Object[] args = getArgs(out, gxpContext, overrides);

        try {
            writeMethod.invoke(getGxpInstance(), args);
        } catch (Exception e) {
            throw new RuntimeException(createDebugString(args, e), e);
        }
    }

    protected Object[] getArgs(Appendable out, GxpContext gxpContext, Map overrides) {
        List<Object> argList = getArgListFromValueStack(overrides);
        Object[] args = new Object[argList.size() + 2];
        args[0] = out;
        args[1] = gxpContext;
        int index = 2;
        for (Iterator<Object> i = argList.iterator(); i.hasNext(); index++) {
            args[index] = i.next();
        }
        return args;
    }

    /**
     * @return the object on which to call the write and getGxpClosure methods. If
     *         the methods are static, this can return {@code null}
     */
    protected Object getGxpInstance() {
        return null;
    }

    /**
     * Creates GXP closure. Pulls GXP parameters from Struts 2 value stack.
     */
    public T getGxpClosure() {
        return getGxpClosure(null, null);
    }

    /**
     * Creates GXP closure. Pulls GXP parameters from Struts 2 value stack.
     *
     * @param body   is pushed onto the stack if this GXP has a
     *               {@link MarkupClosure} (or subclass) parameter named "body".
     * @param params comes first on the value stack.
     */
    @SuppressWarnings("unchecked")
    protected T getGxpClosure(T body, Map params) {
        final Map overrides = getOverrides(body, params);

        Object[] args = getArgListFromValueStack(overrides).toArray();

        try {
            return (T) getGxpClosureMethod.invoke(getGxpInstance(), args);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(createDebugString(args, e), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(createDebugString(args, e), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(createDebugString(args, e), e);
        }
    }

    protected Map getOverrides(T body, Map params) {
        final Map overrides = new HashMap();
        if (hasBodyParam && body != null) {
            overrides.put(Param.BODY_PARAM_NAME, body);
        }
        if (params != null) {
            overrides.putAll(params);
        }
        return overrides;
    }

    /**
     * Iterates over GXP parameters, pulls value from value stack for each
     * parameter, and appends the values to an argument list which will
     * be passed to a method on a GXP.
     *
     * @param overrides parameter map pushed onto the value stack
     */
    List getArgListFromValueStack(Map overrides) {

        ValueStack valueStack = valueStackFactory.createValueStack(ActionContext.getContext().getValueStack());

        // add default values to the bottom of the stack. if no action provides
        // a getter for a param, the default value will be used.
        valueStack.getRoot().add(this.defaultValues);

        // push override parameters onto the stack.
        if (overrides != null && !overrides.isEmpty()) {
            valueStack.push(overrides);
        }

        List args = new ArrayList(params.size());
        for (Param param : getParams()) {
            try {
                args.add(valueStack.findValue(param.getName(), param.getType()));
            } catch (Exception e) {
                throw new RuntimeException("Exception while finding '" + param.getName() + "'.", e);
            }
        }

        return args;
    }

    /**
     * Combines parameter names and types into <code>Param</code> objects.
     */
    List<Param> lookupParams() {
        List<Param> params = new ArrayList<Param>();

        List<String> parameterNames = lookupParameterNames();
        List<Class<?>> parameterTypes = lookupParameterTypes();
        Iterator<Class<?>> parameterTypeIterator = parameterTypes.iterator();

        // If there are more parameter names than parameter types it means that we are
        // using instantiable GXPs and there are 1 or more constructor parameters.
        // Constructor params will always be first in the list, so just drop an appropriate
        // number of elements from the beginning of the list.
        if (parameterNames.size() > parameterTypes.size()) {
            parameterNames = parameterNames.subList(parameterNames.size() - parameterTypes.size(), parameterNames.size());
        }

        for (String name : parameterNames) {
            Class paramType = parameterTypeIterator.next();
            Param param = new Param(gxpClass, name, paramType);
            params.add(param);

            if (param.isBody()) {
                hasBodyParam = true;
            }

            if (param.isOptional()) {
                defaultValues.put(param.getName(), param.getDefaultValue());
            }
        }

        this.defaultValues = Collections.unmodifiableMap(this.defaultValues);
        return Collections.unmodifiableList(params);
    }

    /**
     * Gets list of parameter types.
     */
    List<Class<?>> lookupParameterTypes() {
        List<Class<?>> parameterTypes = Arrays.asList(writeMethod.getParameterTypes());
        // skip the first two, gxp_out and gxp_context. they are for internal use.
        return parameterTypes.subList(2, parameterTypes.size());
    }

    /**
     * Gets list of parameter names.
     */
    List<String> lookupParameterNames() {
        try {
            return (List<String>) gxpClass.getMethod("getArgList").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns first method with the given name. Should not be used if the
     * method is overloaded.
     */
    protected static Method lookupMethodByName(Class clazz, String name) {
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(name)) {
                return methods[i];
            }
        }
        throw new RuntimeException("No " + name + "(...) method found for "
                + clazz.getName() + ".");
    }

    public Class getGxpClass() {
        return this.gxpClass;
    }

    /**
     * Returns list of parameters requested by GXP.
     */
    public List<Param> getParams() {
        return params;
    }

    /**
     * Returns generated GXP class given an absolute path to a GXP file.
     * The current implementation assumes that the GXP and generated Java source
     * file share the same name with different extensions.
     */
    @VisibleForTesting
    public static Class getGxpClassForPath(String gxpPath) {
        int offset = (gxpPath.charAt(0) == '/') ? 1 : 0;
        String className = gxpPath.substring(offset, gxpPath.length() - 4).replace('/', '.');
        try {
            return getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static ClassLoader getClassLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return (loader == null) ? ClassLoader.getSystemClassLoader() : loader;
    }

    /**
     * Creates debug String which can be tacked onto an exception.
     */
    String createDebugString(Object[] args, Exception exception) {
        StringBuffer buffer = new StringBuffer();
        printExceptionTraceToBuffer(exception, buffer);
        buffer.append("\nException in GXP: ").append(gxpClass.getName()).append(". Params:");
        int index = 2;
        for (Param param : getParams()) {
            try {
                Object arg = args[index++];
                String typesMatch = "n/a (null)";
                if (arg != null) {
                    if (doesArgumentTypeMatchParamType(param, arg)) {
                        typesMatch = "YES";
                    } else {
                        typesMatch = "NO";
                    }
                }
                buffer.append("\n  ")
                        .append(param.toString())
                        .append(" = ")
                        .append(arg)
                        .append("; ")
                        .append("[types match? ")
                        .append(typesMatch)
                        .append("]");
            } catch (Exception e) {
                buffer.append(" >Error getting information for param # ").append(index).append("< ");
            }
        }
        buffer.append("\nStack trace: ");
        return buffer.toString();
    }

    private void printExceptionTraceToBuffer(Exception e,
                                             StringBuffer buffer) {
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        buffer.append(out.getBuffer().toString());
    }

    private boolean doesArgumentTypeMatchParamType(Param param, Object arg) {
        Class paramType = param.getType();
        Class<? extends Object> argClass = arg.getClass();

        // TODO(jpelly): Handle all primitive unwrapping (ie, Boolean --> boolean).
        if (boolean.class.equals(paramType) && Boolean.class.equals(argClass)) {
            return true;
        } else if (char.class.equals(paramType) && Character.class.equals(argClass)) {
            return true;
        }
        return paramType.isAssignableFrom(argClass);
    }

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }
}
