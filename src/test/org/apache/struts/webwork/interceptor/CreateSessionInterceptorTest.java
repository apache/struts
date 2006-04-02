/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Mock;
import org.jmock.core.constraint.IsEqual;
import org.jmock.core.matcher.InvokeOnceMatcher;

import org.apache.struts.webwork.ServletActionContext;
import org.apache.struts.webwork.StrutsTestCase;
import com.opensymphony.xwork.mock.MockActionInvocation;

/**
 * Test case for CreateSessionInterceptor.
 * 
 * @author tm_jee
 * @version $Date: 2006/03/04 15:06:48 $ $Id: CreateSessionInterceptorTest.java,v 1.2 2006/03/04 15:06:48 rainerh Exp $
 */
public class CreateSessionInterceptorTest extends StrutsTestCase {

	public void testCreateSession() throws Exception {
		Mock httpServletRequestMock = new Mock(HttpServletRequest.class);
		httpServletRequestMock.expects(new InvokeOnceMatcher()).method("getSession").with(new IsEqual(Boolean.TRUE));
		HttpServletRequest request = (HttpServletRequest) httpServletRequestMock.proxy();

		ServletActionContext.setRequest(request);

		CreateSessionInterceptor interceptor = new CreateSessionInterceptor();
		interceptor.intercept(new MockActionInvocation());

		httpServletRequestMock.verify();
	}
}
