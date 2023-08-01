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
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Tests {@link InsertAttributeModel}.
 */
public class InsertAttributeModelTest {

    /**
     * The mock resolver.
     */
    private AttributeResolver resolver;

    /**
     * The model to test.
     */
    private InsertAttributeModel model;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        resolver = createMock(AttributeResolver.class);
        model = new InsertAttributeModel(resolver);
    }

    /**
     * Test method for {@link InsertAttributeModel
     * #execute(boolean, String, String, Object, String, String, String,
     * Attribute, boolean, Request, ModelBody)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testExecute() throws IOException {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        Attribute attribute = new Attribute("myValue");
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        ModelBody modelBody = createMock(ModelBody.class);

        modelBody.evaluateWithoutWriting();
        expect(request.getApplicationContext()).andReturn(applicationContext).times(2);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();

        container.prepare("myPreparer", request);
        expect(resolver.computeAttribute(container, attribute, "myName", "myRole", false, "myDefaultValue", "myDefaultValueRole", "myDefaultValueType", request)).andReturn(attribute);
        expect(container.startContext(request)).andReturn(attributeContext);
        container.endContext(request);
        container.render(attribute, request);

        replay(resolver, container, request, applicationContext, modelBody);
        model.execute(false, "myPreparer", "myRole", "myDefaultValue", "myDefaultValueRole", "myDefaultValueType", "myName", attribute, false, request, modelBody);
        verify(resolver, container, request, applicationContext, modelBody);
    }

    /**
     * Test method for {@link InsertAttributeModel
     * #execute(boolean, String, String, Object, String, String, String,
     * Attribute, boolean, Request, ModelBody)} when ignore flag is set.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testExecuteIgnore() throws IOException {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        Deque<Object> composeStack = new ArrayDeque<>();
        requestScope.put(ComposeStackUtil.COMPOSE_STACK_ATTRIBUTE_NAME, composeStack);
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        ModelBody modelBody = createMock(ModelBody.class);

        modelBody.evaluateWithoutWriting();
        expect(request.getApplicationContext()).andReturn(applicationContext).times(2);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();

        container.prepare("myPreparer", request);
        expect(resolver.computeAttribute(container, null, "myName", "myRole", true, "myDefaultValue", "myDefaultValueRole", "myDefaultValueType", request)).andReturn(null);
        expect(container.startContext(request)).andReturn(attributeContext);
        container.endContext(request);

        replay(resolver, container, request, applicationContext, modelBody);
        model.execute(true, "myPreparer", "myRole", "myDefaultValue", "myDefaultValueRole", "myDefaultValueType", "myName", null, false, request, modelBody);
        verify(resolver, container, request, applicationContext, modelBody);
    }
}
