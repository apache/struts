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
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.mock.MockContainer;
import com.opensymphony.xwork2.mock.MockInterceptor;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.struts2.dispatcher.HttpParameters;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A partial test of DefaultActionInvocation.
 * Created to change interceptor chain logic.
 *
 * @author <a href="mailto:kristian at zenior.no">Kristian Rosenvold</a>
 */
public class DefaultActionInvocationTest extends XWorkTestCase {
    private static final String HTTP_REQUEST = "com.opensymphony.xwork2.DefaultActionInvocationTest.HttpServletRequest";
    private static final String HTTP_RESPONSE = "com.opensymphony.xwork2.DefaultActionInvocationTest.HttpServletResponse";

    /**
     * Tests interceptor chain invoke.
     *
     * @throws Exception when action throws exception
     */
    public void testInvoke() throws Exception {
        List<InterceptorMapping> interceptorMappings = new ArrayList<>();
        MockInterceptor mockInterceptor1 = new MockInterceptor();
        mockInterceptor1.setFoo("test1");
        mockInterceptor1.setExpectedFoo("test1");
        interceptorMappings.add(new InterceptorMapping("test1", mockInterceptor1));
        MockInterceptor mockInterceptor2 = new MockInterceptor();
        interceptorMappings.add(new InterceptorMapping("test2", mockInterceptor2));
        mockInterceptor2.setFoo("test2");
        mockInterceptor2.setExpectedFoo("test2");
        MockInterceptor mockInterceptor3 = new MockInterceptor();
        interceptorMappings.add(new InterceptorMapping("test3", mockInterceptor3));
        mockInterceptor3.setFoo("test3");
        mockInterceptor3.setExpectedFoo("test3");

        DefaultActionInvocation defaultActionInvocation = new DefaultActionInvocationTester(interceptorMappings);
        container.inject(defaultActionInvocation);
        defaultActionInvocation.stack = container.getInstance(ValueStackFactory.class).createValueStack();

        defaultActionInvocation.invoke();
        assertTrue(mockInterceptor1.isExecuted());
        assertTrue(mockInterceptor2.isExecuted());
        assertTrue(mockInterceptor3.isExecuted());
    }

    public void testSerialization() throws Exception {
        // given
        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        DefaultActionInvocation actionInvocation = new DefaultActionInvocation(extraContext, false);
        actionInvocation.setContainer(new MockContainer());

        extraContext.put(HTTP_REQUEST, new MockHttpServletRequest());
        extraContext.put(HTTP_RESPONSE, new MockHttpServletResponse());
        actionInvocation.invocationContext = new ActionContext(extraContext);

        // when
        DefaultActionInvocation serializable = (DefaultActionInvocation) actionInvocation.serialize();

        // then
        assertNull(actionInvocation.container);
        assertNull(serializable.container);

        assertFalse("request should be removed from actionInvocation",
                actionInvocation.invocationContext.getContextMap().containsKey(HTTP_REQUEST));
        assertFalse("response should be removed from actionInvocation",
                actionInvocation.invocationContext.getContextMap().containsKey(HTTP_RESPONSE));
        assertFalse("request should be removed from serializable",
                serializable.invocationContext.getContextMap().containsKey(HTTP_REQUEST));
        assertFalse("response should be removed from serializable",
                serializable.invocationContext.getContextMap().containsKey(HTTP_RESPONSE));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(serializable);
        oos.close();
        assertTrue("should have serialized data", baos.size() > 0);
        baos.close();
    }

