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
package com.opensymphony.xwork2.inject;

import junit.framework.TestCase;

/**
 * ContainerImpl Tester.
 *
 * @author Lukasz Lenart
 * @version 1.0
 * @since <pre>11/26/2008</pre>
 */
public class ContainerImplTest extends TestCase {

    private Container c;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContainerBuilder cb = new ContainerBuilder();
        cb.constant("methodCheck.name", "Lukasz");
        cb.constant("fieldCheck.name", "Lukasz");
        c = cb.create(false);
    }

    /**
     * Inject values into field
     */
    public void testFieldInjector() throws Exception {

        FieldCheck fieldCheck = new FieldCheck();

        try {
            c.inject(fieldCheck);
            assertTrue(true);
        } catch (DependencyException expected) {
            fail("No exception expected!");
        }

        assertEquals(fieldCheck.getName(), "Lukasz");
    }

    /**
     * Inject values into method
     */
    public void testMethodInjector() throws Exception {

        MethodCheck methodCheck = new MethodCheck();

        try {
            c.inject(methodCheck);
            assertTrue(true);
        } catch (DependencyException expected) {
            fail("No exception expected!");
        }
    }

    /**
     * Inject values into field under SecurityManager
     */
    public void testFieldInjectorWithSecurityEnabled() throws Exception {

        System.setSecurityManager(new SecurityManager());

        FieldCheck fieldCheck = new FieldCheck();

        try {
            c.inject(fieldCheck);
            assertEquals(fieldCheck.getName(), "Lukasz");
            fail("Exception should be thrown!");
        } catch (DependencyException expected) {
            // that was expected
        }
    }

    /**
     * Inject values into method under SecurityManager
     */
    public void testMethodInjectorWithSecurityEnabled() throws Exception {

        // not needed, already set
        //System.setSecurityManager(new SecurityManager());

        MethodCheck methodCheck = new MethodCheck();

        try {
            c.inject(methodCheck);
            assertEquals(methodCheck.getName(), "Lukasz");
            fail("Exception sould be thrown!");
        } catch (DependencyException expected) {
            // that was expected
        }
    }

    class FieldCheck {

        @Inject("fieldCheck.name")
        private String name;

        public String getName() {
            return name;
        }
    }

    class MethodCheck {

        private String name;

        @Inject("methodCheck.name")
        private void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
