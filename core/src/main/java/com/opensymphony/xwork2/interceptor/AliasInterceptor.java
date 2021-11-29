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
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.Evaluated;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.StrutsConstants;

import java.util.Map;


/**
 * <!-- START SNIPPET: description -->
 *
 * The aim of this Interceptor is to alias a named parameter to a different named parameter. By acting as the glue
 * between actions sharing similar parameters (but with different names), it can help greatly with action chaining.
 *
 * <p>Action's alias expressions should be in the form of  <code>#{ "name1" : "alias1", "name2" : "alias2" }</code>.
 * This means that assuming an action (or something else in the stack) has a value for the expression named <i>name1</i> and the
 * action this interceptor is applied to has a setter named <i>alias1</i>, <i>alias1</i> will be set with the value from
 * <i>name1</i>.
 * </p>
 *
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Interceptor parameters:</u></p>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>aliasesKey (optional) - the name of the action parameter to look for the alias map (by default this is
 * <i>aliases</i>).</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p><u>Extending the interceptor:</u></p>
 *
 * <!-- START SNIPPET: extending -->
 *
 * This interceptor does not have any known extension points.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p><u>Example code:</u></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;!-- The value for the foo parameter will be applied as if it were named bar --&gt;
 *     &lt;param name="aliases"&gt;#{ 'foo' : 'bar' }&lt;/param&gt;
 *
 *     &lt;interceptor-ref name="alias"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Matthew Payne
 */
public class AliasInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LogManager.getLogger(AliasInterceptor.class);

    private static final String DEFAULT_ALIAS_KEY = "aliases";
    protected String aliasesKey = DEFAULT_ALIAS_KEY;

    protected ValueStackFactory valueStackFactory;
    protected LocalizedTextProvider localizedTextProvider;
    protected boolean devMode = false;

    private ExcludedPatternsChecker excludedPatterns;
    private AcceptedPatternsChecker acceptedPatterns;

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        this.devMode = Boolean.parseBoolean(mode);
    }   

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
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
     * <p>
     * Sets the name of the action parameter to look for the alias map.
     * </p>
     *
     * <p>
     * Default is <code>aliases</code>.
     * </p>
     *
     * @param aliasesKey  the name of the action parameter
     */
    public void setAliasesKey(String aliasesKey) {
        this.aliasesKey = aliasesKey;
    }

    @Override public String intercept(ActionInvocation invocation) throws Exception {

        ActionConfig config = invocation.getProxy().getConfig();
        ActionContext ac = invocation.getInvocationContext();
        Object action = invocation.getAction();

        // get the action's parameters
        final Map<String, String> parameters = config.getParams();

        if (parameters.containsKey(aliasesKey)) {

            String aliasExpression = parameters.get(aliasesKey);
            ValueStack stack = ac.getValueStack();
            Object obj = stack.findValue(aliasExpression);

            if (obj instanceof Map) {
                //get secure stack
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
                    newStack.getActionContext().withLocale(stack.getActionContext().getLocale());
                }

                // override
                Map aliases = (Map) obj;
                for (Object o : aliases.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    String name = entry.getKey().toString();
                    if (isNotAcceptableExpression(name)) {
                        continue;
                    }
                    String alias = (String) entry.getValue();
                    if (isNotAcceptableExpression(alias)) {
                        continue;
                    }
                    Evaluated value = new Evaluated(stack.findValue(name));
                    if (!value.isDefined()) {
                        // workaround
                        HttpParameters contextParameters = ActionContext.getContext().getParameters();

                        if (null != contextParameters) {
                            Parameter param = contextParameters.get(name);
                            if (param.isDefined()) {
                                value = new Evaluated(param.getValue());
                            }
                        }
                    }
                    if (value.isDefined()) {
                        try {
                            newStack.setValue(alias, value.get());
                        } catch (RuntimeException e) {
                            if (devMode) {
                                String developerNotification = localizedTextProvider.findText(ParametersInterceptor.class, "devmode.notification", ActionContext.getContext().getLocale(), "Developer Notification:\n{0}", new Object[]{
                                        "Unexpected Exception caught setting '" + entry.getKey() + "' on '" + action.getClass() + ": " + e.getMessage()
                                });
                                LOG.error(developerNotification);
                                if (action instanceof ValidationAware) {
                                    ((ValidationAware) action).addActionMessage(developerNotification);
                                }
                            }
                        }
                    }
                }

                if (clearableStack) {
                    stack.getActionContext().withConversionErrors(newStack.getActionContext().getConversionErrors());
                }
            } else {
                LOG.debug("invalid alias expression: {}", aliasesKey);
            }
        }
        
        return invocation.invoke();
    }

    protected boolean isAccepted(String paramName) {
        AcceptedPatternsChecker.IsAccepted result = acceptedPatterns.isAccepted(paramName);
        if (result.isAccepted()) {
            return true;
        }

        LOG.warn("Parameter [{}] didn't match accepted pattern [{}]! See Accepted / Excluded patterns at\n" +
                        "https://struts.apache.org/security/#accepted--excluded-patterns",
                paramName, result.getAcceptedPattern());

        return false;
    }

    protected boolean isExcluded(String paramName) {
        ExcludedPatternsChecker.IsExcluded result = excludedPatterns.isExcluded(paramName);
        if (!result.isExcluded()) {
            return false;
        }

        LOG.warn("Parameter [{}] matches excluded pattern [{}]! See Accepted / Excluded patterns at\n" +
                        "https://struts.apache.org/security/#accepted--excluded-patterns",
                paramName, result.getExcludedPattern());

        return true;
    }

    /**
     * Checks if expression contains vulnerable code
     *
     * @param expression of interceptor
     * @return true|false
     */
    protected boolean isNotAcceptableExpression(String expression) {
        return isExcluded(expression) || !isAccepted(expression);
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
