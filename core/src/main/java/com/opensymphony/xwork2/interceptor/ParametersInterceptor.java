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
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * This interceptor sets all parameters on the value stack.
 */
public class ParametersInterceptor extends MethodFilterInterceptor {

    private static final Logger LOG = LogManager.getLogger(ParametersInterceptor.class);

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

            if (isAcceptableParameter(parameterName, action)) {
                acceptableParameters.put(parameterName, entry.getValue());
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

        if (clearableStack && (stack.getContext() != null) && (newStack.getContext() != null))
            stack.getContext().put(ActionContext.CONVERSION_ERRORS, newStack.getContext().get(ActionContext.CONVERSION_ERRORS));

        addParametersToContext(ActionContext.getContext(), acceptableParameters);
    }

    protected void notifyDeveloperParameterException(Object action, String property, String message) {
        String developerNotification = "Unexpected Exception caught setting '" + property + "' on '" + action.getClass() + ": " + message;
        if (action instanceof TextProvider) {
            TextProvider tp = (TextProvider) action;
            developerNotification = tp.getText("devmode.notification",
                    "Developer Notification:\n{0}",
                    new String[]{ developerNotification }
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

    protected boolean acceptableName(String name) {
        boolean accepted = isWithinLengthLimit(name) && !isExcluded(name) && isAccepted(name);
        if (devMode && accepted) { // notify only when in devMode
            LOG.debug("Parameter [{}] was accepted and will be appended to action!", name);
        }
        return accepted;
    }

	protected boolean isWithinLengthLimit( String name ) {
        boolean matchLength = name.length() <= paramNameMaxLength;
        if (!matchLength) {
            LOG.debug("Parameter [{}] is too long, allowed length is [{}]", name, String.valueOf(paramNameMaxLength));
        }
        return matchLength;
	}

    protected boolean isAccepted(String paramName) {
        AcceptedPatternsChecker.IsAccepted result = acceptedPatterns.isAccepted(paramName);
        if (result.isAccepted()) {
            return true;
        }
        LOG.debug("Parameter [{}] didn't match accepted pattern [{}]!", paramName, result.getAcceptedPattern());
        return false;
    }

    protected boolean isExcluded(String paramName) {
        ExcludedPatternsChecker.IsExcluded result = excludedPatterns.isExcluded(paramName);
        if (result.isExcluded()) {
            LOG.debug("Parameter [{}] matches excluded pattern [{}]!", paramName, result.getExcludedPattern());
            return true;
        }
        return false;
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

}
