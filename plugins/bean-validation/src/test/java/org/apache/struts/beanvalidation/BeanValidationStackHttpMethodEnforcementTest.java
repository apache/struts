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
package org.apache.struts.beanvalidation;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.StrutsBeanSelectionProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.Map;

/** The Bean Validation default stack enforces HTTP method annotations. */
public class BeanValidationStackHttpMethodEnforcementTest extends XWorkTestCase {

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

    public void testBeanValidationDefaultStackRejectsGetOnPostOnlyAction() throws Exception {
        assertGetRejected("/bv");
    }

    public void testBeanValidationDefaultStackAllowsPostOnPostOnlyAction() throws Exception {
        MockHttpServletResponse response = invoke("/bv", "POST");
        assertEquals(200, response.getStatus());
        assertNull(response.getHeader("Allow"));
        assertTrue("action must run", PostOnlyAction.invoked);
    }
}
