/*
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
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.MemberAccessValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;

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

    private static final Logger LOG = LogManager.getLogger(OgnlValueStack.class);

    private static final long serialVersionUID = 370737852934925530L;

    private static final String MAP_IDENTIFIER_KEY = "com.opensymphony.xwork2.util.OgnlValueStack.MAP_IDENTIFIER_KEY";

    protected CompoundRoot root;
    protected transient Map<String, Object> context;
    protected Class defaultType;
    protected Map<Object, Object> overrides;
    protected transient OgnlUtil ognlUtil;
    protected transient SecurityMemberAccess securityMemberAccess;

    private transient XWorkConverter converter;
    private boolean devMode;
    private boolean logMissingProperties;

    protected OgnlValueStack(XWorkConverter xworkConverter, CompoundRootAccessor accessor, TextProvider prov, boolean allowStaticMethodAccess, boolean allowStaticFieldAccess) {
        setRoot(xworkConverter, accessor, new CompoundRoot(), allowStaticMethodAccess, allowStaticFieldAccess);
        push(prov);
    }

    protected OgnlValueStack(ValueStack vs, XWorkConverter xworkConverter, CompoundRootAccessor accessor, boolean allowStaticMethodAccess, boolean allowStaticFieldAccess) {
        setRoot(xworkConverter, accessor, new CompoundRoot(vs.getRoot()), allowStaticMethodAccess, allowStaticFieldAccess);
    }

    @Inject
    protected void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
        securityMemberAccess.setExcludedClasses(ognlUtil.getExcludedClasses());
        securityMemberAccess.setExcludedPackageNamePatterns(ognlUtil.getExcludedPackageNamePatterns());
        securityMemberAccess.setExcludedPackageNames(ognlUtil.getExcludedPackageNames());
        securityMemberAccess.setDisallowProxyMemberAccess(ognlUtil.isDisallowProxyMemberAccess());
    }

    protected void setRoot(XWorkConverter xworkConverter, CompoundRootAccessor accessor, CompoundRoot compoundRoot,
                           boolean allowStaticMethodAccess, boolean allowStaticFieldAccess) {
        this.root = compoundRoot;
        this.securityMemberAccess = new SecurityMemberAccess(allowStaticMethodAccess, allowStaticFieldAccess);
        this.context = Ognl.createDefaultContext(this.root, securityMemberAccess, accessor, new OgnlTypeConverterWrapper(xworkConverter));
        context.put(VALUE_STACK, this);
        ((OgnlContext) context).setTraceEvaluations(false);
        ((OgnlContext) context).setKeepLastEvaluation(false);
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    protected void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean(mode);
    }

    @Inject(value = StrutsConstants.STRUTS_OGNL_LOG_MISSING_PROPERTIES, required = false)
    protected void setLogMissingProperties(String logMissingProperties) {
        this.logMissingProperties = BooleanUtils.toBoolean(logMissingProperties);
    }

    /**
     * @see com.opensymphony.xwork2.util.ValueStack#getContext()
     */
    public Map<String, Object> getContext() {
        return context;
    }

    @Override
    public ActionContext getActionContext() {
        return ActionContext.of(context);
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
        context.put(REPORT_ERRORS_ON_NO_PROP, throwExceptionOnFailure || logMissingProperties ? Boolean.TRUE : Boolean.FALSE);
        ognlUtil.setValue(expr, context, root, value);
    }

    private void cleanUpContext(Map<String, Object> context) {
        ReflectionContextState.clear(context);
        context.remove(XWorkConverter.CONVERSION_PROPERTY_FULLNAME);
        context.remove(REPORT_ERRORS_ON_NO_PROP);
    }

    protected void handleRuntimeException(String expr, Object value, boolean throwExceptionOnFailure, RuntimeException re) {
        if (throwExceptionOnFailure) {
            String message = ErrorMessageBuilder.create()
                    .errorSettingExpressionWithValue(expr, value)
                    .build();
            throw new StrutsException(message, re);
        } else {
            LOG.warn("Error setting value [{}] with expression [{}]", value, expr, re);
        }
    }

    protected void handleOgnlException(String expr, Object value, boolean throwExceptionOnFailure, OgnlException e) {
        if (e != null && e.getReason() instanceof SecurityException) {
            LOG.error("Could not evaluate this expression due to security constraints: [{}]", expr, e);
        }
    	boolean shouldLog = shouldLogMissingPropertyWarning(e);
    	String msg = null;
    	if (throwExceptionOnFailure || shouldLog) {
            msg = ErrorMessageBuilder.create().errorSettingExpressionWithValue(expr, value).build();
        }
        if (shouldLog) {
            LOG.warn(msg, e);
    	}
    	
        if (throwExceptionOnFailure) {
            throw new StrutsException(msg, e);
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

    protected void setupExceptionOnFailure(boolean throwExceptionOnFailure) {
        if (throwExceptionOnFailure || logMissingProperties) {
            context.put(THROW_EXCEPTION_ON_FAILURE, true);
        }
    }

    private Object tryFindValueWhenExpressionIsNotNull(String expr) throws OgnlException {
        if (expr == null) {
            return null;
        }
        return tryFindValue(expr);
    }

    protected Object handleOtherException(String expr, boolean throwExceptionOnFailure, Exception e) {
        logLookupFailure(expr, e);

        if (throwExceptionOnFailure)
            throw new StrutsException(e);

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

    protected Object handleOgnlException(String expr, boolean throwExceptionOnFailure, OgnlException e) {
        Object ret = null;
        if (e != null && e.getReason() instanceof SecurityException) {
            LOG.error("Could not evaluate this expression due to security constraints: [{}]", expr, e);
        } else {
            ret = findInContext(expr);
        }
        if (ret == null) {
            if (shouldLogMissingPropertyWarning(e)) {
                LOG.warn("Could not find property [{}]!", expr, e);
            }
            if (throwExceptionOnFailure) {
                throw new StrutsException(e);
            }
        }
        return ret;
    }

    protected boolean shouldLogMissingPropertyWarning(OgnlException e) {
        return (e instanceof NoSuchPropertyException ||
                (e instanceof MethodFailedException && e.getReason() instanceof NoSuchMethodException))
        		&& logMissingProperties;
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

    protected Object findInContext(String name) {
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
        if (devMode) {
            LOG.warn("Caught an exception while evaluating expression '{}' against value stack", expr, e);
            LOG.warn("NOTE: Previous warning message was issued due to devMode set to true.");
        } else {
            LOG.debug("Caught an exception while evaluating expression '{}' against value stack", expr, e);
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
        final boolean allowStaticMethod = BooleanUtils.toBoolean(cont.getInstance(String.class, StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS));
        final boolean allowStaticField = BooleanUtils.toBoolean(cont.getInstance(String.class, StrutsConstants.STRUTS_ALLOW_STATIC_FIELD_ACCESS));
        OgnlValueStack aStack = new OgnlValueStack(xworkConverter, accessor, prov, allowStaticMethod, allowStaticField);
        aStack.setOgnlUtil(cont.getInstance(OgnlUtil.class));
        aStack.setRoot(xworkConverter, accessor, this.root, allowStaticMethod, allowStaticField);

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
    protected void setXWorkConverter(final XWorkConverter converter) {
        this.converter = converter;
    }
}
