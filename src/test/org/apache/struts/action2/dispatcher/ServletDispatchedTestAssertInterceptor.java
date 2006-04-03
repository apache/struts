/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 2/10/2003
 *
 */
package org.apache.struts.action2.dispatcher;

import org.apache.struts.action2.TestAction;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;
import junit.framework.Assert;


/**
 * @author CameronBraid
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
