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
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.XWorkTestCase;

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

}
