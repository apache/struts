package org.apache.struts2.rest;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.DefaultUnknownHandlerManager;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.mock.MockInterceptor;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.XWorkTestCaseHelper;
import junit.framework.TestCase;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.HttpHeaderResult;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;

public class RestActionInvocationTest extends TestCase {

	RestActionInvocation restActionInvocation;
	MockHttpServletRequest request;
	MockHttpServletResponse response;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		restActionInvocation = new RestActionInvocationTester();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		ServletActionContext.setRequest(request);
		ServletActionContext.setResponse(response);

	}
	
	/**
	 * Test the correct action results: null, String, HttpHeaders, Result
	 * @throws Exception
	 */
	public void testSaveResult() throws Exception {

		Object methodResult = "index";
		ActionConfig actionConfig = restActionInvocation.getProxy().getConfig();
		assertEquals("index", restActionInvocation.saveResult(actionConfig, methodResult));
    	
		setUp();
    	methodResult = new DefaultHttpHeaders("show");
    	assertEquals("show", restActionInvocation.saveResult(actionConfig, methodResult));
    	assertEquals(methodResult, restActionInvocation.httpHeaders);
    	
		setUp();
    	methodResult = new HttpHeaderResult(HttpServletResponse.SC_ACCEPTED);
    	assertEquals(null, restActionInvocation.saveResult(actionConfig, methodResult));
    	assertEquals(methodResult, restActionInvocation.createResult());

		setUp();
    	try {
    		methodResult = new Object();
    		restActionInvocation.saveResult(actionConfig, methodResult);

    		// ko
    		assertFalse(true);
    		
    	} catch (ConfigurationException c) {
    		// ok, object not allowed
    	}
	}
	
	/**
	 * Test the target selection: exception, error messages, model and null
	 * @throws Exception
	 */
	public void testSelectTarget() throws Exception {
		
		// Exception
		Exception e = new Exception();
		restActionInvocation.getStack().set("exception", e);
		restActionInvocation.selectTarget();
		assertEquals(e, restActionInvocation.target);

		// Error messages
		setUp();
		String actionMessage = "Error!";
		RestActionSupport action = (RestActionSupport)restActionInvocation.getAction();
		action.addActionError(actionMessage);
		Map<String, Object> errors = new HashMap<String, Object>();
		List<String> list = new ArrayList<String>();
		list.add(actionMessage);
    	errors.put("actionErrors", list);
    	restActionInvocation.selectTarget();
		assertEquals(errors, restActionInvocation.target);
		
    	// Model with get and no content in post, put, delete
    	setUp();
		RestAction restAction = (RestAction)restActionInvocation.getAction();
		List<String> model = new ArrayList<String>();
		model.add("Item");
		restAction.model = model;
		request.setMethod("GET");
		restActionInvocation.selectTarget();
		assertEquals(model, restActionInvocation.target);
		request.setMethod("POST");
		restActionInvocation.selectTarget();
		assertEquals(null, restActionInvocation.target);
		request.setMethod("PUT");
		restActionInvocation.selectTarget();
		assertEquals(null, restActionInvocation.target);
		request.setMethod("DELETE");
		restActionInvocation.selectTarget();
		assertEquals(null, restActionInvocation.target);

        // disable content restriction to GET only
        model = new ArrayList<String>();
        model.add("Item1");
        restAction.model = model;

        request.setMethod("POST");
        restActionInvocation.setRestrictToGet("false");
        restActionInvocation.selectTarget();
        assertEquals(model, restActionInvocation.target);
        assertEquals(model.get(0), "Item1");
    }

	/**
	 * Test the not modified status code.
	 * @throws Exception
	 */
	public void testResultNotModified() throws Exception {

		request.addHeader("If-None-Match", "123");
		request.setMethod("GET");

		RestAction restAction = (RestAction)restActionInvocation.getAction();
		List<String> model = new ArrayList<String>() {
			@Override
			public int hashCode() {
				return 123;
			}
		};
		model.add("Item");
		restAction.model = model;
		
		restActionInvocation.processResult();
		assertEquals(SC_NOT_MODIFIED, response.getStatus());
        
    }
	
	/**
	 * Test the default error result.
	 * @throws Exception
	 */
	public void testDefaultErrorResult() throws Exception {
		
		// Exception
		Exception e = new Exception();
		restActionInvocation.getStack().set("exception", e);
		request.setMethod("GET");

		RestAction restAction = (RestAction)restActionInvocation.getAction();
		List<String> model = new ArrayList<String>();
		model.add("Item");
		restAction.model = model;
		
		restActionInvocation.setDefaultErrorResultName("default-error");
		ResultConfig resultConfig = new ResultConfig.Builder("default-error", 
			"org.apache.struts2.dispatcher.HttpHeaderResult")
	    	.addParam("status", "123").build();
	    ActionConfig actionConfig = new ActionConfig.Builder("org.apache.rest", 
				"RestAction", "org.apache.rest.RestAction")
	    	.addResultConfig(resultConfig)
	    	.build();
	    ((MockActionProxy)restActionInvocation.getProxy()).setConfig(actionConfig);
		
		restActionInvocation.processResult();
		assertEquals(123, response.getStatus());
		
	}
	
	public void testNoResult() throws Exception {
		
		RestAction restAction = (RestAction)restActionInvocation.getAction();
		List<String> model = new ArrayList<String>();
		model.add("Item");
		restAction.model = model;
		request.setMethod("GET");
		restActionInvocation.setResultCode("index");

		try {
			restActionInvocation.processResult();

    		// ko
    		assertFalse(true);
    		
    	} catch (ConfigurationException c) {
    		// ok, no result
    	}

	}
	
	/**
	 * Test the global execution
	 * @throws Exception
	 */
	public void testInvoke() throws Exception {
        
	    // Default index method return 'success'
	    ((MockActionProxy)restActionInvocation.getProxy()).setMethod("index");

	    // Define result 'success'
		ResultConfig resultConfig = new ResultConfig.Builder("success", 
			"org.apache.struts2.dispatcher.HttpHeaderResult")
	    	.addParam("status", "123").build();
	    ActionConfig actionConfig = new ActionConfig.Builder("org.apache.rest", 
				"RestAction", "org.apache.rest.RestAction")
	    	.addResultConfig(resultConfig)
	    	.build();
	    ((MockActionProxy)restActionInvocation.getProxy()).setConfig(actionConfig);

		request.setMethod("GET");
		
        restActionInvocation.setOgnlUtil(new OgnlUtil());
        restActionInvocation.invoke();

        assertEquals(123, response.getStatus());
    }


    class RestActionInvocationTester extends RestActionInvocation {
    	RestActionInvocationTester() {
            super(new HashMap<String, Object>(), true);
            List<InterceptorMapping> interceptorMappings = new ArrayList<InterceptorMapping>();
            MockInterceptor mockInterceptor = new MockInterceptor();
            mockInterceptor.setFoo("interceptor");
            mockInterceptor.setExpectedFoo("interceptor");
            interceptorMappings.add(new InterceptorMapping("interceptor", mockInterceptor));
            interceptors = interceptorMappings.iterator();
            MockActionProxy actionProxy = new MockActionProxy();
            ActionConfig actionConfig = new ActionConfig.Builder("org.apache.rest", 
    				"RestAction", "org.apache.rest.RestAction").build();
            actionProxy.setConfig(actionConfig);
            proxy = actionProxy;
            action = new RestAction();
            setMimeTypeHandlerSelector(new DefaultContentTypeHandlerManager());
            unknownHandlerManager = new DefaultUnknownHandlerManager();
			try {
				XWorkTestCaseHelper.setUp();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			invocationContext = ActionContext.getContext();
			container = ActionContext.getContext().getContainer();
			stack = ActionContext.getContext().getValueStack();
			objectFactory = container.getInstance(ObjectFactory.class);
			
        }
    	
    }

    class RestAction extends RestActionSupport implements ModelDriven<List<String>> {

    	List<String> model;
		
    	public List<String> getModel() {
			return model;
		}
    	
    }
}
