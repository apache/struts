/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.core.impl;

import org.apache.tiles.core.locale.impl.DefaultLocaleResolver;
import org.apache.tiles.request.Request;
import org.junit.Test;

import java.util.Locale;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertSame;

/**
 * Tests {@link DefaultLocaleResolver}.
 */
public class DefaultLocaleResolverTest {

    /**
     * Test method for {@link DefaultLocaleResolver#resolveLocale(Request)}.
     */
    @Test
    public void testResolveLocale() {
        Request request = createMock(Request.class);
        Map<String, Object> sessionScope = createMock(Map.class);
        Locale locale = Locale.ITALY;

        expect(request.getContext("session")).andReturn(sessionScope);
        expect(sessionScope.get(DefaultLocaleResolver.LOCALE_KEY)).andReturn(null);
        expect(request.getRequestLocale()).andReturn(locale);

        replay(request, sessionScope);
        DefaultLocaleResolver resolver = new DefaultLocaleResolver();
        assertSame(locale, resolver.resolveLocale(request));
        verify(request, sessionScope);
    }

}
