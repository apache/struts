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

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.XWorkTestCase;

import java.util.Map;
import java.util.Set;

public class SecurityMemberAccessConfigSharingTest extends XWorkTestCase {

    /**
     * Reference identity proves no re-parsing occurred: any re-parse necessarily
     * allocates a fresh set.
     * <p>
     * The instance-to-instance {@code assertSame} calls below are necessary but not sufficient:
     * for a field whose default is {@link java.util.Collections#emptySet()}, two independently
     * <em>unseeded</em> instances would also compare same, since {@code emptySet()} returns a
     * JVM-wide singleton. Only {@code excludedClasses}, whose default {@code Set.of(...)} allocates
     * a fresh instance per object, is proven by the instance-to-instance form alone. Every field is
     * therefore additionally compared directly against the shared {@link SecurityMemberAccessConfig}
     * bean, which fails on omission regardless of the default's identity.
     * <p>
     * That direct comparison is itself vacuous unless the configured value actually differs from the
     * hardcoded default: {@code SecurityMemberAccess} and {@code SecurityMemberAccessConfig} share the
     * same hardcoded defaults, so an unseeded field and a config parsed from an all-default container
     * would also compare equal/same by coincidence. The container is therefore reloaded here with every
     * relevant constant set away from its default, so a config value only matches the instance's field
     * when {@code useConfig} actually ran.
     */
    public void testConfigDerivedSetsAreSharedAcrossInstances() throws Exception {
        loadButSet(Map.of(
                StrutsConstants.STRUTS_ALLOW_STATIC_FIELD_ACCESS, "false",
                StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAME_PATTERNS, "^org\\.apache\\.struts2\\.ognl\\.testpkg\\..*",
                StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAMES, "org.apache.struts2.ognl.testpkg",
                StrutsConstants.STRUTS_EXCLUDED_PACKAGE_EXEMPT_CLASSES, "java.lang.String",
                StrutsConstants.STRUTS_ALLOWLIST_ENABLE, "true",
                StrutsConstants.STRUTS_ALLOWLIST_CLASSES, "java.lang.String",
                StrutsConstants.STRUTS_ALLOWLIST_PACKAGE_NAMES, "org.apache.struts2.ognl.testpkg",
                StrutsConstants.STRUTS_DISALLOW_PROXY_OBJECT_ACCESS, "true",
                StrutsConstants.STRUTS_DISALLOW_PROXY_MEMBER_ACCESS, "true",
                StrutsConstants.STRUTS_DISALLOW_DEFAULT_PACKAGE_ACCESS, "true"));

        SecurityMemberAccess first = container.getInstance(SecurityMemberAccess.class);
        SecurityMemberAccess second = container.getInstance(SecurityMemberAccess.class);
        SecurityMemberAccessConfig config = container.getInstance(SecurityMemberAccessConfig.class);

        assertNotSame("expected a prototype bean", first, second);

        Set<String> firstExcluded = SecurityMemberAccessTest.reflectField(first, "excludedClasses");
        Set<String> secondExcluded = SecurityMemberAccessTest.reflectField(second, "excludedClasses");
        assertSame("excluded classes were re-parsed per instance", firstExcluded, secondExcluded);

        Set<String> firstPackages = SecurityMemberAccessTest.reflectField(first, "excludedPackageNames");
        Set<String> secondPackages = SecurityMemberAccessTest.reflectField(second, "excludedPackageNames");
        assertSame("excluded package names were re-parsed per instance", firstPackages, secondPackages);

        assertSame("excludedClasses not seeded from config",
                config.getExcludedClasses(), SecurityMemberAccessTest.reflectField(first, "excludedClasses"));
        assertSame("excludedPackageNamePatterns not seeded from config",
                config.getExcludedPackageNamePatterns(), SecurityMemberAccessTest.reflectField(first, "excludedPackageNamePatterns"));
        assertSame("excludedPackageNames not seeded from config",
                config.getExcludedPackageNames(), SecurityMemberAccessTest.reflectField(first, "excludedPackageNames"));
        assertSame("excludedPackageExemptClasses not seeded from config",
                config.getExcludedPackageExemptClasses(), SecurityMemberAccessTest.reflectField(first, "excludedPackageExemptClasses"));
        assertSame("allowlistClasses not seeded from config",
                config.getAllowlistClasses(), SecurityMemberAccessTest.reflectField(first, "allowlistClasses"));
        assertSame("allowlistPackageNames not seeded from config",
                config.getAllowlistPackageNames(), SecurityMemberAccessTest.reflectField(first, "allowlistPackageNames"));

        boolean firstAllowStaticFieldAccess = SecurityMemberAccessTest.reflectField(first, "allowStaticFieldAccess");
        assertEquals("allowStaticFieldAccess not seeded from config",
                config.isAllowStaticFieldAccess(), firstAllowStaticFieldAccess);
        boolean firstEnforceAllowlistEnabled = SecurityMemberAccessTest.reflectField(first, "enforceAllowlistEnabled");
        assertEquals("enforceAllowlistEnabled not seeded from config",
                config.isEnforceAllowlistEnabled(), firstEnforceAllowlistEnabled);
        boolean firstDisallowProxyObjectAccess = SecurityMemberAccessTest.reflectField(first, "disallowProxyObjectAccess");
        assertEquals("disallowProxyObjectAccess not seeded from config",
                config.isDisallowProxyObjectAccess(), firstDisallowProxyObjectAccess);
        boolean firstDisallowProxyMemberAccess = SecurityMemberAccessTest.reflectField(first, "disallowProxyMemberAccess");
        assertEquals("disallowProxyMemberAccess not seeded from config",
                config.isDisallowProxyMemberAccess(), firstDisallowProxyMemberAccess);
        boolean firstDisallowDefaultPackageAccess = SecurityMemberAccessTest.reflectField(first, "disallowDefaultPackageAccess");
        assertEquals("disallowDefaultPackageAccess not seeded from config",
                config.isDisallowDefaultPackageAccess(), firstDisallowDefaultPackageAccess);
    }

