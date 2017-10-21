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

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * An interceptor that makes sure there are not validation, conversion or action errors before allowing the interceptor chain to continue. 
 * If a single FieldError or ActionError (including the ones replicated by the Message Store Interceptor in a redirection) is found, the INPUT result will be triggered.
 * <b>This interceptor does not perform any validation</b>.
 * </p>
 *
 * <p>
 * This interceptor does nothing if the name of the method being invoked is specified in the <b>excludeMethods</b>
 * parameter. <b>excludeMethods</b> accepts a comma-delimited list of method names. For example, requests to
 * <b>foo!input.action</b> and <b>foo!back.action</b> will be skipped by this interceptor if you set the
 * <b>excludeMethods</b> parameter to "input, back".
 * </p>
 *
 * <p>
 * <b>Note:</b> As this method extends off MethodFilterInterceptor, it is capable of
 * deciding if it is applicable only to selective methods in the action class. This is done by adding param tags
 * for the interceptor element, naming either a list of excluded method names and/or a list of included method
 * names, whereby includeMethods overrides excludedMethods. A single * sign is interpreted as wildcard matching
 * all methods for both parameters.
 * See {@link MethodFilterInterceptor} for more info.
 * </p>
 *
 * <p>
 * This interceptor also supports the following interfaces which can implemented by actions:
 * </p>
 *
 * <ul>
 *     <li>ValidationAware - implemented by ActionSupport class</li>
 *     <li>ValidationWorkflowAware - allows changing result name programmatically</li>
 *     <li>ValidationErrorAware - notifies action about errors and also allow change result name</li>
 * </ul>
 *
 * <p>
 * You can also use InputConfig annotation to change result name returned when validation errors occurred.
 * </p>
 *
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Interceptor parameters:</u></p>
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * <li>inputResultName - Default to "input". Determine the result name to be returned when
 * an action / field error is found.</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 * <p><u>Extending the interceptor:</u></p>
 *
 * <!-- START SNIPPET: extending -->
 *
 * <p>There are no known extension points for this interceptor.</p>
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p><u>Example code:</u></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
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
 *
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
 *
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

    private static final Logger LOG = LogManager.getLogger(DefaultWorkflowInterceptor.class);

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
     * @param invocation the action invocation
     * @return String result name
     */
    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ValidationAware) {
            ValidationAware validationAwareAction = (ValidationAware) action;

            if (validationAwareAction.hasErrors()) {
                LOG.debug("Errors on action [{}], returning result name [{}]", validationAwareAction, inputResultName);

                String resultName = inputResultName;
                resultName = processValidationWorkflowAware(action, resultName);
                resultName = processInputConfig(action, invocation.getProxy().getMethod(), resultName);
                resultName = processValidationErrorAware(action, resultName);

                return resultName;
            }
        }

        return invocation.invoke();
    }

    /**
     * Process {@link ValidationWorkflowAware} interface
     *
     * @param action action object
     * @param currentResultName current result name
     *
     * @return result name
     */
    private String processValidationWorkflowAware(final Object action, final String currentResultName) {
        String resultName = currentResultName;
        if (action instanceof ValidationWorkflowAware) {
            resultName = ((ValidationWorkflowAware) action).getInputResultName();
            LOG.debug("Changing result name from [{}] to [{}] because of processing [{}] interface applied to [{}]",
                        currentResultName, resultName, ValidationWorkflowAware.class.getSimpleName(), action);
        }
        return resultName;
    }

    /**
     * Process {@link InputConfig} annotation applied to method
     * @param action action object
     * @param method method
     * @param currentResultName current result name
     *
     * @return result name
     *
     * @throws Exception in case of any errors
     */
    protected String processInputConfig(final Object action, final String method, final String currentResultName) throws Exception {
        String resultName = currentResultName;
        InputConfig annotation = MethodUtils.getAnnotation(action.getClass().getMethod(method, EMPTY_CLASS_ARRAY),
                InputConfig.class ,true,true);
        if (annotation != null) {
            if (StringUtils.isNotEmpty(annotation.methodName())) {
                resultName = (String) MethodUtils.invokeMethod(action, true, annotation.methodName());
            } else {
                resultName = annotation.resultName();
            }
            LOG.debug("Changing result name from [{}] to [{}] because of processing annotation [{}] on action [{}]",
                        currentResultName, resultName, InputConfig.class.getSimpleName(), action);
        }
        return resultName;
    }

    /**
     * Notify action if it implements {@link ValidationErrorAware} interface
     *
     * @param action action object
     * @param currentResultName current result name
     *
     * @return result name
     * @see ValidationErrorAware
     */
    protected String processValidationErrorAware(final Object action, final String currentResultName) {
        String resultName = currentResultName;
        if (action instanceof ValidationErrorAware) {
            resultName = ((ValidationErrorAware) action).actionErrorOccurred(currentResultName);
            LOG.debug("Changing result name from [{}] to [{}] because of processing interface [{}] on action [{}]",
                        currentResultName, resultName, ValidationErrorAware.class.getSimpleName(), action);
        }
        return resultName;
    }

}
