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

package org.apache.struts2.dispatcher;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsTestCase;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;


/**
 * HttpHeaderResultTest
 *
 */
public class HttpHeaderResultTest extends StrutsTestCase {

    ActionInvocation invocation;
    HttpHeaderResult result;
    HttpServletResponse response;
    Mock responseMock;
    ReflectionProvider reflectionProvider;


    public void testHeaderValuesAreNotParsedWhenParseIsFalse() throws Exception {
        Map params = new HashMap();
        params.put("headers.foo", "${bar}");
        params.put("headers.baz", "baz");

        Map values = new HashMap();
        values.put("bar", "abc");
        ActionContext.getContext().getValueStack().push(values);

        reflectionProvider.setProperties(params, result);

        responseMock.expect("addHeader", C.args(C.eq("foo"), C.eq("${bar}")));
        responseMock.expect("addHeader", C.args(C.eq("baz"), C.eq("baz")));
        result.setParse(false);
        result.execute(invocation);
        responseMock.verify();
    }

    public void testHeaderValuesAreParsedAndSet() throws Exception {
        Map params = new HashMap();
        params.put("headers.foo", "${bar}");
        params.put("headers.baz", "baz");

        Map values = new HashMap();
        values.put("bar", "abc");
        ActionContext.getContext().getValueStack().push(values);

        reflectionProvider.setProperties(params, result);

        responseMock.expect("addHeader", C.args(C.eq("foo"), C.eq("abc")));
        responseMock.expect("addHeader", C.args(C.eq("baz"), C.eq("baz")));
        result.execute(invocation);
        responseMock.verify();
    }
    
    public void testErrorMessageIsParsedAndSet() throws Exception {
        ActionContext.getContext().getValueStack().set("errMsg", "abc");
        result.setError(404);
        result.setErrorMessage("${errMsg}");
        
        responseMock.expect("sendError", C.args(C.eq(404), C.eq("abc")));
        result.execute(invocation);
        responseMock.verify();
    }
    
    public void testErrorMessageIsNotParsedAndSet() throws Exception {
        ActionContext.getContext().getValueStack().set("errMsg", "abc");
        result.setError(404);
        result.setParse(false);
        result.setErrorMessage("${errMsg}");
        
        responseMock.expect("sendError", C.args(C.eq(404), C.eq("${errMsg}")));
        result.execute(invocation);
        responseMock.verify();
    }

    public void testStatusIsSet() throws Exception {
        responseMock.expect("setStatus", C.eq(123));
        result.setStatus(123);
        result.execute(invocation);
        responseMock.verify();
    }
    
    public void testErrorIsSet() throws Exception {
        responseMock.expect("sendError", C.eq(404));
        result.setError(404);
        result.execute(invocation);
        responseMock.verify();
    }

    protected void setUp() throws Exception {
        super.setUp();
        result = new HttpHeaderResult();
        responseMock = new Mock(HttpServletResponse.class);
        response = (HttpServletResponse) responseMock.proxy();
        invocation = (ActionInvocation) new Mock(ActionInvocation.class).proxy();
        reflectionProvider = container.getInstance(ReflectionProvider.class);
        ServletActionContext.setResponse(response);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
    }
}
