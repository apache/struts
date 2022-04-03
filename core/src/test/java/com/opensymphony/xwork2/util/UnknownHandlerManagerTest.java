/*
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
package com.opensymphony.xwork2.util;

import java.util.List;

import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.UnknownHandlerManagerMock;
import com.opensymphony.xwork2.DefaultUnknownHandlerManager;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.providers.ConfigurationTestBase;
import com.opensymphony.xwork2.config.providers.SomeUnknownHandler;

/**
 * Test UnknownHandlerUtil
 */
public class UnknownHandlerManagerTest extends ConfigurationTestBase {

    public void testStack() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-unknownhandler-stack.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        loadConfigurationProviders(provider);
        configurationManager.reload();

        UnknownHandlerManager unknownHandlerManager = new DefaultUnknownHandlerManager();
        container.inject(unknownHandlerManager);
        List<UnknownHandler> unknownHandlers = unknownHandlerManager.getUnknownHandlers();

        assertNotNull(unknownHandlers);
        assertEquals(2, unknownHandlers.size());

        UnknownHandler uh1 = unknownHandlers.get(0);
        UnknownHandler uh2 = unknownHandlers.get(1);

        assertTrue(uh1 instanceof SomeUnknownHandler);
        assertTrue(uh2 instanceof SomeUnknownHandler);
    }

    public void testEmptyStack() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-unknownhandler-stack-empty.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        loadConfigurationProviders(provider);
        configurationManager.reload();

        UnknownHandlerManager unknownHandlerManager = new DefaultUnknownHandlerManager();
        container.inject(unknownHandlerManager);
        List<UnknownHandler> unknownHandlers = unknownHandlerManager.getUnknownHandlers();

        assertNotNull(unknownHandlers);
        assertEquals(2, unknownHandlers.size());

        UnknownHandler uh1 = unknownHandlers.get(0);
        UnknownHandler uh2 = unknownHandlers.get(1);

        assertTrue(uh1 instanceof SomeUnknownHandler);
        assertTrue(uh2 instanceof SomeUnknownHandler);
    }

    public void testInvocationOrder() throws ConfigurationException, NoSuchMethodException {
        SomeUnknownHandler uh1 = new SomeUnknownHandler();
        uh1.setActionMethodResult("uh1");

        SomeUnknownHandler uh2 = new SomeUnknownHandler();
        uh2.setActionMethodResult("uh2");

        UnknownHandlerManagerMock uhm = new UnknownHandlerManagerMock();
        uhm.addUnknownHandler(uh1);
        uhm.addUnknownHandler(uh2);

        //should pick the first one
        assertEquals("uh1", uhm.handleUnknownMethod(null, null));

        //should pick the second one
        uh1.setActionMethodResult(null);
        assertEquals("uh2", uhm.handleUnknownMethod(null, null));

        //should not pick any
        uh1.setActionMethodResult(null);
        uh2.setActionMethodResult(null);
        try {
            uhm.handleUnknownMethod(null, null);
            fail("Should throw exception!");
        } catch (NoSuchMethodException e) {
            assertTrue(true);
        }
    }
}
