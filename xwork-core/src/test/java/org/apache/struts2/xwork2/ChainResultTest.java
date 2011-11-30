/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on 28/02/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork2;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.ValueStack;
import junit.framework.TestCase;

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
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-sample.xml"));
    }

    public void testNamespaceAndActionExpressionEvaluation() throws Exception {
        ActionChainResult result = new ActionChainResult();
        result.setActionName("${actionName}");
        result.setNamespace("${namespace}");

        String expectedActionName = "testActionName";
        String expectedNamespace = "testNamespace";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("actionName", expectedActionName);
        values.put("namespace", expectedNamespace);

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(values);

        Mock actionProxyMock = new Mock(ActionProxy.class);
        actionProxyMock.expect("execute");

        ActionProxyFactory testActionProxyFactory = new NamespaceActionNameTestActionProxyFactory(expectedNamespace, expectedActionName, (ActionProxy) actionProxyMock.proxy());
        result.setActionProxyFactory(testActionProxyFactory);
        try {

            ActionContext testContext = new ActionContext(stack.getContext());
            ActionContext.setContext(testContext);
            result.execute(null);
            actionProxyMock.verify();
        } finally {
            ActionContext.setContext(null);
        }
    }

    public void testRecursiveChain() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "InfiniteRecursionChain", null);

        try {
            proxy.execute();
            fail("did not detected repeated chain to an action");
        } catch (XWorkException e) {
        }
    }

    private class NamespaceActionNameTestActionProxyFactory implements ActionProxyFactory {
        private ActionProxy returnVal;
        private String expectedActionName;
        private String expectedNamespace;

        public NamespaceActionNameTestActionProxyFactory(String expectedNamespace, String expectedActionName, ActionProxy returnVal) {
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
