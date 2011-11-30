package com.opensymphony.xwork2.interceptor.annotations;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.StubValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author martin.gilday
 * @author jafl
 *
 */
public class AnnotationParameterFilterUnitTest extends TestCase {

	ValueStack stack;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		stack = new StubValueStack();
	}

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
		
		Action action = new BlockingByDefaultAction();
		stack.push(action);
		
		Mock mockInvocation = new Mock(ActionInvocation.class);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.matchAndReturn("getAction", action);
		mockInvocation.matchAndReturn("getStack", stack);
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
		
		Action action = new AllowingByDefaultAction();
		stack.push(action);
		
		Mock mockInvocation = new Mock(ActionInvocation.class);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.matchAndReturn("getAction", action);
		mockInvocation.matchAndReturn("getStack", stack);
		mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
		
		ActionInvocation invocation = (ActionInvocation) mockInvocation.proxy();
		
		AnnotationParameterFilterIntereptor intereptor = new AnnotationParameterFilterIntereptor();
		intereptor.intercept(invocation);
		
		assertEquals("Paramter map should contain one entry", 1, parameterMap.size());
		assertNotNull(parameterMap.get("job"));
		assertNull(parameterMap.get("name"));
		
	}

	/**
	 * Only "name" should remain in the parameter map.  All others
	 * should be removed
	 * @throws Exception
	 */
	public void testBlockingByDefaultWithModel() throws Exception {
		
		Map contextMap = new HashMap();
		Map parameterMap = new HashMap();
		
		parameterMap.put("job", "Baker");
		parameterMap.put("name", "Martin");
		parameterMap.put("m1", "s1");
		parameterMap.put("m2", "s2");
		
		contextMap.put(ActionContext.PARAMETERS, parameterMap);
		stack.push(new BlockingByDefaultModel());
		
		Mock mockInvocation = new Mock(ActionInvocation.class);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.matchAndReturn("getAction", new BlockingByDefaultAction());
		mockInvocation.matchAndReturn("getStack", stack);
		mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
		
		ActionInvocation invocation = (ActionInvocation) mockInvocation.proxy();
		
		AnnotationParameterFilterIntereptor intereptor = new AnnotationParameterFilterIntereptor();
		intereptor.intercept(invocation);
		
		assertEquals("Paramter map should contain two entries", 2, parameterMap.size());
		assertNull(parameterMap.get("job"));
		assertNotNull(parameterMap.get("name"));
		assertNotNull(parameterMap.get("m1"));
		assertNull(parameterMap.get("m2"));
		
	}

	/**
	 * "name" should be removed from the map, as it is blocked.
	 * All other parameters should remain
	 * @throws Exception
	 */
	public void testAllowingByDefaultWithModel() throws Exception {
		
		Map contextMap = new HashMap();
		Map parameterMap = new HashMap();
		
		parameterMap.put("job", "Baker");
		parameterMap.put("name", "Martin");
		parameterMap.put("m1", "s1");
		parameterMap.put("m2", "s2");
		
		contextMap.put(ActionContext.PARAMETERS, parameterMap);
		stack.push(new AllowingByDefaultModel());
		
		Mock mockInvocation = new Mock(ActionInvocation.class);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.matchAndReturn("getAction", new AllowingByDefaultAction());
		mockInvocation.matchAndReturn("getStack", stack);
		mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
		
		ActionInvocation invocation = (ActionInvocation) mockInvocation.proxy();
		
		AnnotationParameterFilterIntereptor intereptor = new AnnotationParameterFilterIntereptor();
		intereptor.intercept(invocation);
		
		assertEquals("Paramter map should contain two entries", 2, parameterMap.size());
		assertNotNull(parameterMap.get("job"));
		assertNull(parameterMap.get("name"));
		assertNull(parameterMap.get("m1"));
		assertNotNull(parameterMap.get("m2"));
		
	}
	
}
