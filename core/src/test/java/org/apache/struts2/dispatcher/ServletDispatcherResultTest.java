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

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ognl.Ognl;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.StrutsStatics;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;


/**
 *
 */
public class ServletDispatcherResultTest extends StrutsInternalTestCase implements StrutsStatics {

    public void testInclude() {
        ServletDispatcherResult view = new ServletDispatcherResult();
        view.setLocation("foo.jsp");

        Mock dispatcherMock = new Mock(RequestDispatcher.class);
        dispatcherMock.expect("include", C.ANY_ARGS);

        Mock requestMock = new Mock(HttpServletRequest.class);
        requestMock.expectAndReturn("getAttribute", "struts.actiontag.invocation", null);
        requestMock.expectAndReturn("getRequestDispatcher", C.args(C.eq("foo.jsp")), dispatcherMock.proxy());

        Mock responseMock = new Mock(HttpServletResponse.class);
        responseMock.expectAndReturn("isCommitted", Boolean.TRUE);

        ActionContext ac = new ActionContext(Ognl.createDefaultContext(null));
        ActionContext.setContext(ac);
        ServletActionContext.setRequest((HttpServletRequest) requestMock.proxy());
        ServletActionContext.setResponse((HttpServletResponse) responseMock.proxy());

        try {
            view.execute(null);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        dispatcherMock.verify();
        requestMock.verify();
        dispatcherMock.verify();
    }

    public void testSimple() {
        ServletDispatcherResult view = new ServletDispatcherResult();
        view.setLocation("foo.jsp");

        Mock dispatcherMock = new Mock(RequestDispatcher.class);
        dispatcherMock.expect("forward", C.ANY_ARGS);

        Mock requestMock = new Mock(HttpServletRequest.class);
        requestMock.expectAndReturn("getAttribute", "struts.actiontag.invocation", null);
        requestMock.expectAndReturn("getAttribute", "javax.servlet.include.servlet_path", null);
        requestMock.expectAndReturn("getRequestDispatcher", C.args(C.eq("foo.jsp")), dispatcherMock.proxy());
        requestMock.expect("setAttribute", C.ANY_ARGS); // this is a bad mock, but it works
        requestMock.expect("setAttribute", C.ANY_ARGS); // this is a bad mock, but it works
        requestMock.matchAndReturn("getRequestURI", "foo.jsp");

        Mock responseMock = new Mock(HttpServletResponse.class);
        responseMock.expectAndReturn("isCommitted", Boolean.FALSE);

        ActionContext ac = new ActionContext(Ognl.createDefaultContext(null));
        ActionContext.setContext(ac);
        ServletActionContext.setRequest((HttpServletRequest) requestMock.proxy());
        ServletActionContext.setResponse((HttpServletResponse) responseMock.proxy());

        try {
            view.execute(null);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        dispatcherMock.verify();
        requestMock.verify();
        dispatcherMock.verify();
    }
}
