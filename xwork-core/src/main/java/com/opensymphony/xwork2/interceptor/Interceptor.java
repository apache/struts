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

import com.opensymphony.xwork2.ActionInvocation;

import java.io.Serializable;


/**
 * <!-- START SNIPPET: introduction -->
 * <p/>
 * An interceptor is a stateless class that follows the interceptor pattern, as
 * found in {@link  javax.servlet.Filter} and in AOP languages.
 * <p/>
 * <p/>
 * <p/>
 * Interceptors are objects that dynamically intercept Action invocations.
 * They provide the developer with the opportunity to define code that can be executed
 * before and/or after the execution of an action. They also have the ability
 * to prevent an action from executing. Interceptors provide developers a way to
 * encapulate common functionality in a re-usable form that can be applied to
 * one or more Actions.
 * <p/>
 * <p/>
 * <p/>
 * Interceptors <b>must</b> be stateless and not assume that a new instance will be created for each request or Action.
 * Interceptors may choose to either short-circuit the {@link ActionInvocation} execution and return a return code
 * (such as {@link com.opensymphony.xwork2.Action#SUCCESS}), or it may choose to do some processing before
 * and/or after delegating the rest of the procesing using {@link ActionInvocation#invoke()}.
 * <p/>
 * <!-- END SNIPPET: introduction -->
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: parameterOverriding -->
 * <p/>
 * Interceptor's parameter could be overriden through the following ways :-
 * <p/>
 * <p/>
 * <p/>
 * <b>Method 1:</b>
 * <pre>
 * &lt;action name="myAction" class="myActionClass"&gt;
 *     &lt;interceptor-ref name="exception"/&gt;
 *     &lt;interceptor-ref name="alias"/&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="servletConfig"/&gt;
 *     &lt;interceptor-ref name="prepare"/&gt;
 *     &lt;interceptor-ref name="i18n"/&gt;
 *     &lt;interceptor-ref name="chain"/&gt;
 *     &lt;interceptor-ref name="modelDriven"/&gt;
 *     &lt;interceptor-ref name="fileUpload"/&gt;
 *     &lt;interceptor-ref name="staticParams"/&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="conversionError"/&gt;
 *     &lt;interceptor-ref name="validation"&gt;
 *     &lt;param name="excludeMethods"&gt;myValidationExcudeMethod&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;interceptor-ref name="workflow"&gt;
 *     &lt;param name="excludeMethods"&gt;myWorkflowExcludeMethod&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 * &lt;/action&gt;
 * </pre>
 * <p/>
 * <b>Method 2:</b>
 * <pre>
 * &lt;action name="myAction" class="myActionClass"&gt;
 *   &lt;interceptor-ref name="defaultStack"&gt;
 *     &lt;param name="validation.excludeMethods"&gt;myValidationExcludeMethod&lt;/param&gt;
 *     &lt;param name="workflow.excludeMethods"&gt;myWorkflowExcludeMethod&lt;/param&gt;
 *   &lt;/interceptor-ref&gt;
 * &lt;/action&gt;
 * </pre>
 * <p/>
 * <p/>
 * <p/>
 * In the first method, the whole default stack is copied and the parameter then
 * changed accordingly.
 * <p/>
 * <p/>
 * <p/>
 * In the second method, the 'interceptor-ref' refer to an existing
 * interceptor-stack, namely defaultStack in this example, and override the validator
 * and workflow interceptor excludeMethods typically in this case. Note that in the
 * 'param' tag, the name attribute contains a dot (.) the word before the dot(.)
 * specifies the interceptor name whose parameter is to be overridden and the word after
 * the dot (.) specifies the parameter itself. Essetially it is as follows :-
 * <p/>
 * <pre>
 *    &lt;interceptor-name&gt;.&lt;parameter-name&gt;
 * </pre>
 * <p/>
 * <b>Note</b> also that in this case the 'interceptor-ref' name attribute
 * is used to indicate an interceptor stack which makes sense as if it is referring
 * to the interceptor itself it would be just using Method 1 describe above.
 * <p/>
 * <!-- END SNIPPET: parameterOverriding -->
 * <p/>
 * <p/>
 * <b>Nested Interceptor param overriding</b>
 * <p/>
 * <!-- START SNIPPET: nestedParameterOverriding -->
 * <p/>
 * Interceptor stack parameter overriding could be nested into as many level as possible, though it would
 * be advisable not to nest it too deep as to avoid confusion, For example,
 * <pre>
 * &lt;interceptor name="interceptor1" class="foo.bar.Interceptor1" /&gt;
 * &lt;interceptor name="interceptor2" class="foo.bar.Interceptor2" /&gt;
 * &lt;interceptor name="interceptor3" class="foo.bar.Interceptor3" /&gt;
 * &lt;interceptor name="interceptor4" class="foo.bar.Interceptor4" /&gt;
 * &lt;interceptor-stack name="stack1"&gt;
 *     &lt;interceptor-ref name="interceptor1" /&gt;
 * &lt;/interceptor-stack&gt;
 * &lt;interceptor-stack name="stack2"&gt;
 *     &lt;interceptor-ref name="intercetor2" /&gt;
 *     &lt;interceptor-ref name="stack1" /&gt;
 * &lt;/interceptor-stack&gt;
 * &lt;interceptor-stack name="stack3"&gt;
 *     &lt;interceptor-ref name="interceptor3" /&gt;
 *     &lt;interceptor-ref name="stack2" /&gt;
 * &lt;/interceptor-stack&gt;
 * &lt;interceptor-stack name="stack4"&gt;
 *     &lt;interceptor-ref name="interceptor4" /&gt;
 *     &lt;interceptor-ref name="stack3" /&gt;
 *  &lt;/interceptor-stack&gt;
 * </pre>
 * Assuming the interceptor has the following properties
 * <table border="1" width="100%">
 * <tr>
 * <td>Interceptor</td>
 * <td>property</td>
 * </tr>
 * <tr>
 * <td>Interceptor1</td>
 * <td>param1</td>
 * </tr>
 * <tr>
 * <td>Interceptor2</td>
 * <td>param2</td>
 * </tr>
 * <tr>
 * <td>Interceptor3</td>
 * <td>param3</td>
 * </tr>
 * <tr>
 * <td>Interceptor4</td>
 * <td>param4</td>
 * </tr>
 * </table>
 * We could override them as follows :-
 * <pre>
 *    &lt;action ... &gt;
 *        &lt;!-- to override parameters of interceptor located directly in the stack  --&gt;
 *        &lt;interceptor-ref name="stack4"&gt;
 *           &lt;param name="interceptor4.param4"&gt; ... &lt;/param&gt;
 *        &lt;/interceptor-ref&gt;
 *    &lt;/action&gt;
 * <p/>
 *    &lt;action ... &gt;
 *        &lt;!-- to override parameters of interceptor located under nested stack --&gt;
 *        &lt;interceptor-ref name="stack4"&gt;
 *            &lt;param name="stack3.interceptor3.param3"&gt; ... &lt;/param&gt;
 *            &lt;param name="stack3.stack2.interceptor2.param2"&gt; ... &lt;/param&gt;
 *            &lt;param name="stack3.stack2.stack1.interceptor1.param1"&gt; ... &lt;/param&gt;
 *        &lt;/interceptor-ref&gt;
 *    &lt;/action&gt;
 *  </pre>
 * <p/>
 * <!-- END SNIPPET: nestedParameterOverriding -->
 *
 * @author Jason Carreira
 * @author tmjee
 * @version $Date$ $Id$
 */
public interface Interceptor extends Serializable {

    /**
     * Called to let an interceptor clean up any resources it has allocated.
     */
    void destroy();

    /**
     * Called after an interceptor is created, but before any requests are processed using
     * {@link #intercept(com.opensymphony.xwork2.ActionInvocation) intercept} , giving
     * the Interceptor a chance to initialize any needed resources.
     */
    void init();

    /**
     * Allows the Interceptor to do some processing on the request before and/or after the rest of the processing of the
     * request by the {@link ActionInvocation} or to short-circuit the processing and just return a String return code.
     *
     * @param invocation the action invocation
     * @return the return code, either returned from {@link ActionInvocation#invoke()}, or from the interceptor itself.
     * @throws Exception any system-level error, as defined in {@link com.opensymphony.xwork2.Action#execute()}.
     */
    String intercept(ActionInvocation invocation) throws Exception;

}
