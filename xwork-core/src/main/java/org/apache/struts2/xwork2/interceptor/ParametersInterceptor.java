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
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.conversion.impl.InstantiatingNullHandler;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.MemberAccessValueStack;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <!-- START SNIPPET: description -->
 * This interceptor sets all parameters on the value stack.
 * <p/>
 * This interceptor gets all parameters from {@link ActionContext#getParameters()} and sets them on the value stack by
 * calling {@link ValueStack#setValue(String, Object)}, typically resulting in the values submitted in a form
 * request being applied to an action in the value stack. Note that the parameter map must contain a String key and
 * often containers a String[] for the value.
 * <p/>
 * <p/> The interceptor takes one parameter named 'ordered'. When set to true action properties are guaranteed to be
 * set top-down which means that top action's properties are set first. Then it's subcomponents properties are set.
 * The reason for this order is to enable a 'factory' pattern. For example, let's assume that one has an action
 * that contains a property named 'modelClass' that allows to choose what is the underlying implementation of model.
 * By assuring that modelClass property is set before any model properties are set, it's possible to choose model
 * implementation during action.setModelClass() call. Similiarily it's possible to use action.setPrimaryKey()
 * property set call to actually load the model class from persistent storage. Without any assumption on parameter
 * order you have to use patterns like 'Preparable'.
 * <p/>
 * <p/> Because parameter names are effectively OGNL statements, it is important that security be taken in to account.
 * This interceptor will not apply any values in the parameters map if the expression contains an assignment (=),
 * multiple expressions (,), or references any objects in the context (#). This is all done in the {@link
 * #acceptableName(String)} method. In addition to this method, if the action being invoked implements the {@link
 * ParameterNameAware} interface, the action will be consulted to determine if the parameter should be set.
 * <p/>
 * <p/> In addition to these restrictions, a flag ({@link ReflectionContextState#DENY_METHOD_EXECUTION}) is set such that
 * no methods are allowed to be invoked. That means that any expression such as <i>person.doSomething()</i> or
 * <i>person.getName()</i> will be explicitely forbidden. This is needed to make sure that your application is not
 * exposed to attacks by malicious users.
 * <p/>
 * <p/> While this interceptor is being invoked, a flag ({@link ReflectionContextState#CREATE_NULL_OBJECTS}) is turned
 * on to ensure that any null reference is automatically created - if possible. See the type conversion documentation
 * and the {@link InstantiatingNullHandler} javadocs for more information.
 * <p/>
 * <p/> Finally, a third flag ({@link XWorkConverter#REPORT_CONVERSION_ERRORS}) is set that indicates any errors when
 * converting the the values to their final data type (String[] -&gt; int) an unrecoverable error occured. With this
 * flag set, the type conversion errors will be reported in the action context. See the type conversion documentation
 * and the {@link XWorkConverter} javadocs for more information.
 * <p/>
 * <p/> If you are looking for detailed logging information about your parameters, turn on DEBUG level logging for this
 * interceptor. A detailed log of all the parameter keys and values will be reported.
 * <p/>
 * <p/>
 * <b>Note:</b> Since XWork 2.0.2, this interceptor extends {@link MethodFilterInterceptor}, therefore being
 * able to deal with excludeMethods / includeMethods parameters. See [Workflow Interceptor]
 * (class {@link DefaultWorkflowInterceptor}) for documentation and examples on how to use this feature.
 * <p/>
 * <!-- END SNIPPET: description -->
 * <p/>
 * <p/> <u>Interceptor parameters:</u>
 * <p/>
 * <!-- START SNIPPET: parameters -->
 * <p/>
 * <ul>
 * <p/>
 * <li>ordered - set to true if you want the top-down property setter behaviour</li>
 * <p/>
 * </ul>
 * <p/>
 * <!-- END SNIPPET: parameters -->
 * <p/>
 * <p/> <u>Extending the interceptor:</u>
 * <p/>
 * <!-- START SNIPPET: extending -->
 * <p/>
 * <p/> The best way to add behavior to this interceptor is to utilize the {@link ParameterNameAware} interface in your
 * actions. However, if you wish to apply a global rule that isn't implemented in your action, then you could extend
 * this interceptor and override the {@link #acceptableName(String)} method.
 * <p/>
 * <!-- END SNIPPET: extending -->
 * <p/>
 * <p/> <u>Example code:</u>
 * <p/>
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

    boolean ordered = false;
    Set<Pattern> excludeParams = Collections.emptySet();
    Set<Pattern> acceptParams = Collections.emptySet();
    static boolean devMode = false;

    // Allowed names of parameters
    private String acceptedParamNames = "[a-zA-Z0-9\\.\\]\\[\\(\\)_'\\s]+";
    private Pattern acceptedPattern = Pattern.compile(acceptedParamNames);

    private ValueStackFactory valueStackFactory;

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject("devMode")
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }

    public void setAcceptParamNames(String commaDelim) {
        Collection<String> acceptPatterns = asCollection(commaDelim);
        if (acceptPatterns != null) {
            acceptParams = new HashSet<Pattern>();
            for (String pattern : acceptPatterns) {
                acceptParams.add(Pattern.compile(pattern));
            }
        }
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

    protected void setParameters(Object action, ValueStack stack, final Map<String, Object> parameters) {
        ParameterNameAware parameterNameAware = (action instanceof ParameterNameAware)
                ? (ParameterNameAware) action : null;

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

            boolean acceptableName = acceptableName(name)
                    && (parameterNameAware == null
                    || parameterNameAware.acceptableParameterName(name));

            if (acceptableName) {
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
            accessValueStack.setAcceptProperties(acceptParams);
            accessValueStack.setExcludeProperties(excludeParams);
        }

        for (Map.Entry<String, Object> entry : acceptableParameters.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            try {
                newStack.setValue(name, value);
            } catch (RuntimeException e) {
                if (devMode) {
                    String developerNotification = LocalizedTextUtil.findText(ParametersInterceptor.class, "devmode.notification", ActionContext.getContext().getLocale(), "Developer Notification:\n{0}", new Object[]{
                             "Unexpected Exception caught setting '" + name + "' on '" + action.getClass() + ": " + e.getMessage()
                    });
                    LOG.error(developerNotification);
                    if (action instanceof ValidationAware) {
                        ((ValidationAware) action).addActionMessage(developerNotification);
                    }
                }
            }
        }

        if (clearableStack && (stack.getContext() != null) && (newStack.getContext() != null))
            stack.getContext().put(ActionContext.CONVERSION_ERRORS, newStack.getContext().get(ActionContext.CONVERSION_ERRORS));

        addParametersToContext(ActionContext.getContext(), acceptableParameters);
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

    private String getParameterLogMap(Map<String, Object> parameters) {
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
        return isAccepted(name) && !isExcluded(name);
    }

    protected boolean isAccepted(String paramName) {
        if (!this.acceptParams.isEmpty()) {
            for (Pattern pattern : acceptParams) {
                Matcher matcher = pattern.matcher(paramName);
                if (matcher.matches()) {
                    return true;
                }
            }
            return false;
        } else
            return acceptedPattern.matcher(paramName).matches();
    }

    protected boolean isExcluded(String paramName) {
        if (!this.excludeParams.isEmpty()) {
            for (Pattern pattern : excludeParams) {
                Matcher matcher = pattern.matcher(paramName);
                if (matcher.matches()) {
                    return true;
                }
            }
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
     * Gets a set of regular expressions of parameters to remove
     * from the parameter map
     *
     * @return A set of compiled regular expression patterns
     */
    protected Set getExcludeParamsSet() {
        return excludeParams;
    }

    /**
     * Sets a comma-delimited list of regular expressions to match
     * parameters that should be removed from the parameter map.
     *
     * @param commaDelim A comma-delimited list of regular expressions
     */
    public void setExcludeParams(String commaDelim) {
        Collection<String> excludePatterns = asCollection(commaDelim);
        if (excludePatterns != null) {
            excludeParams = new HashSet<Pattern>();
            for (String pattern : excludePatterns) {
                excludeParams.add(Pattern.compile(pattern));
            }
        }
    }

    /**
     * Return a collection from the comma delimited String.
     *
     * @param commaDelim the comma delimited String.
     * @return A collection from the comma delimited String. Returns <tt>null</tt> if the string is empty.
     */
    private Collection<String> asCollection(String commaDelim) {
        if (commaDelim == null || commaDelim.trim().length() == 0) {
            return null;
        }
        return TextParseUtil.commaDelimitedStringToSet(commaDelim);
    }

}
