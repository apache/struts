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
package org.apache.struts2.interceptor.httpmethod;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

public class HttpMethodHierarchyTest extends StrutsInternalTestCase {

    private MockHttpServletResponse run(String action, String verb) throws Exception {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider("struts-http-method-hierarchy.xml"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> context = ActionContext.of()
            .withServletRequest(new MockHttpServletRequest(verb, "/" + action))
            .withServletResponse(response).getContextMap();
        HttpMethodHierarchyActions.RAN.set(0);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", action, null, context, false, true);
        proxy.execute();
        return response;
    }

    private void assertGetRejected(String action) throws Exception {
        MockHttpServletResponse response = run(action, "GET");
        assertEquals(405, response.getStatus());
        assertEquals("POST", response.getHeader("Allow"));
        assertEquals("action must not run", 0, HttpMethodHierarchyActions.RAN.get());
    }

    public void testControlNamedMethodGetRejected() throws Exception {
        assertGetRejected("NamedPost");
    }

    public void testControlClassLevelGetRejected() throws Exception {
        assertGetRejected("ClassPost");
    }

    public void testExecuteMethodAnnotationGetRejected() throws Exception {
        assertGetRejected("ExecutePost");
    }

    public void testSubclassOfClassLevelGetRejected() throws Exception {
        assertGetRejected("SubOfClassPost");
    }

    public void testOverriddenMethodRetainsAnnotation() throws Exception {
        assertGetRejected("SubOverride");
    }

    public void testSubclassOfInterfaceDefaultMethodGetRejected() throws Exception {
        assertGetRejected("SubOfImplementsMethodMarker");
    }

    public void testSubclassOfImplementedInterfaceClassLevelGetRejected() throws Exception {
        assertGetRejected("SubOfImplementsClassMarker");
    }

    public void testPostAllowedForEveryAnnotatedActionShape() throws Exception {
        for (String action : new String[]{"NamedPost", "ClassPost", "ExecutePost", "SubOfClassPost",
                "SubOverride", "SubOfImplementsMethodMarker", "SubOfImplementsClassMarker"}) {
            MockHttpServletResponse response = run(action, "POST");
            assertEquals(action, 200, response.getStatus());
            assertNull(action, response.getHeader("Allow"));
            assertEquals(action, 1, HttpMethodHierarchyActions.RAN.get());
        }
    }
}
