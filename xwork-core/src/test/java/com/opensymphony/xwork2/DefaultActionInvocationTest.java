package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.mock.MockContainer;
import com.opensymphony.xwork2.mock.MockInterceptor;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
        List<InterceptorMapping> interceptorMappings = new ArrayList<InterceptorMapping>();
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
        DefaultActionInvocation actionInvocation = new DefaultActionInvocation(new HashMap<String, Object>(), false);
        actionInvocation.setContainer(new MockContainer());

        // when
        DefaultActionInvocation serializable = (DefaultActionInvocation) actionInvocation.serialize();

        // then
        assertNull(actionInvocation.container);
        assertNull(serializable.container);
    }

    public void testDeserialization() throws Exception {
        // given
        DefaultActionInvocation actionInvocation = new DefaultActionInvocation(new HashMap<String, Object>(), false);
        MockContainer mockContainer = new MockContainer();
        ActionContext.getContext().setContainer(mockContainer);

        // when
        DefaultActionInvocation deserializable = (DefaultActionInvocation) actionInvocation.deserialize(ActionContext.getContext());

        // then
        assertNotNull(actionInvocation.container);
        assertNotNull(deserializable.container);
        assertEquals(mockContainer, deserializable.container);
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

    public void testInvokingExistingDoInputMethod() throws Exception {
        // given
        DefaultActionInvocation dai = new DefaultActionInvocation(new HashMap<String, Object>(), false) {
            public ValueStack getStack() {
                return new StubValueStack();
            }
        };

        SimpleAction action = new SimpleAction();
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("with");

        dai.proxy = proxy;
        dai.ognlUtil = new OgnlUtil();

        // when
        String result = dai.invokeAction(action, null);

        // then
        assertEquals("with", result);
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

    public void testInvokingExistingDoMethodThatThrowsException() throws Exception {
        // given
        DefaultActionInvocation dai = new DefaultActionInvocation(new HashMap<String, Object>(), false) {
            public ValueStack getStack() {
                return new StubValueStack();
            }
        };

        UnknownHandlerManager uhm = new DefaultUnknownHandlerManager() {
            @Override
            public boolean hasUnknownHandlers() {
                return false;
            }
        };

        SimpleAction action = new SimpleAction() {
            @Override
            public String doWith() throws Exception {
                throw new IllegalArgumentException();
            }
        };
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("with");

        dai.proxy = proxy;
        dai.ognlUtil = new OgnlUtil();
        dai.unknownHandlerManager = uhm;

        // when
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

    @Deprecated
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

        SimpleAction action = new SimpleAction() {
            @Override
            public String doWith() throws Exception {
                throw new IllegalArgumentException();
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
            dai.invokeAction(action, null);
        } catch (Exception e) {
            actual = e;
        }

        // then
        assertNotNull(actual);
        assertTrue(actual instanceof NoSuchMethodException);
    }

    @Deprecated
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

        SimpleAction action = new SimpleAction() {
            @Override
            public String doWith() throws Exception {
                throw new IllegalArgumentException();
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
            dai.invokeAction(action, null);
        } catch (Exception e) {
            actual = e;
        }

        // then
        assertNotNull(actual);
        assertTrue(actual instanceof NoSuchMethodException);
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
