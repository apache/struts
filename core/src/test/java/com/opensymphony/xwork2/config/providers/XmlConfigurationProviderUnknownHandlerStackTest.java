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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.DefaultUnknownHandlerManager;

import java.util.List;

public class XmlConfigurationProviderUnknownHandlerStackTest extends ConfigurationTestBase {

    public void testStackWithElements() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-unknownhandler-stack.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        loadConfigurationProviders(provider);
        configurationManager.reload();

        List<UnknownHandlerConfig> unknownHandlerStack = configuration.getUnknownHandlerStack();
        assertNotNull(unknownHandlerStack);
        assertEquals(2, unknownHandlerStack.size());

        assertEquals("uh1", unknownHandlerStack.get(0).getName());
        assertEquals("uh2", unknownHandlerStack.get(1).getName());

        UnknownHandlerManager unknownHandlerManager = new DefaultUnknownHandlerManager();
        container.inject(unknownHandlerManager);
        assertTrue(unknownHandlerManager.hasUnknownHandlers());
    }

    public void testEmptyStack() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-unknownhandler-stack-empty.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        loadConfigurationProviders(provider);
        configurationManager.reload();

        List<UnknownHandlerConfig> unknownHandlerStack = configuration.getUnknownHandlerStack();
        assertNull(unknownHandlerStack);
    }
}