    public void testDeserialization() throws Exception {
        // given
        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        DefaultActionInvocation actionInvocation = new DefaultActionInvocation(extraContext, false);
        actionInvocation.invocationContext = new ActionContext(extraContext);

        MockContainer mockContainer = new MockContainer();
        ActionContext.getContext().setContainer(mockContainer);

        Map<String, Object> acContextMap = ActionContext.getContext().getContextMap();
        MockHttpServletRequest request = new MockHttpServletRequest();
        acContextMap.put(HTTP_REQUEST, request);
        MockHttpServletResponse response = new MockHttpServletResponse();
        acContextMap.put(HTTP_RESPONSE, response);

        // when
        DefaultActionInvocation deserializable = (DefaultActionInvocation) actionInvocation.deserialize(ActionContext.getContext());

        // then
        assertNotNull(actionInvocation.container);
        assertNotNull(deserializable.container);
        assertEquals(mockContainer, deserializable.container);

        assertTrue("request should be restored into actionInvocation",
                actionInvocation.invocationContext.getContextMap().containsKey(HTTP_REQUEST));
        assertTrue("response should be restored into actionInvocation",
                actionInvocation.invocationContext.getContextMap().containsKey(HTTP_RESPONSE));
        assertTrue("request should be restored into deserializable",
                deserializable.invocationContext.getContextMap().containsKey(HTTP_REQUEST));
        assertTrue("response should be restored into deserializable",
                deserializable.invocationContext.getContextMap().containsKey(HTTP_RESPONSE));

        assertEquals(request, actionInvocation.invocationContext.getContextMap().get(HTTP_REQUEST));
        assertEquals(response, actionInvocation.invocationContext.getContextMap().get(HTTP_RESPONSE));
        assertEquals(request, deserializable.invocationContext.getContextMap().get(HTTP_REQUEST));
        assertEquals(response, deserializable.invocationContext.getContextMap().get(HTTP_RESPONSE));
    }

    public void testInvokingExistingExecuteMethod() throws Exception {
        // given
        DefaultActionInvocation dai = new DefaultActionInvocation(new HashMap<String, Object>(), false) {
            public ValueStack getStack() {
                return new StubValueStack();
            }
        };

        SimpleAction action = new SimpleAction() {
            @Override
            public String execute() throws Exception {
                return SUCCESS;
            }
        };
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("execute");

        dai.proxy = proxy;
        dai.ognlUtil = new OgnlUtil();

        // when
        String result = dai.invokeAction(action, null);

        // then
        assertEquals("success", result);
    }

    public void testInvokingMissingMethod() throws Exception {
        // given
        DefaultActionInvocation dai = new DefaultActionInvocation(new HashMap<String, Object>(), false) {
            public ValueStack getStack() {
                return new StubValueStack();
            }
        };

        SimpleAction action = new SimpleAction() {
            @Override
            public String execute() throws Exception {
                return ERROR;
            }
        };
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("notExists");

        UnknownHandlerManager uhm = new DefaultUnknownHandlerManager() {
            @Override
            public boolean hasUnknownHandlers() {
                return false;
            }
        };

        dai.proxy = proxy;
        dai.ognlUtil = new OgnlUtil();
        dai.unknownHandlerManager = uhm;

        // when
        Throwable actual = null;
        try {
            dai.invokeAction(action, null);
        } catch (Exception e) {
            actual = e;
        }

        // then
        assertNotNull(actual);
        assertTrue(actual instanceof NoSuchMethodException);
    }

    public void testInvokingExistingMethodThatThrowsException() throws Exception {
        // given
        DefaultActionInvocation dai = new DefaultActionInvocation(new HashMap<String, Object>(), false) {
            public ValueStack getStack() {
                return new StubValueStack();
            }
        };

        SimpleAction action = new SimpleAction() {
            @Override
            public String execute() throws Exception {
                throw new IllegalArgumentException();
            }
        };
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("execute");

        dai.proxy = proxy;
        dai.ognlUtil = new OgnlUtil();

        // when
        Throwable actual = null;
        try {
            dai.invokeAction(action, null);
        } catch (Exception e) {
            actual = e;
        }

        // then
        assertNotNull(actual);
        assertTrue(actual instanceof IllegalArgumentException);
    }

