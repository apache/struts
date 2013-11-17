/*
 * $Id$
 *
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

package org.apache.struts2.util;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsInternalTestCase;

import java.util.HashMap;
import java.util.Map;


/**
 * InvocationSessionStoreTest
 *
 */
public class InvocationSessionStoreTest extends StrutsInternalTestCase {

    private static final String INVOCATION_KEY = "org.apache.struts2.util.InvocationSessionStoreTest.invocation";
    private static final String TOKEN_VALUE = "org.apache.struts2.util.InvocationSessionStoreTest.token";


    private ActionInvocation invocation;
    private Map session;
    private Mock invocationMock;
    private ValueStack stack;


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
        super.setUp();
        stack = ActionContext.getContext().getValueStack();

        ActionContext actionContext = new ActionContext(stack.getContext());
        ActionContext.setContext(actionContext);

        session = new HashMap();
        actionContext.setSession(session);

        invocationMock = new Mock(ActionInvocation.class);
        invocation = (ActionInvocation) invocationMock.proxy();
        invocationMock.matchAndReturn("serialize", invocation);
        invocationMock.matchAndReturn("deserialize", actionContext, invocation);

        actionContext.setValueStack(stack);
        invocationMock.matchAndReturn("getStack", stack);

        Mock proxyMock = new Mock(ActionProxy.class);
        proxyMock.matchAndReturn("getInvocation", invocation);

        ActionProxy proxy = (ActionProxy) proxyMock.proxy();

        invocationMock.matchAndReturn("getProxy", proxy);
    }
}
