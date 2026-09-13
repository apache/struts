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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CspNonceSourceTest {

    @Test
    public void canonicalValueWins() {
        assertEquals(CspNonceSource.REQUEST, CspNonceSource.resolve("request", "session"));
    }

    @Test
    public void legacyValueAppliesWhenCanonicalIsBlank() {
        assertEquals(CspNonceSource.REQUEST, CspNonceSource.resolve(" ", "request"));
        assertEquals(CspNonceSource.REQUEST, CspNonceSource.resolve(null, "request"));
    }

    @Test
    public void defaultsToSessionWhenNothingIsSet() {
        assertEquals(CspNonceSource.SESSION, CspNonceSource.resolve(null, null));
        assertEquals(CspNonceSource.SESSION, CspNonceSource.resolve("", " "));
    }

    @Test
    public void valueIsCaseInsensitive() {
        assertEquals(CspNonceSource.REQUEST, CspNonceSource.resolve("Request", null));
        assertEquals(CspNonceSource.SESSION, CspNonceSource.resolve(null, "SESSION"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownValueIsRejected() {
        CspNonceSource.resolve("cookie", null);
    }
}
