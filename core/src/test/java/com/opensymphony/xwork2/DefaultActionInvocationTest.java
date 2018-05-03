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
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.mock.MockInterceptor;
import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


/**
 * A partial test of DefaultActionInvocation.
 * Created to change interceptor chain logic.
 *
 * @author <a href="mailto:kristian at zenior.no">Kristian Rosenvold</a>
 */
public class DefaultActionInvocationTest extends XWorkTestCase {

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

        defaultActionInvocation.setResultCode("");//is possible when result is not executed already
        defaultActionInvocation.invoke();
        assertTrue(mockInterceptor1.isExecuted());
        assertTrue(mockInterceptor2.isExecuted());
        assertTrue(mockInterceptor3.isExecuted());
        assertTrue(defaultActionInvocation.isExecuted());
        try {
            defaultActionInvocation.setResultCode("");
            fail("should not possible when result already executed");
        } catch (Exception ignored) {
        }
        try {
            defaultActionInvocation.invoke();
            fail("should not possible when result already executed");
        } catch (Exception ignored) {
        }
    }

    public void testInvokingExistingExecuteMethod() throws Exception {
        // given
        DefaultActionInvocation dai = new DefaultActionInvocation(ActionContext.getContext().getContextMap(), false);
        container.inject(dai);

        SimpleAction action = new SimpleAction() {
            @Override
            public String execute() throws Exception {
                return SUCCESS;
            }
        };
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("execute");

        dai.stack = container.getInstance(ValueStackFactory.class).createValueStack();
        dai.proxy = proxy;
        dai.ognlUtil = new OgnlUtil();

        // when
        String result = dai.invokeAction(action, null);

        // then
        assertEquals("success", result);
    }

    public void testInvokingMissingMethod() throws Exception {
        // given
        DefaultActionInvocation dai = new DefaultActionInvocation(ActionContext.getContext().getContextMap(), false);
        container.inject(dai);

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

        dai.stack = container.getInstance(ValueStackFactory.class).createValueStack();
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
        DefaultActionInvocation dai = new DefaultActionInvocation(ActionContext.getContext().getContextMap(), false);
        container.inject(dai);

        SimpleAction action = new SimpleAction() {
            @Override
            public String execute() throws Exception {
                throw new IllegalArgumentException();
            }
        };
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("execute");

        dai.stack = container.getInstance(ValueStackFactory.class).createValueStack();
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
        DefaultActionInvocation dai = new DefaultActionInvocation(ActionContext.getContext().getContextMap(), false);
        container.inject(dai);

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

        dai.stack = container.getInstance(ValueStackFactory.class).createValueStack();
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
        DefaultActionInvocation dai = new DefaultActionInvocation(ActionContext.getContext().getContextMap(), false);
        container.inject(dai);

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

        dai.stack = container.getInstance(ValueStackFactory.class).createValueStack();
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
        DefaultActionInvocation dai = new DefaultActionInvocation(ActionContext.getContext().getContextMap(), false);
        container.inject(dai);

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

        dai.stack = container.getInstance(ValueStackFactory.class).createValueStack();
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

    public void testInvokeWithAsyncManager() throws Exception {
        DefaultActionInvocation dai = new DefaultActionInvocation(new HashMap<String, Object>(), false);
        dai.stack = container.getInstance(ValueStackFactory.class).createValueStack();

        final Semaphore lock = new Semaphore(1);
        lock.acquire();
        dai.setAsyncManager(new AsyncManager() {
            Object asyncActionResult;
            @Override
            public boolean hasAsyncActionResult() {
                return asyncActionResult != null;
            }

            @Override
            public Object getAsyncActionResult() {
                return asyncActionResult;
            }

            @Override
            public void invokeAsyncAction(Callable asyncAction) {
                try {
                    asyncActionResult = asyncAction.call();
                } catch (Exception e) {
                    asyncActionResult = e;
                }
                lock.release();
            }
        });

        dai.action = new Callable<Callable<String>>() {
            @Override
            public Callable<String> call() throws Exception {
                return new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "success";
                    }
                };
            }
        };

        MockActionProxy actionProxy = new MockActionProxy();
        actionProxy.setMethod("call");
        dai.proxy = actionProxy;

        final boolean[] preResultExecuted = new boolean[1];
        dai.addPreResultListener(new PreResultListener() {
            @Override
            public void beforeResult(ActionInvocation invocation, String resultCode) {
                preResultExecuted[0] = true;
            }
        });

        List<InterceptorMapping> interceptorMappings = new ArrayList<>();
        MockInterceptor mockInterceptor1 = new MockInterceptor();
        mockInterceptor1.setFoo("test1");
        mockInterceptor1.setExpectedFoo("test1");
        interceptorMappings.add(new InterceptorMapping("test1", mockInterceptor1));
        dai.interceptors = interceptorMappings.iterator();

        dai.ognlUtil = new OgnlUtil();

        dai.invoke();

        assertTrue("interceptor1 should be executed", mockInterceptor1.isExecuted());
        assertFalse("preResultListener should no be executed", preResultExecuted[0]);
        assertNotNull("an async action should be saved", dai.asyncAction);
        assertFalse("invocation should not be executed", dai.executed);
        assertNull("a null result should be passed to upper and wait for the async result", dai.resultCode);

        if(lock.tryAcquire(1500L, TimeUnit.MILLISECONDS)) {
            try {
                dai.invoke();
                assertTrue("preResultListener should be executed", preResultExecuted[0]);
                assertNull("async action should be cleared", dai.asyncAction);
                assertTrue("invocation should be executed", dai.executed);
                assertEquals("success", dai.resultCode);
            } finally {
                lock.release();
            }
        } else {
            lock.release();
            fail("async result did not received on timeout!");
        }
    }

    public void testActionEventListener() throws Exception {
        ActionProxy actionProxy = actionProxyFactory.createActionProxy("",
                "ExceptionFoo", "exceptionMethod", new HashMap<String, Object>());
        DefaultActionInvocation defaultActionInvocation = (DefaultActionInvocation) actionProxy.getInvocation();

        SimpleActionEventListener actionEventListener = new SimpleActionEventListener("prepared", "exceptionHandled");
        defaultActionInvocation.setActionEventListener(actionEventListener);
        defaultActionInvocation.init(actionProxy);

        SimpleAction action = (SimpleAction) defaultActionInvocation.getAction();
        action.setThrowException(true);

        defaultActionInvocation.unknownHandlerManager = new DefaultUnknownHandlerManager() {
            @Override
            public boolean hasUnknownHandlers() {
                return false;
            }
        };

        String result = defaultActionInvocation.invoke();

        // then
        assertEquals("prepared", action.getName());
        assertEquals("exceptionHandled", result);
    }

    public void testActionChainResult() throws Exception {
        ActionProxy actionProxy = actionProxyFactory.createActionProxy("", "Foo", null,
                new HashMap<String, Object>());
        DefaultActionInvocation defaultActionInvocation = (DefaultActionInvocation) actionProxy.getInvocation();
        defaultActionInvocation.init(actionProxy);

        SimpleAction action = (SimpleAction) defaultActionInvocation.getAction();
        action.setFoo(1);
        action.setBar(2);

        defaultActionInvocation.invoke();

        // then
        assertTrue(defaultActionInvocation.result instanceof ActionChainResult);
        Result result = defaultActionInvocation.getResult();
        assertTrue(result instanceof MockResult);
    }

    public void testNoResultDefined() throws Exception {
        ActionProxy actionProxy = actionProxyFactory.createActionProxy("", "Foo", null,
                new HashMap<String, Object>());
        DefaultActionInvocation defaultActionInvocation = (DefaultActionInvocation) actionProxy.getInvocation();
        defaultActionInvocation.init(actionProxy);

        try {
            defaultActionInvocation.invoke();//foo==bar so returns error which is not defined
            fail("should not possible when result is not defined");
        } catch (Exception ignored) {
        }
    }

    public void testNullResultPossible() throws Exception {
        ActionProxy actionProxy = actionProxyFactory.createActionProxy("",
                "NullFoo", "nullMethod", new HashMap<String, Object>());
        DefaultActionInvocation defaultActionInvocation = (DefaultActionInvocation) actionProxy.getInvocation();
        defaultActionInvocation.init(actionProxy);

        String result = defaultActionInvocation.invoke();

        assertNull(result);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider configurationProvider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }


    private class SimpleActionEventListener implements ActionEventListener {

        private String name;
        private String result;

        SimpleActionEventListener(String name, String result) {

            this.name = name;
            this.result = result;
        }

        @Override
        public Object prepare(Object action, ValueStack stack) {
            ((SimpleAction)action).setName(name);
            return action;
        }

        @Override
        public String handleException(Throwable t, ValueStack stack) {
            return result;
        }
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
