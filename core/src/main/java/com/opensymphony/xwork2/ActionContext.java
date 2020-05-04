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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsException;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * The ActionContext is the context in which an {@link Action} is executed. Each context is basically a
 * container of objects an action needs for execution like the session, parameters, locale, etc.
 * </p>
 *
 * <p>
 * The ActionContext is thread local which means that values stored in the ActionContext are
 * unique per thread. See the {@link ThreadLocal} class for more information. The benefit of
 * this is you don't need to worry about a user specific action context, you just get it:
 * </p>
 *
 * <code>ActionContext context = ActionContext.getContext();</code>
 *
 * <p>
 * Finally, because of the thread local usage you don't need to worry about making your actions thread safe.
 * </p>
 *
 * @author Patrick Lightbody
 * @author Bill Lynch (docs)
 */
public class ActionContext implements Serializable {

    static ThreadLocal<ActionContext> actionContext = new ThreadLocal<>();

    /**
     * Constant for the name of the action being executed.
     *
     * @deprecated scope will be narrowed to "private", use helper methods instead
     */
    @Deprecated
    public static final String ACTION_NAME = "com.opensymphony.xwork2.ActionContext.name";

    /**
     * Constant for the {@link com.opensymphony.xwork2.util.ValueStack OGNL value stack}.
     *
     * @deprecated scope will be narrowed to "private", use helper methods instead
     */
    @Deprecated
    public static final String VALUE_STACK = ValueStack.VALUE_STACK;

    /**
     * Constant for the action's session.
     *
     * @deprecated scope will be narrowed to "private", use helper methods instead
     */
    @Deprecated
    public static final String SESSION = "com.opensymphony.xwork2.ActionContext.session";

    /**
     * Constant for the action's application context.
     *
     * @deprecated scope will be narrowed to "private", use helper methods instead
     */
    @Deprecated
    public static final String APPLICATION = "com.opensymphony.xwork2.ActionContext.application";

    /**
     * Constant for the action's parameters.
     *
     * @deprecated scope will be narrowed to "private", use helper methods instead
     */
    @Deprecated
    public static final String PARAMETERS = "com.opensymphony.xwork2.ActionContext.parameters";

    /**
     * Constant for the action's locale.
     *
     * @deprecated scope will be narrowed to "private", use helper methods instead
     */
    @Deprecated
    public static final String LOCALE = "com.opensymphony.xwork2.ActionContext.locale";

    /**
     * Constant for the action's {@link com.opensymphony.xwork2.ActionInvocation invocation} context.
     *
     * @deprecated scope will be narrowed to "private", use helper methods instead
     */
    @Deprecated
    public static final String ACTION_INVOCATION = "com.opensymphony.xwork2.ActionContext.actionInvocation";

    /**
     * Constant for the map of type conversion errors.
     *
     * @deprecated scope will be narrowed to "private", use helper methods instead
     */
    @Deprecated
    public static final String CONVERSION_ERRORS = "com.opensymphony.xwork2.ActionContext.conversionErrors";

    /**
     * Constant for the container
     *
     * @deprecated scope will be narrowed to "private", use helper methods instead
     */
    @Deprecated
    public static final String CONTAINER = "com.opensymphony.xwork2.ActionContext.container";

    private final Map<String, Object> context;

    /**
     * Creates a new ActionContext initialized with another context.
     *
     * @param context a context map.
     */
    protected ActionContext(Map<String, Object> context) {
        this.context = context;
    }

