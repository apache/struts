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

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidationException;

import java.util.HashMap;

/**
 * Unit test for ExceptionMappingInterceptor.
 * 
 * @author Matthew E. Porter (matthew dot porter at metissian dot com)
 */
public class ExceptionMappingInterceptorTest extends XWorkTestCase {

    ActionInvocation invocation;
    ExceptionMappingInterceptor interceptor;
    Mock mockInvocation;
    ValueStack stack;


    public void testThrownExceptionMatching() throws Exception {
        this.setUpWithExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new XWorkException("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));
        String result = interceptor.intercept(invocation);
        assertNotNull(stack.findValue("exception"));
        assertEquals(stack.findValue("exception"), exception);
        assertEquals(result, "spooky");
        ExceptionHolder holder = (ExceptionHolder) stack.getRoot().get(0); // is on top of the root
        assertNotNull(holder.getExceptionStack()); // to invoke the method for unit test
    }

    public void testThrownExceptionMatching2() throws Exception {
        this.setUpWithExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new ValidationException("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));
        String result = interceptor.intercept(invocation);
        assertNotNull(stack.findValue("exception"));
        assertEquals(stack.findValue("exception"), exception);
        assertEquals(result, "throwable");
    }

    public void testNoThrownException() throws Exception {
        this.setUpWithExceptionMappings();

        Mock action = new Mock(Action.class);
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));
        String result = interceptor.intercept(invocation);
        assertEquals(result, Action.SUCCESS);
        assertNull(stack.findValue("exception"));
    }

    public void testThrownExceptionNoMatch() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLogging() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategory() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelFatal() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("fatal");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
        
        assertEquals("fatal", interceptor.getLogLevel());
        assertEquals(true, interceptor.isLogEnabled());
        assertEquals("showcase.unhandled", interceptor.getLogCategory());
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelError() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("error");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelWarn() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("warn");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelInfo() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("info");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelDebug() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("debug");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelTrace() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("trace");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingUnknownLevel() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogLevel("xxx");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (IllegalArgumentException e) {
        	// success
        }
    }

    private void setupWithoutExceptionMappings() {
        ActionConfig actionConfig = new ActionConfig.Builder("", "", "").build();
        Mock actionProxy = new Mock(ActionProxy.class);
        actionProxy.expectAndReturn("getConfig", actionConfig);
        mockInvocation.expectAndReturn("getProxy", ((ActionProxy) actionProxy.proxy()));
        invocation = (ActionInvocation) mockInvocation.proxy();
    }

    private void setUpWithExceptionMappings() {
        ActionConfig actionConfig = new ActionConfig.Builder("", "", "")
                .addExceptionMapping(new ExceptionMappingConfig.Builder("xwork", "com.opensymphony.xwork2.XWorkException", "spooky").build())
                .addExceptionMapping(new ExceptionMappingConfig.Builder("throwable", "java.lang.Throwable", "throwable").build())
                .build();
        Mock actionProxy = new Mock(ActionProxy.class);
        actionProxy.expectAndReturn("getConfig", actionConfig);
        mockInvocation.expectAndReturn("getProxy", ((ActionProxy) actionProxy.proxy()));

        invocation = (ActionInvocation) mockInvocation.proxy();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();
        mockInvocation = new Mock(ActionInvocation.class);
        mockInvocation.expectAndReturn("getStack", stack);
        mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(new HashMap()));
        interceptor = new ExceptionMappingInterceptor();
        interceptor.init();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        interceptor.destroy();
        invocation = null;
        interceptor = null;
        mockInvocation = null;
        stack = null;
    }

}
