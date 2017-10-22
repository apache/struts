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
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.junit.Assert;

/**
 * Mock for an {@link com.opensymphony.xwork2.interceptor.Interceptor}.
 *
 * @author Jason Carreira
 */
public class MockInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 2692551676567227756L;
    
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MockInterceptor)) {
            return false;
        }

        final MockInterceptor testInterceptor = (MockInterceptor) o;

        if (executed != testInterceptor.executed) {
            return false;
        }

        if ((expectedFoo != null) ? (!expectedFoo.equals(testInterceptor.expectedFoo)) : (testInterceptor.expectedFoo != null))
        {
            return false;
        }

        if ((foo != null) ? (!foo.equals(testInterceptor.foo)) : (testInterceptor.foo != null)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ((expectedFoo != null) ? expectedFoo.hashCode() : 0);
        result = (29 * result) + ((foo != null) ? foo.hashCode() : 0);
        result = (29 * result) + (executed ? 1 : 0);

        return result;
    }

    /**
     * Allows the Interceptor to do some processing on the request before and/or after the rest of the processing of the
     * request by the DefaultActionInvocation or to short-circuit the processing and just return a String return code.
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        executed = true;
        Assert.assertNotSame(DEFAULT_FOO_VALUE, foo);
        Assert.assertEquals(expectedFoo, foo);

        return invocation.invoke();
    }
}
