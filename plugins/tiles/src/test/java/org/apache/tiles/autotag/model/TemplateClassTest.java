/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.autotag.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TemplateClassTest {

    /**
     * Test method for {@link TemplateClass#TemplateClass(String)}.
     */
    @Test
    public void testTemplateConstructor1() {
        TemplateClass templateClass = new TemplateClass("name");
        assertEquals("name", templateClass.getName());
        assertNull(templateClass.getTagName());
        assertNull(templateClass.getTagClassPrefix());
        assertNull(templateClass.getExecuteMethod());
        Collection<TemplateParameter> params = templateClass.getParameters();
        assertTrue(params.isEmpty());
    }

    /**
     * Test method for {@link TemplateClass#TemplateClass(String, String, String, TemplateMethod)}.
     */
    @Test
    public void testTemplateConstructor2() {
        TemplateMethod method = createMock(TemplateMethod.class);

        replay(method);
        TemplateClass templateClass = new TemplateClass("name", "tagName", "tagClassPrefix", method);
        assertEquals("name", templateClass.getName());
        assertEquals("tagName", templateClass.getTagName());
        assertEquals("tagClassPrefix", templateClass.getTagClassPrefix());
        assertEquals(method, templateClass.getExecuteMethod());
        verify(method);
    }

    /**
     * Test method for {@link TemplateClass#getSimpleName()}.
     */
    @Test
    public void testGetSimpleName() {
        TemplateClass templateClass = new TemplateClass("name");
        assertEquals("name", templateClass.getSimpleName());
        templateClass = new TemplateClass("org.whatever.Hello");
        assertEquals("Hello", templateClass.getSimpleName());
    }

    /**
     * Test method for {@link TemplateClass#setDocumentation(String)}.
     */
    @Test
    public void testSetDocumentation() {
        TemplateClass templateClass = new TemplateClass("name");
        templateClass.setDocumentation("docs");
        assertEquals("docs", templateClass.getDocumentation());
    }

    /**
     * Test method for {@link TemplateClass#getParameters()}.
     */
    @Test
    public void testGetParameters() {
        TemplateParameter param1 = createMock(TemplateParameter.class);
        TemplateParameter param2 = createMock(TemplateParameter.class);
        TemplateParameter param3 = createMock(TemplateParameter.class);
        TemplateParameter param4 = createMock(TemplateParameter.class);
        TemplateMethod method = createMock(TemplateMethod.class);
        List<TemplateParameter> params = new ArrayList<>();

        expect(method.getParameters()).andReturn(params);
        expect(param1.isRequest()).andReturn(true);
        expect(param2.isRequest()).andReturn(false);
        expect(param2.isBody()).andReturn(true);
        expect(param3.isRequest()).andReturn(false);
        expect(param3.isBody()).andReturn(false);
        expect(param4.isRequest()).andReturn(false);
        expect(param4.isBody()).andReturn(false);
        expect(param3.getName()).andReturn("param1");
        expect(param4.getName()).andReturn("param2");

        replay(param1, param2, param3, param4, method);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        params.add(param4);

        TemplateClass templateClass = new TemplateClass("name", "tagName", "tagClassPrefix", method);
        Collection<TemplateParameter> returnedParams = templateClass.getParameters();
        Iterator<TemplateParameter> paramIt = returnedParams.iterator();
        assertSame(param3, paramIt.next());
        assertSame(param4, paramIt.next());
        assertFalse(paramIt.hasNext());
        verify(param1, param2, param3, param4, method);
    }

    /**
     * Test method for {@link TemplateClass#hasBody()}.
     */
    @Test
    public void testHasBody() {
        TemplateMethod method = createMock(TemplateMethod.class);
        expect(method.hasBody()).andReturn(true);

        replay(method);
        TemplateClass templateClass = new TemplateClass("name", "tagName", "tagClassPrefix", method);
        assertTrue(templateClass.hasBody());
        verify(method);
    }

    /**
     * Test method for {@link TemplateClass#toString()}.
     */
    @Test
    public void testToString() {
        TemplateMethod method = new TemplateMethod("method", new ArrayList<>());
        TemplateClass templateClass = new TemplateClass("name", "tagName", "tagClassPrefix", method);
        assertEquals("TemplateClass [name=name, tagName=tagName, tagClassPrefix=tagClassPrefix, " + "documentation=null, executeMethod=TemplateMethod " + "[name=method, documentation=null, parameters={}]]", templateClass.toString());
    }

}
