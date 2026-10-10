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
package org.apache.struts2.config.entities;

import org.apache.struts2.XWorkTestCase;

import java.util.Map;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

public class PackageConfigTest extends XWorkTestCase {

    public void testFullDefaultInterceptorRef() {
        PackageConfig cfg1 = new PackageConfig.Builder("pkg1")
                .defaultInterceptorRef("ref1").build();
        PackageConfig cfg2 = new PackageConfig.Builder("pkg2").defaultInterceptorRef("ref2").build();
        PackageConfig cfg = new PackageConfig.Builder("pkg")
                .addParent(cfg1)
                .addParent(cfg2)
                .build();

        assertEquals("ref2", cfg.getFullDefaultInterceptorRef());
    }

    public void testStrictDMIInheritance() {
        // given
        PackageConfig parent = new PackageConfig.Builder("parent").build();

        // when
        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .build();

        // then
        assertTrue(child.isStrictMethodInvocation());
    }

    public void testStrictDMIInheritanceDisabledInParentPackage() {
        // given
        PackageConfig parent = new PackageConfig.Builder("parent")
                .strictMethodInvocation(false)
                .build();

        // when
        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .build();

        // then
        assertTrue(child.isStrictMethodInvocation());
    }

    public void testStrictDMIInheritanceDisabledInBothPackage() {
        // given
        PackageConfig parent = new PackageConfig.Builder("parent")
                .strictMethodInvocation(false)
                .build();

        // when
        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .strictMethodInvocation(false)
                .build();

        // then
        assertFalse(child.isStrictMethodInvocation());
    }

    public void testStrictDMIInheritanceDisabledInChildPackage() {
        // given
        PackageConfig parent = new PackageConfig.Builder("parent").build();

        // when
        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .strictMethodInvocation(false)
                .build();

        // then
        assertFalse(child.isStrictMethodInvocation());
    }

    public void testScopedConstantsChildOverridesParent() {
        PackageConfig parent = new PackageConfig.Builder("parent")
                .addScopedConstant("first", "parent-first")
                .addScopedConstant("second", "parent-second")
                .build();

        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .addScopedConstant("second", "child-second")
                .build();

        assertEquals(Map.of("second", "child-second"), child.getScopedConstants());
        assertEquals(Map.of("first", "parent-first", "second", "child-second"), child.getAllScopedConstants());
    }

    public void testScopedConstantsInheritedThroughTwoLevels() {
        PackageConfig grandparent = new PackageConfig.Builder("grandparent")
                .addScopedConstant("first", "grandparent-first")
                .build();
        PackageConfig parent = new PackageConfig.Builder("parent")
                .addParent(grandparent)
                .build();

        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .build();

        assertEquals(Map.of(), child.getScopedConstants());
        assertEquals(Map.of("first", "grandparent-first"), child.getAllScopedConstants());
    }

    public void testScopedConstantsFirstAddedParentWins() {
        PackageConfig first = new PackageConfig.Builder("first")
                .addScopedConstant("shared", "from-first")
                .build();
        PackageConfig second = new PackageConfig.Builder("second")
                .addScopedConstant("shared", "from-second")
                .build();

        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(first)
                .addParent(second)
                .build();

        assertEquals("from-first", child.getAllScopedConstants().get("shared"));
    }

    public void testScopedConstantsSurviveBuilderCopy() {
        PackageConfig parent = new PackageConfig.Builder("parent")
                .addScopedConstant("first", "parent-first")
                .build();
        PackageConfig original = new PackageConfig.Builder("child")
                .addParent(parent)
                .addScopedConstant("second", "child-second")
                .build();

        PackageConfig copy = new PackageConfig.Builder(original).build();

        assertEquals(original.getScopedConstants(), copy.getScopedConstants());
        assertEquals(original.getAllScopedConstants(), copy.getAllScopedConstants());
    }

    public void testScopedConstantsAreUnmodifiableAfterBuild() {
        PackageConfig config = new PackageConfig.Builder("pkg")
                .addScopedConstant("first", "value")
                .build();

        Map<String, String> own = config.getScopedConstants();
        Map<String, String> all = config.getAllScopedConstants();

        assertThrows(UnsupportedOperationException.class, () -> own.put("x", "y"));
        assertThrows(UnsupportedOperationException.class, () -> all.put("x", "y"));
    }

    public void testScopedConstantsTakePartInEquality() {
        PackageConfig plain = new PackageConfig.Builder("pkg").build();
        PackageConfig scoped = new PackageConfig.Builder("pkg").addScopedConstant("first", "value").build();

        assertNotEquals(plain, scoped);
    }

}
