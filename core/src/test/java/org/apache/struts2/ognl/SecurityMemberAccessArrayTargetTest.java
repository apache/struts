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

import ognl.OgnlContext;
import org.apache.struts2.util.StrutsProxyService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Pins the reasoning behind WW-5676, which decided that {@link SecurityMemberAccess#toPackageName}
 * must keep resolving arrays to the empty package rather than to the element type's package.
 * <p>
 * The concern WW-5676 was raised to investigate was that an array of an excluded-package type, say
 * {@code java.io.File[]}, resolves to the empty package and so slips past
 * {@code struts.excludedPackageNames} even though {@code java.io} is excluded by default. That
 * reads like a defensive gap, but the package check is unreachable for array targets, because of
 * the two facts pinned below:
 * <ol>
 *   <li>every member reflectively reachable on an array class declares in {@code java.lang.Object}
 *       &mdash; array {@code clone()} is a JVM-internal method absent from the reflection view; and</li>
 *   <li>{@code java.lang.Object} is permanently excluded &mdash; it is the built-in default of
 *       {@code excludedClasses} and the setters only ever accumulate onto that default.</li>
 * </ol>
 * {@code checkExclusionList} tests the declaring class before the package, so it always denies at
 * the first check and never reaches the package comparison.
 * <p>
 * Resolving arrays to the element package would therefore tighten nothing, while genuinely
 * loosening the allowlist: {@code struts.allowlist.packageNames=com.app} would begin to allowlist
 * {@code com.app.Thing[]} implicitly, which today requires an explicit
 * {@code struts.allowlist.classes} entry. These tests fail loudly if either fact stops holding,
 * because that is what would turn the decision around.
 */
public class SecurityMemberAccessArrayTargetTest {

    private static final List<Class<?>> ARRAY_SHAPES = List.of(
            String[].class,
            File[].class,
            int[].class,
            Object[][].class,
            SecurityMemberAccess[].class);

    private OgnlContext context;
    private SecurityMemberAccess sma;

    @Before
    public void setUp() {
        context = ognl.Ognl.createDefaultContext(null);
        ProviderAllowlist providerAllowlist = mock(ProviderAllowlist.class);
        ThreadAllowlist threadAllowlist = mock(ThreadAllowlist.class);
        when(providerAllowlist.getProviderAllowlist()).thenReturn(new HashSet<>());
        when(threadAllowlist.getAllowlist()).thenReturn(new HashSet<>());
        sma = new SecurityMemberAccess(providerAllowlist, threadAllowlist);
        sma.setProxyService(new StrutsProxyService(new StrutsProxyCacheFactory<>("1000", "basic")));
    }

    /**
     * Fact one. If a future JDK exposes further members on array classes, the unreachability
     * argument breaks and WW-5676 has to be reopened.
     */
    @Test
    public void everyReflectiveMemberOfAnArrayClassDeclaresInObject() {
        for (Class<?> arrayClass : ARRAY_SHAPES) {
            assertThat(arrayClass.getMethods())
                    .as("public methods of %s", arrayClass.getName())
                    .isNotEmpty()
                    .allSatisfy(method -> assertThat(method.getDeclaringClass()).isEqualTo(Object.class));
            assertThat(arrayClass.getDeclaredMethods())
                    .as("declared methods of %s", arrayClass.getName())
                    .isEmpty();
            assertThat(arrayClass.getFields())
                    .as("public fields of %s, including the synthetic length", arrayClass.getName())
                    .isEmpty();
        }
    }

    /**
     * Fact one, continued. Array {@code clone()} is a JVM-internal method: the JLS gives array
     * types a public {@code clone()}, but it is not reflectively discoverable, so it can never
     * reach {@code checkExclusionList} with the array type as its declaring class.
     */
    @Test
    public void arrayCloneIsNotReflectivelyReachable() {
        for (Class<?> arrayClass : ARRAY_SHAPES) {
            assertThatExceptionOfType(NoSuchMethodException.class)
                    .as("clone() of %s", arrayClass.getName())
                    .isThrownBy(() -> arrayClass.getMethod("clone"));
        }
    }

    /**
     * Fact two. {@code useExcludedClasses} folds into the existing set rather than replacing it,
     * so no configuration can drop the built-in {@code java.lang.Object} entry.
     */
    @Test
    public void objectStaysExcludedWhateverIsConfigured() {
        assertThat(sma.isClassExcluded(Object.class))
                .as("java.lang.Object is excluded by default")
                .isTrue();

        sma.useExcludedClasses("java.lang.Class,java.lang.Runtime");

        assertThat(sma.isClassExcluded(Object.class))
                .as("java.lang.Object stays excluded after excludedClasses is configured without it")
                .isTrue();
    }

    /**
     * The payoff: no member of an array target is accessible, so the empty package name that
     * {@code toPackageName} returns for arrays is never compared against
     * {@code struts.excludedPackageNames} in the first place.
     */
    @Test
    public void noMemberOfAnArrayTargetIsAccessible() {
        for (boolean allowlistEnabled : new boolean[]{true, false}) {
            sma.useEnforceAllowlistEnabled(String.valueOf(allowlistEnabled));
            File[] target = {new File("/tmp")};
            for (Method member : target.getClass().getMethods()) {
                assertThat(sma.isAccessible(context, target, member, member.getName()))
                        .as("allowlistEnabled=%s member=%s", allowlistEnabled, member)
                        .isFalse();
            }
        }
    }
}
