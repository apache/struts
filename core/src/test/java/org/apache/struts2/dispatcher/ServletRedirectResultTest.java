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

package org.apache.struts2.dispatcher;

import static javax.servlet.http.HttpServletResponse.SC_SEE_OTHER;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ognl.Ognl;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.views.util.DefaultUrlHelper;
import org.easymock.IMocksControl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;


/**
 */
public class ServletRedirectResultTest extends StrutsInternalTestCase implements StrutsStatics {

    protected ServletRedirectResult view;
    private Mock requestMock;
    private Mock responseMock;
    protected ActionInvocation ai;


    public void testAbsoluteRedirect() {
        view.setLocation("/bar/foo.jsp");
        responseMock.expectAndReturn("encodeRedirectURL", "/context/bar/foo.jsp", "/context/bar/foo.jsp");
        responseMock.expect("sendRedirect", C.args(C.eq("/context/bar/foo.jsp")));

        try {
            view.execute(ai);
            requestMock.verify();
            responseMock.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testFullUrlRedirect() {
        view.setLocation("http://localhost/bar/foo.jsp");
        responseMock.expectAndReturn("encodeRedirectURL", C.args(C.eq("http://localhost/bar/foo.jsp")), "http://localhost/bar/foo.jsp");
        responseMock.expect("sendRedirect", C.args(C.eq("http://localhost/bar/foo.jsp")));

        try {
            view.execute(ai);
            requestMock.verify();
            responseMock.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testFullUrlRedirectWithSpaces() {
        view.setLocation("http://localhost/bar/foo some.pdf");
        responseMock.expectAndReturn("encodeRedirectURL", C.args(C.eq("http://localhost/bar/foo some.pdf")), "http://localhost/bar/foo some.pdf");
        responseMock.expect("sendRedirect", C.args(C.eq("http://localhost/bar/foo some.pdf")));

        try {
            view.execute(ai);
            requestMock.verify();
            responseMock.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testFullUrlRedirectWithParams() {
        view.setLocation("http://localhost/bar/foo.action?param=1&param 2=3");
        responseMock.expectAndReturn("encodeRedirectURL", C.args(C.eq("http://localhost/bar/foo.action?param=1&param 2=3")), "http://localhost/bar/foo.action?param=1&param 2=3");
        responseMock.expect("sendRedirect", C.args(C.eq("http://localhost/bar/foo.action?param=1&param 2=3")));

        try {
            view.execute(ai);
            requestMock.verify();
            responseMock.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testAbsoluteRedirect303() {
        view.setLocation("/bar/foo.jsp");
        view.setStatusCode(303);
        responseMock.expectAndReturn("encodeRedirectURL", "/context/bar/foo.jsp", "/context/bar/foo.jsp");
        responseMock.expect("setStatus", C.args(C.eq(SC_SEE_OTHER)));
        responseMock.expect("setHeader", C.args(C.eq("Location"), C.eq("/context/bar/foo.jsp")));
        StringWriter writer = new StringWriter();
        responseMock.matchAndReturn("getWriter", new PrintWriter(writer));

        try {
            view.execute(ai);
            requestMock.verify();
            responseMock.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("/context/bar/foo.jsp", writer.toString());
    }

    public void testAbsoluteRedirectAnchor() {
        view.setLocation("/bar/foo.jsp");
        view.setAnchor("fragment");
        responseMock.expectAndReturn("encodeRedirectURL", "/context/bar/foo.jsp#fragment", "/context/bar/foo.jsp#fragment");
        responseMock.expect("sendRedirect", C.args(C.eq("/context/bar/foo.jsp#fragment")));

        try {
            view.execute(ai);
            requestMock.verify();
            responseMock.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    public void testPrependServletContextFalse() {
        view.setLocation("/bar/foo.jsp");
        view.setPrependServletContext(false);
        responseMock.expectAndReturn("encodeRedirectURL", "/bar/foo.jsp", "/bar/foo.jsp");
        responseMock.expect("sendRedirect", C.args(C.eq("/bar/foo.jsp")));

        try {
            view.execute(ai);
            requestMock.verify();
            responseMock.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testRelativeRedirect() throws Exception {
        view.setLocation("foo.jsp");
        requestMock.expectAndReturn("getParameterMap", new HashMap());
        requestMock.expectAndReturn("getServletPath", "/namespace/some.action");
        requestMock.expectAndReturn("getRequestURI", "/namespace/some.action");
        requestMock.expectAndReturn("getAttribute", C.ANY_ARGS, null);
        responseMock.expectAndReturn("encodeRedirectURL", "/context/namespace/foo.jsp", "/context/namespace/foo.jsp");
        responseMock.expect("sendRedirect", C.args(C.eq("/context/namespace/foo.jsp")));

        view.execute(ai);

        requestMock.verify();
        responseMock.verify();
    }
    
    public void testMultipleParametersRedirect() throws Exception {
        view.setLocation("foo.jsp?foo=bar&amp;baz=jim");
        requestMock.expectAndReturn("getParameterMap", new HashMap());
        requestMock.expectAndReturn("getServletPath", "/namespace/some.action");
        requestMock.expectAndReturn("getRequestURI", "/namespace/some.action");
        requestMock.expectAndReturn("getAttribute", C.ANY_ARGS, null);
        responseMock.expectAndReturn("encodeRedirectURL", "/context/namespace/foo.jsp?foo=bar&amp;baz=jim", "/context/namespace/foo.jsp?foo=bar&baz=jim");
        responseMock.expect("sendRedirect", C.args(C.eq("/context/namespace/foo.jsp?foo=bar&baz=jim")));

        view.execute(ai);

        requestMock.verify();
        responseMock.verify();
    }

    public void testIncludeParameterInResult() throws Exception {

        ResultConfig resultConfig = new ResultConfig.Builder("", "")
            .addParam("namespace", "someNamespace")
            .addParam("encode", "true")
            .addParam("parse", "true")
            .addParam("location", "someLocation")
            .addParam("prependServletContext", "true")
            .addParam("method", "someMethod")
            .addParam("statusCode", "333")
            .addParam("param1", "value 1")
            .addParam("param2", "value 2")
            .addParam("param3", "value 3")
            .build();

        ActionContext context = ActionContext.getContext();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        context.put(ServletActionContext.HTTP_REQUEST, req);
        context.put(ServletActionContext.HTTP_RESPONSE, res);


        Map<String, ResultConfig> results=  new HashMap<String, ResultConfig>();
        results.put("myResult", resultConfig);

        ActionConfig actionConfig = new ActionConfig.Builder("", "", "")
                .addResultConfigs(results).build();

        ServletRedirectResult result = new ServletRedirectResult();
        result.setLocation("/myNamespace/myAction.action");
        result.setParse(false);
        result.setEncode(false);
        result.setPrependServletContext(false);
        result.setAnchor("fragment");
        result.setUrlHelper(new DefaultUrlHelper());

        IMocksControl control = createControl();
        ActionProxy mockActionProxy = control.createMock(ActionProxy.class);
        ActionInvocation mockInvocation = control.createMock(ActionInvocation.class);
        expect(mockInvocation.getProxy()).andReturn(mockActionProxy);
        expect(mockInvocation.getResultCode()).andReturn("myResult");
        expect(mockActionProxy.getConfig()).andReturn(actionConfig);
        expect(mockInvocation.getInvocationContext()).andReturn(context);

        control.replay();
        result.setActionMapper(container.getInstance(ActionMapper.class));
        result.execute(mockInvocation);
        assertEquals("/myNamespace/myAction.action?param1=value+1&param2=value+2&param3=value+3#fragment", res.getRedirectedUrl());
        control.verify();
    }

    public void testIncludeCollectionParameterInResult() throws Exception {
        List<String> paramValues = new ArrayList<String>();
        paramValues.add("value 1");
        paramValues.add("");
        paramValues.add("value 2");
        paramValues.add(null);

        ResultConfig resultConfig = new ResultConfig.Builder("", "")
            .addParam("namespace", "someNamespace")
            .addParam("param", "${list}")
            .build();

        ActionContext context = ActionContext.getContext();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        context.put(ServletActionContext.HTTP_REQUEST, req);
        context.put(ServletActionContext.HTTP_RESPONSE, res);

        Map<String, ResultConfig> results=  new HashMap<String, ResultConfig>();
        results.put("myResult", resultConfig);

        ActionConfig actionConfig = new ActionConfig.Builder("", "", "")
                .addResultConfigs(results).build();

        ServletRedirectResult result = new ServletRedirectResult();
        result.setLocation("/myNamespace/myAction.action");
        result.setParse(true);
        result.setEncode(false);
        result.setPrependServletContext(false);
        result.setUrlHelper(new DefaultUrlHelper());
        result.setSuppressEmptyParameters(true);

        IMocksControl control = createControl();
        ActionProxy mockActionProxy = control.createMock(ActionProxy.class);
        ActionInvocation mockInvocation = control.createMock(ActionInvocation.class);

        ValueStack mockValueStack = control.createMock(ValueStack.class);
        Map<String, Object> mockContext = new HashMap<String, Object>();
        mockContext.put(ActionContext.CONTAINER, container);

        expect(mockInvocation.getStack()).andReturn(mockValueStack);
        expect(mockValueStack.getContext()).andReturn(mockContext);

        expect(mockInvocation.getStack()).andReturn(mockValueStack);

        expect(mockValueStack.findValue("list")).andReturn(paramValues); // no asType !!!

        expect(mockInvocation.getProxy()).andReturn(mockActionProxy);
        expect(mockInvocation.getResultCode()).andReturn("myResult");
        expect(mockActionProxy.getConfig()).andReturn(actionConfig);
        expect(mockInvocation.getInvocationContext()).andReturn(context);

        expect(mockValueStack.getContext()).andReturn(mockContext);

        control.replay();
        result.setActionMapper(container.getInstance(ActionMapper.class));
        result.execute(mockInvocation);
        assertEquals("/myNamespace/myAction.action?param=value+1&param=value+2", res.getRedirectedUrl());
        control.verify();
    }

    protected void setUp() throws Exception {
        super.setUp();
        configurationManager.getConfiguration().
            addPackageConfig("foo", new PackageConfig.Builder("foo").namespace("/namespace").build());

        view = new ServletRedirectResult();
        container.inject(view);

        responseMock = new Mock(HttpServletResponse.class);

        requestMock = new Mock(HttpServletRequest.class);
        requestMock.matchAndReturn("getContextPath", "/context");

         ResultConfig resultConfig = new ResultConfig.Builder("", "").build();

        Map<String, ResultConfig> results=  new HashMap<String, ResultConfig>();
        results.put("myResult", resultConfig);

        ActionConfig actionConfig = new ActionConfig.Builder("", "", "")
                .addResultConfigs(results).build();

        ActionContext ac = new ActionContext(Ognl.createDefaultContext(null));
        ac.put(ServletActionContext.HTTP_REQUEST, requestMock.proxy());
        ac.put(ServletActionContext.HTTP_RESPONSE, responseMock.proxy());
        MockActionInvocation ai = new MockActionInvocation();
        ai.setInvocationContext(ac);
        ai.setResultCode("myResult");
        ActionProxy mockActionProxy = createNiceMock(ActionProxy.class);
        ai.setProxy(mockActionProxy);
        expect(mockActionProxy.getConfig()).andReturn(actionConfig).anyTimes();
        replay(mockActionProxy);
        this.ai = ai;
        ai.setStack(ActionContext.getContext().getValueStack());
    }
}
