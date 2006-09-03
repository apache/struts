// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

public class RequestContextTest extends TestCase {

    public void testFindCookiesForName() {
        final HttpServletRequest servletRequest = createMock(HttpServletRequest.class);
        Cookie one = new Cookie("foo", "one");
        Cookie two = new Cookie("foo", "two");
        Cookie three = new Cookie("bar", "three");
        Cookie[] cookies = { one, two, three };
        expect(servletRequest.getCookies()).andReturn(cookies);

        replay(servletRequest);

        RequestContextImpl requestContext = new RequestContextImpl(null) {
            public HttpServletRequest getServletRequest() {
                return servletRequest;
            }
        };

        List<Cookie> fooCookies = Arrays.asList(one, two);
        assertEquals(fooCookies, requestContext.findCookiesForName("foo"));
    }

    public void testInitialCallInContext() throws Exception {
        final ActionInvocation invocation = createMock(ActionInvocation.class);
        final ActionContext actionContext = new ActionContext(new HashMap());
        expect(invocation.getInvocationContext()).andReturn(actionContext);

        final boolean[] called = new boolean[1];
        Callable<String> callable = new Callable<String>() {
            public String call() throws Exception {
                RequestContextImpl requestContext = RequestContextImpl.get();
                assertSame(actionContext, requestContext.xworkContext);
                assertSame(invocation,
                        ((ActionContextImpl) requestContext.getActionContext()).invocation);
                called[0] = true;
                return "foo";
            }
        };

        replay(invocation);

        assertEquals("foo", RequestContextImpl.callInContext(invocation, callable));
        assertTrue(called[0]);
        assertNull(RequestContextImpl.threadLocalRequestContext.get()[0]);
    }

    public void testNestedCallInContext() throws Exception {
        // TODO: After we implement ActionContext.getNext(), getPrevious().
    }
}

