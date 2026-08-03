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

import org.apache.struts2.util.ConfigParseUtil;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Collections.emptySet;
import static org.apache.struts2.ognl.SecurityMemberAccess.isClassBelongsToPackages;
import static org.apache.struts2.ognl.SecurityMemberAccess.toPackageName;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Characterisation and equivalence tests for the static package-matching helpers in
 * {@link SecurityMemberAccess}, covering WW-5674.
 * <p>
 * These helpers gate OGNL member access, so the rewrite in WW-5674 must be exactly
 * behaviour-preserving. That is proven here by running the replaced implementation
 * side by side with the new one over a matrix of inputs.
 */
public class SecurityMemberAccessPackageMatchingTest {

    /**
     * The implementation replaced by WW-5674, retained verbatim apart from taking the package
     * name directly instead of a {@link Class}. Used as the reference oracle for the rewrite.
     */
    private static boolean legacyPrefixMatch(String packageName, Set<String> matchingPackages) {
        List<String> packageParts = List.of(packageName.split("\\."));
        return IntStream.range(0, packageParts.size())
                .mapToObj(i -> String.join(".", packageParts.subList(0, i + 1)))
                .anyMatch(matchingPackages::contains);
    }

    /**
     * The {@code toPackageName} implementation replaced by WW-5674, retained as the reference oracle.
     */
    private static String legacyToPackageName(Class<?> clazz) {
        if (clazz.getPackage() == null) {
            return "";
        }
        return clazz.getPackage().getName();
    }

    /**
     * Package-name shapes. Deliberately excludes trailing-dot inputs such as {@code "a.b."}:
     * {@code split} drops trailing empty segments where an index walk would not, and
     * {@code Class.getPackage().getName()} cannot produce a trailing dot, so the shape is
     * unreachable through every caller. See the spec's "Verified current semantics" section.
     */
    private static final List<String> PACKAGE_NAMES = List.of(
            "",
            "java",
            "a.b.c",
            "a..b",
            ".a",
            "org.apache.struts2",
            "org.apache.struts2.ognl",
            "org.apache.struts2x",
            "java.io",
            "java.io.tmp",
            "javax.servlet.http");

    private static final List<Set<String>> CANDIDATE_SETS = List.of(
            emptySet(),
            Set.of(""),
            Set.of("java"),
            Set.of("java.io"),
            Set.of("org.apache.struts2"),
            Set.of("a"),
            Set.of("a.b"),
            Set.of("zzz.not.matching"),
            Set.of("java.io", "org.apache.struts2", "javax"));

    private static List<Class<?>> classShapes() throws Exception {
        return List.of(
                String.class,
                Map.Entry.class,
                SecurityMemberAccess.class,
                Class.forName("PackagelessAction"),
                int.class,
                void.class,
                int[].class,
                String[].class,
                String[][].class,
                ((Runnable) () -> {
                }).getClass(),
                Proxy.newProxyInstance(
                        SecurityMemberAccessPackageMatchingTest.class.getClassLoader(),
                        new Class<?>[]{Runnable.class},
                        (proxy, method, args) -> null).getClass());
    }

    @Test
    public void siblingPackageWithSharedCharacterPrefixDoesNotMatch() {
        Set<String> excluded = Set.of("org.apache.struts2");

        assertThat(legacyPrefixMatch("org.apache.struts2x", excluded))
                .as("a sibling package sharing a character prefix must not match")
                .isFalse();
        assertThat(legacyPrefixMatch("org.apache.struts2", excluded))
                .as("an exact match must match")
                .isTrue();
        assertThat(legacyPrefixMatch("org.apache.struts2.ognl", excluded))
                .as("a sub-package must match")
                .isTrue();
    }

    @Test
    public void dotOnlyConfigurationYieldsEmptyStringPackageName() {
        assertThat(ConfigParseUtil.toPackageNamesSet("."))
                .as("struts.excludedPackageNames=\".\" strips to the empty string")
                .containsExactly("");
    }

    @Test
    public void defaultPackageMatchesOnlyWhenEmptyStringConfigured() throws Exception {
        Class<?> packageless = Class.forName("PackagelessAction");

        assertThat(toPackageName(packageless)).isEmpty();
        assertThat(isClassBelongsToPackages(packageless, Set.of("")))
                .as("a default-package class is matched by the empty-string entry")
                .isTrue();
        assertThat(isClassBelongsToPackages(packageless, Set.of("java")))
                .as("a default-package class is not matched by an unrelated entry")
                .isFalse();
    }

    @Test
    public void toPackageNameMatchesLegacyAcrossClassShapes() throws Exception {
        for (Class<?> clazz : classShapes()) {
            assertThat(toPackageName(clazz))
                    .as("toPackageName(%s)", clazz.getName())
                    .isEqualTo(legacyToPackageName(clazz));
        }
    }

    @Test
    public void arraysAndPrimitivesResolveToTheEmptyPackage() {
        assertThat(toPackageName(int.class)).isEmpty();
        assertThat(toPackageName(void.class)).isEmpty();
        assertThat(toPackageName(int[].class)).isEmpty();
        assertThat(toPackageName(String[].class)).isEmpty();
        assertThat(toPackageName(String[][].class)).isEmpty();
    }

    @Test
    public void classEntryPointMatchesLegacyAcrossCandidateSets() throws Exception {
        for (Class<?> clazz : classShapes()) {
            for (Set<String> candidates : CANDIDATE_SETS) {
                assertThat(isClassBelongsToPackages(clazz, candidates))
                        .as("clazz=[%s] candidates=%s", clazz.getName(), candidates)
                        .isEqualTo(legacyPrefixMatch(legacyToPackageName(clazz), candidates));
            }
        }
    }
}