    public void testConfigBeanIsASingleton() {
        SecurityMemberAccessConfig instance = container.getInstance(SecurityMemberAccessConfig.class);
        assertNotNull("SecurityMemberAccessConfig is not registered in the container", instance);
        assertSame(instance, container.getInstance(SecurityMemberAccessConfig.class));
    }

    /**
     * The shared sets must not be perturbed by a deprecated setter call on one instance.
     */
    public void testDeprecatedSetterDoesNotLeakToSiblings() throws Exception {
        SecurityMemberAccess mutated = container.getInstance(SecurityMemberAccess.class);
        SecurityMemberAccess untouched = container.getInstance(SecurityMemberAccess.class);
        SecurityMemberAccessConfig config = container.getInstance(SecurityMemberAccessConfig.class);

        Set<String> before = SecurityMemberAccessTest.reflectField(untouched, "excludedClasses");
        mutated.useExcludedClasses("java.lang.Runtime");
        Set<String> after = SecurityMemberAccessTest.reflectField(untouched, "excludedClasses");

        assertSame("a sibling instance was affected", before, after);
        assertFalse("the shared config was mutated", config.getExcludedClasses().contains("java.lang.Runtime"));

        Set<String> mutatedSet = SecurityMemberAccessTest.reflectField(mutated, "excludedClasses");
        assertTrue("the setter did not affect its own instance", mutatedSet.contains("java.lang.Runtime"));
    }

    /**
     * Guards the fail-open hole avoided by using setter rather than constructor injection:
     * a subclass calling the two-argument super constructor must still receive the config.
     */
    public void testSubclassReceivesConfigThroughInheritedSetter() throws Exception {
        SubclassedSecurityMemberAccess subclassed = new SubclassedSecurityMemberAccess(
                container.getInstance(ProviderAllowlist.class),
                container.getInstance(ThreadAllowlist.class));

        container.inject(subclassed);

        Set<String> excluded = SecurityMemberAccessTest.reflectField(subclassed, "excludedClasses");
        assertSame("subclass did not receive the shared config",
                container.getInstance(SecurityMemberAccessConfig.class).getExcludedClasses(), excluded);
    }

    static class SubclassedSecurityMemberAccess extends SecurityMemberAccess {
        SubclassedSecurityMemberAccess(ProviderAllowlist providerAllowlist, ThreadAllowlist threadAllowlist) {
            super(providerAllowlist, threadAllowlist);
        }
    }

    /**
     * Dev-mode exclusions must be in force from the first access, with no lazy flip.
     */
    public void testDevModeExclusionsApplyWithoutAnAccess() throws Exception {
        loadButSet(Map.of(
                StrutsConstants.STRUTS_DEVMODE, "true",
                StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_CLASSES, "java.lang.ProcessBuilder"));

        SecurityMemberAccess sma = container.getInstance(SecurityMemberAccess.class);
        Set<String> excluded = SecurityMemberAccessTest.reflectField(sma, "excludedClasses");

        assertTrue("dev-mode exclusions were not applied at startup",
                excluded.contains("java.lang.ProcessBuilder"));
    }

    public void testDevModeMethodsAreGone() throws Exception {
        for (String name : new String[]{"useDevMode", "useDevModeExcludedClasses",
                "useDevModeExcludedPackageNamePatterns", "useDevModeExcludedPackageNames",
                "useDevModeExcludedPackageExemptClasses", "useDevModeConfiguration"}) {
            for (java.lang.reflect.Method method : SecurityMemberAccess.class.getDeclaredMethods()) {
                assertFalse("SecurityMemberAccess still declares " + name, method.getName().equals(name));
            }
        }
    }
}
