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
package org.apache.struts2.ognl;

import org.junit.Test;

import java.util.Set;
import java.util.regex.Pattern;

import static org.apache.struts2.util.ConfigParseUtil.toNewClassesSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SecurityMemberAccessConfigTest {

    /**
     * Frozen oracle: the accumulation SecurityMemberAccess performed before WW-5675.
     * Never delete this, and never make it delegate to production code.
     */
    private static Set<String> legacyExcludedClassAccumulation(boolean allowStaticFieldAccess, String configured) {
        Set<String> excludedClasses = Set.of(Object.class.getName());
        if (!allowStaticFieldAccess) {
            excludedClasses = toNewClassesSet(excludedClasses, Class.class.getName());
        }
        return toNewClassesSet(excludedClasses, configured);
    }

    private SecurityMemberAccessConfig configWith(boolean devMode, String excludedClasses, String devModeExcludedClasses) {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useDevMode(String.valueOf(devMode));
        config.useExcludedClasses(excludedClasses);
        config.useDevModeExcludedClasses(devModeExcludedClasses);
        config.init();
        return config;
    }

    @Test
    public void excludedClassesMatchLegacyAccumulation() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useExcludedClasses("java.lang.Runtime,java.lang.ProcessBuilder");
        config.init();

        assertEquals(legacyExcludedClassAccumulation(true, "java.lang.Runtime,java.lang.ProcessBuilder"),
                config.getExcludedClasses());
    }

    @Test
    public void disallowingStaticFieldAccessAddsClassToExclusions() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useAllowStaticFieldAccess("false");
        config.useExcludedClasses("java.lang.Runtime");
        config.init();

        assertFalse(config.isAllowStaticFieldAccess());
        assertEquals(legacyExcludedClassAccumulation(false, "java.lang.Runtime"), config.getExcludedClasses());
    }

    /**
     * The container iterates getDeclaredMethods(), whose order the JDK leaves unspecified.
     * The accumulation must therefore be commutative, as it was before WW-5675.
     */
    @Test
    public void setterOrderDoesNotAffectExcludedClasses() {
        SecurityMemberAccessConfig forward = new SecurityMemberAccessConfig();
        forward.useAllowStaticFieldAccess("false");
        forward.useExcludedClasses("java.lang.Runtime");
        forward.init();

        SecurityMemberAccessConfig reverse = new SecurityMemberAccessConfig();
        reverse.useExcludedClasses("java.lang.Runtime");
        reverse.useAllowStaticFieldAccess("false");
        reverse.init();

        assertEquals(forward.getExcludedClasses(), reverse.getExcludedClasses());
    }

    @Test
    public void devModeDisabledPublishesNormalExclusions() {
        SecurityMemberAccessConfig config = configWith(false, "java.lang.Runtime", "java.lang.ProcessBuilder");

        assertTrue(config.getExcludedClasses().contains("java.lang.Runtime"));
        assertFalse(config.getExcludedClasses().contains("java.lang.ProcessBuilder"));
    }

    @Test
    public void devModeEnabledPublishesDevModeExclusions() {
        SecurityMemberAccessConfig config = configWith(true, "java.lang.Runtime", "java.lang.ProcessBuilder");

        assertTrue(config.getExcludedClasses().contains("java.lang.ProcessBuilder"));
        assertFalse(config.getExcludedClasses().contains("java.lang.Runtime"));
    }

    @Test
    public void packageNamesAreStrippedOfDots() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useExcludedPackageNames("java.io.,.java.net");
        config.init();

        assertTrue(config.getExcludedPackageNames().contains("java.io"));
        assertTrue(config.getExcludedPackageNames().contains("java.net"));
    }

    @Test
    public void patternsAreCompiledOnce() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useExcludedPackageNamePatterns("^java\\.lang\\..*");
        config.init();

        Set<Pattern> patterns = config.getExcludedPackageNamePatterns();
        assertEquals(1, patterns.size());
        assertTrue(patterns.iterator().next().matcher("java.lang.Runtime").matches());
    }

    /**
     * A missing init() must fail closed: production exclusions, never the dev-mode ones.
     */
    @Test
    public void withoutInitTheNormalExclusionsApply() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useDevMode("true");
        config.useExcludedClasses("java.lang.Runtime");
        config.useDevModeExcludedClasses("java.lang.ProcessBuilder");

        assertTrue(config.getExcludedClasses().contains("java.lang.Runtime"));
    }

    @Test
    public void allowlistPackageNamesUnionDefaultsToRequiredPackagesOnly() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();

        assertEquals(Set.of("org.apache.struts2.validator.validators",
                        "org.apache.struts2.components",
                        "org.apache.struts2.views.jsp"),
                config.getAllowlistPackageNamesUnion());
    }

    @Test
    public void allowlistPackageNamesUnionRetainsRequiredPackagesWhenConfigured() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useAllowlistPackageNames("com.example.app");

        assertTrue(config.getAllowlistPackageNamesUnion().contains("com.example.app"));
        assertTrue(config.getAllowlistPackageNamesUnion().contains("org.apache.struts2.components"));
        assertTrue(config.getAllowlistPackageNamesUnion().contains("org.apache.struts2.validator.validators"));
        assertTrue(config.getAllowlistPackageNamesUnion().contains("org.apache.struts2.views.jsp"));
    }
}
