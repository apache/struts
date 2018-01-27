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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.providers.SomeUnknownHandler;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Partial test to the DefaultUnknownHandlerManager to understand the relationship between Manager and Handlers.
 *
 */
public class DefaultUnknownHandlerManagerTest extends TestCase {

    ActionConfig actionConfig;
    SomeUnknownHandler someUnknownHandler;

    /**
     * Relationshsip when UnknownAction method is called.
     *
     */
    public void testHandleUnknownAction() {

        DefaultUnknownHandlerManager defaultUnknownHandlerManager = new DefaultUnknownHandlerManager();
        defaultUnknownHandlerManager.unknownHandlers = new ArrayList<>();
        defaultUnknownHandlerManager.unknownHandlers.add(someUnknownHandler);

        ActionConfig newActionConfig = defaultUnknownHandlerManager.handleUnknownAction("arbitraryNameSpace", "arbitraryActionName");

        assertEquals(newActionConfig, actionConfig);
    }

    /**
     * Relationship when UnknownActionMethod method called.
     *
     */
    public void testHandelUnknownActionMethod() throws Exception {
        DefaultUnknownHandlerManager defaultUnknownHandlerManager = new DefaultUnknownHandlerManager();
        defaultUnknownHandlerManager.unknownHandlers = new ArrayList<>();
        defaultUnknownHandlerManager.unknownHandlers.add(someUnknownHandler);

        String result = null;

        for (int i = 0; i < 10 ; i++) {
            result = (String) defaultUnknownHandlerManager.handleUnknownMethod(this, "someMethodName");
            assertEquals(result, "specialActionMethod");
        }

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Make sure we are using the actionConfig we initialized.
        ActionConfig.Builder actionConfigBuilder = new ActionConfig.Builder( "com", "someAction", "someClass");
        actionConfig = actionConfigBuilder.build();
        someUnknownHandler = new SomeUnknownHandler();

        someUnknownHandler.setActionConfig(actionConfig);
        someUnknownHandler.setActionMethodResult("specialActionMethod");
    }

}
