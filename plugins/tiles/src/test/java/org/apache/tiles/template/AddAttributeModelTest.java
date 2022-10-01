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
import org.apache.tiles.api.ListAttribute;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link AddAttributeModel}.
 */
public class AddAttributeModelTest {

    /**
     * The model to test.
     */
    private AddAttributeModel model;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        model = new AddAttributeModel();
    }

    /**
     * Test method for {@link AddAttributeModel
     * #execute(java.lang.Object, java.lang.String, java.lang.String, java.lang.String,
     * Request, ModelBody)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testExecute() throws IOException {
        Request request = createMock(Request.class);
        ModelBody modelBody = createMock(ModelBody.class);
        Deque<Object> composeStack = new ArrayDeque<>();
        ListAttribute listAttribute = new ListAttribute();
        Attribute attribute;
        composeStack.push(listAttribute);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(ComposeStackUtil.COMPOSE_STACK_ATTRIBUTE_NAME, composeStack);

        expect(request.getContext("request")).andReturn(requestScope).times(2);
        expect(modelBody.evaluateAsString()).andReturn(null);
        expect(modelBody.evaluateAsString()).andReturn("myBody");

        replay(request, modelBody);
        model.execute("myValue", "myExpression", "myRole", "myType",
            request, modelBody);
        List<Attribute> attributes = listAttribute.getValue();
        assertEquals(1, attributes.size());
        attribute = attributes.iterator().next();
        assertEquals("myValue", attribute.getValue());
        assertEquals("myExpression", attribute.getExpressionObject().getExpression());
        assertEquals("myRole", attribute.getRole());
        assertEquals("myType", attribute.getRenderer());

        composeStack.clear();
        listAttribute = new ListAttribute();
        attribute = new Attribute();
        composeStack.push(listAttribute);
        composeStack.push(attribute);

        model.execute(null, "myExpression", "myRole", "myType", request,
            modelBody);
        attributes = listAttribute.getValue();
        assertEquals(1, attributes.size());
        attribute = attributes.iterator().next();
        assertEquals("myBody", attribute.getValue());
        assertEquals("myExpression", attribute.getExpressionObject()
            .getExpression());
        assertEquals("myRole", attribute.getRole());
        assertEquals("myType", attribute.getRenderer());
        verify(request, modelBody);
    }

}
