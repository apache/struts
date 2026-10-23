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
package org.apache.struts2.convention;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.junit.StrutsJUnit4TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Convention actions carry no {@code http-method} attribute, so the HTTP method annotations on the
 * action class or method are what {@code HttpMethodInterceptor} enforces for them (WW-5786).
 * Fixtures live in {@code actions.httpmethod}, whose package-level {@code @Namespace} gives them the
 * {@code /httpmethod} namespace and result path of their own.
 */
public class ConventionHttpMethodTest extends StrutsJUnit4TestCase<Object> {

    private static final String NAMESPACE = "/httpmethod";

    @Test
    public void conventionActionsDeclareNoHttpMethodAttribute() {
        for (String actionName : new String[]{"post-only", "create-order", "upload", "profile"}) {
            ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig(NAMESPACE, actionName);
            assertNotNull(actionName + " must be mapped in " + NAMESPACE, config);
            assertTrue(actionName + " must rely on annotations alone", config.getHttpMethods().isEmpty());
        }
    }

    @Test
    public void classLevelAnnotationAllowsListedMethod() throws Exception {
        request.setMethod("POST");

        String output = executeAction(NAMESPACE + "/post-only.action");

        assertTrue(output.contains("post-only executed"));
    }

    @Test
    public void classLevelAnnotationRejectsWith405WhenNoBadRequestResult() throws Exception {
        dispatch("GET", NAMESPACE + "/post-only.action");

        assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
        assertEquals("POST", response.getHeader("Allow"));
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void methodLevelAnnotationOnActionMappedMethodAllowsListedMethod() throws Exception {
        request.setMethod("POST");

        String output = executeAction(NAMESPACE + "/create-order.action");

        assertTrue(output.contains("create-order executed"));
    }

    @Test
    public void methodLevelAnnotationOnActionMappedMethodRejectsWith405() throws Exception {
        dispatch("GET", NAMESPACE + "/create-order.action");

        assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
        assertEquals("POST", response.getHeader("Allow"));
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void actionWithConventionBadRequestTemplateAllowsListedMethod() throws Exception {
        request.setMethod("POST");

        String output = executeAction(NAMESPACE + "/upload.action");

        assertTrue(output.contains("upload executed"));
    }

    @Test
    public void rejectionRendersBadRequestTemplateDiscoveredByConvention() throws Exception {
        dispatch("GET", NAMESPACE + "/upload.action");

        assertNotEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
        assertNull(response.getHeader("Allow"));
        assertTrue(response.getContentAsString().contains("upload rejected by convention template"));
    }

    @Test
    public void actionWithDeclaredBadRequestResultAllowsListedMethod() throws Exception {
        request.setMethod("PUT");

        String output = executeAction(NAMESPACE + "/profile.action");

        assertTrue(output.contains("profile executed"));
    }

    @Test
    public void rejectionRendersBadRequestResultDeclaredByAnnotation() throws Exception {
        dispatch("GET", NAMESPACE + "/profile.action");

        assertNotEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
        assertNull(response.getHeader("Allow"));
        assertTrue(response.getContentAsString().contains("profile rejected by declared result"));
    }

    /**
     * {@link #executeAction(String)} turns any non-200 into an exception, so rejected requests go
     * straight to the dispatcher and the test reads the response itself.
     */
    private void dispatch(String httpMethod, String uri) throws Exception {
        request.setMethod(httpMethod);
        request.setRequestURI(uri);
        ActionMapping mapping = getActionMapping(request);
        assertNotNull(mapping);
        Dispatcher.getInstance().serviceAction(request, response, mapping);
    }

    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml,struts-convention-configuration.xml";
    }
}
