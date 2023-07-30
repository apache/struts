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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.MemberAccessValueStack;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * This interceptor sets all parameters on the value stack.
 */
public class ParametersInterceptor extends MethodFilterInterceptor {

    private static final Logger LOG = LogManager.getLogger(ParametersInterceptor.class);

    protected static final int PARAM_NAME_MAX_LENGTH = 100;

    private static final Pattern DMI_IGNORED_PATTERN = Pattern.compile("^(action|method):.*", Pattern.CASE_INSENSITIVE);

    private int paramNameMaxLength = PARAM_NAME_MAX_LENGTH;
    private boolean devMode = false;
    private boolean dmiEnabled = false;

    protected boolean ordered = false;

    private ValueStackFactory valueStackFactory;
    private ExcludedPatternsChecker excludedPatterns;
    private AcceptedPatternsChecker acceptedPatterns;
    private Set<Pattern> excludedValuePatterns = null;
    private Set<Pattern> acceptedValuePatterns = null;

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean(mode);
    }

    @Inject
    public void setExcludedPatterns(ExcludedPatternsChecker excludedPatterns) {
        this.excludedPatterns = excludedPatterns;
    }

    @Inject
    public void setAcceptedPatterns(AcceptedPatternsChecker acceptedPatterns) {
        this.acceptedPatterns = acceptedPatterns;
    }

    @Inject(value = StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION, required = false)
    protected void setDynamicMethodInvocation(String dmiEnabled) {
        this.dmiEnabled = Boolean.parseBoolean(dmiEnabled);
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
    static final Comparator<String> rbCollator = (s1, s2) -> {
        int l1 = countOGNLCharacters(s1);
        int l2 = countOGNLCharacters(s2);
        return l1 < l2 ? -1 : (l2 < l1 ? 1 : s1.compareTo(s2));
    };

    @Override
    public String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        if (!(action instanceof NoParameters)) {
            ActionContext ac = invocation.getInvocationContext();
            HttpParameters parameters = retrieveParameters(ac);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting params {}", getParameterLogMap(parameters));
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
    protected HttpParameters retrieveParameters(ActionContext ac) {
        return ac.getParameters();
    }


    /**
     * Adds the parameters into context's ParameterMap
     *
     * @param ac        The action context
     * @param newParams The parameter map to apply
     *                  <p>
     *                  In this class this is a no-op, since the parameters were fetched from the same location.
     *                  In subclasses both retrieveParameters() and addParametersToContext() should be overridden.
     *                  </p>
     */
    protected void addParametersToContext(ActionContext ac, Map<String, ?> newParams) {
    }

    protected void setParameters(final Object action, ValueStack stack, HttpParameters parameters) {
        HttpParameters params;
        Map<String, Parameter> acceptableParameters;
        if (ordered) {
            params = HttpParameters.create().withComparator(getOrderedComparator()).withParent(parameters).build();
            acceptableParameters = new TreeMap<>(getOrderedComparator());
        } else {
            params = HttpParameters.create().withParent(parameters).build();
            acceptableParameters = new TreeMap<>();
        }

        for (Map.Entry<String, Parameter> entry : params.entrySet()) {
            String parameterName = entry.getKey();
            boolean isAcceptableParameter = isAcceptableParameter(parameterName, action);
            isAcceptableParameter &= isAcceptableParameterValue(entry.getValue(), action);

            if (isAcceptableParameter) {
                acceptableParameters.put(parameterName, entry.getValue());
            }
        }

        ValueStack newStack = valueStackFactory.createValueStack(stack);
        boolean clearableStack = newStack instanceof ClearableValueStack;
        if (clearableStack) {
            //if the stack's context can be cleared, do that to prevent OGNL
            //from having access to objects in the stack, see XW-641
            ((ClearableValueStack) newStack).clearContextValues();
            Map<String, Object> context = newStack.getContext();
            ReflectionContextState.setCreatingNullObjects(context, true);
            ReflectionContextState.setDenyMethodExecution(context, true);
            ReflectionContextState.setReportingConversionErrors(context, true);

            //keep locale from original context
            newStack.getActionContext().withLocale(stack.getActionContext().getLocale()).withValueStack(stack);
        }

        boolean memberAccessStack = newStack instanceof MemberAccessValueStack;
        if (memberAccessStack) {
            //block or allow access to properties
            //see WW-2761 for more details
            MemberAccessValueStack accessValueStack = (MemberAccessValueStack) newStack;
            accessValueStack.useAcceptProperties(acceptedPatterns.getAcceptedPatterns());
            accessValueStack.useExcludeProperties(excludedPatterns.getExcludedPatterns());
        }

        for (Map.Entry<String, Parameter> entry : acceptableParameters.entrySet()) {
            String name = entry.getKey();
            Parameter value = entry.getValue();
            try {
                newStack.setParameter(name, value.getObject());
            } catch (RuntimeException e) {
                if (devMode) {
                    notifyDeveloperParameterException(action, name, e.getMessage());
                }
            }
        }

        if (clearableStack) {
            stack.getActionContext().withConversionErrors(newStack.getActionContext().getConversionErrors());
        }

        addParametersToContext(ActionContext.getContext(), acceptableParameters);
    }

    protected void notifyDeveloperParameterException(Object action, String property, String message) {
        String developerNotification = "Unexpected Exception caught setting '" + property + "' on '" + action.getClass() + ": " + message;
        if (action instanceof TextProvider) {
            TextProvider tp = (TextProvider) action;
            developerNotification = tp.getText("devmode.notification",
                "Developer Notification:\n{0}",
                new String[]{developerNotification}
            );
        }

        LOG.error(developerNotification);

        if (action instanceof ValidationAware) {
            // see https://issues.apache.org/jira/browse/WW-4066
            Collection<String> messages = ((ValidationAware) action).getActionMessages();
            messages.add(message);
            ((ValidationAware) action).setActionMessages(messages);
        }
    }

    /**
     * Checks if name of parameter can be accepted or thrown away
     *
     * @param name   parameter name
     * @param action current action
     * @return true if parameter is accepted
     */
    protected boolean isAcceptableParameter(String name, Object action) {
        ParameterNameAware parameterNameAware = (action instanceof ParameterNameAware) ? (ParameterNameAware) action : null;
        return acceptableName(name) && (parameterNameAware == null || parameterNameAware.acceptableParameterName(name));
    }

    /**
     * Checks if parameter value can be accepted or thrown away
     *
     * @param param  the parameter
     * @param action current action
     * @return true if parameter is accepted
     */
    protected boolean isAcceptableParameterValue(Parameter param, Object action) {
        ParameterValueAware parameterValueAware = (action instanceof ParameterValueAware) ? (ParameterValueAware) action : null;
        boolean acceptableParamValue = (parameterValueAware == null || parameterValueAware.acceptableParameterValue(param.getValue()));
        if (hasParamValuesToExclude() || hasParamValuesToAccept()) {
            // Additional validations to process
            acceptableParamValue &= acceptableValue(param.getName(), param.getValue());
        }
        return acceptableParamValue;
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

    protected String getParameterLogMap(HttpParameters parameters) {
        if (parameters == null) {
            return "NONE";
        }

        StringBuilder logEntry = new StringBuilder();
        for (Map.Entry<String, Parameter> entry : parameters.entrySet()) {
            logEntry.append(entry.getKey());
            logEntry.append(" => ");
            logEntry.append(entry.getValue().getValue());
            logEntry.append(" ");
        }

        return logEntry.toString();
    }

    /**
     * Validates the name passed is:
     * * Within the max length of a parameter name
     * * Is not excluded
     * * Is accepted
     *
     * @param name - Name to check
     * @return true if accepted
     */
    protected boolean acceptableName(String name) {
        if (isIgnoredDMI(name)) {
            LOG.trace("DMI is enabled, ignoring DMI method: {}", name);
            return false;
        }
        boolean accepted = isWithinLengthLimit(name) && !isExcluded(name) && isAccepted(name);
        if (devMode && accepted) { // notify only when in devMode
            LOG.debug("Parameter [{}] was accepted and will be appended to action!", name);
        }
        return accepted;
    }

    private boolean isIgnoredDMI(String name) {
        if (dmiEnabled) {
            return DMI_IGNORED_PATTERN.matcher(name).matches();
        } else {
            return false;
        }
    }

    /**
     * Validates:
     * * Value is null/blank
     * * Value is not excluded
     * * Value is accepted
     *
     * @param name  - Param name (for logging)
     * @param value - value to check
     * @return true if accepted
     */
    protected boolean acceptableValue(String name, String value) {
        boolean accepted = (value == null || value.isEmpty() || (!isParamValueExcluded(value) && isParamValueAccepted(value)));
        if (!accepted) {
            String message = "Value [{}] of parameter [{}] was not accepted and will be dropped!";
            if (devMode) {
                LOG.warn(message, value, name);
            } else {
                LOG.debug(message, value, name);
            }
        }
        return accepted;
    }

    protected boolean isWithinLengthLimit(String name) {
        boolean matchLength = name.length() <= paramNameMaxLength;
        if (!matchLength) {
            if (devMode) { // warn only when in devMode
                LOG.warn("Parameter [{}] is too long, allowed length is [{}]. Use Interceptor Parameter Overriding " +
                        "to override the limit, see more at\n" +
                        "https://struts.apache.org/core-developers/interceptors.html#interceptor-parameter-overriding",
                    name, paramNameMaxLength);
            } else {
                LOG.warn("Parameter [{}] is too long, allowed length is [{}]", name, paramNameMaxLength);
            }
        }
        return matchLength;
    }

    protected boolean isAccepted(String paramName) {
        AcceptedPatternsChecker.IsAccepted result = acceptedPatterns.isAccepted(paramName);
        if (result.isAccepted()) {
            return true;
        } else if (devMode) { // warn only when in devMode
            LOG.warn("Parameter [{}] didn't match accepted pattern [{}]! See Accepted / Excluded patterns at\n" +
                    "https://struts.apache.org/security/#accepted--excluded-patterns",
                paramName, result.getAcceptedPattern());
        } else {
            LOG.debug("Parameter [{}] didn't match accepted pattern [{}]!", paramName, result.getAcceptedPattern());
        }
        return false;
    }

    protected boolean isExcluded(String paramName) {
        ExcludedPatternsChecker.IsExcluded result = excludedPatterns.isExcluded(paramName);
        if (result.isExcluded()) {
            if (devMode) { // warn only when in devMode
                LOG.warn("Parameter [{}] matches excluded pattern [{}]! See Accepted / Excluded patterns at\n" +
                        "https://struts.apache.org/security/#accepted--excluded-patterns",
                    paramName, result.getExcludedPattern());
            } else {
                LOG.debug("Parameter [{}] matches excluded pattern [{}]!", paramName, result.getExcludedPattern());
            }
            return true;
        }
        return false;
    }

    protected boolean isParamValueExcluded(String value) {
        if (!hasParamValuesToExclude()) {
            LOG.debug("'excludedValuePatterns' not defined so anything is allowed");
            return false;
        }
        for (Pattern excludedValuePattern : excludedValuePatterns) {
            if (excludedValuePattern.matcher(value).matches()) {
                if (devMode) {
                    LOG.warn("Parameter value [{}] matches excluded pattern [{}]! See Accepting/Excluding parameter values at\n" +
                            "https://struts.apache.org/core-developers/parameters-interceptor#excluding-parameter-values",
                        value, excludedValuePatterns);
                } else {
                    LOG.debug("Parameter value [{}] matches excluded pattern [{}]", value, excludedValuePattern);
                }
                return true;
            }
        }
        return false;
    }

    protected boolean isParamValueAccepted(String value) {
        if (!hasParamValuesToAccept()) {
            LOG.debug("'acceptedValuePatterns' not defined so anything is allowed");
            return true;
        }
        for (Pattern acceptedValuePattern : acceptedValuePatterns) {
            if (acceptedValuePattern.matcher(value).matches()) {
                return true;
            }
        }
        if (devMode) {
            LOG.warn("Parameter value [{}] didn't match accepted pattern [{}]! See Accepting/Excluding parameter values at\n" +
                    "https://struts.apache.org/core-developers/parameters-interceptor#excluding-parameter-values",
                value, acceptedValuePatterns);
        } else {
            LOG.debug("Parameter value [{}] was not accepted!", value);
        }
        return false;
    }

    private boolean hasParamValuesToExclude() {
        return excludedValuePatterns != null && excludedValuePatterns.size() > 0;
    }

    private boolean hasParamValuesToAccept() {
        return acceptedValuePatterns != null && acceptedValuePatterns.size() > 0;
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
     * <p>
     * Don't change the default unless you know what you are doing in terms
     * of security implications.
     * </p>
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

    /**
     * Sets a comma-delimited list of regular expressions to match
     * values of parameters that should be accepted and included in the parameter map.
     *
     * @param commaDelimitedPatterns A comma-delimited set of regular expressions
     */
    public void setAcceptedValuePatterns(String commaDelimitedPatterns) {
        Set<String> patterns = TextParseUtil.commaDelimitedStringToSet(commaDelimitedPatterns);
        if (acceptedValuePatterns == null) {
            // Limit unwanted log entries (for 1st call, acceptedValuePatterns null)
            LOG.debug("Sets accepted value patterns to [{}], note this may impact the safety of your application!", patterns);
        } else {
            LOG.warn("Replacing accepted patterns [{}] with [{}], be aware that this may impact safety of your application!",
                acceptedValuePatterns, patterns);
        }
        acceptedValuePatterns = new HashSet<>(patterns.size());
        try {
            for (String pattern : patterns) {
                acceptedValuePatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
            }
        } finally {
            acceptedValuePatterns = Collections.unmodifiableSet(acceptedValuePatterns);
        }
    }

    /**
     * Sets a comma-delimited list of regular expressions to match
     * values of parameters that should be removed from the parameter map.
     *
     * @param commaDelimitedPatterns A comma-delimited set of regular expressions
     */
    public void setExcludedValuePatterns(String commaDelimitedPatterns) {
        Set<String> patterns = TextParseUtil.commaDelimitedStringToSet(commaDelimitedPatterns);
        if (excludedValuePatterns == null) {
            // Limit unwanted log entries (for 1st call, excludedValuePatterns null)
            LOG.debug("Setting excluded value patterns to [{}]", patterns);
        } else {
            LOG.warn("Replacing excluded value patterns [{}] with [{}], be aware that this may impact safety of your application!",
                excludedValuePatterns, patterns);
        }
        excludedValuePatterns = new HashSet<>(patterns.size());
        try {
            for (String pattern : patterns) {
                excludedValuePatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
            }
        } finally {
            excludedValuePatterns = Collections.unmodifiableSet(excludedValuePatterns);
        }
    }
}
