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

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.StrutsBeanSelectionProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.interceptor.httpmethod.HttpDelete;
import org.apache.struts2.interceptor.httpmethod.HttpMethod;
import org.apache.struts2.interceptor.httpmethod.HttpMethodAware;
import org.apache.struts2.interceptor.httpmethod.HttpPost;
import org.apache.struts2.interceptor.httpmethod.HttpPut;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.Map;

/** The REST default stack enforces HTTP method annotations. */
public class RestStackHttpMethodEnforcementTest extends XWorkTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(
                new StrutsXmlConfigurationProvider("struts-default.xml"),
                new StrutsXmlConfigurationProvider("struts-plugin.xml"),
                new StrutsXmlConfigurationProvider("ww-httpmethod-stack.xml"),
                new DefaultPropertiesProvider(),
                new StrutsBeanSelectionProvider());
    }

    private MockHttpServletResponse invoke(String namespace, String verb) throws Exception {
        PostOnlyAction.invoked = false;
        MockHttpServletRequest request = new MockHttpServletRequest(verb, namespace + "/mark");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> ctx = new HashMap<>();
        ctx.put(StrutsStatics.HTTP_REQUEST, request);
        ctx.put(StrutsStatics.HTTP_RESPONSE, response);
        ctx.putAll(ActionContext.of(ctx).withParameters(org.apache.struts2.dispatcher.HttpParameters.create(new HashMap<String, Object>()).build()).getContextMap());
        ActionProxy proxy = actionProxyFactory.createActionProxy(namespace, "mark", null, ctx);
        proxy.execute();
        return response;
    }

    private void assertGetRejected(String namespace) throws Exception {
        MockHttpServletResponse response = invoke(namespace, "GET");
        assertEquals(405, response.getStatus());
        assertEquals("POST", response.getHeader("Allow"));
        assertFalse("action must not run", PostOnlyAction.invoked);
    }

    public void testControlCoreDefaultStackRejectsGetOnPostOnlyAction() throws Exception {
        assertGetRejected("/core");
    }

    public void testRestDefaultStackRejectsGetOnPostOnlyAction() throws Exception {
        assertGetRejected("/rest");
    }

    public void testRestDefaultStackAllowsPostOnPostOnlyAction() throws Exception {
        MockHttpServletResponse response = invoke("/rest", "POST");
        assertEquals(200, response.getStatus());
        assertNull(response.getHeader("Allow"));
        assertTrue("action must run", PostOnlyAction.invoked);
    }

    public void testActualPutIsAllowedOnPutAnnotatedUpdate() throws Exception {
        assertMappedRequest("native", "PUT", null, "update", 200, null, 1);
    }

    public void testActualDeleteIsAllowedOnDeleteAnnotatedDestroy() throws Exception {
        assertMappedRequest("native", "DELETE", null, "destroy", 200, null, 1);
    }

    public void testTunneledPutIsRejectedOnPutAnnotatedUpdate() throws Exception {
        assertMappedRequest("native", "POST", "put", "update", 405, "PUT", 0);
    }

    public void testTunneledDeleteIsRejectedOnDeleteAnnotatedDestroy() throws Exception {
        assertMappedRequest("native", "POST", "delete", "destroy", 405, "DELETE", 0);
    }

    public void testTunneledPutIsAllowedOnPostAnnotatedUpdate() throws Exception {
        assertMappedRequest("tunneled", "POST", "put", "update", 200, null, 1);
    }

    public void testTunneledDeleteIsAllowedOnPostAnnotatedDestroy() throws Exception {
        assertMappedRequest("tunneled", "POST", "delete", "destroy", 200, null, 1);
    }

    public void testActualPutIsStillAllowedWhenUpdateAlsoAllowsPost() throws Exception {
        assertMappedRequest("tunneled", "PUT", null, "update", 200, null, 1);
    }

    public void testActualDeleteIsStillAllowedWhenDestroyAlsoAllowsPost() throws Exception {
        assertMappedRequest("tunneled", "DELETE", null, "destroy", 200, null, 1);
    }

    private void assertMappedRequest(String actionName, String verb, String override, String actionMethod,
                                     int status, String allow, int invocations) throws Exception {
        String path = "/rest/" + actionName + "/1";
        MockHttpServletRequest request = new MockHttpServletRequest(verb, path);
        request.setServletPath(path);
        if (override != null) {
            request.setParameter(RestActionMapper.HTTP_METHOD_PARAM, override);
        }
        RestActionMapper mapper = new RestActionMapper();
        container.inject(mapper);
        ActionMapping mapping = mapper.getMapping(request, configurationManager);
        assertNotNull(mapping);
        assertEquals("/rest", mapping.getNamespace());
        assertEquals(actionName, mapping.getName());
        assertEquals(actionMethod, mapping.getMethod());

        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> context = ActionContext.of()
                .withServletRequest(request)
                .withServletResponse(response)
                .withActionMapping(mapping)
                .withParameters(HttpParameters.create(request.getParameterMap()).build())
                .getContextMap();
        ActionProxy proxy = actionProxyFactory.createActionProxy(mapping.getNamespace(), mapping.getName(),
                mapping.getMethod(), context, false, true);
        NativeVerbAction action = (NativeVerbAction) proxy.getAction();
        proxy.execute();

        assertEquals(status, response.getStatus());
        assertEquals(allow, response.getHeader("Allow"));
        assertEquals(invocations, action.invocations);
        assertEquals(HttpMethod.parse(verb), action.method);
    }

    public static class NativeVerbAction extends ActionSupport implements HttpMethodAware {
        private int invocations;
        private HttpMethod method;

        @HttpPut
        public String update() {
            invocations++;
            return NONE;
        }

        @HttpDelete
        public String destroy() {
            invocations++;
            return NONE;
        }

        @Override
        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        @Override
        public String getBadRequestResultName() {
            return null;
        }
    }

    public static class TunneledVerbAction extends NativeVerbAction {
        @HttpPost
        @HttpPut
        @Override
        public String update() {
            return super.update();
        }

        @HttpPost
        @HttpDelete
        @Override
        public String destroy() {
            return super.destroy();
        }
    }
}
