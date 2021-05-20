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

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.util.ValueStack;
import junit.framework.TestCase;
import org.apache.struts2.StrutsException;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;

import java.util.HashMap;
import java.util.Map;


/**
 * @author CameronBraid
 */
public class ChainResultTest extends XWorkTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider configurationProvider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }

    public void testNamespaceAndActionExpressionEvaluation() throws Exception {
        ActionChainResult result = new ActionChainResult();
        result.setActionName("${actionName}");
        result.setNamespace("${namespace}");

        String expectedActionName = "testActionName";
        String expectedNamespace = "testNamespace";
        Map<String, Object> values = new HashMap<>();
        values.put("actionName", expectedActionName);
        values.put("namespace", expectedNamespace);

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(values);

        Mock actionProxyMock = new Mock(ActionProxy.class);
        actionProxyMock.matchAndReturn("getActionName", expectedActionName);
        actionProxyMock.matchAndReturn("getMethod", "execute");
        actionProxyMock.expect("execute");

        ActionProxyFactory testActionProxyFactory = new NamespaceActionNameTestActionProxyFactory(expectedNamespace, expectedActionName, (ActionProxy) actionProxyMock.proxy());
        result.setActionProxyFactory(testActionProxyFactory);

        ActionProxy actionProxy = (ActionProxy) actionProxyMock.proxy();
        result.setActionProxyFactory(testActionProxyFactory);

        Mock invocationMock = new Mock(ActionInvocation.class);
        invocationMock.matchAndReturn("getProxy", actionProxy);
        invocationMock.matchAndReturn("getInvocationContext", ActionContext.getContext());

        result.execute((ActionInvocation) invocationMock.proxy());

        actionProxyMock.verify();
    }

    public void testWithNoNamespace() throws Exception {
        ActionChainResult result = new ActionChainResult();
        result.setActionName("${actionName}");

        String expectedActionName = "testActionName";
        String expectedNamespace = "${1-1}";
        Map<String, Object> values = new HashMap<>();
        values.put("actionName", expectedActionName);

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(values);

        Mock actionProxyMock = new Mock(ActionProxy.class);
        actionProxyMock.expect("execute");
        actionProxyMock.expectAndReturn("getNamespace", expectedNamespace);
        actionProxyMock.expectAndReturn("getActionName", expectedActionName);
        actionProxyMock.expectAndReturn("getMethod", null);

        ActionProxy actionProxy = (ActionProxy) actionProxyMock.proxy();
        ActionProxyFactory testActionProxyFactory = new NamespaceActionNameTestActionProxyFactory(expectedNamespace, expectedActionName, actionProxy);
        result.setActionProxyFactory(testActionProxyFactory);

        Mock invocationMock = new Mock(ActionInvocation.class);
        invocationMock.matchAndReturn("getProxy", actionProxy);
        invocationMock.matchAndReturn("getInvocationContext", ActionContext.getContext());
        try {

            ActionContext.bind(stack.getActionContext());
            result.execute((ActionInvocation) invocationMock.proxy());
            actionProxyMock.verify();
        } finally {
            ActionContext.clear();
        }
    }

    public void testRecursiveChain() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "InfiniteRecursionChain", null, null);

        try {
            proxy.execute();
            fail("did not detected repeated chain to an action");
        } catch (StrutsException e) {
            assertTrue(true);
        }
    }

    public void testNamespaceChain() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "chain_with_namespace", null, null);
        ((SimpleAction)proxy.getAction()).setBlah("%{foo}");

        proxy.execute();

        assertTrue(proxy.getInvocation().getResult() instanceof MockResult);
        MockResult result = (MockResult) proxy.getInvocation().getResult();
        assertEquals("%{foo}", result.getInvocation().getProxy().getNamespace());
    }

    private static class NamespaceActionNameTestActionProxyFactory implements ActionProxyFactory {
        private final ActionProxy returnVal;
        private final String expectedActionName;
        private final String expectedNamespace;

        NamespaceActionNameTestActionProxyFactory(String expectedNamespace, String expectedActionName, ActionProxy returnVal) {
            this.expectedNamespace = expectedNamespace;
            this.expectedActionName = expectedActionName;
            this.returnVal = returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext) {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext) {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(ActionInvocation actionInvocation, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, String method, boolean executeResult, boolean cleanupContext) {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName,
                                             Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) throws Exception {
            return null;
        }
    }
}
