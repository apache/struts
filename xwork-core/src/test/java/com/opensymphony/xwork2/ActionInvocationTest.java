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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import java.util.HashMap;


/**
 * @author $Author$
 * @version $Revision$
 */
public class ActionInvocationTest extends XWorkTestCase {

    public void testCommandInvocation() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy(
                "baz", "commandTest", null, null);
        assertEquals("success", baseActionProxy.execute());

        ActionProxy commandActionProxy = actionProxyFactory.createActionProxy(
                "baz", "myCommand", null, null);
        assertEquals(SimpleAction.COMMAND_RETURN_CODE, commandActionProxy.execute());
    }

    public void testCommandInvocationDoMethod() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy(
                "baz", "doMethodTest", null, null);
        assertEquals("input", baseActionProxy.execute());
    }

    public void testCommandInvocationUnknownHandler() throws Exception {

        DefaultActionProxy baseActionProxy = (DefaultActionProxy) actionProxyFactory.createActionProxy(
                "baz", "unknownMethodTest", "unknownmethod", null);
        UnknownHandler unknownHandler = new UnknownHandler() {
			public ActionConfig handleUnknownAction(String namespace, String actionName) throws XWorkException { return null;}
			public Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig, String resultCode) throws XWorkException {
				return null;
			}
			public Object handleUnknownActionMethod(Object action, String methodName) throws NoSuchMethodException {
				if (methodName.equals("unknownmethod")) {
					return "found";
				} else {
					return null;
				}
			}
        };

        UnknownHandlerManagerMock uhm = new UnknownHandlerManagerMock();
        uhm.addUnknownHandler(unknownHandler);
        ((DefaultActionInvocation)baseActionProxy.getInvocation()).setUnknownHandlerManager(uhm);

        assertEquals("found", baseActionProxy.execute());
    }

    public void testResultReturnInvocationAndWired() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy(
                "baz", "resultAction", null, null);
        assertEquals(null, baseActionProxy.execute());
        assertTrue(SimpleAction.resultCalled);
    }

    public void testSimple() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("blah", "this is blah");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy( "", "Foo", null, extraContext);
            proxy.execute();
            assertEquals("this is blah", proxy.getInvocation().getStack().findValue("[1].blah"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Override protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider configurationProvider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }
}
