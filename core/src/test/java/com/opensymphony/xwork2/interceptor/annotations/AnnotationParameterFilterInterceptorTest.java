package com.opensymphony.xwork2.interceptor.annotations;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.StubValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import junit.framework.TestCase;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * @author martin.gilday
 * @author jafl
 *
 */
public class AnnotationParameterFilterInterceptorTest extends TestCase {

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

		Map<String, Object> contextMap = new HashMap<>();
		Map<String, Object> parameterMap = new HashMap<>();
		
		parameterMap.put("job", "Baker");
		parameterMap.put("name", "Martin");

		contextMap.put(ActionContext.PARAMETERS, HttpParameters.create(parameterMap).build());
		
		Action action = new BlockingByDefaultAction();
		stack.push(action);
		
		Mock mockInvocation = new Mock(ActionInvocation.class);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.matchAndReturn("getAction", action);
		mockInvocation.matchAndReturn("getStack", stack);
		mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));

		ActionInvocation invocation = (ActionInvocation) mockInvocation.proxy();
		
		AnnotationParameterFilterInterceptor interceptor = new AnnotationParameterFilterInterceptor();
		interceptor.intercept(invocation);

		HttpParameters parameters = invocation.getInvocationContext().getParameters();
		assertEquals("Parameter map should contain one entry", 1, parameters.getNames().size());
		assertFalse(parameters.get("job").isDefined());
		assertTrue(parameters.get("name").isDefined());
		
	}

	/**
	 * "name" should be removed from the map, as it is blocked.
	 * All other parameters should remain
	 * @throws Exception
	 */
	public void testAllowingByDefault() throws Exception {

		Map<String, Object> contextMap = new HashMap<>();
		Map<String, Object> parameterMap = new HashMap<>();
		
		parameterMap.put("job", "Baker");
		parameterMap.put("name", "Martin");

		contextMap.put(ActionContext.PARAMETERS, HttpParameters.create(parameterMap).build());
		
		Action action = new AllowingByDefaultAction();
		stack.push(action);
		
		Mock mockInvocation = new Mock(ActionInvocation.class);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.matchAndReturn("getAction", action);
		mockInvocation.matchAndReturn("getStack", stack);
		mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));

		ActionInvocation invocation = (ActionInvocation) mockInvocation.proxy();
		
		AnnotationParameterFilterInterceptor interceptor = new AnnotationParameterFilterInterceptor();
		interceptor.intercept(invocation);

		HttpParameters parameters = invocation.getInvocationContext().getParameters();
		assertEquals("Paramwter map should contain one entry", 1, parameters.getNames().size());
		assertTrue(parameters.get("job").isDefined());
		assertFalse(parameters.get("name").isDefined());
		
	}

	/**
	 * Only "name" should remain in the parameter map.  All others
	 * should be removed
	 * @throws Exception
	 */
	public void testBlockingByDefaultWithModel() throws Exception {

		Map<String, Object> contextMap = new HashMap<>();
		Map<String, Object> parameterMap = new HashMap<>();
		
		parameterMap.put("job", "Baker");
		parameterMap.put("name", "Martin");
		parameterMap.put("m1", "s1");
		parameterMap.put("m2", "s2");

		contextMap.put(ActionContext.PARAMETERS, HttpParameters.create(parameterMap).build());
		stack.push(new BlockingByDefaultModel());
		
		Mock mockInvocation = new Mock(ActionInvocation.class);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.matchAndReturn("getAction", new BlockingByDefaultAction());
		mockInvocation.matchAndReturn("getStack", stack);
		mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));

		ActionInvocation invocation = (ActionInvocation) mockInvocation.proxy();
		
		AnnotationParameterFilterInterceptor interceptor = new AnnotationParameterFilterInterceptor();
		interceptor.intercept(invocation);

		HttpParameters parameters = invocation.getInvocationContext().getParameters();
		assertEquals("Parameter map should contain two entries", 2, parameters.getNames().size());
		assertFalse(parameters.get("job").isDefined());
		assertTrue(parameters.get("name").isDefined());
		assertTrue(parameters.get("m1").isDefined());
		assertFalse(parameters.get("m2").isDefined());
		
	}

	/**
	 * "name" should be removed from the map, as it is blocked.
	 * All other parameters should remain
	 * @throws Exception
	 */
	public void testAllowingByDefaultWithModel() throws Exception {

		Map<String, Object> contextMap = new HashMap<>();
		Map<String, Object> parameterMap = new HashMap<>();
		
		parameterMap.put("job", "Baker");
		parameterMap.put("name", "Martin");
		parameterMap.put("m1", "s1");
		parameterMap.put("m2", "s2");

		contextMap.put(ActionContext.PARAMETERS, HttpParameters.create(parameterMap).build());
		stack.push(new AllowingByDefaultModel());
		
		Mock mockInvocation = new Mock(ActionInvocation.class);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.matchAndReturn("getAction", new AllowingByDefaultAction());
		mockInvocation.matchAndReturn("getStack", stack);
		mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
		mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));

		ActionInvocation invocation = (ActionInvocation) mockInvocation.proxy();
		
		AnnotationParameterFilterInterceptor interceptor = new AnnotationParameterFilterInterceptor();
		interceptor.intercept(invocation);

		HttpParameters parameters = invocation.getInvocationContext().getParameters();
		assertEquals("Parameter map should contain two entries", 2, parameters.getNames().size());
		assertTrue(parameters.get("job").isDefined());
		assertFalse(parameters.get("name").isDefined());
		assertFalse(parameters.get("m1").isDefined());
		assertTrue(parameters.get("m2").isDefined());
		
	}
	
}
