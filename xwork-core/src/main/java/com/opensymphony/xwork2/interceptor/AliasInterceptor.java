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

package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Map;


/**
 * <!-- START SNIPPET: description -->
 *
 * The aim of this Interceptor is to alias a named parameter to a different named parameter. By acting as the glue
 * between actions sharing similiar parameters (but with different names), it can help greatly with action chaining.
 *
 * <p/>  Action's alias expressions should be in the form of  <code>#{ "name1" : "alias1", "name2" : "alias2" }</code>.
 * This means that assuming an action (or something else in the stack) has a value for the expression named <i>name1</i> and the
 * action this interceptor is applied to has a setter named <i>alias1</i>, <i>alias1</i> will be set with the value from
 * <i>name1</i>.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
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
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * This interceptor does not have any known extension points.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
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

    private static final Logger LOG = LoggerFactory.getLogger(AliasInterceptor.class);

    private static final String DEFAULT_ALIAS_KEY = "aliases";
    protected String aliasesKey = DEFAULT_ALIAS_KEY;

    protected ValueStackFactory valueStackFactory;
    static boolean devMode = false;

    @Inject(XWorkConstants.DEV_MODE)
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }   

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    /**
     * Sets the name of the action parameter to look for the alias map.
     * <p/>
     * Default is <code>aliases</code>.
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

            if (obj != null && obj instanceof Map) {
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
                    context.put(ActionContext.LOCALE, stack.getContext().get(ActionContext.LOCALE));
                }

                // override
                Map aliases = (Map) obj;
                for (Object o : aliases.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    String name = entry.getKey().toString();
                    String alias = (String) entry.getValue();
                    Object value = stack.findValue(name);
                    if (null == value) {
                        // workaround
                        Map<String, Object> contextParameters = ActionContext.getContext().getParameters();

                        if (null != contextParameters) {
                            value = contextParameters.get(name);
                        }
                    }
                    if (null != value) {
                        try {
                            newStack.setValue(alias, value);
                        } catch (RuntimeException e) {
                            if (devMode) {
                                String developerNotification = LocalizedTextUtil.findText(ParametersInterceptor.class, "devmode.notification", ActionContext.getContext().getLocale(), "Developer Notification:\n{0}", new Object[]{
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

                if (clearableStack && (stack.getContext() != null) && (newStack.getContext() != null))
                    stack.getContext().put(ActionContext.CONVERSION_ERRORS, newStack.getContext().get(ActionContext.CONVERSION_ERRORS));
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("invalid alias expression:" + aliasesKey);
                }
            }
        }
        
        return invocation.invoke();
    }
    
}
