package com.opensymphony.xwork2.interceptor.annotations;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author martin.gilday
 *
 */
public class AnnotationParameterFilterUnitTest extends TestCase {

	/**
	 * Only "name" should remain in the parameter map.  All others
	 * should be removed
	 * @throws Exception
	 */
	public void testBlockingByDefault() throws Exception {
		
		Map contextMap = new HashMap();
		Map parameterMap = new HashMap();
		
		parameterMap.put("job", "Baker");
		parameterMap.put("name", "Martin");
		
		contextMap.put(ActionContext.PARAMETERS, parameterMap);
		
		Mock mockInvocation = new Mock(ActionInvocation.class);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
        mockInvocation.matchAndReturn("getAction", new BlockingByDefaultAction());
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
        
		ActionInvocation invocation = (ActionInvocation) mockInvocation.proxy();
		
		AnnotationParameterFilterIntereptor intereptor = new AnnotationParameterFilterIntereptor();
		intereptor.intercept(invocation);
		
		assertEquals("Paramter map should contain one entry", 1, parameterMap.size());
		assertNull(parameterMap.get("job"));
		assertNotNull(parameterMap.get("name"));
		
	}

	/**
	 * "name" should be removed from the map, as it is blocked.
	 * All other parameters should remain
	 * @throws Exception
	 */
	public void testAllowingByDefault() throws Exception {
		
		Map contextMap = new HashMap();
		Map parameterMap = new HashMap();
		
		parameterMap.put("job", "Baker");
		parameterMap.put("name", "Martin");
		
		contextMap.put(ActionContext.PARAMETERS, parameterMap);
		
		Mock mockInvocation = new Mock(ActionInvocation.class);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
        mockInvocation.matchAndReturn("getAction", new AllowingByDefaultAction());
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
        
		ActionInvocation invocation = (ActionInvocation) mockInvocation.proxy();
		
		AnnotationParameterFilterIntereptor intereptor = new AnnotationParameterFilterIntereptor();
		intereptor.intercept(invocation);
		
		assertEquals("Paramter map should contain one entry", 1, parameterMap.size());
		assertNotNull(parameterMap.get("job"));
		assertNull(parameterMap.get("name"));
		
	}
	
}
