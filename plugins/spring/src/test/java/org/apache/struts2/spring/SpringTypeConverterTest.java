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
package org.apache.struts2.spring;

import junit.framework.TestCase;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.support.StaticApplicationContext;

/**
 * Tests for Spring bean name support in type converters.
 * <p>
 * Verifies that Spring bean names can be used in struts-conversion.properties
 * instead of fully qualified class names via SpringObjectFactory.getClassInstance().
 * </p>
 *
 * @see <a href="https://issues.apache.org/jira/browse/WW-4291">WW-4291</a>
 */
public class SpringTypeConverterTest extends TestCase {

    private StaticApplicationContext sac;
    private SpringObjectFactory objectFactory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        sac = new StaticApplicationContext();
        objectFactory = new SpringObjectFactory();
        objectFactory.setApplicationContext(sac);
    }

    @Override
    public void tearDown() throws Exception {
        sac = null;
        objectFactory = null;
        super.tearDown();
    }

    /**
     * Tests that SpringObjectFactory.getClassInstance() can resolve Spring bean names.
     * This is the core mechanism that enables WW-4291 - when user conversion properties
     * are processed during late initialization, SpringObjectFactory will be available
     * and can resolve bean names via containsBean() check.
     */
    public void testGetClassInstanceBySpringBeanName() throws Exception {
        // Register a class as a Spring bean
        sac.registerSingleton("myTestBean", TestBean.class, new MutablePropertyValues());

        // SpringObjectFactory.getClassInstance() should resolve bean name to class
        Class<?> clazz = objectFactory.getClassInstance("myTestBean");

        assertNotNull("Should resolve Spring bean name to class", clazz);
        assertEquals(TestBean.class, clazz);
    }

    /**
     * Tests that getClassInstance() falls back to class loading for fully qualified
     * class names (backward compatibility).
     */
    public void testGetClassInstanceByClassName() throws Exception {
        // Should work with fully qualified class name even if not a Spring bean
        Class<?> clazz = objectFactory.getClassInstance(TestBean.class.getName());

        assertNotNull("Should resolve class name", clazz);
        assertEquals(TestBean.class, clazz);
    }

    /**
     * Tests that an invalid bean/class name produces ClassNotFoundException.
     */
    public void testInvalidBeanNameThrowsException() {
        try {
            objectFactory.getClassInstance("nonExistentBeanOrClass");
            fail("Should throw ClassNotFoundException for non-existent bean/class");
        } catch (ClassNotFoundException e) {
            // Expected
            assertTrue("Exception message should mention the class name",
                e.getMessage().contains("nonExistentBeanOrClass"));
        }
    }

    /**
     * Tests that Spring bean takes precedence over class name when both exist.
     * If a bean is registered with a name that could also be a class, the bean wins.
     */
    public void testSpringBeanTakesPrecedence() throws Exception {
        // Register a bean with a name that looks like a class
        sac.registerSingleton("org.apache.struts2.spring.SpringTypeConverterTest$AnotherBean",
            TestBean.class, new MutablePropertyValues());

        // Should get TestBean.class (from Spring) not try to load the literal class name
        Class<?> clazz = objectFactory.getClassInstance(
            "org.apache.struts2.spring.SpringTypeConverterTest$AnotherBean");

        // The bean was registered with TestBean.class, so that's what we should get
        assertEquals(TestBean.class, clazz);
    }

    /**
     * A simple test bean class.
     */
    public static class TestBean {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * Another test bean class for precedence testing.
     */
    public static class AnotherBean {
        // Empty
    }
}
