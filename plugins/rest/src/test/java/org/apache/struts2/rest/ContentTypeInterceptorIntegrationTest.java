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
package org.apache.struts2.rest;

import com.mockobjects.dynamic.AnyConstraintMatcher;
import com.mockobjects.dynamic.Mock;
import junit.framework.TestCase;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.action.Action;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.apache.struts2.interceptor.parameter.StrutsParameterAuthorizer;
import org.apache.struts2.ognl.DefaultOgnlBeanInfoCacheFactory;
import org.apache.struts2.ognl.DefaultOgnlExpressionCacheFactory;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.ognl.StrutsOgnlGuard;
import org.apache.struts2.ognl.StrutsProxyCacheFactory;
import org.apache.struts2.rest.handler.JacksonJsonHandler;
import org.apache.struts2.util.StrutsProxyService;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.apache.struts2.ognl.OgnlCacheFactory.CacheType.LRU;

/**
 * Integration tests for ContentTypeInterceptor that use a real {@link JacksonJsonHandler}
 * and a real {@link StrutsParameterAuthorizer}, end-to-end. Verifies that property
 * filtering actually occurs on the deserialized object — not merely that the wiring runs.
 */
public class ContentTypeInterceptorIntegrationTest extends TestCase {

    private ContentTypeInterceptor interceptor;
    private SecureRestAction action;
    private Mock mockActionInvocation;
    private Mock mockSelector;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        action = new SecureRestAction();
        setupInterceptorWithAction(action);
    }

    private void setupInterceptorWithAction(Object actionInstance) {
        var ognlUtil = new OgnlUtil(
                new DefaultOgnlExpressionCacheFactory<>("1000", LRU.toString()),
                new DefaultOgnlBeanInfoCacheFactory<>("1000", LRU.toString()),
                new StrutsOgnlGuard());
        var proxyService = new StrutsProxyService(new StrutsProxyCacheFactory<>("1000", "basic"));

        StrutsParameterAuthorizer authorizer = new StrutsParameterAuthorizer();
        authorizer.setOgnlUtil(ognlUtil);
        authorizer.setProxyService(proxyService);
        authorizer.setRequireAnnotations(Boolean.TRUE.toString());

        interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer(authorizer);
        interceptor.setRequireAnnotations(Boolean.TRUE.toString());

        mockActionInvocation = new Mock(ActionInvocation.class);
        mockSelector = new Mock(ContentTypeHandlerManager.class);
        // ContentTypeInterceptor calls getAction() twice when requireAnnotations=true
        mockActionInvocation.expectAndReturn("getAction", actionInstance);
        mockActionInvocation.expectAndReturn("getAction", actionInstance);
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockSelector.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) { return true; }
        }, new JacksonJsonHandler());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockSelector.proxy());
    }

    private void runWithBody(String body) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(body.getBytes());
        request.setContentType("application/json");

        ActionContext.of()
                .withActionMapping(new ActionMapping())
                .withServletRequest(request)
                .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        mockSelector.verify();
        mockActionInvocation.verify();
    }

    public void testAnnotatedTopLevelPropertyIsApplied() throws Exception {
        runWithBody("{\"name\":\"alice\"}");
        assertEquals("alice", action.getName());
    }

    public void testUnannotatedTopLevelPropertyIsRejected() throws Exception {
        runWithBody("{\"role\":\"admin\"}");
        assertNull("unannotated 'role' must not be set", action.getRole());
    }

    public void testMixedPropertiesFilteredCorrectly() throws Exception {
        runWithBody("{\"name\":\"alice\",\"role\":\"admin\"}");
        assertEquals("alice", action.getName());
        assertNull(action.getRole());
    }

    public void testNestedPropertyAuthorizedWhenDepthAllows() throws Exception {
        runWithBody("{\"address\":{\"city\":\"Warsaw\",\"zip\":\"00-001\"}}");
        assertNotNull("address should be set", action.getAddress());
        assertEquals("Warsaw", action.getAddress().getCity());
        assertEquals("00-001", action.getAddress().getZip());
    }

    public void testNestedPropertyRejectedWhenDepthInsufficient() throws Exception {
        // shallowAddress has @StrutsParameter on the setter (depth-0 authorized) but the getter
        // has no depth>=1 annotation. The Jackson path enters shallowAddress (constructed by
        // Jackson) but skipChildren on each inner property — so the Address is non-null but its
        // city/zip fields stay null.
        runWithBody("{\"shallowAddress\":{\"city\":\"Warsaw\",\"zip\":\"00-001\"}}");
        assertNotNull("shallowAddress is depth-0 authorized; Jackson constructs it",
                action.getShallowAddress());
        assertNull("nested city must be rejected (depth-1 not authorized)",
                action.getShallowAddress().getCity());
        assertNull("nested zip must be rejected (depth-1 not authorized)",
                action.getShallowAddress().getZip());
    }

    // --- Tests proving the new Jackson authorization path is in use ---

    public void testJacksonHandlerDoesNotRequireNoArgConstructor() throws Exception {
        // The legacy two-phase copy required a no-arg constructor on the target. Jackson's
        // readerForUpdating populates the existing instance directly, so this constraint
        // is gone — proof that the new AuthorizationAware path is being taken.
        NoNoArgAction noNoArg = new NoNoArgAction("preserved-pre-deserialization-value");
        setupInterceptorWithAction(noNoArg);
        runWithBody("{\"name\":\"alice\"}");
        assertEquals("alice", noNoArg.getName());
        assertEquals("pre-existing field must be preserved (no fresh-instance copy)",
                "preserved-pre-deserialization-value", noNoArg.getRequiredField());
    }

    public void testRejectedAtParentNeverInstantiatesNestedObject() throws Exception {
        // Stronger guarantee than the two-phase copy: when the parent property is rejected,
        // Jackson's skipChildren() discards the entire JSON subtree and the nested object
        // is never constructed. role-typed fixture: address requires a setter @StrutsParameter
        // for depth-0 authorization. By giving address a fresh action where address is depth-0
        // unauthorized, we prove the setter is never called and address stays null.
        // (We use a custom action where address has no setter annotation.)
        UnauthorizedNestedAction restrictedAction = new UnauthorizedNestedAction();
        setupInterceptorWithAction(restrictedAction);
        runWithBody("{\"unauthorized\":{\"city\":\"Warsaw\"}}");
        assertNull("unauthorized property must be rejected at parent — Jackson never enters",
                restrictedAction.getUnauthorized());
    }

    // --- Test fixtures for new path verification ---

    /**
     * Action with no public no-arg constructor — would fail the legacy two-phase copy's
     * createFreshInstance check, but works fine with the Jackson authorization path.
     */
    public static class NoNoArgAction extends ActionSupport {
        private final String requiredField;
        private String name;

        public NoNoArgAction(String requiredField) {
            this.requiredField = requiredField;
        }

        public String getName() { return name; }

        @StrutsParameter
        public void setName(String name) { this.name = name; }

        public String getRequiredField() { return requiredField; }
    }

    /**
     * Action with a property that has NO @StrutsParameter on its setter — depth-0 authorization
     * fails, so Jackson must never enter this property nor instantiate the nested object.
     */
    public static class UnauthorizedNestedAction extends ActionSupport {
        private SecureRestAction.Address unauthorized;

        public SecureRestAction.Address getUnauthorized() { return unauthorized; }

        // No @StrutsParameter annotation — depth-0 path "unauthorized" is rejected.
        public void setUnauthorized(SecureRestAction.Address unauthorized) {
            this.unauthorized = unauthorized;
        }
    }
}
