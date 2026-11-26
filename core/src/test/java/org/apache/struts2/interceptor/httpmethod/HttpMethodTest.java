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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

public class HttpMethodTest {

    @Test
    public void shouldConvertHttpRequestMethod_toProperEnum() {
        // given
        String httpRequestMethod = "post";

        // when
        HttpMethod httpMethod = HttpMethod.parse(httpRequestMethod);

        // then
        assertEquals(HttpMethod.POST, httpMethod);
    }

    @Test
    public void shouldValueOfThrowsException() {
        // given
        String httpRequestMethod = "post";

        // when
        Throwable expected = null;
        try {
            HttpMethod.valueOf(httpRequestMethod);
        } catch (IllegalArgumentException e) {
            expected = e;
        }

        // then
        assertNotNull(expected);
        assertEquals(expected.getClass(), IllegalArgumentException.class);
    }


    @Test
    public void shouldParseUnknownRequestMethod_asNull_notRaise() {
        // given : a request method this enum does not know about, e.g. a WebDAV verb
        String httpRequestMethod = "PROPFIND";

        // when
        HttpMethod httpMethod = HttpMethod.parseOrNull(httpRequestMethod);

        // then
        assertNull(httpMethod);
    }

    @Test
    public void shouldParseKnownRequestMethod_regardlessOfCase() {
        // given
        String httpRequestMethod = "get";

        // when
        HttpMethod httpMethod = HttpMethod.parseOrNull(httpRequestMethod);

        // then
        assertEquals(HttpMethod.GET, httpMethod);
    }

    @Test
    public void shouldParseNullRequestMethod_asNull() {
        // given
        String httpRequestMethod = null;

        // when
        HttpMethod httpMethod = HttpMethod.parseOrNull(httpRequestMethod);

        // then
        assertNull(httpMethod);
    }

    @Test
    public void shouldStillRejectUnknownMethodInConfiguration() {
        // given : configuration is trusted input, an unknown name there is a real error
        String configuredMethod = "PROPFIND";

        // when / then
        assertThrows(IllegalArgumentException.class, () -> HttpMethod.parse(configuredMethod));
    }

}
