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

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.lang.reflect.Method;

/**
 * <!-- START SNIPPET: description -->
 * <p/>
 * An interceptor that makes sure there are not validation errors before allowing the interceptor chain to continue.
 * <b>This interceptor does not perform any validation</b>.
 * <p/>
 * <p/>This interceptor does nothing if the name of the method being invoked is specified in the <b>excludeMethods</b>
 * parameter. <b>excludeMethods</b> accepts a comma-delimited list of method names. For example, requests to
 * <b>foo!input.action</b> and <b>foo!back.action</b> will be skipped by this interceptor if you set the
 * <b>excludeMethods</b> parameter to "input, back".
 * <p/>
 * <b>Note:</b> As this method extends off MethodFilterInterceptor, it is capable of
 * deciding if it is applicable only to selective methods in the action class. This is done by adding param tags
 * for the interceptor element, naming either a list of excluded method names and/or a list of included method
 * names, whereby includeMethods overrides excludedMethods. A single * sign is interpreted as wildcard matching
 * all methods for both parameters.
 * See {@link MethodFilterInterceptor} for more info.
 * <p/>
 * <!-- END SNIPPET: description -->
 * <p/>
 * <p/> <u>Interceptor parameters:</u>
 * <p/>
 * <!-- START SNIPPET: parameters -->
 * <p/>
 * <ul>
 * <p/>
 * <li>inputResultName - Default to "input". Determine the result name to be returned when
 * an action / field error is found.</li>
 * <p/>
 * </ul>
 * <p/>
 * <!-- END SNIPPET: parameters -->
 * <p/>
 * <p/> <u>Extending the interceptor:</u>
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: extending -->
 * <p/>
 * There are no known extension points for this interceptor.
 * <p/>
 * <!-- END SNIPPET: extending -->
 * <p/>
 * <p/> <u>Example code:</u>
 * <p/>
 * <pre>
 * <!-- START SNIPPET: example -->
 * <p/>
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <p/>
 * &lt;-- In this case myMethod as well as mySecondMethod of the action class
 *        will not pass through the workflow process --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"&gt;
 *         &lt;param name="excludeMethods"&gt;myMethod,mySecondMethod&lt;/param&gt;
 *     &lt;/interceptor-ref name="workflow"&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <p/>
 * &lt;-- In this case, the result named "error" will be used when
 *        an action / field error is found --&gt;
 * &lt;-- The Interceptor will only be applied for myWorkflowMethod method of action
 *        classes, since this is the only included method while any others are excluded --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"&gt;
 *        &lt;param name="inputResultName"&gt;error&lt;/param&gt;
 *         &lt;param name="excludeMethods"&gt;*&lt;/param&gt;
 *         &lt;param name="includeMethods"&gt;myWorkflowMethod&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <p/>
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * @author Philip Luppens
 * @author tm_jee
 */
public class DefaultWorkflowInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = 7563014655616490865L;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorkflowInterceptor.class);

    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    
    private String inputResultName = Action.INPUT;

    /**
     * Set the <code>inputResultName</code> (result name to be returned when
     * a action / field error is found registered). Default to {@link Action#INPUT}
     *
     * @param inputResultName what result name to use when there was validation error(s).
     */
    public void setInputResultName(String inputResultName) {
        this.inputResultName = inputResultName;
    }

    /**
     * Intercept {@link ActionInvocation} and returns a <code>inputResultName</code>
     * when action / field errors is found registered.
     *
     * @return String result name
     */
    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ValidationAware) {
            ValidationAware validationAwareAction = (ValidationAware) action;

            if (validationAwareAction.hasErrors()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Errors on action " + validationAwareAction + ", returning result name 'input'");
                }

                String resultName = inputResultName;

                if (action instanceof ValidationWorkflowAware) {
                    resultName = ((ValidationWorkflowAware) action).getInputResultName();
                }

                InputConfig annotation = action.getClass().getMethod(invocation.getProxy().getMethod(), EMPTY_CLASS_ARRAY).getAnnotation(InputConfig.class);
                if (annotation != null) {
                    if (!annotation.methodName().equals("")) {
                        Method method = action.getClass().getMethod(annotation.methodName());
                        resultName = (String) method.invoke(action);
                    } else {
                        resultName = annotation.resultName();
                    }
                }


                return resultName;
            }
        }

        return invocation.invoke();
    }

}
