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

import com.opensymphony.xwork2.SimpleFooAction;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.util.logging.Logger;

/**
 * Unit test for {@link TimerInterceptor}.
 *
 * @author Claus Ibsen
 */
public class TimerInterceptorTest extends XWorkTestCase {

    private MyTimerInterceptor interceptor;
    private MockActionInvocation mai;
    private MockActionProxy ap;


    public void testTimerInterceptor() throws Exception {
        TimerInterceptor real = new TimerInterceptor();
        real.init();
        real.intercept(mai);
        real.destroy();
    }

    public void testInvalidLogLevel() throws Exception {
        TimerInterceptor real = new TimerInterceptor();
        real.setLogLevel("xxxx");
        real.init();
        try {
            real.intercept(mai);
            fail("Should not have reached this point.");
        } catch (IllegalArgumentException e) {
        	// success
        }
    }

    public void testDefault() throws Exception {
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.LOG);
    }

    public void testNoNamespace() throws Exception {
        ap.setNamespace(null);
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.LOG);
    }

    public void testInputMethod() throws Exception {
        ap.setMethod("input");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!input] took "));
        assertSame(interceptor.logger, TimerInterceptor.LOG);
    }

    public void testTraceLevel() throws Exception {
        interceptor.setLogLevel("trace");
        interceptor.intercept(mai);
        assertNull(interceptor.message); // no default logging at trace level
        assertEquals("trace", interceptor.getLogLevel());
    }

    public void testDebugLevel() throws Exception {
        interceptor.setLogLevel("debug");
        interceptor.intercept(mai);
        assertNull(interceptor.message); // no default logging at debug level
    }

    public void testInfoLevel() throws Exception {
        interceptor.setLogLevel("info");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.LOG);
    }

    public void testWarnLevel() throws Exception {
        interceptor.setLogLevel("warn");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.LOG);
    }

    public void testErrorLevel() throws Exception {
        interceptor.setLogLevel("error");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.LOG);
    }

    public void testFatalLevel() throws Exception {
        interceptor.setLogLevel("fatal");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.LOG);
    }

    public void testLogCategory() throws Exception {
        interceptor.setLogCategory("com.mycompany.myapp.actiontiming");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertNotSame(interceptor.logger, TimerInterceptor.LOG);
    }

    public void testLogCategoryLevel() throws Exception {
        interceptor.setLogCategory("com.mycompany.myapp.actiontiming");
        interceptor.setLogLevel("error");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertNotSame(interceptor.logger, TimerInterceptor.LOG);
        assertEquals("com.mycompany.myapp.actiontiming", interceptor.getLogCategory());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new MyTimerInterceptor();
        interceptor.init();

        mai = new MockActionInvocation();
        ap = new MockActionProxy();
        ap.setActionName("myAction");
        ap.setNamespace("myApp");
        ap.setMethod("execute");
        mai.setAction(new SimpleFooAction());
        mai.setProxy(ap);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        interceptor.destroy();
        ap = null;
        mai = null;
    }

    private class MyTimerInterceptor extends TimerInterceptor {

        private Logger logger;
        private String message;

        @Override
        protected void doLog(Logger logger, String message) {
            super.doLog(logger, message);

            this.logger = logger;
            this.message = message;
        }
    }

}
