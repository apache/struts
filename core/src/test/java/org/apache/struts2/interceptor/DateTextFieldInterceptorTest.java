package org.apache.struts2.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsInternalTestCase;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;

/**
 * Unit test for DateTextFieldInterceptor. 
 */
public class DateTextFieldInterceptorTest extends StrutsInternalTestCase {

    private DateTextFieldInterceptor interceptor;
    private MockActionInvocation ai;
    private Map<String, Object> param;
    
    protected void setUp() throws Exception {
    	super.setUp();
    	param = new HashMap<String, Object>();
    	
    	interceptor = new DateTextFieldInterceptor();
    	ai = new MockActionInvocation();
    	ai.setInvocationContext(ActionContext.getContext());
    	ActionContext.getContext().setParameters(param);
    }
	
	public void testNoParam() throws Exception {
		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		assertEquals(0, param.size());
	}

	public void testOneDateTextField() throws Exception {
		param.put("__year_name", new String[]{"2000"});
		param.put("__month_name", new String[]{"06"});
		param.put("__day_name", new String[]{"15"});

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();
		
		assertFalse(param.containsKey("__year_name"));
		assertFalse(param.containsKey("__month_name"));
		assertFalse(param.containsKey("__day_name"));
		assertTrue(param.containsKey("name"));
		assertEquals(1, param.size());
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-06-15"); 
		assertEquals(date, param.get("name"));
	}

}
