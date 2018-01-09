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
        cb.factory(EarlyInitializable.class, EarlyInitializableBean.class, Scope.SINGLETON);
        cb.factory(Initializable.class, InitializableBean.class, Scope.SINGLETON);
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
    public void disabled_testFieldInjectorWithSecurityEnabled() throws Exception {

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
    public void disabled_testMethodInjectorWithSecurityEnabled() throws Exception {

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

    public void testEarlyInitializable() throws Exception {
        assertTrue("should being initialized already", EarlyInitializableBean.initializedEarly);

        EarlyInitializableCheck earlyInitializableCheck = new EarlyInitializableCheck();
        c.inject(earlyInitializableCheck);
        assertEquals("initialized early", ((EarlyInitializableBean) earlyInitializableCheck.getEarlyInitializable()).getMessage());

        EarlyInitializableCheck earlyInitializableCheck2 = new EarlyInitializableCheck();
        c.inject(earlyInitializableCheck2);
        assertEquals("initialized early", ((EarlyInitializableBean) earlyInitializableCheck2.getEarlyInitializable()).getMessage());
    }

    public void testInitializable() throws Exception {
        assertFalse("should not being initialized already", InitializableBean.initialized);

        InitializableCheck initializableCheck = new InitializableCheck();
        c.inject(initializableCheck);
        assertTrue("should being initialized here", InitializableBean.initialized);
        assertEquals("initialized", ((InitializableBean) initializableCheck.getInitializable()).getMessage());

        InitializableCheck initializableCheck2 = new InitializableCheck();
        c.inject(initializableCheck2);
        assertEquals("initialized", ((InitializableBean) initializableCheck2.getInitializable()).getMessage());
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

    class InitializableCheck {

        private Initializable initializable;

        @Inject
        public void setInitializable(Initializable initializable) {
            this.initializable = initializable;
        }

        public Initializable getInitializable() {
            return initializable;
        }
    }

    class EarlyInitializableCheck {

        private EarlyInitializable earlyInitializable;

        @Inject
        public void setEarlyInitializable(EarlyInitializable earlyInitializable) {
            this.earlyInitializable = earlyInitializable;
        }

        public EarlyInitializable getEarlyInitializable() {
            return earlyInitializable;
        }
    }

}
