/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.HashMap;
import java.util.Map;


/**
 * Unit test for {@link ConversionErrorInterceptor}.
 *
 * @author Jason Carreira
 */
public class ConversionErrorInterceptorTest extends XWorkTestCase {

    protected ActionContext context;
    protected ActionInvocation invocation;
    protected ConversionErrorInterceptor interceptor;
    protected Map<String, Object> conversionErrors;
    protected Mock mockInvocation;
    protected ValueStack stack;


    public void testFieldErrorAdded() throws Exception {
        conversionErrors.put("foo", 123L);

        SimpleAction action = new SimpleAction();
        mockInvocation.expectAndReturn("getAction", action);
        stack.push(action);
        mockInvocation.matchAndReturn("getAction", action);
        assertNull(action.getFieldErrors().get("foo"));
        interceptor.intercept(invocation);
        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("foo"));
    }

    public void testFieldErrorWithMapKeyAdded() throws Exception {
        String fieldName = "foo['1'].intValue";
        conversionErrors.put(fieldName, "bar");
        ActionSupport action = new ActionSupport();
        mockInvocation.expectAndReturn("getAction", action);
        stack.push(action);
        mockInvocation.matchAndReturn("getAction", action);
        assertNull(action.getFieldErrors().get(fieldName));
        interceptor.intercept(invocation);
        assertTrue(action.hasFieldErrors()); // This fails!
        assertNotNull(action.getFieldErrors().get(fieldName));
    }

    public void testWithPreResultListener() throws Exception {
        conversionErrors.put("foo", "Hello");

        ActionContext ac = createActionContext();
        MockActionInvocation mai = createActionInvocation(ac);
        SimpleAction action = createAction(mai);

        assertNull(action.getFieldErrors().get("foo"));
        assertEquals(55, stack.findValue("foo"));

        interceptor.intercept(mai);

        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("foo"));

        assertEquals("Hello", stack.findValue("foo")); // assume that the original value is reset
    }

    /**
     * See WW-3668
     *
     * @throws Exception
     */
    public void testWithPreResultListenerAgainstMaliciousCode() throws Exception {
        conversionErrors.put("foo", "\" + #root + \"");

        ActionContext ac = createActionContext();

        MockActionInvocation mai = createActionInvocation(ac);

        SimpleAction action = createAction(mai);
        assertNull(action.getFieldErrors().get("foo"));
        assertEquals(55, stack.findValue("foo"));

        interceptor.intercept(mai);

        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("foo"));

        assertEquals("\" + #root + \"", stack.findValue("foo"));
    }

    private MockActionInvocation createActionInvocation(ActionContext ac) {
        MockActionInvocation mai = new MockActionInvocation();
        mai.setInvocationContext(ac);
        mai.setStack(stack);
        return mai;
    }

    private SimpleAction createAction(MockActionInvocation mai) {
        SimpleAction action = new SimpleAction();
        action.setFoo(55);
        mai.setAction(action);
        stack.push(action);
        return action;
    }

    private ActionContext createActionContext() {
        ActionContext ac = new ActionContext(stack.getContext());
        ac.setConversionErrors(conversionErrors);
        ac.setValueStack(stack);
        return ac;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new ConversionErrorInterceptor();
        mockInvocation = new Mock(ActionInvocation.class);
        invocation = (ActionInvocation) mockInvocation.proxy();
        stack = ActionContext.getContext().getValueStack();
        context = new ActionContext(stack.getContext());
        conversionErrors = new HashMap<String, Object>();
        context.setConversionErrors(conversionErrors);
        mockInvocation.matchAndReturn("getInvocationContext", context);
        mockInvocation.expect("addPreResultListener", C.isA(PreResultListener.class));
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
    }
}
