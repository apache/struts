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

package org.apache.tiles.core.startup;

import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.core.factory.AbstractTilesContainerFactory;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link AbstractTilesInitializer}.
 *
 * @version $Rev$ $Date$
 */
public class AbstractTilesInitializerTest {

    /**
     * A mock Tiles container factory.
     */
    private AbstractTilesContainerFactory containerFactory;

    /**
     * The object to test.
     */
    private AbstractTilesInitializer initializer;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        containerFactory = createMock(AbstractTilesContainerFactory.class);
        initializer = new AbstractTilesInitializer() {

            @Override
            protected AbstractTilesContainerFactory createContainerFactory(
                ApplicationContext context) {
                return containerFactory;
            }
        };
    }

    /**
     * Test method for {@link AbstractTilesInitializer#initialize(ApplicationContext)}.
     */
    @Test
    public void testInitialize() {
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> scope = createMock(Map.class);

        expect(containerFactory.createContainer(context)).andReturn(container);
        expect(context.getApplicationScope()).andReturn(scope).anyTimes();
        expect(scope.put(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE,
            context)).andReturn(null);
        expect(scope.put(TilesAccess.CONTAINER_ATTRIBUTE, container)).andReturn(null);
        expect(scope.remove(TilesAccess.CONTAINER_ATTRIBUTE)).andReturn(container);

        replay(containerFactory, context, container, scope);
        initializer.initialize(context);
        initializer.destroy();
        verify(containerFactory, context, container, scope);
    }

    /**
     * Test method for {@link AbstractTilesInitializer#createTilesApplicationContext(ApplicationContext)}.
     */
    @Test
    public void testCreateTilesApplicationContext() {
        ApplicationContext context = createMock(ApplicationContext.class);
        replay(containerFactory, context);
        assertEquals(context, initializer.createTilesApplicationContext(context));
        verify(containerFactory, context);
    }

    /**
     * Test method for {@link AbstractTilesInitializer#getContainerKey(ApplicationContext)}.
     */
    @Test
    public void testGetContainerKey() {
        ApplicationContext context = createMock(ApplicationContext.class);
        replay(containerFactory, context);
        assertNull(initializer.getContainerKey(context));
        verify(containerFactory, context);
    }

    /**
     * Test method for {@link AbstractTilesInitializer#createContainer(ApplicationContext)}.
     */
    @Test
    public void testCreateContainer() {
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);

        expect(containerFactory.createContainer(context)).andReturn(container);

        replay(containerFactory, context, container);
        assertEquals(container, initializer.createContainer(context));
        verify(containerFactory, context, container);
    }
}