    public void testUnknownHandlerManagerThatThrowsException() throws Exception {
        // given
        DefaultActionInvocation dai = new DefaultActionInvocation(new HashMap<String, Object>(), false) {
            public ValueStack getStack() {
                return new StubValueStack();
            }
        };

        UnknownHandlerManager uhm = new DefaultUnknownHandlerManager() {
            @Override
            public boolean hasUnknownHandlers() {
                return true;
            }

            @Override
            public Object handleUnknownMethod(Object action, String methodName) throws NoSuchMethodException {
                throw new NoSuchMethodException();
            }
        };

        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("notExists");

        dai.proxy = proxy;
        dai.ognlUtil = new OgnlUtil();
        dai.unknownHandlerManager = uhm;

        // when
        // when
        Throwable actual = null;
        try {
            dai.invokeAction(new SimpleAction(), null);
        } catch (Exception e) {
            actual = e;
        }

        // then
        assertNotNull(actual);
        assertTrue(actual instanceof NoSuchMethodException);
    }

    public void testUnknownHandlerManagerThatReturnsNull() throws Exception {
        // given
        DefaultActionInvocation dai = new DefaultActionInvocation(new HashMap<String, Object>(), false) {
            public ValueStack getStack() {
                return new StubValueStack();
            }
        };

        UnknownHandlerManager uhm = new DefaultUnknownHandlerManager() {
            @Override
            public boolean hasUnknownHandlers() {
                return true;
            }

            @Override
            public Object handleUnknownMethod(Object action, String methodName) throws NoSuchMethodException {
                return null;
            }
        };

        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("notExists");

        dai.proxy = proxy;
        dai.ognlUtil = new OgnlUtil();
        dai.unknownHandlerManager = uhm;

        // when
        Throwable actual = null;
        try {
            dai.invokeAction(new SimpleAction(), null);
        } catch (Exception e) {
            actual = e;
        }

        // then
        assertNotNull(actual);
        assertTrue(actual instanceof NoSuchMethodException);
    }

    public void testUnknownHandlerManagerThatReturnsSuccess() throws Exception {
        // given
        DefaultActionInvocation dai = new DefaultActionInvocation(new HashMap<String, Object>(), false) {
            public ValueStack getStack() {
                return new StubValueStack();
            }
        };

        UnknownHandlerManager uhm = new DefaultUnknownHandlerManager() {
            @Override
            public boolean hasUnknownHandlers() {
                return true;
            }

            @Override
            public Object handleUnknownMethod(Object action, String methodName) throws NoSuchMethodException {
                return "success";
            }
        };

        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("notExists");

        dai.proxy = proxy;
        dai.ognlUtil = new OgnlUtil();
        dai.unknownHandlerManager = uhm;

        // when
        String result = dai.invokeAction(new SimpleAction(), null);

        // then
        assertNotNull(result);
        assertEquals("success", result);
    }

    public void testInvokeWithLazyParams() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("blah", "this is blah");

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        DefaultActionInvocation defaultActionInvocation = new DefaultActionInvocation(extraContext, true);
        container.inject(defaultActionInvocation);

        ActionProxy actionProxy = actionProxyFactory.createActionProxy( "", "LazyFoo", null, extraContext);
        defaultActionInvocation.init(actionProxy);
        defaultActionInvocation.invoke();

        SimpleAction action = (SimpleAction) defaultActionInvocation.getAction();

        assertEquals("this is blah", action.getBlah());
        assertEquals("this is blah", action.getName());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider configurationProvider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }

}

class DefaultActionInvocationTester extends DefaultActionInvocation {
    DefaultActionInvocationTester(List<InterceptorMapping> interceptorMappings) {
        super(new HashMap<String, Object>(), false);
        interceptors = interceptorMappings.iterator();
        MockActionProxy actionProxy = new MockActionProxy();
        actionProxy.setMethod("execute");
        actionProxy.setConfig(new ActionConfig.Builder("foo", "bar", "clazz").addResultConfig(new ResultConfig.Builder("buzz", "fizz").build()).build());
        proxy = actionProxy;
        action = new ActionSupport();
    }
}
