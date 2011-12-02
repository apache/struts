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
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.Parameterizable;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor populates the action with the static parameters defined in the action configuration. If the action
 * implements {@link Parameterizable}, a map of the static parameters will be also be passed directly to the action.
 * The static params will be added to the request params map, unless "merge" is set to false.
 *
 * <p/> Parameters are typically defined with &lt;param&gt; elements within xwork.xml.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
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
 * <p/> <u>Extending the interceptor:</u>
 *
 * <!-- START SNIPPET: extending -->
 *
 * <p/>There are no extension points to this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
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

    static boolean devMode = false;

    private static final Logger LOG = LoggerFactory.getLogger(StaticParametersInterceptor.class);

    private ValueStackFactory valueStackFactory;

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject("devMode")
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }    

    public void setParse(String value) {
        this.parse = Boolean.valueOf(value).booleanValue();
    }

     public void setMerge(String value) {
        this.merge = Boolean.valueOf(value).booleanValue();
    }

    /**
     * Overwrites already existing parameters from other sources.
     * Static parameters are the successor over previously set parameters, if true.
     *
     * @param value
     */
    public void setOverwrite(String value) {
        this.overwrite = Boolean.valueOf(value).booleanValue();
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionConfig config = invocation.getProxy().getConfig();
        Object action = invocation.getAction();

        final Map<String, String> parameters = config.getParams();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting static parameters " + parameters);
        }

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
     * As default, static parameters will not overwrite existing paramaters from other sources.
     * If you want the static parameters as successor over already existing parameters, set overwrite to <tt>true</tt>.
     *
     * @param ac        The action context
     * @param newParams The parameter map to apply
     */
    protected void addParametersToContext(ActionContext ac, Map<String, ?> newParams) {
        Map<String, Object> previousParams = ac.getParameters();

        Map<String, Object> combinedParams;
        if ( overwrite ) {
            if (previousParams != null) {
                combinedParams = new TreeMap<String, Object>(previousParams);
            } else {
                combinedParams = new TreeMap<String, Object>();
            }
            if ( newParams != null) {
                combinedParams.putAll(newParams);
            }
        } else {
            if (newParams != null) {
                combinedParams = new TreeMap<String, Object>(newParams);
            } else {
                combinedParams = new TreeMap<String, Object>();
            }
            if ( previousParams != null) {
                combinedParams.putAll(previousParams);
            }
        }
        ac.setParameters(combinedParams);
    }
}
