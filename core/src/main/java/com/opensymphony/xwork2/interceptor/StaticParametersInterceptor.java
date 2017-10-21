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
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.Parameterizable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.Collections;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor populates the action with the static parameters defined in the action configuration. If the action
 * implements {@link Parameterizable}, a map of the static parameters will be also be passed directly to the action.
 * The static params will be added to the request params map, unless "merge" is set to false.
 *
 * <p> Parameters are typically defined with &lt;param&gt; elements within xwork.xml.</p>
 *
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Interceptor parameters:</u></p>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>None</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p><u>Extending the interceptor:</u></p>
 *
 * <!-- START SNIPPET: extending -->
 *
 * <p>There are no extension points to this interceptor.</p>
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p> <u>Example code:</u></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="staticParams"&gt;
 *          &lt;param name="parse"&gt;true&lt;/param&gt;
 *          &lt;param name="overwrite"&gt;false&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 */
public class StaticParametersInterceptor extends AbstractInterceptor {

    private boolean parse;
    private boolean overwrite;
    private boolean merge = true;
    private boolean devMode = false;

    private static final Logger LOG = LogManager.getLogger(StaticParametersInterceptor.class);

    private ValueStackFactory valueStackFactory;
    private LocalizedTextProvider localizedTextProvider;

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject(XWorkConstants.DEV_MODE)
    public void setDevMode(String mode) {
        devMode = BooleanUtils.toBoolean(mode);
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    public void setParse(String value) {
        this.parse = BooleanUtils.toBoolean(value);
    }

     public void setMerge(String value) {
         this.merge = BooleanUtils.toBoolean(value);
    }

    /**
     * Overwrites already existing parameters from other sources.
     * Static parameters are the successor over previously set parameters, if true.
     *
     * @param value enable overwrites of already existing parameters from other sources
     */
    public void setOverwrite(String value) {
        this.overwrite = BooleanUtils.toBoolean(value);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionConfig config = invocation.getProxy().getConfig();
        Object action = invocation.getAction();

        final Map<String, String> parameters = config.getParams();

        LOG.debug("Setting static parameters: {}", parameters);

        // for actions marked as Parameterizable, pass the static parameters directly
        if (action instanceof Parameterizable) {
            ((Parameterizable) action).setParams(parameters);
        }

        if (parameters != null) {
            ActionContext ac = ActionContext.getContext();
            Map<String, Object> contextMap = ac.getContextMap();
            try {
                ReflectionContextState.setCreatingNullObjects(contextMap, true);
                ReflectionContextState.setReportingConversionErrors(contextMap, true);
                final ValueStack stack = ac.getValueStack();

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

                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    Object val = entry.getValue();
                    if (parse && val instanceof String) {
                        val = TextParseUtil.translateVariables(val.toString(), stack);
                    }
                    try {
                        newStack.setValue(entry.getKey(), val);
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

                 if (clearableStack && (stack.getContext() != null) && (newStack.getContext() != null))
                    stack.getContext().put(ActionContext.CONVERSION_ERRORS, newStack.getContext().get(ActionContext.CONVERSION_ERRORS));

                if (merge)
                    addParametersToContext(ac, parameters);
            } finally {
                ReflectionContextState.setCreatingNullObjects(contextMap, false);
                ReflectionContextState.setReportingConversionErrors(contextMap, false);
            }
        }
        return invocation.invoke();
    }


    /**
     * @param ac The action context
     * @return the parameters from the action mapping in the context.  If none found, returns
     *         an empty map.
     */
    protected Map<String, String> retrieveParameters(ActionContext ac) {
        ActionConfig config = ac.getActionInvocation().getProxy().getConfig();
        if (config != null) {
            return config.getParams();
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Adds the parameters into context's ParameterMap.
     * As default, static parameters will not overwrite existing parameters from other sources.
     * If you want the static parameters as successor over already existing parameters, set overwrite to <tt>true</tt>.
     *
     * @param ac        The action context
     * @param newParams The parameter map to apply
     */
    protected void addParametersToContext(ActionContext ac, Map<String, ?> newParams) {
        HttpParameters previousParams = ac.getParameters();

        HttpParameters.Builder combinedParams = HttpParameters.create();
        if (overwrite) {
            if (previousParams != null) {
                combinedParams = combinedParams.withParent(previousParams);
            }
            if (newParams != null) {
                combinedParams = combinedParams.withExtraParams(newParams);
            }
        } else {
            if (newParams != null) {
                combinedParams = combinedParams.withExtraParams(newParams);
            }
            if (previousParams != null) {
                combinedParams = combinedParams.withParent(previousParams);
            }
        }
        ac.setParameters(combinedParams.build());
    }
}
