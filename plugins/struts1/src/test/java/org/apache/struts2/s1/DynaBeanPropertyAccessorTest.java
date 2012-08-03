/*
 * $Id$
 *
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

package org.apache.struts2.s1;

import junit.framework.*;
import java.io.*;
import java.util.*;
import org.apache.commons.beanutils.*;

import ognl.*;

/**  Description of the Class */
public class DynaBeanPropertyAccessorTest extends TestCase {

    protected DynaBean bean = null;
    
    public DynaBeanPropertyAccessorTest(String name) throws Exception {
        super(name);
    }


    public static void main(String args[]) {
        junit.textui.TestRunner.run(DynaBeanPropertyAccessorTest.class);
    }

    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {

        // Instantiate a new DynaBean instance
        DynaClass dynaClass = createDynaClass();
        bean = dynaClass.newInstance();

        // Initialize the DynaBean's property values (like TestBean)
        bean.set("booleanProperty", new Boolean(true));
        bean.set("booleanSecond", new Boolean(true));
        bean.set("doubleProperty", new Double(321.0));
        bean.set("floatProperty", new Float((float) 123.0));
        int intArray[] = { 0, 10, 20, 30, 40 };
        bean.set("intArray", intArray);
        int intIndexed[] = { 0, 10, 20, 30, 40 };
        bean.set("intIndexed", intIndexed);
        bean.set("intProperty", new Integer(123));
        List listIndexed = new ArrayList();
        listIndexed.add("String 0");
        listIndexed.add("String 1");
        listIndexed.add("String 2");
        listIndexed.add("String 3");
        listIndexed.add("String 4");
        bean.set("listIndexed", listIndexed);
        bean.set("longProperty", new Long((long) 321));
        HashMap mappedProperty = new HashMap();
        mappedProperty.put("First Key", "First Value");
        mappedProperty.put("Second Key", "Second Value");
        bean.set("mappedProperty", mappedProperty);
        HashMap mappedIntProperty = new HashMap();
        mappedIntProperty.put("One", new Integer(1));
        mappedIntProperty.put("Two", new Integer(2));
        bean.set("mappedIntProperty", mappedIntProperty);
        // Property "nullProperty" is not initialized, so it should return null
        bean.set("shortProperty", new Short((short) 987));
        String stringArray[] =
                { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringArray", stringArray);
        String stringIndexed[] =
                { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringIndexed", stringIndexed);
        bean.set("stringProperty", "This is a string");

    }




    public void testGetProperty() throws Exception {
        
        DynaBeanPropertyAccessor trans = new DynaBeanPropertyAccessor();
        assertTrue("This is a string".equals(trans.getProperty(null, bean, "stringProperty"))); 
        assertTrue(trans.getProperty(null, bean, "listIndexed") instanceof List); 
        
    }

    public void testSetProperty() throws Exception {
        
        DynaBeanPropertyAccessor trans = new DynaBeanPropertyAccessor();
        trans.setProperty(null, bean, "stringProperty", "bob");
        assertTrue("bob".equals(trans.getProperty(null, bean, "stringProperty"))); 
        
    }

    public void testOGNL() throws Exception {
        
        OgnlRuntime.setPropertyAccessor(DynaBean.class, new DynaBeanPropertyAccessor());

        assertTrue("This is a string".equals(Ognl.getValue("stringProperty", bean)));

    }


    /**
     * Create and return a <code>DynaClass</code> instance for our test
     * <code>DynaBean</code>.
     */
    protected DynaClass createDynaClass() {

        int intArray[] = new int[0];
        String stringArray[] = new String[0];

        DynaClass dynaClass = new BasicDynaClass
                ("TestDynaClass", null,
                        new DynaProperty[]{
                            new DynaProperty("booleanProperty", Boolean.TYPE),
                            new DynaProperty("booleanSecond", Boolean.TYPE),
                            new DynaProperty("doubleProperty", Double.TYPE),
                            new DynaProperty("floatProperty", Float.TYPE),
                            new DynaProperty("intArray", intArray.getClass()),
                            new DynaProperty("intIndexed", intArray.getClass()),
                            new DynaProperty("intProperty", Integer.TYPE),
                            new DynaProperty("listIndexed", List.class),
                            new DynaProperty("longProperty", Long.TYPE),
                            new DynaProperty("mappedProperty", Map.class),
                            new DynaProperty("mappedIntProperty", Map.class),
                            new DynaProperty("nullProperty", String.class),
                            new DynaProperty("shortProperty", Short.TYPE),
                            new DynaProperty("stringArray", stringArray.getClass()),
                            new DynaProperty("stringIndexed", stringArray.getClass()),
                            new DynaProperty("stringProperty", String.class),
                        });
        return (dynaClass);

    }


}

