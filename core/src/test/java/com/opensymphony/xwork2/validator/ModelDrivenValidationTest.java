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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ModelDrivenValidationTest
 *
 * @author Jason Carreira
 *         Created Oct 1, 2003 10:08:25 AM
 */
public class ModelDrivenValidationTest extends XWorkTestCase {

    public void testModelDrivenValidation() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("count", new String[]{"11"});

        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        XmlConfigurationProvider provider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "TestModelDrivenValidation", null, context);
        assertEquals(Action.SUCCESS, proxy.execute());

        ModelDrivenAction action = (ModelDrivenAction) proxy.getAction();
        assertTrue(action.hasFieldErrors());
        assertTrue(action.getFieldErrors().containsKey("count"));
        assertEquals("count must be between 1 and 10, current value is 11.", ((List) action.getFieldErrors().get("count")).get(0));
    }

}
