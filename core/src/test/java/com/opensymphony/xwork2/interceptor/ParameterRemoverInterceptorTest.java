package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import junit.framework.TestCase;
import org.apache.struts2.dispatcher.HttpParameters;
import org.easymock.MockControl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author tmjee
 * @version $Date$ $Id$
 */
public class ParameterRemoverInterceptorTest extends TestCase {

	protected Map<String, Object> contextMap;
	protected ActionContext context;
	protected MockControl actionInvocationControl;
	protected ActionInvocation actionInvocation;
	
	@Override
    protected void setUp() throws Exception {
		contextMap = new LinkedHashMap<>();
		context = new ActionContext(contextMap);
		
		actionInvocationControl = MockControl.createControl(ActionInvocation.class);
		actionInvocation = (ActionInvocation) actionInvocationControl.getMock();
		actionInvocationControl.expectAndDefaultReturn(actionInvocation.getAction(),  new SampleAction());
		actionInvocationControl.expectAndDefaultReturn(actionInvocation.getInvocationContext(), context);
		actionInvocationControl.expectAndDefaultReturn(actionInvocation.invoke(), "success");
	}
	
	public void testInterception1() throws Exception {
		contextMap.put(ActionContext.PARAMETERS, HttpParameters.create(new LinkedHashMap<String, Object>() {
			{
				put("param1", new String[]{"paramValue1"});
				put("param2", new String[]{"paramValue2"});
				put("param3", new String[]{"paramValue3"});
				put("param", new String[]{"paramValue"});
			}
		}).build());
		
		actionInvocationControl.replay();
		
		ParameterRemoverInterceptor interceptor = new ParameterRemoverInterceptor();
		interceptor.setParamNames("param1,param2");
		interceptor.setParamValues("paramValue1,paramValue2");
		interceptor.intercept(actionInvocation);
		
		HttpParameters params = (HttpParameters) contextMap.get(ActionContext.PARAMETERS);
		assertEquals(params.getNames().size(), 2);
		assertTrue(params.contains("param3"));
		assertTrue(params.contains("param"));
		assertEquals(params.get("param3").getValue(), "paramValue3");
		assertEquals(params.get("param").getValue(), "paramValue");
		
		actionInvocationControl.verify();
	}
	
	
	public void testInterception2() throws Exception {
		contextMap.put(ActionContext.PARAMETERS, HttpParameters.create(new LinkedHashMap<String, Object>() {
			{
				put("param1", new String[] { "paramValue2" });
				put("param2", new String[] { "paramValue1" });
			}
		}).build());
		
		actionInvocationControl.replay();
		
		ParameterRemoverInterceptor interceptor = new ParameterRemoverInterceptor();
		interceptor.setParamNames("param1,param2");
		interceptor.setParamValues("paramValue1,paramValue2");
		interceptor.intercept(actionInvocation);
		
		HttpParameters params = (HttpParameters) contextMap.get(ActionContext.PARAMETERS);
		assertEquals(params.getNames().size(), 0);
		
		actionInvocationControl.verify();
	}
	
	
	public void testInterception3() throws Exception {
		contextMap.put(ActionContext.PARAMETERS, HttpParameters.create(new LinkedHashMap<String, Object>() {
			{
				put("param1", new String[] { "paramValueOne" });
				put("param2", new String[] { "paramValueTwo" });
			}
		}).build());
		
		actionInvocationControl.replay();
		
		ParameterRemoverInterceptor interceptor = new ParameterRemoverInterceptor();
		interceptor.setParamNames("param1,param2");
		interceptor.setParamValues("paramValue1,paramValue2");
		interceptor.intercept(actionInvocation);
		
		HttpParameters params = (HttpParameters) contextMap.get(ActionContext.PARAMETERS);
		assertEquals(params.getNames().size(), 2);
		assertTrue(params.contains("param1"));
		assertTrue(params.contains("param2"));
		assertEquals(params.get("param1").getValue(), "paramValueOne");
		assertEquals(params.get("param2").getValue(), "paramValueTwo");
		
		actionInvocationControl.verify();
	}
	
	class SampleAction extends ActionSupport {
		private static final long serialVersionUID = 7489487258845368260L;
	}
}
