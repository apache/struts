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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.opensymphony.xwork2.security.DefaultAcceptedPatternsChecker;
import com.opensymphony.xwork2.security.DefaultExcludedPatternsChecker;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.easymock.MockControl;
import org.springframework.mock.web.MockHttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;

public class CookieInterceptorTest extends StrutsInternalTestCase {


    public void testIntercepDefault() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("cookie1", "cookie1value"),
                new Cookie("cookie2", "cookie2value"),
                new Cookie("cookie3", "cookie3value")
        );
        ServletActionContext.setRequest(request);

        MockActionWithCookieAware action = new MockActionWithCookieAware();

        ActionContext.getContext().getValueStack().push(action);

        MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
        ActionInvocation invocation = (ActionInvocation) actionInvocationControl.getMock();
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.getAction(), action);
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.invoke(), Action.SUCCESS);

        actionInvocationControl.replay();

        // by default the interceptor doesn't accept any cookies
        CookieInterceptor interceptor = new CookieInterceptor();
        interceptor.setExcludedPatternsChecker(new DefaultExcludedPatternsChecker());
        interceptor.setAcceptedPatternsChecker(new DefaultAcceptedPatternsChecker());

        interceptor.intercept(invocation);

        assertTrue(action.getCookiesMap().isEmpty());
        assertNull(action.getCookie1(), null);
        assertNull(action.getCookie2(), null);
        assertNull(action.getCookie3(), null);
        assertNull(ActionContext.getContext().getValueStack().findValue("cookie1"));
        assertNull(ActionContext.getContext().getValueStack().findValue("cookie2"));
        assertNull(ActionContext.getContext().getValueStack().findValue("cookie3"));
    }

    public void testInterceptAll1() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("cookie1", "cookie1value"),
                new Cookie("cookie2", "cookie2value"),
                new Cookie("cookie3", "cookie3value")
        );
        ServletActionContext.setRequest(request);

        MockActionWithCookieAware action = new MockActionWithCookieAware();

        ActionContext.getContext().getValueStack().push(action);

        MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
        ActionInvocation invocation = (ActionInvocation) actionInvocationControl.getMock();
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.getAction(), action);
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.invoke(), Action.SUCCESS);

        actionInvocationControl.replay();

        CookieInterceptor interceptor = new CookieInterceptor();
        interceptor.setExcludedPatternsChecker(new DefaultExcludedPatternsChecker());
        interceptor.setAcceptedPatternsChecker(new DefaultAcceptedPatternsChecker());
        interceptor.setCookiesName("*");
        interceptor.setCookiesValue("*");
        interceptor.intercept(invocation);

        assertFalse(action.getCookiesMap().isEmpty());
        assertEquals(action.getCookiesMap().size(), 3);
        assertEquals(action.getCookiesMap().get("cookie1"), "cookie1value");
        assertEquals(action.getCookiesMap().get("cookie2"), "cookie2value");
        assertEquals(action.getCookiesMap().get("cookie3"), "cookie3value");
        assertEquals(action.getCookie1(), "cookie1value");
        assertEquals(action.getCookie2(), "cookie2value");
        assertEquals(action.getCookie3(), "cookie3value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie1"), "cookie1value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie2"), "cookie2value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie3"), "cookie3value");
    }


    public void testInterceptAll2() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("cookie1", "cookie1value"),
                new Cookie("cookie2", "cookie2value"),
                new Cookie("cookie3", "cookie3value")
        );
        ServletActionContext.setRequest(request);

        MockActionWithCookieAware action = new MockActionWithCookieAware();

        ActionContext.getContext().getValueStack().push(action);

        MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
        ActionInvocation invocation = (ActionInvocation) actionInvocationControl.getMock();
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.getAction(), action);
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.invoke(), Action.SUCCESS);

        actionInvocationControl.replay();

        CookieInterceptor interceptor = new CookieInterceptor();
        interceptor.setExcludedPatternsChecker(new DefaultExcludedPatternsChecker());
        interceptor.setAcceptedPatternsChecker(new DefaultAcceptedPatternsChecker());
        interceptor.setCookiesName("cookie1, cookie2, cookie3");
        interceptor.setCookiesValue("cookie1value, cookie2value, cookie3value");
        interceptor.intercept(invocation);

        assertFalse(action.getCookiesMap().isEmpty());
        assertEquals(action.getCookiesMap().size(), 3);
        assertEquals(action.getCookiesMap().get("cookie1"), "cookie1value");
        assertEquals(action.getCookiesMap().get("cookie2"), "cookie2value");
        assertEquals(action.getCookiesMap().get("cookie3"), "cookie3value");
        assertEquals(action.getCookie1(), "cookie1value");
        assertEquals(action.getCookie2(), "cookie2value");
        assertEquals(action.getCookie3(), "cookie3value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie1"), "cookie1value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie2"), "cookie2value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie3"), "cookie3value");
    }

    public void testInterceptSelectedCookiesNameOnly1() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("cookie1", "cookie1value"),
                new Cookie("cookie2", "cookie2value"),
                new Cookie("cookie3", "cookie3value")
        );
        ServletActionContext.setRequest(request);

        MockActionWithCookieAware action = new MockActionWithCookieAware();

        ActionContext.getContext().getValueStack().push(action);

        MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
        ActionInvocation invocation = (ActionInvocation) actionInvocationControl.getMock();
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.getAction(), action);
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.invoke(), Action.SUCCESS);

        actionInvocationControl.replay();

        CookieInterceptor interceptor = new CookieInterceptor();
        interceptor.setExcludedPatternsChecker(new DefaultExcludedPatternsChecker());
        interceptor.setAcceptedPatternsChecker(new DefaultAcceptedPatternsChecker());
        interceptor.setCookiesName("cookie1, cookie3");
        interceptor.setCookiesValue("cookie1value, cookie2value, cookie3value");
        interceptor.intercept(invocation);

        assertFalse(action.getCookiesMap().isEmpty());
        assertEquals(action.getCookiesMap().size(), 2);
        assertEquals(action.getCookiesMap().get("cookie1"), "cookie1value");
        assertEquals(action.getCookiesMap().get("cookie2"), null);
        assertEquals(action.getCookiesMap().get("cookie3"), "cookie3value");
        assertEquals(action.getCookie1(), "cookie1value");
        assertEquals(action.getCookie2(), null);
        assertEquals(action.getCookie3(), "cookie3value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie1"), "cookie1value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie2"), null);
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie3"), "cookie3value");
    }

    public void testInterceptSelectedCookiesNameOnly2() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("cookie1", "cookie1value"),
                new Cookie("cookie2", "cookie2value"),
                new Cookie("cookie3", "cookie3value")
        );
        ServletActionContext.setRequest(request);

        MockActionWithCookieAware action = new MockActionWithCookieAware();

        ActionContext.getContext().getValueStack().push(action);

        MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
        ActionInvocation invocation = (ActionInvocation) actionInvocationControl.getMock();
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.getAction(), action);
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.invoke(), Action.SUCCESS);

        actionInvocationControl.replay();

        CookieInterceptor interceptor = new CookieInterceptor();
        interceptor.setExcludedPatternsChecker(new DefaultExcludedPatternsChecker());
        interceptor.setAcceptedPatternsChecker(new DefaultAcceptedPatternsChecker());
        interceptor.setCookiesName("cookie1, cookie3");
        interceptor.setCookiesValue("*");
        interceptor.intercept(invocation);

        assertFalse(action.getCookiesMap().isEmpty());
        assertEquals(action.getCookiesMap().size(), 2);
        assertEquals(action.getCookiesMap().get("cookie1"), "cookie1value");
        assertEquals(action.getCookiesMap().get("cookie2"), null);
        assertEquals(action.getCookiesMap().get("cookie3"), "cookie3value");
        assertEquals(action.getCookie1(), "cookie1value");
        assertEquals(action.getCookie2(), null);
        assertEquals(action.getCookie3(), "cookie3value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie1"), "cookie1value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie2"), null);
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie3"), "cookie3value");
    }

    public void testInterceptSelectedCookiesNameOnly3() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("cookie1", "cookie1value"),
                new Cookie("cookie2", "cookie2value"),
                new Cookie("cookie3", "cookie3value")
        );
        ServletActionContext.setRequest(request);

        MockActionWithCookieAware action = new MockActionWithCookieAware();

        ActionContext.getContext().getValueStack().push(action);

        MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
        ActionInvocation invocation = (ActionInvocation) actionInvocationControl.getMock();
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.getAction(), action);
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.invoke(), Action.SUCCESS);

        actionInvocationControl.replay();

        CookieInterceptor interceptor = new CookieInterceptor();
        interceptor.setExcludedPatternsChecker(new DefaultExcludedPatternsChecker());
        interceptor.setAcceptedPatternsChecker(new DefaultAcceptedPatternsChecker());
        interceptor.setCookiesName("cookie1, cookie3");
        interceptor.setCookiesValue("");
        interceptor.intercept(invocation);

        assertFalse(action.getCookiesMap().isEmpty());
        assertEquals(action.getCookiesMap().size(), 2);
        assertEquals(action.getCookiesMap().get("cookie1"), "cookie1value");
        assertEquals(action.getCookiesMap().get("cookie2"), null);
        assertEquals(action.getCookiesMap().get("cookie3"), "cookie3value");
        assertEquals(action.getCookie1(), "cookie1value");
        assertEquals(action.getCookie2(), null);
        assertEquals(action.getCookie3(), "cookie3value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie1"), "cookie1value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie2"), null);
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie3"), "cookie3value");
    }


    public void testInterceptSelectedCookiesNameAndValue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("cookie1", "cookie1value"),
                new Cookie("cookie2", "cookie2value"),
                new Cookie("cookie3", "cookie3value")
        );
        ServletActionContext.setRequest(request);

        MockActionWithCookieAware action = new MockActionWithCookieAware();

        ActionContext.getContext().getValueStack().push(action);

        MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
        ActionInvocation invocation = (ActionInvocation) actionInvocationControl.getMock();
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.getAction(), action);
        actionInvocationControl.expectAndDefaultReturn(
                                                       invocation.invoke(), Action.SUCCESS);

        actionInvocationControl.replay();

        CookieInterceptor interceptor = new CookieInterceptor();
        interceptor.setExcludedPatternsChecker(new DefaultExcludedPatternsChecker());
        interceptor.setAcceptedPatternsChecker(new DefaultAcceptedPatternsChecker());
        interceptor.setCookiesName("cookie1, cookie3");
        interceptor.setCookiesValue("cookie1value");
        interceptor.intercept(invocation);

        assertFalse(action.getCookiesMap().isEmpty());
        assertEquals(action.getCookiesMap().size(), 1);
        assertEquals(action.getCookiesMap().get("cookie1"), "cookie1value");
        assertEquals(action.getCookiesMap().get("cookie2"), null);
        assertEquals(action.getCookiesMap().get("cookie3"), null);
        assertEquals(action.getCookie1(), "cookie1value");
        assertEquals(action.getCookie2(), null);
        assertEquals(action.getCookie3(), null);
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie1"), "cookie1value");
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie2"), null);
        assertEquals(ActionContext.getContext().getValueStack().findValue("cookie3"), null);
    }

    public void testCookiesWithClassPollution() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String pollution1 = "model['class']['classLoader']['jarPath']";
        String pollution2 = "model.class.classLoader.jarPath";
        String pollution3 = "class.classLoader.jarPath";
        String pollution4 = "class['classLoader']['jarPath']";
        String pollution5 = "model[\"class\"]['classLoader']['jarPath']";
        String pollution6 = "class[\"classLoader\"]['jarPath']";

        request.setCookies(
                new Cookie(pollution1, "pollution1"),
                new Cookie("pollution1", pollution1),
                new Cookie(pollution2, "pollution2"),
                new Cookie("pollution2", pollution2),
                new Cookie(pollution3, "pollution3"),
                new Cookie("pollution3", pollution3),
                new Cookie(pollution4, "pollution4"),
                new Cookie("pollution4", pollution4),
                new Cookie(pollution5, "pollution5"),
                new Cookie("pollution5", pollution5),
                new Cookie(pollution6, "pollution6"),
                new Cookie("pollution6", pollution6)
            );
        ServletActionContext.setRequest(request);

        final Map<String, Boolean> excludedName = new HashMap<String, Boolean>();

        CookieInterceptor interceptor = new CookieInterceptor() {
            @Override
            protected boolean isAcceptableName(String name) {
                boolean accepted = super.isAcceptableName(name);
                excludedName.put(name, accepted);
                return accepted;
            }
        };
        DefaultExcludedPatternsChecker excludedPatternsChecker = new DefaultExcludedPatternsChecker();
        excludedPatternsChecker.setAdditionalExcludePatterns(".*(^|\\.|\\[|'|\")class(\\.|\\[|'|\").*");
        interceptor.setExcludedPatternsChecker(excludedPatternsChecker);
        interceptor.setAcceptedPatternsChecker(new DefaultAcceptedPatternsChecker());
        interceptor.setCookiesName("*");

        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(new MockActionWithCookieAware());

        interceptor.intercept(invocation);

        assertFalse(excludedName.get(pollution1));
        assertFalse(excludedName.get(pollution2));
        assertFalse(excludedName.get(pollution3));
        assertFalse(excludedName.get(pollution4));
        assertFalse(excludedName.get(pollution5));
        assertFalse(excludedName.get(pollution6));
    }

    public void testCookiesWithStrutsInternalsAccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String sessionCookieName = "session.userId";
        String sessionCookieValue = "session.userId=1";
        String appCookieName = "application.userId";
        String appCookieValue = "application.userId=1";
        String reqCookieName = "request.userId";
        String reqCookieValue = "request.userId=1";

        request.setCookies(
                new Cookie(sessionCookieName, "1"),
                new Cookie("1", sessionCookieValue),
                new Cookie(appCookieName, "1"),
                new Cookie("1", appCookieValue),
                new Cookie(reqCookieName, "1"),
                new Cookie("1", reqCookieValue)
            );
        ServletActionContext.setRequest(request);

        final Map<String, Boolean> excludedName = new HashMap<String, Boolean>();

        CookieInterceptor interceptor = new CookieInterceptor() {
            @Override
            protected boolean isAcceptableName(String name) {
                boolean accepted = super.isAcceptableName(name);
                excludedName.put(name, accepted);
                return accepted;
            }
        };
        interceptor.setExcludedPatternsChecker(new DefaultExcludedPatternsChecker());
        interceptor.setAcceptedPatternsChecker(new DefaultAcceptedPatternsChecker());
        interceptor.setCookiesName("*");

        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(new MockActionWithCookieAware());

        interceptor.intercept(invocation);

        assertFalse(excludedName.get(sessionCookieName));
        assertFalse(excludedName.get(appCookieName));
        assertFalse(excludedName.get(reqCookieName));
    }

    public static class MockActionWithCookieAware extends ActionSupport implements CookiesAware {

        private static final long serialVersionUID = -6202290616812813386L;

        private Map cookies = Collections.EMPTY_MAP;
        private String cookie1;
        private String cookie2;
        private String cookie3;

        public void setCookiesMap(Map<String, String> cookies) {
            this.cookies = cookies;
        }

        public Map getCookiesMap() {
            return this.cookies;
        }

        public String getCookie1() { return cookie1; }
        public void setCookie1(String cookie1) { this.cookie1 = cookie1; }

        public String getCookie2() { return cookie2; }
        public void setCookie2(String cookie2) { this.cookie2 = cookie2; }

        public String getCookie3() { return cookie3; }
        public void setCookie3(String cookie3) { this.cookie3 = cookie3; }
    }

}
