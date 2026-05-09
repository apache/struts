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
import org.apache.struts2.action.Action;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
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
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.expectAndReturn("getAction", action);
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
        // shallowAddress has @StrutsParameter (depth=0) — its nested fields should NOT be set
        runWithBody("{\"shallowAddress\":{\"city\":\"Warsaw\",\"zip\":\"00-001\"}}");
        // The top-level shallowAddress reference itself may be created (Jackson behavior)
        // but its nested city/zip must remain null because depth-1 isn't allowed.
        if (action.getShallowAddress() != null) {
            assertNull("nested city should be rejected (depth insufficient)",
                    action.getShallowAddress().getCity());
            assertNull("nested zip should be rejected (depth insufficient)",
                    action.getShallowAddress().getZip());
        }
    }
}
