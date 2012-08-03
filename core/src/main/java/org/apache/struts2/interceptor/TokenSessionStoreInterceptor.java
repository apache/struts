/*
 * $Id$
 *
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

package org.apache.struts2.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.InvocationSessionStore;
import org.apache.struts2.util.TokenHelper;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor builds off of the {@link TokenInterceptor}, providing advanced logic for handling invalid tokens.
 * Unlike the normal token interceptor, this interceptor will attempt to provide intelligent fail-over in the event of
 * multiple requests using the same session. That is, it will block subsequent requests until the first request is
 * complete, and then instead of returning the <i>invalid.token</i> code, it will attempt to display the same response
 * that the original, valid action invocation would have displayed if no multiple requests were submitted in the first
 * place.
 *
 * <p/>
 *
 * <b>NOTE:</b> As this method extends off MethodFilterInterceptor, it is capable of
 * deciding if it is applicable only to selective methods in the action class. See
 * <code>MethodFilterInterceptor</code> for more info.
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
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="tokenSession/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;-- In this case, myMethod of the action class will not
 *        get checked for invalidity of token --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="tokenSession&gt;
 *         &lt;param name="excludeMethods"&gt;myMethod&lt;/param&gt;
 *     &lt;/interceptor-ref name="tokenSession&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
public class TokenSessionStoreInterceptor extends TokenInterceptor {

    private static final long serialVersionUID = -9032347965469098195L;

    /* (non-Javadoc)
     * @see org.apache.struts2.interceptor.TokenInterceptor#handleInvalidToken(com.opensymphony.xwork2.ActionInvocation)
     */
    protected String handleInvalidToken(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();

        HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) ac.get(ServletActionContext.HTTP_RESPONSE);
        String tokenName = TokenHelper.getTokenName();
        String token = TokenHelper.getToken(tokenName);

        if ((tokenName != null) && (token != null)) {
            Map params = ac.getParameters();
            params.remove(tokenName);
            params.remove(TokenHelper.TOKEN_NAME_FIELD);

			String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(tokenName);
            ActionInvocation savedInvocation = InvocationSessionStore.loadInvocation(sessionTokenName, token);

            if (savedInvocation != null) {
                // set the valuestack to the request scope
                ValueStack stack = savedInvocation.getStack();
                Map context = stack.getContext();
                request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

                ActionContext savedContext = savedInvocation.getInvocationContext();
                savedContext.getContextMap().put(ServletActionContext.HTTP_REQUEST, request);
                savedContext.getContextMap().put(ServletActionContext.HTTP_RESPONSE, response);
                Result result = savedInvocation.getResult();

                if ((result != null) && (savedInvocation.getProxy().getExecuteResult())) {
                    synchronized (context) {
                        result.execute(savedInvocation);
                    }
                }

                // turn off execution of this invocations result
                invocation.getProxy().setExecuteResult(false);

                return savedInvocation.getResultCode();
            }
        }

        return INVALID_TOKEN_CODE;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.interceptor.TokenInterceptor#handleValidToken(com.opensymphony.xwork2.ActionInvocation)
     */
    protected String handleValidToken(ActionInvocation invocation) throws Exception {
        // we know the token name and token must be there
        String key = TokenHelper.getTokenName();
        String token = TokenHelper.getToken(key);
		String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(key);
		InvocationSessionStore.storeInvocation(sessionTokenName, token, invocation);

        return invocation.invoke();
    }
}
