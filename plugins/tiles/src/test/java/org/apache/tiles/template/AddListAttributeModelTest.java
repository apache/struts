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

import org.apache.tiles.api.ListAttribute;
import org.apache.tiles.autotag.core.runtime.ModelBody;
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
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link AddListAttributeModel}.
 */
public class AddListAttributeModelTest {

    /**
     * The model to test.
     */
    private AddListAttributeModel model;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        model = new AddListAttributeModel();
    }

    /**
     * Test method for
     * {@link AddListAttributeModel#execute(String, Request, ModelBody)}
     * .
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testExecute() throws IOException {
        Deque<Object> composeStack = new ArrayDeque<>();
        Request request = createMock(Request.class);
        Map<String, Object> requestScope = new HashMap<>();
        ModelBody modelBody = createMock(ModelBody.class);

        modelBody.evaluateWithoutWriting();
        requestScope.put(ComposeStackUtil.COMPOSE_STACK_ATTRIBUTE_NAME, composeStack);
        expect(request.getContext("request")).andReturn(requestScope);

        replay(request, modelBody);
        ListAttribute parent = new ListAttribute();
        composeStack.push(parent);
        model.execute("myRole", request, modelBody);
        assertEquals(1, composeStack.size());
        assertEquals(parent, composeStack.pop());
        assertEquals(1, parent.getValue().size());
        ListAttribute listAttribute = (ListAttribute) parent.getValue().get(0);
        assertEquals("myRole", listAttribute.getRole());
        verify(request, modelBody);
    }
}
