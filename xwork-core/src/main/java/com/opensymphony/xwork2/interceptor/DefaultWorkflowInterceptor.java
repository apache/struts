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
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.lang.reflect.Method;

/**
 * <!-- START SNIPPET: description -->
 * <p/>
 * An interceptor that makes sure there are not validation errors before allowing the interceptor chain to continue.
 * <b>This interceptor does not perform any validation</b>.
 * <p/>
 * This interceptor does nothing if the name of the method being invoked is specified in the <b>excludeMethods</b>
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
 * This interceptor also supports the following interfaces which can implemented by actions:
 * <ul>
 *     <li>ValidationAware - implemented by ActionSupport class</li>
 *     <li>ValidationWorkflowAware - allows changing result name programmatically</li>
 *     <li>ValidationErrorAware - notifies action about errors and also allow change result name</li>
 * </ul>
 *
 * You can also use InputConfig annotation to change result name returned when validation errors occurred.
 *
 * <!-- END SNIPPET: description -->
 *
 * <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * <li>inputResultName - Default to "input". Determine the result name to be returned when
 * an action / field error is found.</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 * <u>Extending the interceptor:</u>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <u>Example code:</u>
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
                    LOG.debug("Errors on action [#0], returning result name [#1]", validationAwareAction, inputResultName);
                }

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
     */
    private String processValidationWorkflowAware(final Object action, final String currentResultName) {
        String resultName = currentResultName;
        if (action instanceof ValidationWorkflowAware) {
            resultName = ((ValidationWorkflowAware) action).getInputResultName();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Changing result name from [#0] to [#1] because of processing [#2] interface applied to [#3]",
                        currentResultName, resultName, ValidationWorkflowAware.class.getSimpleName(), action);
            }
        }
        return resultName;
    }

    /**
     * Process {@link InputConfig} annotation applied to method
     */
    protected String processInputConfig(final Object action, final String method, final String currentResultName) throws Exception {
        String resultName = currentResultName;
        InputConfig annotation = action.getClass().getMethod(method, EMPTY_CLASS_ARRAY).getAnnotation(InputConfig.class);
        if (annotation != null) {
            if (!annotation.methodName().equals("")) {
                Method m = action.getClass().getMethod(annotation.methodName());
                resultName = (String) m.invoke(action);
            } else {
                resultName = annotation.resultName();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Changing result name from [#0] to [#1] because of processing annotation [#2] on action [#3]",
                        currentResultName, resultName, InputConfig.class.getSimpleName(), action);
            }
        }
        return resultName;
    }

    /**
     * Notify action if it implements {@see ValidationErrorAware} interface
     */
    protected String processValidationErrorAware(final Object action, final String currentResultName) {
        String resultName = currentResultName;
        if (action instanceof ValidationErrorAware) {
            resultName = ((ValidationErrorAware) action).actionErrorOccurred(currentResultName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Changing result name from [#0] to [#1] because of processing interface [#2] on action [#3]",
                        currentResultName, resultName, ValidationErrorAware.class.getSimpleName(), action);
            }
        }
        return resultName;
    }

}
