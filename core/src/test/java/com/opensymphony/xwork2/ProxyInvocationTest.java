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

import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Contribed by: Ruben Inoto
 */
public class ProxyInvocationTest extends XWorkTestCase {

    /**
     * Sets a ProxyObjectFactory as ObjectFactory (so the FooAction will always be retrieved
     * as a FooProxy), and it tries to call invokeAction on the TestActionInvocation.
     * 
     * It should fail, because the Method got from the action (actually a FooProxy) 
     * will be executed on the InvocationHandler of the action (so, in the action itself). 
     */
    public void testProxyInvocation() throws Exception {

        ActionProxy proxy = actionProxyFactory
            .createActionProxy("", "ProxyInvocation", null, createDummyContext());
        ActionInvocation invocation = proxy.getInvocation();
        
        String result = invocation.invokeActionOnly();
        assertEquals("proxyResult", result);

    }

    /** 
     * Needed for the creation of the action proxy
     */
    private Map<String, Object> createDummyContext() {
        Map<String, Object> params = new HashMap<>();
        params.put("blah", "this is blah");
        Map<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, params);
        return extraContext;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider configurationProvider = new XmlConfigurationProvider("xwork-proxyinvoke.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }
}
