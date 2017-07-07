/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.MemberAccessValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.logging.LoggerUtils;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Ognl implementation of a value stack that allows for dynamic Ognl expressions to be evaluated against it. When evaluating an expression,
 * the stack will be searched down the stack, from the latest objects pushed in to the earliest, looking for a bean with a getter or setter
 * for the given property or a method of the given name (depending on the expression being evaluated).
 *
 * @author Patrick Lightbody
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class OgnlValueStack implements Serializable, ValueStack, ClearableValueStack, MemberAccessValueStack {

    public static final String THROW_EXCEPTION_ON_FAILURE = OgnlValueStack.class.getName() + ".throwExceptionOnFailure";

    private static final long serialVersionUID = 370737852934925530L;

    private static final String MAP_IDENTIFIER_KEY = "com.opensymphony.xwork2.util.OgnlValueStack.MAP_IDENTIFIER_KEY";
    private static final Logger LOG = LoggerFactory.getLogger(OgnlValueStack.class);

    CompoundRoot root;
    transient Map<String, Object> context;
    Class defaultType;
    Map<Object, Object> overrides;
    transient OgnlUtil ognlUtil;
    transient SecurityMemberAccess securityMemberAccess;
    private transient XWorkConverter converter;

    private boolean devMode;
    private boolean logMissingProperties;

    protected OgnlValueStack(XWorkConverter xworkConverter, CompoundRootAccessor accessor, TextProvider prov, boolean allowStaticAccess) {
        setRoot(xworkConverter, accessor, new CompoundRoot(), allowStaticAccess);
        push(prov);
    }

    protected OgnlValueStack(ValueStack vs, XWorkConverter xworkConverter, CompoundRootAccessor accessor, boolean allowStaticAccess) {
        setRoot(xworkConverter, accessor, new CompoundRoot(vs.getRoot()), allowStaticAccess);
    }

    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
        securityMemberAccess.setExcludedClasses(ognlUtil.getExcludedClasses());
        securityMemberAccess.setExcludedPackageNamePatterns(ognlUtil.getExcludedPackageNamePatterns());
        securityMemberAccess.setExcludedPackageNames(ognlUtil.getExcludedPackageNames());
        securityMemberAccess.setDisallowProxyMemberAccess(ognlUtil.isDisallowProxyMemberAccess());
    }

    protected void setRoot(XWorkConverter xworkConverter, CompoundRootAccessor accessor, CompoundRoot compoundRoot,
                           boolean allowStaticMethodAccess) {
        this.root = compoundRoot;
        this.securityMemberAccess = new SecurityMemberAccess(allowStaticMethodAccess);
        this.context = Ognl.createDefaultContext(this.root, accessor, new OgnlTypeConverterWrapper(xworkConverter), securityMemberAccess);
        context.put(VALUE_STACK, this);
        Ognl.setClassResolver(context, accessor);
        ((OgnlContext) context).setTraceEvaluations(false);
        ((OgnlContext) context).setKeepLastEvaluation(false);
    }

    @Inject(XWorkConstants.DEV_MODE)
    public void setDevMode(String mode) {
        devMode = "true".equalsIgnoreCase(mode);
    }

    @Inject(value = "logMissingProperties", required = false)
    public void setLogMissingProperties(String logMissingProperties) {
        this.logMissingProperties = "true".equalsIgnoreCase(logMissingProperties);
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#getContext()
     */
    public Map<String, Object> getContext() {
        return context;
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#setDefaultType(java.lang.Class)
     */
    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#setExprOverrides(java.util.Map)
     */
    public void setExprOverrides(Map<Object, Object> overrides) {
        if (this.overrides == null) {
            this.overrides = overrides;
        } else {
            this.overrides.putAll(overrides);
        }
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#getExprOverrides()
     */
    public Map<Object, Object> getExprOverrides() {
        return this.overrides;
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#getRoot()
     */
    public CompoundRoot getRoot() {
        return root;
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#setParameter(String, Object)
     */
    public void setParameter(String expr, Object value) {
        setValue(expr, value, devMode);
    }

    /**

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#setValue(java.lang.String, java.lang.Object)
     */
    public void setValue(String expr, Object value) {
        setValue(expr, value, devMode);
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#setValue(java.lang.String, java.lang.Object, boolean)
     */
    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        Map<String, Object> context = getContext();
        try {
            trySetValue(expr, value, throwExceptionOnFailure, context);
        } catch (OgnlException e) {
            handleOgnlException(expr, value, throwExceptionOnFailure, e);
        } catch (RuntimeException re) { //XW-281
            handleRuntimeException(expr, value, throwExceptionOnFailure, re);
        } finally {
            cleanUpContext(context);
        }
    }

    private void trySetValue(String expr, Object value, boolean throwExceptionOnFailure, Map<String, Object> context) throws OgnlException {
        context.put(XWorkConverter.CONVERSION_PROPERTY_FULLNAME, expr);
        context.put(REPORT_ERRORS_ON_NO_PROP, (throwExceptionOnFailure) ? Boolean.TRUE : Boolean.FALSE);
        ognlUtil.setValue(expr, context, root, value);
    }

    private void cleanUpContext(Map<String, Object> context) {
        ReflectionContextState.clear(context);
        context.remove(XWorkConverter.CONVERSION_PROPERTY_FULLNAME);
        context.remove(REPORT_ERRORS_ON_NO_PROP);
    }

    private void handleRuntimeException(String expr, Object value, boolean throwExceptionOnFailure, RuntimeException re) {
        if (throwExceptionOnFailure) {
            String message = ErrorMessageBuilder.create()
                    .errorSettingExpressionWithValue(expr, value)
                    .build();
            throw new XWorkException(message, re);
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Error setting value [#0] with expression [#1]", re, value.toString(), expr);
            }
        }
    }

    private void handleOgnlException(String expr, Object value, boolean throwExceptionOnFailure, OgnlException e) {
    	boolean shouldLog = shouldLogMissingPropertyWarning(e);
    	String msg = null;
    	if (throwExceptionOnFailure || shouldLog) {
    		msg = ErrorMessageBuilder.create()
                    .errorSettingExpressionWithValue(expr, value)
                    .build();
    	}
    	if (shouldLog) {
            LOG.warn(msg, e);
    	}
    	
        if (throwExceptionOnFailure) {
            throw new XWorkException(msg, e);
        }
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#findString(java.lang.String)
     */
    public String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    public String findString(String expr, boolean throwExceptionOnFailure) {
        return (String) findValue(expr, String.class, throwExceptionOnFailure);
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#findValue(java.lang.String)
     */
    public Object findValue(String expr, boolean throwExceptionOnFailure) {
        try {
            setupExceptionOnFailure(throwExceptionOnFailure);
            return tryFindValueWhenExpressionIsNotNull(expr);
        } catch (OgnlException e) {
            return handleOgnlException(expr, throwExceptionOnFailure, e);
        } catch (Exception e) {
            return handleOtherException(expr, throwExceptionOnFailure, e);
        } finally {
            ReflectionContextState.clear(context);
        }
    }

    private void setupExceptionOnFailure(boolean throwExceptionOnFailure) {
        if (throwExceptionOnFailure) {
            context.put(THROW_EXCEPTION_ON_FAILURE, true);
        }
    }

    private Object tryFindValueWhenExpressionIsNotNull(String expr) throws OgnlException {
        if (expr == null) {
            return null;
        }
        return tryFindValue(expr);
    }

    private Object handleOtherException(String expr, boolean throwExceptionOnFailure, Exception e) {
        logLookupFailure(expr, e);

        if (throwExceptionOnFailure)
            throw new XWorkException(e);

        return findInContext(expr);
    }

    private Object tryFindValue(String expr) throws OgnlException {
        Object value;
        expr = lookupForOverrides(expr);
        if (defaultType != null) {
            value = findValue(expr, defaultType);
        } else {
            value = getValueUsingOgnl(expr);
            if (value == null) {
                value = findInContext(expr);
            }
        }
        return value;
    }

    private String lookupForOverrides(String expr) {
        if ((overrides != null) && overrides.containsKey(expr)) {
            expr = (String) overrides.get(expr);
        }
        return expr;
    }

    private Object getValueUsingOgnl(String expr) throws OgnlException {
        try {
            return ognlUtil.getValue(expr, context, root);
        } finally {
            context.remove(THROW_EXCEPTION_ON_FAILURE);
        }
    }

    public Object findValue(String expr) {
        return findValue(expr, false);
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#findValue(java.lang.String, java.lang.Class)
     */
    public Object findValue(String expr, Class asType, boolean throwExceptionOnFailure) {
        try {
            setupExceptionOnFailure(throwExceptionOnFailure);
            return tryFindValueWhenExpressionIsNotNull(expr, asType);
        } catch (OgnlException e) {
            final Object value = handleOgnlException(expr, throwExceptionOnFailure, e);
            return converter.convertValue(getContext(), value, asType);
        } catch (Exception e) {
            final Object value = handleOtherException(expr, throwExceptionOnFailure, e);
            return converter.convertValue(getContext(), value, asType);
        } finally {
            ReflectionContextState.clear(context);
        }
    }

    private Object tryFindValueWhenExpressionIsNotNull(String expr, Class asType) throws OgnlException {
        if (expr == null) {
            return null;
        }
        return tryFindValue(expr, asType);
    }

    private Object handleOgnlException(String expr, boolean throwExceptionOnFailure, OgnlException e) {
        Object ret = findInContext(expr);
        if (ret == null) {
            if (shouldLogMissingPropertyWarning(e)) {
                LOG.warn("Could not find property [#0]!", e, expr);
            }
            if (throwExceptionOnFailure) {
                throw new XWorkException(e);
            }
        }
        return ret;
    }

    private boolean shouldLogMissingPropertyWarning(OgnlException e) {
        return (e instanceof NoSuchPropertyException || e instanceof MethodFailedException)
        		&& devMode && logMissingProperties;
    }

    private Object tryFindValue(String expr, Class asType) throws OgnlException {
        Object value = null;
        try {
            expr = lookupForOverrides(expr);
            value = getValue(expr, asType);
            if (value == null) {
                value = findInContext(expr);
                return converter.convertValue(getContext(), value, asType);
            }
        } finally {
            context.remove(THROW_EXCEPTION_ON_FAILURE);
        }
        return value;
    }

    private Object getValue(String expr, Class asType) throws OgnlException {
        return ognlUtil.getValue(expr, context, root, asType);
    }

    private Object findInContext(String name) {
        return getContext().get(name);
    }

    public Object findValue(String expr, Class asType) {
        return findValue(expr, asType, false);
    }

    /**
     * Log a failed lookup, being more verbose when devMode=true.
     *
     * @param expr The failed expression
     * @param e    The thrown exception.
     */
    private void logLookupFailure(String expr, Exception e) {
        String msg = LoggerUtils.format("Caught an exception while evaluating expression '#0' against value stack", expr);
        if (devMode && LOG.isWarnEnabled()) {
            LOG.warn(msg, e);
            LOG.warn("NOTE: Previous warning message was issued due to devMode set to true.");
        } else if (LOG.isDebugEnabled()) {
            LOG.debug(msg, e);
        }
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#peek()
     */
    public Object peek() {
        return root.peek();
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#pop()
     */
    public Object pop() {
        return root.pop();
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#push(java.lang.Object)
     */
    public void push(Object o) {
        root.push(o);
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#set(java.lang.String, java.lang.Object)
     */
    public void set(String key, Object o) {
        //set basically is backed by a Map pushed on the stack with a key being put on the map and the Object being the value
        Map setMap = retrieveSetMap();
        setMap.put(key, o);
    }

    private Map retrieveSetMap() {
        Map setMap;
        Object topObj = peek();
        if (shouldUseOldMap(topObj)) {
            setMap = (Map) topObj;
        } else {
            setMap = new HashMap();
            setMap.put(MAP_IDENTIFIER_KEY, "");
            push(setMap);
        }
        return setMap;
    }

    /**
     * check if this is a Map put on the stack  for setting if so just use the old map (reduces waste)
     */
    private boolean shouldUseOldMap(Object topObj) {
        return topObj instanceof Map && ((Map) topObj).get(MAP_IDENTIFIER_KEY) != null;
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#size()
     */
    public int size() {
        return root.size();
    }

    private Object readResolve() {
        // TODO: this should be done better
        ActionContext ac = ActionContext.getContext();
        Container cont = ac.getContainer();
        XWorkConverter xworkConverter = cont.getInstance(XWorkConverter.class);
        CompoundRootAccessor accessor = (CompoundRootAccessor) cont.getInstance(PropertyAccessor.class, CompoundRoot.class.getName());
        TextProvider prov = cont.getInstance(TextProvider.class, "system");
        boolean allow = "true".equals(cont.getInstance(String.class, XWorkConstants.ALLOW_STATIC_METHOD_ACCESS));
        OgnlValueStack aStack = new OgnlValueStack(xworkConverter, accessor, prov, allow);
        aStack.setOgnlUtil(cont.getInstance(OgnlUtil.class));
        aStack.setRoot(xworkConverter, accessor, this.root, allow);

        return aStack;
    }


    public void clearContextValues() {
        //this is an OGNL ValueStack so the context will be an OgnlContext
        //it would be better to make context of type OgnlContext
        ((OgnlContext) context).getValues().clear();
    }

    public void setAcceptProperties(Set<Pattern> acceptedProperties) {
        securityMemberAccess.setAcceptProperties(acceptedProperties);
    }

    public void setExcludeProperties(Set<Pattern> excludeProperties) {
        securityMemberAccess.setExcludeProperties(excludeProperties);
    }

    @Inject
    public void setXWorkConverter(final XWorkConverter converter) {
        this.converter = converter;
    }
}
