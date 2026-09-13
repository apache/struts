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
package org.apache.struts2.interceptor.csp;

import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.csp.CspNonceReader.NonceValue;
import org.apache.struts2.mock.MockActionInvocation;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Proves the nonce source setting reaches both consumers through the container,
 * under the canonical key and under the legacy camel-case key.
 */
public class CspNonceSourceConfigTest extends StrutsInternalTestCase {

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    public void testCanonicalKeyStoresNonceInRequest() throws Exception {
        initDispatcherWithConfigs("struts-default.xml, struts-csp-nonce-source.xml");

        assertNonceStoredInRequestAndReadBack();
    }

    public void testLegacyKeyStoresNonceInRequest() throws Exception {
        initDispatcherWithConfigs("struts-default.xml, struts-csp-nonce-source-legacy.xml");

        assertNonceStoredInRequestAndReadBack();
    }

    @SuppressWarnings("removal")
    public void testDefaultStoresNonceInSession() throws Exception {
        initDispatcherWithConfigs("struts-default.xml");
        assertNull("default.properties must not bind the deprecated key",
                container.getInstance(String.class, StrutsConstants.STRUTS_CSP_NONCE_SOURCE_LEGACY));

        intercept();

        assertNull("nonce must not be a request attribute by default", request.getAttribute("nonce"));
        assertNotNull("nonce must be in the session by default", request.getSession().getAttribute("nonce"));
        NonceValue read = container.getInstance(CspNonceReader.class).readNonceValue(ActionContext.getContext().getValueStack());
        assertEquals(CspNonceSource.SESSION, read.getSource());
        assertEquals(request.getSession().getAttribute("nonce"), read.getNonceValue());
    }

    private void assertNonceStoredInRequestAndReadBack() throws Exception {
        intercept();

        Object nonce = request.getAttribute("nonce");
        assertNotNull("nonce must be a request attribute", nonce);
        assertNull("nonce must not leak into the session", request.getSession().getAttribute("nonce"));

        NonceValue read = container.getInstance(CspNonceReader.class).readNonceValue(ActionContext.getContext().getValueStack());
        assertEquals(CspNonceSource.REQUEST, read.getSource());
        assertEquals(nonce, read.getNonceValue());
    }

    private void intercept() throws Exception {
        request.getSession(true);
        ActionContext context = ActionContext.getContext()
                .withContainer(container)
                .withServletRequest(request)
                .withServletResponse(response)
                .withSession(new SessionMap(request))
                .bind();
        MockActionInvocation mai = new MockActionInvocation();
        mai.setInvocationContext(context);

        CspInterceptor interceptor = new CspInterceptor();
        container.inject(interceptor);
        interceptor.intercept(mai);
    }
}
