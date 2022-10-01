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

package org.apache.tiles.template;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link ImportAttributeModel}.
 */
public class ImportAttributeModelTest {

    /**
     * The size of the attributes collection.
     */
    private static final int ATTRIBUTES_SIZE = 4;

    /**
     * The model to test.
     */
    private ImportAttributeModel model;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        model = new ImportAttributeModel();
    }

    /**
     * Test method for {@link ImportAttributeModel
     * #execute(String, String, String, boolean, Request).
     */
    @Test
    public void testExecuteSingle() {
        TilesContainer container = createMock(TilesContainer.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Attribute attribute = new Attribute();
        Request request = createMock(Request.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(applicationContext);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(attribute);
        expect(container.evaluate(attribute, request)).andReturn("myEvaluatedValue");

        replay(container, attributeContext, request, applicationContext);
        model.execute("myName", "request", null, false, request);
        assertEquals(2, requestScope.size());
        assertEquals("myEvaluatedValue", requestScope.get("myName"));
        verify(container, attributeContext, request, applicationContext);
    }

    /**
     * Test method for {@link ImportAttributeModel
     * #execute(String, String, String, boolean, Request).
     */
    @Test
    public void testExecuteSingleToName() {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Attribute attribute = new Attribute();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(applicationContext);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(attribute);
        expect(container.evaluate(attribute, request)).andReturn("myEvaluatedValue");

        replay(container, attributeContext, request, applicationContext);
        model.execute("myName", "request", "myToName", false, request);
        assertEquals(2, requestScope.size());
        assertEquals("myEvaluatedValue", requestScope.get("myToName"));
        verify(container, attributeContext, request, applicationContext);
    }

    /**
     * Test method for {@link ImportAttributeModel
     * #execute(String, String, String, boolean, Request).
     */
    @Test
    public void testExecuteAll() {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Attribute attribute1 = new Attribute("myValue1");
        Attribute attribute2 = new Attribute("myValue2");
        Attribute attribute3 = new Attribute("myValue3");
        Set<String> cascadedNames = new HashSet<>();
        cascadedNames.add("myName1");
        cascadedNames.add("myName2");
        Set<String> localNames = new HashSet<>();
        localNames.add("myName1");
        localNames.add("myName3");
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(applicationContext);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getCascadedAttributeNames()).andReturn(cascadedNames);
        expect(attributeContext.getLocalAttributeNames()).andReturn(localNames);
        expect(attributeContext.getAttribute("myName1")).andReturn(attribute1).times(2);
        expect(attributeContext.getAttribute("myName2")).andReturn(attribute2);
        expect(attributeContext.getAttribute("myName3")).andReturn(attribute3);
        expect(container.evaluate(attribute1, request)).andReturn("myEvaluatedValue1").times(2);
        expect(container.evaluate(attribute2, request)).andReturn("myEvaluatedValue2");
        expect(container.evaluate(attribute3, request)).andReturn("myEvaluatedValue3");

        replay(container, attributeContext, request, applicationContext);
        model.execute(null, "request", null, false, request);
        assertEquals(ATTRIBUTES_SIZE, requestScope.size());
        assertEquals("myEvaluatedValue1", requestScope.get("myName1"));
        assertEquals("myEvaluatedValue2", requestScope.get("myName2"));
        assertEquals("myEvaluatedValue3", requestScope.get("myName3"));
        verify(container, attributeContext, request, applicationContext);
    }

    /**
     * Test method for {@link ImportAttributeModel
     * #execute(String, String, String, boolean, Request).
     */
    @Test(expected = NoSuchAttributeException.class)
    public void testExecuteSingleNullAttributeException() {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(applicationContext);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(null);

        replay(container, attributeContext, request, applicationContext);
        try {
            model.execute("myName", "request", null, false, request);
        } finally {
            verify(container, attributeContext, request, applicationContext);
        }
    }

    /**
     * Test method for {@link ImportAttributeModel
     * #execute(String, String, String, boolean, Request).
     */
    @Test(expected = NoSuchAttributeException.class)
    public void testExecuteSingleNullAttributeValueException() {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Attribute attribute = new Attribute();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(applicationContext);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(attribute);
        expect(container.evaluate(attribute, request)).andReturn(null);

        replay(container, attributeContext, request, applicationContext);
        try {
            model.execute("myName", "request", null, false, request);
        } finally {
            verify(container, attributeContext, request, applicationContext);
        }
    }

    /**
     * Test method for {@link ImportAttributeModel
     * #execute(String, String, String, boolean, Request).
     */
    @Test(expected = RuntimeException.class)
    public void testExecuteSingleRuntimeException() {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Attribute attribute = new Attribute();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(applicationContext);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(attribute);
        expect(container.evaluate(attribute, request)).andThrow(new RuntimeException());

        replay(container, attributeContext, request, applicationContext);
        try {
            model.execute("myName", "request", null, false, request);
        } finally {
            verify(container, attributeContext, request, applicationContext);
        }
    }

    /**
     * Test method for {@link ImportAttributeModel
     * #execute(String, String, String, boolean, Request).
     */
    @Test
    public void testExecuteSingleNullAttributeIgnore() {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(applicationContext);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(null);

        replay(container, attributeContext, request, applicationContext);
        model.execute("myName", "request", null, true, request);
        verify(container, attributeContext, request, applicationContext);
    }

    /**
     * Test method for {@link ImportAttributeModel
     * #execute(String, String, String, boolean, Request).
     */
    @Test
    public void testExecuteSingleNullAttributeValueIgnore() {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Attribute attribute = new Attribute();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(applicationContext);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(attribute);
        expect(container.evaluate(attribute, request)).andReturn(null);

        replay(container, attributeContext, request, applicationContext);
        model.execute("myName", "request", null, true, request);
        verify(container, attributeContext, request, applicationContext);
    }

    /**
     * Test method for {@link ImportAttributeModel
     * #execute(String, String, String, boolean, Request).
     */
    @Test
    public void testExecuteSingleRuntimeIgnore() {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Attribute attribute = new Attribute();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(applicationContext);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(attribute);
        expect(container.evaluate(attribute, request)).andThrow(new RuntimeException());

        replay(container, attributeContext, request, applicationContext);
        model.execute("myName", "request", null, true, request);
        verify(container, attributeContext, request, applicationContext);
    }
}
