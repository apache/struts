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

import com.opensymphony.xwork2.ActionInvocation;

import java.io.Serializable;

/**
 * <!-- START SNIPPET: introduction -->
 *
 * <p>
 * An interceptor is a stateless class that follows the interceptor pattern, as
 * found in {@link  javax.servlet.Filter} and in AOP languages.
 * </p>
 *
 * <p>
 * Interceptors are objects that dynamically intercept Action invocations.
 * They provide the developer with the opportunity to define code that can be executed
 * before and/or after the execution of an action. They also have the ability
 * to prevent an action from executing. Interceptors provide developers a way to
 * encapsulate common functionality in a re-usable form that can be applied to
 * one or more Actions.
 * </p>
 *
 * <p>
 * Interceptors <b>must</b> be stateless and not assume that a new instance will be created for each request or Action.
 * Interceptors may choose to either short-circuit the {@link ActionInvocation} execution and return a return code
 * (such as {@link com.opensymphony.xwork2.Action#SUCCESS}), or it may choose to do some processing before
 * and/or after delegating the rest of the procesing using {@link ActionInvocation#invoke()}.
 * </p>
 * <!-- END SNIPPET: introduction -->
 *
 * <!-- START SNIPPET: parameterOverriding -->
 * <p>
 * Interceptor's parameter could be overridden through the following ways :-
 * </p>

 * <b>Method 1:</b>
 * <pre>
 * &lt;action name=&quot;myAction&quot; class=&quot;myActionClass&quot;&gt;
 *     &lt;interceptor-ref name=&quot;exception&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;alias&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;params&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;servletConfig&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;prepare&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;i18n&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;chain&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;modelDriven&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;fileUpload&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;staticParams&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;params&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;conversionError&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;validation&quot;&gt;
 *     &lt;param name=&quot;excludeMethods&quot;&gt;myValidationExcudeMethod&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;interceptor-ref name=&quot;workflow&quot;&gt;
 *     &lt;param name=&quot;excludeMethods&quot;&gt;myWorkflowExcludeMethod&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 * &lt;/action&gt;
 * </pre>
 *
 * <b>Method 2:</b>
 * <pre>
 * &lt;action name=&quot;myAction&quot; class=&quot;myActionClass&quot;&gt;
 *   &lt;interceptor-ref name=&quot;defaultStack&quot;&gt;
 *     &lt;param name=&quot;validation.excludeMethods&quot;&gt;myValidationExcludeMethod&lt;/param&gt;
 *     &lt;param name=&quot;workflow.excludeMethods&quot;&gt;myWorkflowExcludeMethod&lt;/param&gt;
 *   &lt;/interceptor-ref&gt;
 * &lt;/action&gt;
 * </pre>
 *
 * <p>
 * In the first method, the whole default stack is copied and the parameter then
 * changed accordingly.
 * </p>
 *
 * <p>
 * In the second method, the 'interceptor-ref' refer to an existing
 * interceptor-stack, namely defaultStack in this example, and override the validator
 * and workflow interceptor excludeMethods typically in this case. Note that in the
 * 'param' tag, the name attribute contains a dot (.) the word before the dot(.)
 * specifies the interceptor name whose parameter is to be overridden and the word after
 * the dot (.) specifies the parameter itself. Essetially it is as follows :-
 * </p>
 *
 * <pre>
 *    &lt;interceptor-name&gt;.&lt;parameter-name&gt;
 * </pre>
 * <p>
 * <b>Note</b> also that in this case the 'interceptor-ref' name attribute
 * is used to indicate an interceptor stack which makes sense as if it is referring
 * to the interceptor itself it would be just using Method 1 describe above.
 * </p>
 * <!-- END SNIPPET: parameterOverriding -->
 *
 * <p>
 * <b>Nested Interceptor param overriding</b>
 * </p>
 *
 * <!-- START SNIPPET: nestedParameterOverriding -->
 * <p>
 * Interceptor stack parameter overriding could be nested into as many level as possible, though it would
 * be advisable not to nest it too deep as to avoid confusion, For example,
 * </p>
 * <pre>
 * &lt;interceptor name=&quot;interceptor1&quot; class=&quot;foo.bar.Interceptor1&quot; /&gt;
 * &lt;interceptor name=&quot;interceptor2&quot; class=&quot;foo.bar.Interceptor2&quot; /&gt;
 * &lt;interceptor name=&quot;interceptor3&quot; class=&quot;foo.bar.Interceptor3&quot; /&gt;
 * &lt;interceptor name=&quot;interceptor4&quot; class=&quot;foo.bar.Interceptor4&quot; /&gt;
 * &lt;interceptor-stack name=&quot;stack1&quot;&gt;
 *     &lt;interceptor-ref name=&quot;interceptor1&quot; /&gt;
 * &lt;/interceptor-stack&gt;
 * &lt;interceptor-stack name=&quot;stack2&quot;&gt;
 *     &lt;interceptor-ref name=&quot;intercetor2&quot; /&gt;
 *     &lt;interceptor-ref name=&quot;stack1&quot; /&gt;
 * &lt;/interceptor-stack&gt;
 * &lt;interceptor-stack name=&quot;stack3&quot;&gt;
 *     &lt;interceptor-ref name=&quot;interceptor3&quot; /&gt;
 *     &lt;interceptor-ref name=&quot;stack2&quot; /&gt;
 * &lt;/interceptor-stack&gt;
 * &lt;interceptor-stack name=&quot;stack4&quot;&gt;
 *     &lt;interceptor-ref name=&quot;interceptor4&quot; /&gt;
 *     &lt;interceptor-ref name=&quot;stack3&quot; /&gt;
 *  &lt;/interceptor-stack&gt;
 * </pre>
 *
 * <p>
 * Assuming the interceptor has the following properties
 * </p>
 *
 * <table border="1" summary="">
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
 *
 * <p>
 * We could override them as follows :
 * </p>
 *
 * <pre>
 *    &lt;action ... &gt;
 *        &lt;!-- to override parameters of interceptor located directly in the stack  --&gt;
 *        &lt;interceptor-ref name=&quot;stack4&quot;&gt;
 *           &lt;param name=&quot;interceptor4.param4&quot;&gt; ... &lt;/param&gt;
 *        &lt;/interceptor-ref&gt;
 *    &lt;/action&gt;
 *
 *    &lt;action ... &gt;
 *        &lt;!-- to override parameters of interceptor located under nested stack --&gt;
 *        &lt;interceptor-ref name=&quot;stack4&quot;&gt;
 *            &lt;param name=&quot;stack3.interceptor3.param3&quot;&gt; ... &lt;/param&gt;
 *            &lt;param name=&quot;stack3.stack2.interceptor2.param2&quot;&gt; ... &lt;/param&gt;
 *            &lt;param name=&quot;stack3.stack2.stack1.interceptor1.param1&quot;&gt; ... &lt;/param&gt;
 *        &lt;/interceptor-ref&gt;
 *    &lt;/action&gt;
 *  </pre>
 *
 * <!-- END SNIPPET: nestedParameterOverriding -->
 *
 * @author Jason Carreira
 * @author tmjee
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
