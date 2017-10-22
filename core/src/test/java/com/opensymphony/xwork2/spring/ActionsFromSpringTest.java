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
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.struts2.dispatcher.HttpParameters;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Test loading actions from the Spring Application Context.
 *
 * @author Simon Stewart
 */
public class ActionsFromSpringTest extends XWorkTestCase {
    private ApplicationContext appContext;

    @Override public void setUp() throws Exception {
        super.setUp();

        // Set up XWork
        XmlConfigurationProvider provider = new XmlConfigurationProvider("com/opensymphony/xwork2/spring/actionContext-xwork.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        appContext = ((SpringObjectFactory)container.getInstance(ObjectFactory.class)).appContext;
    }

    public void testLoadSimpleAction() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "simpleAction", null, null);
        Object action = proxy.getAction();

        Action expected = (Action) appContext.getBean("simple-action");

        assertEquals(expected.getClass(), action.getClass());
    }

    public void testLoadActionWithDependencies() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "dependencyAction", null, null);
        SimpleAction action = (SimpleAction) proxy.getAction();

        assertEquals("injected", action.getBlah());
    }

    public void testProxiedActionIsNotStateful() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "proxiedAction", null, null);
        SimpleAction action = (SimpleAction) proxy.getAction();

        action.setBlah("Hello World");

        proxy = actionProxyFactory.createActionProxy(null, "proxiedAction", null, null);
        action = (SimpleAction) proxy.getAction();

        // If the action is a singleton, this test will fail
        SimpleAction sa = new SimpleAction();
        assertEquals(sa.getBlah(), action.getBlah());

        // And if the advice is not being applied, this will be SUCCESS.
        String result = action.execute();
        assertEquals(Action.INPUT, result);
    }

    public void testAutoProxiedAction() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "autoProxiedAction", null, null);

        SimpleAction action = (SimpleAction) proxy.getAction();

        String result = action.execute();
        assertEquals(Action.INPUT, result);
    }
    
    public void testActionWithSpringResult() throws Exception {
    	        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "simpleActionSpringResult", null, null);
    	                
    	        proxy.execute();
    	        
    	        SpringResult springResult = (SpringResult) proxy.getInvocation().getResult();
    	        assertTrue(springResult.isInitialize());
    	        assertNotNull(springResult.getStringParameter());
    }

    public void testChainingProxiedActions() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "chainedAOPedTestBeanAction",
                null, null);

        proxy.execute();

        // check if AOP works
        TestAspect aspectState = (TestAspect) appContext.getBean("test-aspect");
        // chainedAction.actionMethodName sets name then chainedAction.getCount sets count
        // then chaintoAction.setCount sets count2 then chainedAction.getName sets name again
        // then chaintoAction.actionMethodName sets issueId of the aspect object.
        assertEquals("setName(WW-4105)-setCount(1)-setCount2(1)-setName(WW-4105)-setIssueId(WW-4105)-", aspectState.log);
        assertEquals(aspectState.getName(), aspectState.getIssueId());
        assertEquals("WW-4105", aspectState.getIssueId());
        assertEquals(aspectState.getCount(), aspectState.getCount2());
        assertEquals(1, aspectState.getCount());

        // check if chain works
        TestSubBean chaintoAOPedAction = (TestSubBean) appContext.getBean("pointcutted-test-sub-bean");
        assertEquals(1, chaintoAOPedAction.getCount());
        assertEquals("WW-4105", chaintoAOPedAction.getName());
    }

    public void testProxiedActionIsNotAccessible() throws Exception {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("exposeProxy", "true");
        params.put("issueId", "S2-047");

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy(null,
                "chaintoAOPedTestSubBeanAction", null, extraContext);

        // when
        proxy.execute();
        Object action = proxy.getAction();

        //then
        assertEquals("S2-047", ((TestSubBean) action).getIssueId());
        assertFalse("proxied action is accessible!",
                (boolean) MethodUtils.invokeMethod(action, "isExposeProxy"));
    }
}
