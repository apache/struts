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
package org.apache.struts2.util;

import org.apache.struts2.XWorkTestCase;

import java.util.HashMap;

public class WildcardHelperTest extends XWorkTestCase {

    private WildcardHelper wildcardHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        wildcardHelper = new WildcardHelper();
    }

    public void testMatch() {
        HashMap<String, String> matchedPatterns = new HashMap<>();
        int[] pattern = wildcardHelper.compilePattern("wes-rules");
        assertEquals(wildcardHelper.match(matchedPatterns, "wes-rules", pattern), true);
        assertEquals(wildcardHelper.match(matchedPatterns, "rules-wes", pattern), false);

        pattern = wildcardHelper.compilePattern("wes-*");
        assertEquals(wildcardHelper.match(matchedPatterns, "wes-rules", pattern), true);
        assertEquals("rules".equals(matchedPatterns.get("1")), true);
        assertEquals(wildcardHelper.match(matchedPatterns, "rules-wes", pattern), false);

        pattern = wildcardHelper.compilePattern("path/**/file");
        assertEquals(wildcardHelper.match(matchedPatterns, "path/to/file", pattern), true);
        assertEquals("to".equals(matchedPatterns.get("1")), true);
        assertEquals(wildcardHelper.match(matchedPatterns, "path/to/another/location/of/file", pattern), true);
        assertEquals("to/another/location/of".equals(matchedPatterns.get("1")), true);

        pattern = wildcardHelper.compilePattern("path/*/file");
        assertEquals(wildcardHelper.match(matchedPatterns, "path/to/file", pattern), true);
        assertEquals("to".equals(matchedPatterns.get("1")), true);
        assertEquals(wildcardHelper.match(matchedPatterns, "path/to/another/location/of/file", pattern), false);

        pattern = wildcardHelper.compilePattern("path/*/another/**/file");
        assertEquals(wildcardHelper.match(matchedPatterns, "path/to/another/location/of/file", pattern), true);
        assertEquals("to".equals(matchedPatterns.get("1")), true);
        assertEquals("location/of".equals(matchedPatterns.get("2")), true);
    }

    public void testMatchStrutsPackages() {
        // given
        HashMap<String, String> matchedPatterns = new HashMap<>();
        int[] pattern = wildcardHelper.compilePattern("org.apache.struts2.*");

        // when & then
        assertTrue(wildcardHelper.match(matchedPatterns, "org.apache.struts2.XWorkTestCase", pattern));
        assertEquals("org.apache.struts2.XWorkTestCase", matchedPatterns.get("0"));
        assertEquals("XWorkTestCase", matchedPatterns.get("1"));

        assertTrue(wildcardHelper.match(matchedPatterns, "org.apache.struts2.core.SomeClass", pattern));
        assertEquals("org.apache.struts2.core.SomeClass", matchedPatterns.get("0"));
        assertEquals("core.SomeClass", matchedPatterns.get("1"));

        assertTrue(wildcardHelper.match(matchedPatterns, "org.apache.struts2.", pattern));
        assertEquals("org.apache.struts2.", matchedPatterns.get("0"));
        assertEquals("", matchedPatterns.get("1"));
    }

}
