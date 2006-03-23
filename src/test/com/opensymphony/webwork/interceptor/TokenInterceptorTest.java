/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.interceptor;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.TestConfigurationProvider;
import com.opensymphony.webwork.util.TokenHelper;
import com.opensymphony.webwork.views.jsp.WebWorkMockHttpServletRequest;
import com.opensymphony.webwork.views.jsp.WebWorkMockHttpSession;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


/**
 * TokenInterceptorTest
 *
 * @author Jason Carreira
 *         Created Apr 9, 2003 11:42:01 PM
 */
public class TokenInterceptorTest extends TestCase {

    ActionContext oldContext;
    HttpSession httpSession;
    Map extraContext;
    Map params;
    Map session;
    WebWorkMockHttpServletRequest request;


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
        ActionProxy proxy = buildProxy(getActionName());
        setToken(request);
        assertEquals(Action.SUCCESS, proxy.execute());
    }

    public void testCAllExecute2Times() throws Exception {
        ActionProxy proxy = buildProxy(getActionName());
        setToken(request);
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
    }

    protected void setUp() throws Exception {
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new TestConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();

        session = new HashMap();
        params = new HashMap();
        extraContext = new HashMap();
        extraContext.put(ActionContext.SESSION, session);
        extraContext.put(ActionContext.PARAMETERS, params);

        request = new WebWorkMockHttpServletRequest();
        httpSession = new WebWorkMockHttpSession();
        request.setSession(httpSession);
        request.setParameterMap(params);
        extraContext.put(ServletActionContext.HTTP_REQUEST, request);

        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().putAll(extraContext);
        oldContext = new ActionContext(stack.getContext());
        ActionContext.setContext(oldContext);
    }

    protected ActionProxy buildProxy(String actionName) throws Exception {
        return ActionProxyFactory.getFactory().createActionProxy("", actionName, extraContext, true, true);
    }

    protected void tearDown() throws Exception {
        ConfigurationManager.destroyConfiguration();
        ActionContext.setContext(null);
    }
}
