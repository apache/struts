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
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.easymock.IMocksControl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;

public class PostbackResultTest extends StrutsInternalTestCase {

    public void testWithNoNamespace() throws Exception {

        ActionContext context = ActionContext.getContext();
        ValueStack stack = context.getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        context.put(ServletActionContext.HTTP_REQUEST, req);
        context.put(ServletActionContext.HTTP_RESPONSE, res);

        PostbackResult result = new PostbackResult();
        result.setActionName("myAction${1-1}");
        result.setPrependServletContext(false);

        IMocksControl control = createControl();
        ActionProxy mockActionProxy = control.createMock(ActionProxy.class);
        ActionInvocation mockInvocation = control.createMock(ActionInvocation.class);
        expect(mockInvocation.getInvocationContext()).andReturn(context).anyTimes();
        expect(mockInvocation.getStack()).andReturn(stack).anyTimes();
        expect(mockInvocation.getProxy()).andReturn(mockActionProxy);
        expect(mockActionProxy.getNamespace()).andReturn("${1-1}");

        control.replay();
        result.setActionMapper(container.getInstance(ActionMapper.class));
        result.execute(mockInvocation);
        assertEquals("<!DOCTYPE html><html><body><form action=\"${1-1}/myAction0.action\" method=\"POST\">" +
                "<script>setTimeout(function(){document.forms[0].submit();},0);</script></html>", res.getContentAsString());

        control.verify();
    }

    public void testWithNamespace() throws Exception {

        ActionContext context = ActionContext.getContext();
        ValueStack stack = context.getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        context.put(ServletActionContext.HTTP_REQUEST, req);
        context.put(ServletActionContext.HTTP_RESPONSE, res);

        PostbackResult result = new PostbackResult();
        result.setActionName("myAction${1-1}");
        result.setNamespace("myNamespace${1-1}");
        result.setPrependServletContext(false);

        IMocksControl control = createControl();
        ActionInvocation mockInvocation = control.createMock(ActionInvocation.class);
        expect(mockInvocation.getInvocationContext()).andReturn(context).anyTimes();
        expect(mockInvocation.getStack()).andReturn(stack).anyTimes();

        control.replay();
        result.setActionMapper(container.getInstance(ActionMapper.class));
        result.execute(mockInvocation);
        assertEquals("<!DOCTYPE html><html><body><form action=\"myNamespace0/myAction0.action\" method=\"POST\">" +
                "<script>setTimeout(function(){document.forms[0].submit();},0);</script></html>", res.getContentAsString());

        control.verify();
    }

    public void testExpressionNamespace() throws Exception {

        ActionContext context = ActionContext.getContext();
        context.getContextMap().put("namespaceName", "${1-1}");
        context.getContextMap().put("actionName", "${1-1}");
        context.getContextMap().put("methodName", "${1-1}");
        ValueStack stack = context.getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        context.put(ServletActionContext.HTTP_REQUEST, req);
        context.put(ServletActionContext.HTTP_RESPONSE, res);

        PostbackResult result = new PostbackResult();
        result.setNamespace("/myNamespace${#namespaceName}");
        result.setActionName("myAction${#actionName}");
        result.setMethod("myMethod${#methodName}");
        result.setPrependServletContext(false);

        IMocksControl control = createControl();
        ActionInvocation mockInvocation = control.createMock(ActionInvocation.class);
        expect(mockInvocation.getInvocationContext()).andReturn(context).anyTimes();
        expect(mockInvocation.getStack()).andReturn(stack).anyTimes();

        control.replay();
        result.setActionMapper(container.getInstance(ActionMapper.class));
        result.execute(mockInvocation);
        assertEquals("<!DOCTYPE html><html><body><form action=\"/myNamespace${1-1}/myAction${1-1}!myMethod${1-1}.action\" method=\"POST\">" +
                "<script>setTimeout(function(){document.forms[0].submit();},0);</script></html>", res.getContentAsString());

        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();
        context.put(ServletActionContext.HTTP_REQUEST, req);
        context.put(ServletActionContext.HTTP_RESPONSE, res);
        result.execute(mockInvocation);
        assertEquals("<!DOCTYPE html><html><body><form action=\"/myNamespace0/myAction0!myMethod0.action\" method=\"POST\">" +
                "<script>setTimeout(function(){document.forms[0].submit();},0);</script></html>", res.getContentAsString());

        control.verify();
    }

    public void testPassingNullInvocation() throws Exception{
        Result result = new PostbackResult();
        try {
            result.execute(null);
            fail("Exception should be thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals("Invocation cannot be null!", e.getMessage());
        }
    }


}
