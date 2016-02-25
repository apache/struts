/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.conversion.impl.InstantiatingNullHandler;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.MemberAccessValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * <!-- START SNIPPET: description -->
 * This interceptor sets all parameters on the value stack.
 *
 * This interceptor gets all parameters from {@link ActionContext#getParameters()} and sets them on the value stack by
 * calling {@link ValueStack#setValue(String, Object)}, typically resulting in the values submitted in a form
 * request being applied to an action in the value stack. Note that the parameter map must contain a String key and
 * often containers a String[] for the value.
 *
 * The interceptor takes one parameter named 'ordered'. When set to true action properties are guaranteed to be
 * set top-down which means that top action's properties are set first. Then it's subcomponents properties are set.
 * The reason for this order is to enable a 'factory' pattern. For example, let's assume that one has an action
 * that contains a property named 'modelClass' that allows to choose what is the underlying implementation of model.
 * By assuring that modelClass property is set before any model properties are set, it's possible to choose model
 * implementation during action.setModelClass() call. Similiarily it's possible to use action.setPrimaryKey()
 * property set call to actually load the model class from persistent storage. Without any assumption on parameter
 * order you have to use patterns like 'Preparable'.
 *
 * Because parameter names are effectively OGNL statements, it is important that security be taken in to account.
 * This interceptor will not apply any values in the parameters map if the expression contains an assignment (=),
 * multiple expressions (,), or references any objects in the context (#). This is all done in the {@link
 * #acceptableName(String)} method. In addition to this method, if the action being invoked implements the {@link
 * ParameterNameAware} interface, the action will be consulted to determine if the parameter should be set.
 *
 * In addition to these restrictions, a flag ({@link ReflectionContextState#DENY_METHOD_EXECUTION}) is set such that
 * no methods are allowed to be invoked. That means that any expression such as <i>person.doSomething()</i> or
 * <i>person.getName()</i> will be explicitely forbidden. This is needed to make sure that your application is not
 * exposed to attacks by malicious users.
 *
 * While this interceptor is being invoked, a flag ({@link ReflectionContextState#CREATE_NULL_OBJECTS}) is turned
 * on to ensure that any null reference is automatically created - if possible. See the type conversion documentation
 * and the {@link InstantiatingNullHandler} javadocs for more information.
 *
 * Finally, a third flag ({@link XWorkConverter#REPORT_CONVERSION_ERRORS}) is set that indicates any errors when
 * converting the the values to their final data type (String[] -&gt; int) an unrecoverable error occured. With this
 * flag set, the type conversion errors will be reported in the action context. See the type conversion documentation
 * and the {@link XWorkConverter} javadocs for more information.
 *
 * If you are looking for detailed logging information about your parameters, turn on DEBUG level logging for this
 * interceptor. A detailed log of all the parameter keys and values will be reported.
 *
 * <b>Note:</b> Since XWork 2.0.2, this interceptor extends {@link MethodFilterInterceptor}, therefore being
 * able to deal with excludeMethods / includeMethods parameters. See [Workflow Interceptor]
 * (class {@link DefaultWorkflowInterceptor}) for documentation and examples on how to use this feature.
 * <!-- END SNIPPET: description -->
 *
 * <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 * <li>ordered - set to true if you want the top-down property setter behaviour</li>
 * <li>acceptParamNames - a comma delimited list of regular expressions to describe a whitelist of accepted parameter names.
 * Don't change the default unless you know what you are doing in terms of security implications</li>
 * <li>excludeParams - a comma delimited list of regular expressions to describe a blacklist of not allowed parameter names</li>
 * <li>paramNameMaxLength - the maximum length of parameter names; parameters with longer names will be ignored; the default is 100 characters</li>
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 *  <u>Extending the interceptor:</u>
 *
 * <!-- START SNIPPET: extending -->
 *
 *  The best way to add behavior to this interceptor is to utilize the {@link ParameterNameAware} interface in your
 * actions. However, if you wish to apply a global rule that isn't implemented in your action, then you could extend
 * this interceptor and override the {@link #acceptableName(String)} method.
 *
 * <!-- END SNIPPET: extending -->
 *
 *
 * <!-- START SNIPPET: extending-warning -->
 * Using {@link ParameterNameAware} could be dangerous as {@link ParameterNameAware#acceptableParameterName(String)} takes precedence
 * over ParametersInterceptor which means if ParametersInterceptor excluded given parameter name you can accept it with
 * {@link ParameterNameAware#acceptableParameterName(String)}.
 *
 * The best idea is to define very tight restrictions with ParametersInterceptor and relax them per action with
 * {@link ParameterNameAware#acceptableParameterName(String)}
 * <!-- END SNIPPET: extending-warning -->
 *
 *
 * <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 */
public class ParametersInterceptor extends MethodFilterInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ParametersInterceptor.class);

    protected static final int PARAM_NAME_MAX_LENGTH = 100;

    private int paramNameMaxLength = PARAM_NAME_MAX_LENGTH;
    private boolean devMode = false;

    protected boolean ordered = false;

    private ValueStackFactory valueStackFactory;
    private ExcludedPatternsChecker excludedPatterns;
    private AcceptedPatternsChecker acceptedPatterns;

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject(XWorkConstants.DEV_MODE)
    public void setDevMode(String mode) {
        devMode = "true".equalsIgnoreCase(mode);
    }

    @Inject
    public void setExcludedPatterns(ExcludedPatternsChecker excludedPatterns) {
        this.excludedPatterns = excludedPatterns;
    }

    @Inject
    public void setAcceptedPatterns(AcceptedPatternsChecker acceptedPatterns) {
        this.acceptedPatterns = acceptedPatterns;
    }

    /**
     * If the param name exceeds the configured maximum length it will not be
     * accepted.
     *
     * @param paramNameMaxLength Maximum length of param names
     */
    public void setParamNameMaxLength(int paramNameMaxLength) {
        this.paramNameMaxLength = paramNameMaxLength;
    }

    static private int countOGNLCharacters(String s) {
        int count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (c == '.' || c == '[') count++;
        }
        return count;
    }

    /**
     * Compares based on number of '.' and '[' characters (fewer is higher)
     */
    static final Comparator<String> rbCollator = new Comparator<String>() {
        public int compare(String s1, String s2) {
            int l1 = countOGNLCharacters(s1),
                l2 = countOGNLCharacters(s2);
            return l1 < l2 ? -1 : (l2 < l1 ? 1 : s1.compareTo(s2));
        }

    };

    @Override
    public String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        if (!(action instanceof NoParameters)) {
            ActionContext ac = invocation.getInvocationContext();
            final Map<String, Object> parameters = retrieveParameters(ac);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting params " + getParameterLogMap(parameters));
            }

            if (parameters != null) {
                Map<String, Object> contextMap = ac.getContextMap();
                try {
                    ReflectionContextState.setCreatingNullObjects(contextMap, true);
                    ReflectionContextState.setDenyMethodExecution(contextMap, true);
                    ReflectionContextState.setReportingConversionErrors(contextMap, true);

                    ValueStack stack = ac.getValueStack();
                    setParameters(action, stack, parameters);
                } finally {
                    ReflectionContextState.setCreatingNullObjects(contextMap, false);
                    ReflectionContextState.setDenyMethodExecution(contextMap, false);
                    ReflectionContextState.setReportingConversionErrors(contextMap, false);
                }
            }
        }
        return invocation.invoke();
    }

    /**
     * Gets the parameter map to apply from wherever appropriate
     *
     * @param ac The action context
     * @return The parameter map to apply
     */
    protected Map<String, Object> retrieveParameters(ActionContext ac) {
        return ac.getParameters();
    }


    /**
     * Adds the parameters into context's ParameterMap
     *
     * @param ac        The action context
     * @param newParams The parameter map to apply
     *                  <p/>
     *                  In this class this is a no-op, since the parameters were fetched from the same location.
     *                  In subclasses both retrieveParameters() and addParametersToContext() should be overridden.
     */
    protected void addParametersToContext(ActionContext ac, Map<String, Object> newParams) {
    }

    protected void setParameters(final Object action, ValueStack stack, final Map<String, Object> parameters) {
        Map<String, Object> params;
        Map<String, Object> acceptableParameters;
        if (ordered) {
            params = new TreeMap<String, Object>(getOrderedComparator());
            acceptableParameters = new TreeMap<String, Object>(getOrderedComparator());
            params.putAll(parameters);
        } else {
            params = new TreeMap<String, Object>(parameters);
            acceptableParameters = new TreeMap<String, Object>();
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (isAcceptableParameter(name, action)) {
                acceptableParameters.put(name, entry.getValue());
            }
        }

        ValueStack newStack = valueStackFactory.createValueStack(stack);
        boolean clearableStack = newStack instanceof ClearableValueStack;
        if (clearableStack) {
            //if the stack's context can be cleared, do that to prevent OGNL
            //from having access to objects in the stack, see XW-641
            ((ClearableValueStack)newStack).clearContextValues();
            Map<String, Object> context = newStack.getContext();
            ReflectionContextState.setCreatingNullObjects(context, true);
            ReflectionContextState.setDenyMethodExecution(context, true);
            ReflectionContextState.setReportingConversionErrors(context, true);

            //keep locale from original context
            context.put(ActionContext.LOCALE, stack.getContext().get(ActionContext.LOCALE));
        }

        boolean memberAccessStack = newStack instanceof MemberAccessValueStack;
        if (memberAccessStack) {
            //block or allow access to properties
            //see WW-2761 for more details
            MemberAccessValueStack accessValueStack = (MemberAccessValueStack) newStack;
            accessValueStack.setAcceptProperties(acceptedPatterns.getAcceptedPatterns());
            accessValueStack.setExcludeProperties(excludedPatterns.getExcludedPatterns());
        }

        for (Map.Entry<String, Object> entry : acceptableParameters.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            try {
                newStack.setParameter(name, value);
            } catch (RuntimeException e) {
                if (devMode) {
                    notifyDeveloperParameterException(action, name, e.getMessage());
                }
            }
        }

        if (clearableStack && (stack.getContext() != null) && (newStack.getContext() != null))
            stack.getContext().put(ActionContext.CONVERSION_ERRORS, newStack.getContext().get(ActionContext.CONVERSION_ERRORS));

        addParametersToContext(ActionContext.getContext(), acceptableParameters);
    }

    protected void notifyDeveloperParameterException(Object action, String property, String message) {
        String developerNotification = LocalizedTextUtil.findText(ParametersInterceptor.class, "devmode.notification",
                ActionContext.getContext().getLocale(), "Developer Notification:\n{0}",
                new Object[]{
                        "Unexpected Exception caught setting '" + property + "' on '" + action.getClass() + ": " + message
                }
        );
        LOG.error(developerNotification);
        // see https://issues.apache.org/jira/browse/WW-4066
        if (action instanceof ValidationAware) {
            Collection<String> messages = ((ValidationAware) action).getActionMessages();
            messages.add(message);
            ((ValidationAware) action).setActionMessages(messages);
        }
    }

    /**
     * Checks if name of parameter can be accepted or thrown away
     *
     * @param name parameter name
     * @param action current action
     * @return true if parameter is accepted
     */
    protected boolean isAcceptableParameter(String name, Object action) {
        ParameterNameAware parameterNameAware = (action instanceof ParameterNameAware) ? (ParameterNameAware) action : null;
        return acceptableName(name) && (parameterNameAware == null || parameterNameAware.acceptableParameterName(name));
    }

    /**
     * Gets an instance of the comparator to use for the ordered sorting.  Override this
     * method to customize the ordering of the parameters as they are set to the
     * action.
     *
     * @return A comparator to sort the parameters
     */
    protected Comparator<String> getOrderedComparator() {
        return rbCollator;
    }

    protected String getParameterLogMap(Map<String, Object> parameters) {
        if (parameters == null) {
            return "NONE";
        }

        StringBuilder logEntry = new StringBuilder();
        for (Map.Entry entry : parameters.entrySet()) {
            logEntry.append(String.valueOf(entry.getKey()));
            logEntry.append(" => ");
            if (entry.getValue() instanceof Object[]) {
                Object[] valueArray = (Object[]) entry.getValue();
                logEntry.append("[ ");
                if (valueArray.length > 0 ) {
                    for (int indexA = 0; indexA < (valueArray.length - 1); indexA++) {
                        Object valueAtIndex = valueArray[indexA];
                        logEntry.append(String.valueOf(valueAtIndex));
                        logEntry.append(", ");
                    }
                    logEntry.append(String.valueOf(valueArray[valueArray.length - 1]));
                }
                logEntry.append(" ] ");
            } else {
                logEntry.append(String.valueOf(entry.getValue()));
            }
        }

        return logEntry.toString();
    }

    protected boolean acceptableName(String name) {
        boolean accepted = isWithinLengthLimit(name) && !isExcluded(name) && isAccepted(name);
        if (devMode && accepted) { // notify only when in devMode
            LOG.debug("Parameter [#0] was accepted and will be appended to action!", name);
        }
        return accepted;
    }

	protected boolean isWithinLengthLimit( String name ) {
        boolean matchLength = name.length() <= paramNameMaxLength;
        if (!matchLength) {
            notifyDeveloper("Parameter [#0] is too long, allowed length is [#1]", name, String.valueOf(paramNameMaxLength));
        }
        return matchLength;
	}

    protected boolean isAccepted(String paramName) {
        AcceptedPatternsChecker.IsAccepted result = acceptedPatterns.isAccepted(paramName);
        if (result.isAccepted()) {
            return true;
        }
        notifyDeveloper("Parameter [#0] didn't match accepted pattern [#1]!", paramName, result.getAcceptedPattern());
        return false;
    }

    protected boolean isExcluded(String paramName) {
        ExcludedPatternsChecker.IsExcluded result = excludedPatterns.isExcluded(paramName);
        if (result.isExcluded()) {
            notifyDeveloper("Parameter [#0] matches excluded pattern [#1]!", paramName, result.getExcludedPattern());
            return true;
        }
        return false;
    }

    private void notifyDeveloper(String message, String... parameters) {
        if (devMode) {
            LOG.warn(message, parameters);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug(message, parameters);
            }
        }
    }

    /**
     * Whether to order the parameters or not
     *
     * @return True to order
     */
    public boolean isOrdered() {
        return ordered;
    }

    /**
     * Set whether to order the parameters by object depth or not
     *
     * @param ordered True to order them
     */
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    /**
     * Sets a comma-delimited list of regular expressions to match
     * parameters that are allowed in the parameter map (aka whitelist).
     * <p/>
     * Don't change the default unless you know what you are doing in terms
     * of security implications.
     *
     * @param commaDelim A comma-delimited list of regular expressions
     */
    public void setAcceptParamNames(String commaDelim) {
        acceptedPatterns.setAcceptedPatterns(commaDelim);
    }

    /**
     * Sets a comma-delimited list of regular expressions to match
     * parameters that should be removed from the parameter map.
     *
     * @param commaDelim A comma-delimited list of regular expressions
     */
    public void setExcludeParams(String commaDelim) {
        excludedPatterns.setExcludedPatterns(commaDelim);
    }

}
