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

import com.opensymphony.xwork2.interceptor.Interceptor;
import junit.framework.Assert;


/**
 * TestInterceptor
 *
 * @author Jason Carreira
 *         Created Apr 21, 2003 9:04:06 PM
 */
public class TestInterceptor implements Interceptor {

    public static final String DEFAULT_FOO_VALUE = "fooDefault";


    private String expectedFoo = DEFAULT_FOO_VALUE;
    private String foo = DEFAULT_FOO_VALUE;
    private boolean executed = false;


    public boolean isExecuted() {
        return executed;
    }

    public void setExpectedFoo(String expectedFoo) {
        this.expectedFoo = expectedFoo;
    }

    public String getExpectedFoo() {
        return expectedFoo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return foo;
    }

    /**
     * Called to let an interceptor clean up any resources it has allocated.
     */
    public void destroy() {
    }

    /**
     * Called after an Interceptor is created, but before any requests are processed using the intercept() methodName. This
     * gives the Interceptor a chance to initialize any needed resources.
     */
    public void init() {
    }

    /**
     * Allows the Interceptor to do some processing on the request before and/or after the rest of the processing of the
     * request by the DefaultActionInvocation or to short-circuit the processing and just return a String return code.
     *
     * @param invocation
     * @return
     * @throws Exception
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        executed = true;
        Assert.assertNotSame(DEFAULT_FOO_VALUE, foo);
        Assert.assertEquals(expectedFoo, foo);

        return invocation.invoke();
    }
}
