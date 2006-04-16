/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.dispatcher;

import org.apache.struts.action2.TestAction;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;
import junit.framework.Assert;


/**
 */
public class ServletDispatchedTestAssertInterceptor implements Interceptor {
	
	private static final long serialVersionUID = 1980347231443329805L;

	public ServletDispatchedTestAssertInterceptor() {
        super();
    }

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        Assert.assertTrue(invocation.getAction() instanceof TestAction);

        TestAction testAction = (TestAction) invocation.getAction();

        Assert.assertEquals("bar", testAction.getFoo());

        String result = invocation.invoke();

        return result;
    }
}
