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
package org.apache.struts2;

import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.interceptor.ConditionalInterceptor;
import org.apache.struts2.interceptor.Interceptor;
import org.apache.struts2.interceptor.WithLazyParams;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.mock.MockInterceptor;
import org.apache.struts2.mock.MockResult;
import org.apache.struts2.result.ActionChainResult;
import org.apache.struts2.result.Result;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.apache.struts2.ognl.OgnlUtilTest.createOgnlUtil;
import static org.assertj.core.api.Assertions.assertThat;


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
        MockInterceptor mockInterceptor3 = new MockInterceptor() {
            @Override
            public boolean shouldIntercept(ActionInvocation invocation) {
                return false;
            }
        };
        interceptorMappings.add(new InterceptorMapping("test3", mockInterceptor3));

        MockInterceptor mockInterceptor4 = new MockInterceptor();
        interceptorMappings.add(new InterceptorMapping("test4", mockInterceptor4));
        mockInterceptor4.setFoo("test4");
        mockInterceptor4.setExpectedFoo("test4");

        DefaultActionInvocation defaultActionInvocation = new DefaultActionInvocationTester(interceptorMappings);
        container.inject(defaultActionInvocation);
        defaultActionInvocation.stack = container.getInstance(ValueStackFactory.class).createValueStack();

        defaultActionInvocation.setResultCode("");//is possible when result is not executed already
        defaultActionInvocation.invoke();
        assertTrue(mockInterceptor1.isExecuted());
        assertTrue(mockInterceptor2.isExecuted());
        assertFalse(mockInterceptor3.isExecuted());
        assertTrue(mockInterceptor4.isExecuted());
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

    public void testInvokeSimpleInterceptor() throws Exception {
        List<InterceptorMapping> interceptorMappings = new ArrayList<>();
        Interceptor interceptor1 = new Interceptor() {
            @Override
            public void destroy() {
            }

            @Override
            public void init() {
            }

            @Override
            public String intercept(ActionInvocation invocation) throws Exception {
                return "done";
            }
        };
        interceptorMappings.add(new InterceptorMapping("test1", interceptor1));

        DefaultActionInvocation defaultActionInvocation = new DefaultActionInvocationTester(interceptorMappings);
        container.inject(defaultActionInvocation);
        defaultActionInvocation.stack = container.getInstance(ValueStackFactory.class).createValueStack();

        String result = defaultActionInvocation.invoke();
        assertTrue(defaultActionInvocation.isExecuted());
        assertEquals("done", result);
    }

    public void testInvokeWithDisabledInterceptors() throws Exception {
        // given
        List<InterceptorMapping> interceptorMappings = new ArrayList<>();
        MockInterceptor mockInterceptor1 = new MockInterceptor();
        mockInterceptor1.setFoo("test1");
        mockInterceptor1.setExpectedFoo("test1");
        interceptorMappings.add(new InterceptorMapping("test1", mockInterceptor1));
        MockInterceptor mockInterceptor2 = new MockInterceptor();
        interceptorMappings.add(new InterceptorMapping("test2", mockInterceptor2));
        mockInterceptor2.setDisabled("true");
        MockInterceptor mockInterceptor3 = new MockInterceptor();
        interceptorMappings.add(new InterceptorMapping("test3", mockInterceptor3));
        mockInterceptor3.setFoo("test3");
        mockInterceptor3.setExpectedFoo("test3");

        // when
        DefaultActionInvocation defaultActionInvocation = new DefaultActionInvocationTester(interceptorMappings);
        container.inject(defaultActionInvocation);
        defaultActionInvocation.stack = container.getInstance(ValueStackFactory.class).createValueStack();

        defaultActionInvocation.setResultCode("");
        defaultActionInvocation.invoke();

        // then
        assertTrue(mockInterceptor1.isExecuted());
        assertFalse(mockInterceptor2.isExecuted());
        assertTrue(mockInterceptor3.isExecuted());
        assertTrue(defaultActionInvocation.isExecuted());
    }

    /**
     * WW-5659: {@code executeConditional(ConditionalInterceptor)} was removed in 7.3.0 - it had no
     * callers left, so a subclass still overriding it would have gone silently dead. The
     * two-argument form is now the only extension point and must be handed the interceptor
     * mapping's name, which is what the removed overload could not supply.
     */
    public void testExecuteConditionalOverrideReceivesTheMappingName() throws Exception {
        // given
        List<InterceptorMapping> interceptorMappings = new ArrayList<>();
        MockInterceptor mockInterceptor = new MockInterceptor();
        mockInterceptor.setFoo("test1");
        mockInterceptor.setExpectedFoo("test1");
        interceptorMappings.add(new InterceptorMapping("namedInStack", mockInterceptor));

        List<String> observedNames = new ArrayList<>();
        DefaultActionInvocation defaultActionInvocation = new DefaultActionInvocationTester(interceptorMappings) {
            @Override
            protected String executeConditional(ConditionalInterceptor conditionalInterceptor, String interceptorName) throws Exception {
                observedNames.add(interceptorName);
                return super.executeConditional(conditionalInterceptor, interceptorName);
            }
        };
        container.inject(defaultActionInvocation);
        defaultActionInvocation.stack = container.getInstance(ValueStackFactory.class).createValueStack();

        // when
        defaultActionInvocation.setResultCode("");
        defaultActionInvocation.invoke();

        // then
        assertThat(observedNames).containsExactly("namedInStack");
        assertThat(mockInterceptor.isExecuted()).isTrue();
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
        dai.ognlUtil = createOgnlUtil();

        // when
        String result = dai.invokeAction(action, null);

        // then
        assertEquals("success", result);
    }

    public void testInvokingMissingMethod() {
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
        dai.ognlUtil = createOgnlUtil();
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

    public void testInvokingExistingMethodThatThrowsException() {
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
        dai.ognlUtil = createOgnlUtil();

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

    public void testUnknownHandlerManagerThatThrowsException() {
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
        dai.ognlUtil = createOgnlUtil();
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

    public void testUnknownHandlerManagerThatReturnsNull() {
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
        dai.ognlUtil = createOgnlUtil();
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
        dai.ognlUtil = createOgnlUtil();
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

        ActionContext extraContext = ActionContext.of()
                .withParameters(HttpParameters.create(params).build());

        DefaultActionInvocation defaultActionInvocation = new DefaultActionInvocation(extraContext.getContextMap(), true);
        container.inject(defaultActionInvocation);

        ActionProxy actionProxy = actionProxyFactory.createActionProxy("", "LazyFoo", null, extraContext.getContextMap());
        defaultActionInvocation.init(actionProxy);
        defaultActionInvocation.invoke();

        SimpleAction action = (SimpleAction) defaultActionInvocation.getAction();

        assertEquals("this is blah", action.getBlah());
        assertEquals("this is blah", action.getName());
    }

    /**
     * Test for WW-5586: WithLazyParams interceptors can be configured in interceptor stacks
     * with both static parameters and dynamic expression parameters.
     */
    public void testInvokeWithLazyParamsStackConfiguration() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("blah", "dynamic value");

        ActionContext extraContext = ActionContext.of()
                .withParameters(HttpParameters.create(params).build());

        DefaultActionInvocation defaultActionInvocation = new DefaultActionInvocation(extraContext.getContextMap(), true);
        container.inject(defaultActionInvocation);

        ActionProxy actionProxy = actionProxyFactory.createActionProxy("", "LazyFooWithStackParams", null, extraContext.getContextMap());
        defaultActionInvocation.init(actionProxy);

        // Verify InterceptorMapping has params before invocation (WW-5587)
        List<InterceptorMapping> interceptors = actionProxy.getConfig().getInterceptors();
        InterceptorMapping lazyInterceptor = interceptors.stream()
                .filter(m -> m.getInterceptor() instanceof WithLazyParams)
                .findFirst()
                .orElseThrow(() -> new AssertionError("WithLazyParams interceptor not found"));

        assertFalse("WithLazyParams interceptor should have params in InterceptorMapping",
                lazyInterceptor.getParams().isEmpty());

        defaultActionInvocation.invoke();

        SimpleAction action = (SimpleAction) defaultActionInvocation.getAction();

        // Verify expression parameter is evaluated at invocation time
        assertEquals("dynamic value", action.getName());

        // Verify static parameter is set and not evaluated as expression
        assertEquals("static value", action.getBlah());
    }

    /**
     * Regression for WW-5659: a {@code disabled} param resolved lazily from the value stack must skip
     * the interceptor for that invocation. It arrives through the interceptor mapping's params, so it
     * lands on the per-invocation holder and is honoured there, never on the shared interceptor.
     */
    public void testInvokeWithLazyParamsSkipsLazilyDisabledInterceptor() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("blah", "true");

        ActionContext extraContext = ActionContext.of()
                .withParameters(HttpParameters.create(params).build());

        DefaultActionInvocation defaultActionInvocation = new DefaultActionInvocation(extraContext.getContextMap(), true);
        container.inject(defaultActionInvocation);

        ActionProxy actionProxy = actionProxyFactory.createActionProxy("", "LazyFooLazilyDisabled", null, extraContext.getContextMap());
        defaultActionInvocation.init(actionProxy);
        defaultActionInvocation.invoke();

        SimpleAction action = (SimpleAction) defaultActionInvocation.getAction();

        // the rest of the stack still ran, so the params interceptor applied blah...
        assertThat(action.getBlah()).isEqualTo("true");
        // ...but the lazy interceptor was skipped, so it never applied its foo param to the action
        assertThat(action.getName()).isNull();
    }

    /**
     * Regression for WW-5659: {@code disabled} configured on the interceptor definition never reaches
     * the params holder, so it can only be honoured through {@link org.apache.struts2.interceptor.ConditionalInterceptor#shouldIntercept}.
     * The lazy path must still consult it.
     */
    public void testInvokeWithLazyParamsSkipsStaticallyDisabledInterceptor() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("blah", "dynamic value");

        ActionContext extraContext = ActionContext.of()
                .withParameters(HttpParameters.create(params).build());

        DefaultActionInvocation defaultActionInvocation = new DefaultActionInvocation(extraContext.getContextMap(), true);
        container.inject(defaultActionInvocation);

        ActionProxy actionProxy = actionProxyFactory.createActionProxy("", "LazyFooStaticallyDisabled", null, extraContext.getContextMap());
        defaultActionInvocation.init(actionProxy);
        defaultActionInvocation.invoke();

        SimpleAction action = (SimpleAction) defaultActionInvocation.getAction();

        assertThat(action.getBlah()).isEqualTo("dynamic value");
        assertThat(action.getName()).isNull();
    }

    public void testInvokeWithAsyncManager() throws Exception {
        DefaultActionInvocation dai = new DefaultActionInvocation(new HashMap<>(), false);
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

        dai.action = (Callable<Callable<String>>) () -> (Callable<String>) () -> "success";

        MockActionProxy actionProxy = new MockActionProxy();
        actionProxy.setMethod("call");
        dai.proxy = actionProxy;

        final boolean[] preResultExecuted = new boolean[1];
        dai.addPreResultListener((invocation, resultCode) -> preResultExecuted[0] = true);

        List<InterceptorMapping> interceptorMappings = new ArrayList<>();
        MockInterceptor mockInterceptor1 = new MockInterceptor();
        mockInterceptor1.setFoo("test1");
        mockInterceptor1.setExpectedFoo("test1");
        interceptorMappings.add(new InterceptorMapping("test1", mockInterceptor1));
        dai.interceptors = interceptorMappings.iterator();

        dai.ognlUtil = createOgnlUtil();

        dai.invoke();

        assertTrue("interceptor1 should be executed", mockInterceptor1.isExecuted());
        assertFalse("preResultListener should no be executed", preResultExecuted[0]);
        assertNotNull("an async action should be saved", dai.asyncAction);
        assertFalse("invocation should not be executed", dai.executed);
        assertNull("a null result should be passed to upper and wait for the async result", dai.resultCode);

        if (lock.tryAcquire(1500L, TimeUnit.MILLISECONDS)) {
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
                "ExceptionFoo", "exceptionMethod", new HashMap<>());
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
        ActionProxy actionProxy = actionProxyFactory.createActionProxy("", "Foo", null, new HashMap<>());
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

    public void testNoResultDefined() {
        ActionProxy actionProxy = actionProxyFactory.createActionProxy("", "Foo", null, new HashMap<>());
        DefaultActionInvocation defaultActionInvocation = (DefaultActionInvocation) actionProxy.getInvocation();
        defaultActionInvocation.init(actionProxy);

        try {
            defaultActionInvocation.invoke();//foo==bar so returns error which is not defined
            fail("should not possible when result is not defined");
        } catch (Exception ignored) {
        }
    }

    public void testNullResultPossible() throws Exception {
        ActionProxy actionProxy = actionProxyFactory.createActionProxy("", "NullFoo", "nullMethod", new HashMap<>());
        DefaultActionInvocation defaultActionInvocation = (DefaultActionInvocation) actionProxy.getInvocation();
        defaultActionInvocation.init(actionProxy);

        String result = defaultActionInvocation.invoke();

        assertNull(result);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider configurationProvider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }

    private static class SimpleActionEventListener implements ActionEventListener {

        private final String name;
        private final String result;

        SimpleActionEventListener(String name, String result) {

            this.name = name;
            this.result = result;
        }

        @Override
        public Object prepare(Object action, ValueStack stack) {
            ((SimpleAction) action).setName(name);
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
        super(new HashMap<>(), false);
        interceptors = interceptorMappings.iterator();
        MockActionProxy actionProxy = new MockActionProxy();
        actionProxy.setMethod("execute");
        actionProxy.setConfig(new ActionConfig.Builder("foo", "bar", "clazz").addResultConfig(new ResultConfig.Builder("buzz", "fizz").build()).build());
        proxy = actionProxy;
        action = new ActionSupport();
    }
}
