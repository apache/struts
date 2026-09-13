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
import org.apache.struts2.interceptor.csp.CspNonceReader.NonceValue;
import org.apache.struts2.util.ValueStack;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StrutsCspNonceReaderTest {

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    public void readsNonceFromSession() {
        request.getSession(true).setAttribute("nonce", "abc");

        NonceValue value = new StrutsCspNonceReader("session").readNonceValue(stack());

        assertEquals(CspNonceSource.SESSION, value.getSource());
        assertEquals("abc", value.getNonceValue());
    }

    @Test
    public void reportsMissingSessionWithoutCreatingOne() {
        NonceValue value = new StrutsCspNonceReader("session").readNonceValue(stack());

        assertEquals(CspNonceSource.SESSION, value.getSource());
        assertFalse(value.isNonceValueSet());
        assertNull("reader must not start a session", request.getSession(false));
    }

    @Test
    public void readsNonceFromRequestAttribute() {
        request.setAttribute("nonce", "xyz");

        NonceValue value = new StrutsCspNonceReader("request").readNonceValue(stack());

        assertEquals(CspNonceSource.REQUEST, value.getSource());
        assertEquals("xyz", value.getNonceValue());
    }

    @Test
    public void reportsMissingRequestAttribute() {
        NonceValue value = new StrutsCspNonceReader("request").readNonceValue(stack());

        assertEquals(CspNonceSource.REQUEST, value.getSource());
        assertFalse(value.isNonceValueSet());
    }

    @Test
    public void legacyKeyFallsBackWhenCanonicalIsUnset() {
        request.setAttribute("nonce", "xyz");

        NonceValue value = new StrutsCspNonceReader(null, "request").readNonceValue(stack());

        assertEquals(CspNonceSource.REQUEST, value.getSource());
        assertEquals("xyz", value.getNonceValue());
    }

    private ValueStack stack() {
        ActionContext context = ActionContext.of().withServletRequest(request);
        ValueStack stack = mock(ValueStack.class);
        when(stack.getActionContext()).thenReturn(context);
        return stack;
    }
}
