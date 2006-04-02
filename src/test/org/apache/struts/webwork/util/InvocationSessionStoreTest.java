/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.util;

import com.mockobjects.dynamic.Mock;
import org.apache.struts.webwork.StrutsTestCase;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.HashMap;
import java.util.Map;


/**
 * InvocationSessionStoreTest
 *
 * @author Jason Carreira Created Apr 12, 2003 10:34:53 PM
 */
public class InvocationSessionStoreTest extends StrutsTestCase {

    private static final String INVOCATION_KEY = "org.apache.struts.webwork.util.InvocationSessionStoreTest.invocation";
    private static final String TOKEN_VALUE = "org.apache.struts.webwork.util.InvocationSessionStoreTest.token";


    private ActionInvocation invocation;
    private Map session;
    private Mock invocationMock;
    private OgnlValueStack stack;


    public void testStore() {
        assertNull(InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE));
        InvocationSessionStore.storeInvocation(INVOCATION_KEY, TOKEN_VALUE, invocation);
        assertNotNull(InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE));
        assertEquals(invocation, InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE));
    }

    public void testValueStackReset() {
        ActionContext actionContext = ActionContext.getContext();
        assertEquals(stack, actionContext.getValueStack());
        InvocationSessionStore.storeInvocation(INVOCATION_KEY, TOKEN_VALUE, invocation);
        actionContext.setValueStack(null);
        assertNull(actionContext.getValueStack());
        InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE);
        assertEquals(stack, actionContext.getValueStack());
    }

    protected void setUp() throws Exception {
        stack = new OgnlValueStack();

        ActionContext actionContext = new ActionContext(stack.getContext());
        ActionContext.setContext(actionContext);

        session = new HashMap();
        actionContext.setSession(session);

        invocationMock = new Mock(ActionInvocation.class);
        invocation = (ActionInvocation) invocationMock.proxy();

        actionContext.setValueStack(stack);
        invocationMock.matchAndReturn("getStack", stack);

        Mock proxyMock = new Mock(ActionProxy.class);
        proxyMock.matchAndReturn("getInvocation", invocation);

        ActionProxy proxy = (ActionProxy) proxyMock.proxy();

        invocationMock.matchAndReturn("getProxy", proxy);
    }
}
