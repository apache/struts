package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.mock.MockContainer;
import com.opensymphony.xwork2.mock.MockInterceptor;

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

}

class DefaultActionInvocationTester extends DefaultActionInvocation {
    DefaultActionInvocationTester(List<InterceptorMapping> interceptorMappings) {
        super(new HashMap<String, Object>(), false);
        interceptors = interceptorMappings.iterator();
        MockActionProxy actionProxy = new MockActionProxy();
        actionProxy.setMethod("execute");
        proxy = actionProxy;
        action = new ActionSupport();
    }
}
