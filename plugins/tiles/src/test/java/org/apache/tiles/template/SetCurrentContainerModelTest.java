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

import org.apache.tiles.api.NoSuchContainerException;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link SetCurrentContainerModel}.
 */
public class SetCurrentContainerModelTest {

    /**
     * Test method for {@link SetCurrentContainerModel#execute(String, Request)}.
     */
    @Test
    public void testSetCurrentContainer() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<>();
        attribs.put("myKey", container);
        Map<String, Object> requestScope = new HashMap<>();

        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope);
        expect(request.getApplicationContext()).andReturn(context);
        replay(request, context, container);
        SetCurrentContainerModel model = new SetCurrentContainerModel();
        model.execute("myKey", request);
        assertEquals(container, requestScope.get(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME));
        verify(request, context, container);
    }

    /**
     * Test method for {@link SetCurrentContainerModel#execute(String, Request)}.
     */
    @Test(expected = NoSuchContainerException.class)
    public void testSetCurrentContainerException() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        Map<String, Object> attribs = new HashMap<>();

        expect(request.getApplicationContext()).andReturn(context);
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        replay(request, context);
        try {
            SetCurrentContainerModel model = new SetCurrentContainerModel();
            model.execute("myKey", request);
        } finally {
            verify(request, context);
        }
    }

}
