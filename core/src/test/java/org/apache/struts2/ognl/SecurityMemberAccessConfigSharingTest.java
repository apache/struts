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

import org.apache.struts2.XWorkTestCase;

import java.util.Set;

public class SecurityMemberAccessConfigSharingTest extends XWorkTestCase {

    /**
     * Reference identity proves no re-parsing occurred: any re-parse necessarily
     * allocates a fresh set.
     */
    public void testConfigDerivedSetsAreSharedAcrossInstances() throws Exception {
        SecurityMemberAccess first = container.getInstance(SecurityMemberAccess.class);
        SecurityMemberAccess second = container.getInstance(SecurityMemberAccess.class);

        assertNotSame("expected a prototype bean", first, second);

        Set<String> firstExcluded = SecurityMemberAccessTest.reflectField(first, "excludedClasses");
        Set<String> secondExcluded = SecurityMemberAccessTest.reflectField(second, "excludedClasses");
        assertSame("excluded classes were re-parsed per instance", firstExcluded, secondExcluded);

        Set<String> firstPackages = SecurityMemberAccessTest.reflectField(first, "excludedPackageNames");
        Set<String> secondPackages = SecurityMemberAccessTest.reflectField(second, "excludedPackageNames");
        assertSame("excluded package names were re-parsed per instance", firstPackages, secondPackages);
    }

    public void testConfigBeanIsASingleton() {
        assertSame(container.getInstance(SecurityMemberAccessConfig.class),
                container.getInstance(SecurityMemberAccessConfig.class));
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
}
