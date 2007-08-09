package org.apache.struts2.portlet.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;

import junit.framework.TestCase;

import org.apache.struts2.portlet.PortletActionConstants;
import org.easymock.EasyMock;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

public class PortletAwareInterceptorTest extends TestCase implements PortletActionConstants {

	private PortletAwareInterceptor interceptor;
	
	protected void setUp() throws Exception {
		super.setUp();
		interceptor = new PortletAwareInterceptor();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testPortletRequestIsSet() throws Exception {
		PortletRequest request = EasyMock.createMock(PortletRequest.class);
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put(REQUEST, request);
		PortletRequestAware action = EasyMock.createMock(PortletRequestAware.class);
		action.setPortletRequest(request);
		
		ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
		EasyMock.expect(invocation.getInvocationContext()).andReturn(new ActionContext(ctx));
		EasyMock.expect(invocation.getAction()).andReturn(action);
		
		EasyMock.replay(action);
		EasyMock.replay(invocation);
		
		interceptor.intercept(invocation);
		
		EasyMock.verify(action);
	}
}
