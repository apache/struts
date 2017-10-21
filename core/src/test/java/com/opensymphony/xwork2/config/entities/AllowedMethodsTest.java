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

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class AllowedMethodsTest extends TestCase {

    public void testLiteralMethods() throws Exception {
        // given
        String method = "myMethod";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(false, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed(method));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

    public void testWildcardMethodsWithNoSMI() throws Exception {
        // given
        String method = "my{1}";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(false, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("myMethod"));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

    public void testWildcardMethodsWithSMI() throws Exception {
        // given
        Set<String> literals = new HashSet<>();
        literals.add("my{1}");
        literals.add("myMethod");

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(true, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertFalse(allowedMethods.isAllowed("my{1}"));
        assertTrue(allowedMethods.isAllowed("myMethod"));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

    public void testWildcardWithStarMethodsWithNoSMI() throws Exception {
        // given
        String method = "cancel*Action*";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(false, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("cancelAction"));
        assertFalse(allowedMethods.isAllowed("startEvent"));
    }

    public void testWildcardWithStarMethodsWithSMI() throws Exception {
        // given
        String method = "cancel*";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(true, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("cancel*"));
        assertFalse(allowedMethods.isAllowed("cancelAction"));
        assertFalse(allowedMethods.isAllowed("startEvent"));
    }

    public void testRegexMethods() throws Exception {
        // given
        String method = "regex:my([a-zA-Z].*)";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(true, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("myMethod"));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

}
