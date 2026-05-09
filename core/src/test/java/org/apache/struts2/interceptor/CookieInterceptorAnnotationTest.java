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

import jakarta.servlet.http.Cookie;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.action.Action;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizer;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.apache.struts2.interceptor.parameter.StrutsParameterAuthorizer;
import org.apache.struts2.mock.MockActionInvocation;
import org.springframework.mock.web.MockHttpServletRequest;

public class CookieInterceptorAnnotationTest extends StrutsInternalTestCase {

    private CookieInterceptor interceptor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        interceptor = container.inject(CookieInterceptor.class);
        interceptor.setCookiesName("*");
    }

    @Override
    protected void tearDown() throws Exception {
        // Reset shared singleton state — flags flipped on the container's StrutsParameterAuthorizer
        // would otherwise leak across tests in the same JVM run.
        configureRequireAnnotations(false, false);
        super.tearDown();
    }

    public void testRequireAnnotations_unannotatedSetter_isSkipped() throws Exception {
        configureRequireAnnotations(true, false);
        AnnotatedAction action = new AnnotatedAction();
        invokeWithCookies(action, new Cookie("unannotated", "v"));

        assertNull("unannotated setter must not be populated", action.getUnannotated());
        assertNull(ActionContext.getContext().getValueStack().findValue("unannotated"));
    }

    private void configureRequireAnnotations(boolean require, boolean transitionMode) {
        StrutsParameterAuthorizer authorizer = (StrutsParameterAuthorizer) container.getInstance(ParameterAuthorizer.class);
        authorizer.setRequireAnnotations(Boolean.toString(require));
        authorizer.setRequireAnnotationsTransitionMode(Boolean.toString(transitionMode));
    }

    private void invokeWithCookies(Object action, Cookie... cookies) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookies);
        ServletActionContext.setRequest(request);
        ActionContext.getContext().getValueStack().push(action);

        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        invocation.setInvocationContext(ActionContext.getContext());
        invocation.setResultCode(Action.SUCCESS);

        interceptor.intercept(invocation);
    }

    public static class AnnotatedAction extends ActionSupport {
        private String annotated;
        private String unannotated;

        @StrutsParameter
        public void setAnnotated(String v) { this.annotated = v; }
        public String getAnnotated() { return annotated; }

        public void setUnannotated(String v) { this.unannotated = v; }
        public String getUnannotated() { return unannotated; }
    }
}
