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
import org.apache.struts2.ModelDriven;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.action.Action;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizer;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.apache.struts2.interceptor.parameter.StrutsParameterAuthorizer;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.util.ValueStack;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    public void testRequireAnnotations_annotatedSetter_isInjected() throws Exception {
        configureRequireAnnotations(true, false);
        AnnotatedAction action = new AnnotatedAction();
        invokeWithCookies(action, new Cookie("annotated", "v"));

        assertEquals("v", action.getAnnotated());
        assertEquals("v", ActionContext.getContext().getValueStack().findValue("annotated"));
    }

    public void testRequireAnnotations_annotatedNestedPath_isInjected() throws Exception {
        configureRequireAnnotations(true, false);
        AnnotatedAction action = new AnnotatedAction();
        action.setNested(new NestedBean());
        invokeWithCookies(action, new Cookie("nested.field", "v"));

        assertEquals("v", action.getNested().getField());
    }

    public void testRequireAnnotations_unannotatedNestedPath_isSkipped() throws Exception {
        configureRequireAnnotations(true, false);
        AnnotatedAction action = new AnnotatedAction();
        action.setUnannotatedNested(new NestedBean());
        invokeWithCookies(action, new Cookie("unannotatedNested.field", "v"));

        assertNull(action.getUnannotatedNested().getField());
    }

    public void testRequireAnnotations_transitionMode_exemptsDepthZero() throws Exception {
        configureRequireAnnotations(true, true);
        AnnotatedAction action = new AnnotatedAction();
        invokeWithCookies(action, new Cookie("unannotated", "v"));

        assertEquals("v", action.getUnannotated());
    }

    public void testDefaultConfig_unannotatedSetter_stillInjected() throws Exception {
        configureRequireAnnotations(false, false);
        AnnotatedAction action = new AnnotatedAction();
        invokeWithCookies(action, new Cookie("unannotated", "v"));

        assertEquals("v", action.getUnannotated());
    }

    public void testRequireAnnotations_modelDriven_exemptsModel() throws Exception {
        configureRequireAnnotations(true, false);
        ModelDrivenAction action = new ModelDrivenAction();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("name", "v"));
        ServletActionContext.setRequest(request);
        // ModelDriven contract: the model is pushed on top of the action.
        ActionContext.getContext().getValueStack().push(action);
        ActionContext.getContext().getValueStack().push(action.getModel());

        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        invocation.setInvocationContext(ActionContext.getContext());
        invocation.setResultCode(Action.SUCCESS);

        interceptor.intercept(invocation);

        assertEquals("v", action.getModel().getName());
    }

    public void testSubclassOverridingDeprecatedHook_stillSeesAuthorizationGate() throws Exception {
        configureRequireAnnotations(true, false);
        AtomicInteger calls = new AtomicInteger();
        @SuppressWarnings("deprecation")
        CookieInterceptor subclass = new CookieInterceptor() {
            @Override
            protected void populateCookieValueIntoStack(String name, String value, Map<String, String> map, ValueStack stack) {
                calls.incrementAndGet();
                super.populateCookieValueIntoStack(name, value, map, stack);
            }
        };
        container.inject(subclass);
        subclass.setCookiesName("*");

        AnnotatedAction action = new AnnotatedAction();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("annotated", "ok"), new Cookie("unannotated", "blocked"));
        ServletActionContext.setRequest(req);
        ActionContext.getContext().getValueStack().push(action);

        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        invocation.setInvocationContext(ActionContext.getContext());
        invocation.setResultCode(Action.SUCCESS);
        subclass.intercept(invocation);

        assertEquals("ok", action.getAnnotated());
        assertNull(action.getUnannotated());
        assertEquals("4-arg hook should be invoked exactly once (only for the authorized cookie)", 1, calls.get());
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
        private NestedBean nested;
        private NestedBean unannotatedNested;

        @StrutsParameter
        public void setAnnotated(String v) { this.annotated = v; }
        public String getAnnotated() { return annotated; }

        public void setUnannotated(String v) { this.unannotated = v; }
        public String getUnannotated() { return unannotated; }

        @StrutsParameter(depth = 1)
        public NestedBean getNested() { return nested; }
        public void setNested(NestedBean nested) { this.nested = nested; }

        public NestedBean getUnannotatedNested() { return unannotatedNested; }
        public void setUnannotatedNested(NestedBean v) { this.unannotatedNested = v; }
    }

    public static class NestedBean {
        private String field;
        public String getField() { return field; }
        public void setField(String f) { this.field = f; }
    }

    public static class ModelDrivenAction extends ActionSupport implements ModelDriven<Model> {
        private final Model model = new Model();
        @Override
        public Model getModel() { return model; }
    }

    public static class Model {
        private String name;
        public String getName() { return name; }
        public void setName(String n) { this.name = n; }
    }
}