    /**
     * Creates a new ActionContext based on passed in Map
     * and assign this instance to the current thread
     *
     * @param context a map with context values
     * @return new ActionContext
     */
    public static ActionContext of(Map<String, Object> context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null!");
        }
        return new ActionContext(context);
    }

    /**
     * Binds the provided context with the current thread
     *
     * @param actionContext context to bind to the thread
     * @return context which was bound to the thread
     */
    public static ActionContext bind(ActionContext actionContext) {
        ActionContext.setContext(actionContext);
        return ActionContext.getContext();
    }

    public static boolean containsValueStack(Map<String, Object> context) {
        return context != null && context.containsKey(VALUE_STACK);
    }

    /**
     * Binds this context with the current thread
     *
     * @return this context which was bound to the thread
     */
    public ActionContext bind() {
        ActionContext.setContext(this);
        return ActionContext.getContext();
    }

    /**
     * Wipes out current ActionContext, use wisely!
     */
    public static void clear() {
        actionContext.remove();
    }

    /**
     * Sets the action context for the current thread.
     *
     * @param context the action context.
     */
    private static void setContext(ActionContext context) {
        actionContext.set(context);
    }

    /**
     * Returns the ActionContext specific to the current thread.
     *
     * @return the ActionContext for the current thread, is never <tt>null</tt>.
     */
    public static ActionContext getContext() {
        return actionContext.get();
    }

    /**
     * Sets the action invocation (the execution state).
     *
     * @param actionInvocation the action execution state.
     * @deprecated use {@link #withActionInvocation(ActionInvocation)} instead
     */
    @Deprecated
    public void setActionInvocation(ActionInvocation actionInvocation) {
        put(ACTION_INVOCATION, actionInvocation);
    }

    public ActionContext withActionInvocation(ActionInvocation actionInvocation) {
        put(ACTION_INVOCATION, actionInvocation);
        return this;
    }

    /**
     * Gets the action invocation (the execution state).
     *
     * @return the action invocation (the execution state).
     */
    public ActionInvocation getActionInvocation() {
        return (ActionInvocation) get(ACTION_INVOCATION);
    }

    /**
     * Sets the action's application context.
     *
     * @param application the action's application context.
     * @deprecated use {@link #withApplication(Map)} instead
     */
    @Deprecated
    public void setApplication(Map<String, Object> application) {
        put(APPLICATION, application);
    }

    public ActionContext withApplication(Map<String, Object> application) {
        put(APPLICATION, application);
        return this;
    }

    /**
     * Returns a Map of the ServletContext when in a servlet environment or a generic application level Map otherwise.
     *
     * @return a Map of ServletContext or generic application level Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getApplication() {
        return (Map<String, Object>) get(APPLICATION);
    }

    /**
     * Gets the context map.
     *
     * @return the context map.
     */
    public Map<String, Object> getContextMap() {
        return context;
    }

    /**
     * Sets conversion errors which occurred when executing the action.
     *
     * @param conversionErrors a Map of errors which occurred when executing the action.
     * @deprecated use {@link #withConversionErrors(Map)} instead
     */
    @Deprecated
    public void setConversionErrors(Map<String, ConversionData> conversionErrors) {
        put(CONVERSION_ERRORS, conversionErrors);
    }

    public ActionContext withConversionErrors(Map<String, ConversionData> conversionErrors) {
        put(CONVERSION_ERRORS, conversionErrors);
        return this;
    }

    /**
     * Gets the map of conversion errors which occurred when executing the action.
     *
     * @return the map of conversion errors which occurred when executing the action or an empty map if
     * there were no errors.
     */
    @SuppressWarnings("unchecked")
    public Map<String, ConversionData> getConversionErrors() {
        Map<String, ConversionData> errors = (Map<String, ConversionData>) get(CONVERSION_ERRORS);

        if (errors == null) {
            errors = withConversionErrors(new HashMap<>()).getConversionErrors();
        }

        return errors;
    }

    /**
     * Sets the Locale for the current action.
     *
     * @param locale the Locale for the current action.
     * @deprecated use {@link #withLocale(Locale)} instead
     */
    @Deprecated
    public void setLocale(Locale locale) {
        put(LOCALE, locale);
    }

    public ActionContext withLocale(Locale locale) {
        put(LOCALE, locale);
        return this;
    }

    /**
     * Gets the Locale of the current action. If no locale was ever specified the platform's
     * {@link java.util.Locale#getDefault() default locale} is used.
     *
     * @return the Locale of the current action.
     */
    public Locale getLocale() {
        Locale locale = (Locale) get(LOCALE);

        if (locale == null) {
            locale = Locale.getDefault();
            setLocale(locale);
        }

        return locale;
    }

    /**
     * Sets the name of the current Action in the ActionContext.
     *
     * @param name the name of the current action.
     * @deprecated use {@link #withActionName(String)} instead
     */
    @Deprecated
    public void setName(String name) {
        put(ACTION_NAME, name);
    }

    public ActionContext withActionName(String actionName) {
        put(ACTION_NAME, actionName);
        return this;
    }

    /**
     * Gets the name of the current Action.
     *
     * @return the name of the current action.
     */
    public String getName() {
        return (String) get(ACTION_NAME);
    }

    /**
     * Gets the name of the current Action.
     *
     * @return the name of the current action.
     */
    public String getActionName() {
        return (String) get(ACTION_NAME);
    }

    /**
     * Sets the action parameters.
     *
     * @param parameters the parameters for the current action.
     */
    public void setParameters(HttpParameters parameters) {
        put(PARAMETERS, parameters);
    }

    public ActionContext withParameters(HttpParameters parameters) {
        put(PARAMETERS, parameters);
        return this;
    }

    /**
     * Returns a Map of the HttpServletRequest parameters when in a servlet environment or a generic Map of
     * parameters otherwise.
     *
     * @return a Map of HttpServletRequest parameters or a multipart map when in a servlet environment, or a
     * generic Map of parameters otherwise.
     */
    public HttpParameters getParameters() {
        return (HttpParameters) get(PARAMETERS);
    }

    /**
     * Sets a map of action session values.
     *
     * @param session the session values.
     * @deprecated use {@link #withSession(Map)} instead
     */
    @Deprecated
    public void setSession(Map<String, Object> session) {
        put(SESSION, session);
    }

    public ActionContext withSession(Map<String, Object> session) {
        put(SESSION, session);
        return this;
    }

    /**
     * Gets the Map of HttpSession values when in a servlet environment or a generic session map otherwise.
     *
     * @return the Map of HttpSession values when in a servlet environment or a generic session map otherwise.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSession() {
        return (Map<String, Object>) get(SESSION);
    }

    /**
     * Sets the OGNL value stack.
     *
     * @param stack the OGNL value stack.
     * @deprecated Use {@link #withValueStack(ValueStack)} instead
     */
    @Deprecated
    public void setValueStack(ValueStack stack) {
        put(VALUE_STACK, stack);
    }

    public ActionContext withValueStack(ValueStack valueStack) {
        put(VALUE_STACK, valueStack);
        return this;
    }

    /**
     * Gets the OGNL value stack.
     *
     * @return the OGNL value stack.
     */
    public ValueStack getValueStack() {
        return (ValueStack) get(VALUE_STACK);
    }

    /**
     * Gets the container for this request
     *
     * @param cont The container
     * @deprecated use {@link #withContainer(Container)} instead
     */
    @Deprecated
    public void setContainer(Container cont) {
        put(CONTAINER, cont);
    }

    public ActionContext withContainer(Container container) {
        put(CONTAINER, container);
        return this;
    }

    /**
     * Sets the container for this request
     *
     * @return The container
     */
    public Container getContainer() {
        return (Container) get(CONTAINER);
    }

    public <T> T getInstance(Class<T> type) {
        Container cont = getContainer();
        if (cont != null) {
            return cont.getInstance(type);
        } else {
            throw new StrutsException("Cannot find an initialized container for this request.");
        }
    }

    /**
     * Returns a value that is stored in the current ActionContext by doing a lookup using the value's key.
     *
     * @param key the key used to find the value.
     * @return the value that was found using the key or <tt>null</tt> if the key was not found.
     */
    public Object get(String key) {
        return context.get(key);
    }

    /**
     * Stores a value in the current ActionContext. The value can be looked up using the key.
     *
     * @param key   the key of the value.
     * @param value the value to be stored.
     */
    public void put(String key, Object value) {
        context.put(key, value);
    }

    /**
     * Gets ServletContext associated with current action
     *
     * @return current ServletContext
     */
    public ServletContext getServletContext() {
        return (ServletContext) get(StrutsStatics.SERVLET_CONTEXT);
    }

    /**
     * Assigns ServletContext to action context
     *
     * @param servletContext associated with current request
     * @return ActionContext
     */
    public ActionContext withServletContext(ServletContext servletContext) {
        put(StrutsStatics.SERVLET_CONTEXT, servletContext);
        return this;
    }

    /**
     * Gets ServletRequest associated with current action
     *
     * @return current ServletRequest
     */
    public HttpServletRequest getServletRequest() {
        return (HttpServletRequest) get(StrutsStatics.HTTP_REQUEST);
    }

    /**
     * Assigns ServletRequest to action context
     *
     * @param request associated with current request
     * @return ActionContext
     */
    public ActionContext withServletRequest(HttpServletRequest request) {
        put(StrutsStatics.HTTP_REQUEST, request);
        return this;
    }

    /**
     * Gets ServletResponse associated with current action
     *
     * @return current ServletResponse
     */
    public HttpServletResponse getServletResponse() {
        return (HttpServletResponse) get(StrutsStatics.HTTP_RESPONSE);
    }

    /**
     * Assigns ServletResponse to action context
     *
     * @param response associated with current request
     * @return ActionContext
     */
    public ActionContext withServletResponse(HttpServletResponse response) {
        put(StrutsStatics.HTTP_RESPONSE, response);
        return this;
    }

    /**
     * Gets PageContext associated with current action
     *
     * @return current PageContext
     */
    public PageContext getPageContext() {
        return (PageContext) get(StrutsStatics.PAGE_CONTEXT);
    }

    /**
     * Assigns PageContext to action context
     *
     * @param pageContext associated with current request
     * @return ActionContext
     */
    public ActionContext withPageContext(PageContext pageContext) {
        put(StrutsStatics.PAGE_CONTEXT, pageContext);
        return this;
    }

    /**
     * Gets ActionMapping associated with current action
     *
     * @return current ActionMapping
     */
    public ActionMapping getActionMapping() {
        return (ActionMapping) get(StrutsStatics.ACTION_MAPPING);
    }

    /**
     * Assigns ActionMapping to action context
     *
     * @param actionMapping associated with current request
     * @return ActionContext
     */
    public ActionContext withActionMapping(ActionMapping actionMapping) {
        put(StrutsStatics.ACTION_MAPPING, actionMapping);
        return this;
    }

    /**
     * Assigns an extra context map to action context
     *
     * @param extraContext to add to the current action context
     * @return ActionContext
     */
    public ActionContext withExtraContext(Map<String, Object> extraContext) {
        if (extraContext != null) {
            context.putAll(extraContext);
        }
        return this;
    }

    /**
     * Adds arbitrary key to action context
     *
     * @param key   a string
     * @param value an object
     * @return ActionContext
     */
    public ActionContext with(String key, Object value) {
        put(key, value);
        return this;
    }
}
