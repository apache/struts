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

import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Tests {@link InsertDefinitionModel}.
 */
public class InsertDefinitionModelTest {

    /**
     * The model to test.
     */
    private InsertDefinitionModel model;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        model = new InsertDefinitionModel();
    }

    /**
     * Test method for {@link InsertDefinitionModel
     * #execute(java.lang.String, java.lang.String, String,
     * String, java.lang.String, java.lang.String, boolean, Request, ModelBody)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testExecute() throws IOException {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        ModelBody modelBody = createMock(ModelBody.class);

        modelBody.evaluateWithoutWriting();
        expect(request.getApplicationContext()).andReturn(applicationContext).times(2);
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(container.startContext(request)).andReturn(attributeContext);
        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        container.endContext(request);
        attributeContext.setPreparer("myPreparer");
        attributeContext.setTemplateAttribute(notNull());
        container.render("myDefinitionName", request);

        replay(container, attributeContext, request, applicationContext, modelBody);
        model.execute("myDefinitionName", "myTemplate", "myTemplateType", "myTemplateExpression", "myRole", "myPreparer", false, request, modelBody);
        verify(container, attributeContext, request, applicationContext, modelBody);
    }

}
