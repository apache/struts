/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.conversion.impl;

import junit.framework.TestCase;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class InstantiatingNullHandlerTest extends TestCase {

    public void testBlank() {

    }
    /*public void testInheritance() {
        Tiger t = new Tiger();
        CompoundRoot root = new CompoundRoot();
        root.add(t);

        Map context = new OgnlContext();
        context.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);

        InstantiatingNullHandler nh = new InstantiatingNullHandler();

        Object dogList = nh.nullPropertyValue(context, root, "dogs");
        Class clazz = nh.getCollectionType(Tiger.class, "dogs");
        assertEquals(Dog.class, clazz);
        assertNotNull(dogList);
        assertTrue(dogList instanceof List);

        Object kittenList = nh.nullPropertyValue(context, root, "kittens");
        clazz = nh.getCollectionType(Tiger.class, "kittens");
        assertEquals(Cat.class, clazz);
        assertNotNull(kittenList);
        assertTrue(kittenList instanceof List);
    }*/
}
