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

    /**
     * WW-5594: Tests that pattern "org.apache.struts2.*" does NOT match "org.apache.struts2"
     * (package name without trailing dot).
     * <p>
     * This is important because when checking exclusion patterns, the convention plugin
     * extracts package names from class names using StringUtils.substringBeforeLast(className, "."),
     * which produces package names without trailing dots.
     * <p>
     * For example:
     * - Class "org.apache.struts2.XWorkTestCase" -> package "org.apache.struts2" (no trailing dot)
     * - Pattern "org.apache.struts2.*" requires a literal "." before the wildcard
     * - Result: Pattern doesn't match, class is not excluded
     * <p>
     * The fix is to include both "org.apache.struts2" and "org.apache.struts2.*" in exclusion patterns.
     */
    public void testWW5594_WildcardPatternRequiresTrailingDot() {
        // given
        HashMap<String, String> matchedPatterns = new HashMap<>();
        int[] wildcardPattern = wildcardHelper.compilePattern("org.apache.struts2.*");

        // when & then - Pattern with wildcard does NOT match package name without trailing dot
        // This is the root cause of WW-5594
        assertFalse("Pattern 'org.apache.struts2.*' should NOT match 'org.apache.struts2' (no trailing dot)",
                wildcardHelper.match(matchedPatterns, "org.apache.struts2", wildcardPattern));

        // But it DOES match with trailing dot
        assertTrue("Pattern 'org.apache.struts2.*' should match 'org.apache.struts2.' (with trailing dot)",
                wildcardHelper.match(matchedPatterns, "org.apache.struts2.", wildcardPattern));

        // And it DOES match full class names
        assertTrue("Pattern 'org.apache.struts2.*' should match 'org.apache.struts2.SomeClass'",
                wildcardHelper.match(matchedPatterns, "org.apache.struts2.SomeClass", wildcardPattern));
    }

    /**
     * WW-5594: Tests that exact package pattern matches package names correctly.
     * To properly exclude classes in a root package, use both the exact package name
     * and the wildcard pattern.
     */
    public void testWW5594_ExactPackagePatternMatchesPackageName() {
        // given
        HashMap<String, String> matchedPatterns = new HashMap<>();
        int[] exactPattern = wildcardHelper.compilePattern("org.apache.struts2");

        // when & then - Exact pattern matches exactly
        assertTrue("Exact pattern 'org.apache.struts2' should match 'org.apache.struts2'",
                wildcardHelper.match(matchedPatterns, "org.apache.struts2", exactPattern));

        // But exact pattern does NOT match subpackages
        assertFalse("Exact pattern 'org.apache.struts2' should NOT match 'org.apache.struts2.core'",
                wildcardHelper.match(matchedPatterns, "org.apache.struts2.core", exactPattern));
    }

}
