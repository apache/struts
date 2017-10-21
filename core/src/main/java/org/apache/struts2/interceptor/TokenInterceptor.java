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
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.TokenHelper;

import javax.servlet.http.HttpSession;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * Ensures that only one request per token is processed. This interceptor can make sure that back buttons and double
 * clicks don't cause un-intended side affects. For example, you can use this to prevent careless users who might double
 * click on a "checkout" button at an online store. This interceptor uses a fairly primitive technique for when an
 * invalid token is found: it returns the result <b>invalid.token</b>, which can be mapped in your action configuration.
 * A more complex implementation, {@link TokenSessionStoreInterceptor}, can provide much better logic for when invalid
 * tokens are found.
 * </p>
 *
 * <p>
 * <b>Note:</b> To set a token in your form, you should use the <b>token tag</b>. This tag is required and must be used
 * in the forms that submit to actions protected by this interceptor. Any request that does not provide a token (using
 * the token tag) will be processed as a request with an invalid token.
 * </p>
 *
 * <p>
 * <b>Internationalization Note:</b> The following key could be used to internationalized the action errors generated
 * by this token interceptor
 * </p>
 *
 * <ul>
 *    <li>struts.messages.invalid.token</li>
 * </ul>
 *
 * <p>
 * <b>NOTE:</b> As this method extends off MethodFilterInterceptor, it is capable of
 * deciding if it is applicable only to selective methods in the action class. See
 * <code>MethodFilterInterceptor</code> for more info.
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
 * <li>None</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p><u>Extending the interceptor:</u></p>
 *
 * <!-- START SNIPPET: extending -->
 * <p>
 * While not very common for users to extend, this interceptor is extended by the {@link TokenSessionStoreInterceptor}.
 * The {@link #handleInvalidToken}  and {@link #handleValidToken} methods are protected and available for more
 * interesting logic, such as done with the token session interceptor.
 * </p>
 * <!-- END SNIPPET: extending -->
 *
 * <p><u>Example code:</u></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="token"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;-- In this case, myMethod of the action class will not
 *        get checked for invalidity of token --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="token"&gt;
 *        &lt;param name="excludeMethods"&gt;myMethod&lt;/param&gt;
 *     &lt;/interceptor-ref name="token"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see TokenSessionStoreInterceptor
 * @see TokenHelper
 */
public class TokenInterceptor extends MethodFilterInterceptor {

    private static final Logger LOG = LogManager.getLogger(TokenInterceptor.class);

    public static final String INVALID_TOKEN_CODE = "invalid.token";

    private static final String INVALID_TOKEN_MESSAGE_KEY = "struts.messages.invalid.token";
    private static final String DEFAULT_ERROR_MESSAGE = "The form has already been processed or no token was supplied, please try again.";

    private TextProvider textProvider;

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProvider = textProviderFactory.createInstance(getClass());
    }

    /**
     * @see com.opensymphony.xwork2.interceptor.MethodFilterInterceptor#doIntercept(com.opensymphony.xwork2.ActionInvocation)
     */
    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        LOG.debug("Intercepting invocation to check for valid transaction token.");
        return handleToken(invocation);
    }

    protected String handleToken(ActionInvocation invocation) throws Exception {
        //see WW-2902: we need to use the real HttpSession here, as opposed to the map
        //that wraps the session, because a new wrap is created on every request
        HttpSession session = ServletActionContext.getRequest().getSession(true);
        synchronized (session.getId().intern()) {
            if (!TokenHelper.validToken()) {
                return handleInvalidToken(invocation);
            }
        }
        return handleValidToken(invocation);
    }

    /**
     * Determines what to do if an invalid token is provided. If the action implements {@link ValidationAware}
     *
     * @param invocation the action invocation where the invalid token failed
     * @return the return code to indicate should be processed
     * @throws Exception when any unexpected error occurs.
     */
    protected String handleInvalidToken(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        String errorMessage = getErrorMessage(invocation);

        if (action instanceof ValidationAware) {
            ((ValidationAware) action).addActionError(errorMessage);
        } else {
            LOG.warn(errorMessage);
        }

        return INVALID_TOKEN_CODE;
    }

    protected String getErrorMessage(ActionInvocation invocation) {
        Object action = invocation.getAction();
        if (action instanceof TextProvider) {
            return ((TextProvider) action).getText(INVALID_TOKEN_MESSAGE_KEY, DEFAULT_ERROR_MESSAGE);
        }
        return textProvider.getText(INVALID_TOKEN_MESSAGE_KEY, DEFAULT_ERROR_MESSAGE);
    }

    /**
     * Called when a valid token is found. This method invokes the action by can be changed to do something more
     * interesting.
     *
     * @param invocation the action invocation
     * @return invocation result
     * @throws Exception when any unexpected error occurs.
     */
    protected String handleValidToken(ActionInvocation invocation) throws Exception {
        return invocation.invoke();
    }

}
