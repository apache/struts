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

import org.apache.struts2.action.Action;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.ognl.StrutsContext;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.util.TokenHelper;
import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;
import org.apache.struts2.views.jsp.StrutsMockHttpSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.TreeMap;


/**
 * TokenInterceptorTest
 */
public class TokenInterceptorTest extends StrutsInternalTestCase {

    protected ActionContext oldContext;
    protected HttpSession httpSession;
    protected StrutsContext extraContext;
    protected Map<String, Object> params;
    protected Map<String, Object> session;
    protected StrutsMockHttpServletRequest request;

    public void testNoTokenInParams() throws Exception {
        ActionProxy proxy = buildProxy(getActionName());
        assertEquals(TokenInterceptor.INVALID_TOKEN_CODE, proxy.execute());
    }

    public void testNoTokenInSession() throws Exception {
        assertEquals(oldContext, ActionContext.getContext());

        ActionProxy proxy = buildProxy(getActionName());
        setToken(request);
        ActionContext.getContext().getSession().clear();
        assertEquals(TokenInterceptor.INVALID_TOKEN_CODE, proxy.execute());
    }

    public void testTokenInterceptorSuccess() throws Exception {
        setToken(request);
        ActionProxy proxy = buildProxy(getActionName());
        assertEquals(Action.SUCCESS, proxy.execute());
    }

    public void testCAllExecute2Times() throws Exception {
        setToken(request);
        ActionProxy proxy = buildProxy(getActionName());
        assertEquals(Action.SUCCESS, proxy.execute());

        ActionProxy proxy2 = buildProxy(getActionName());
        // must not call setToken
        // double post will result in a invalid.token return code
        assertEquals(TokenInterceptor.INVALID_TOKEN_CODE, proxy2.execute());
    }

    protected String getActionName() {
        return TestConfigurationProvider.TOKEN_ACTION_NAME;
    }

    protected String setToken(HttpServletRequest request) {
        String token = TokenHelper.setToken();
        setToken(token);

        return token;
    }

    protected void setToken(String token) {
        request.getParameterMap().put(TokenHelper.TOKEN_NAME_FIELD, new String[]{
            TokenHelper.DEFAULT_TOKEN_NAME
        });
        request.getParameterMap().put(TokenHelper.DEFAULT_TOKEN_NAME, new String[]{
            token
        });
        ActionContext.of(extraContext).withParameters(HttpParameters.create(params).build());
    }

    protected void setUp() throws Exception {
        super.setUp();

        loadConfigurationProviders(new TestConfigurationProvider());

        session = new TreeMap<>();
        params = new TreeMap<>();
        extraContext = StrutsContext.empty();
        extraContext = ActionContext.of(extraContext)
            .withSession(session)
            .withParameters(HttpParameters.create().build())
            .getStrutsContext();

        request = new StrutsMockHttpServletRequest();
        httpSession = new StrutsMockHttpSession();
        request.setSession(httpSession);
        request.setParameterMap(params);
        extraContext.put(ServletActionContext.HTTP_REQUEST, request);

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.getContext().putAll(extraContext);
        oldContext = ActionContext.of(stack.getContext()).bind();
    }

    protected ActionProxy buildProxy(String actionName) {
        return actionProxyFactory.createActionProxy("", actionName, null, extraContext);
    }
}
