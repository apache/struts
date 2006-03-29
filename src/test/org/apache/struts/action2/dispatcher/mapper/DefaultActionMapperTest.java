/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.dispatcher.mapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts.action2.dispatcher.ServletRedirectResult;
import org.apache.struts.action2.views.jsp.StrutsMockHttpServletRequest;
import com.opensymphony.xwork.Result;

import junit.framework.TestCase;

/**
 * DefaultActionMapper test case.
 * 
 * @author tm_jee
 * @version $Date: 2006/03/17 09:46:17 $ $Id: DefaultActionMapperTest.java,v 1.3 2006/03/17 09:46:17 rainerh Exp $
 */
public class DefaultActionMapperTest extends TestCase {

	// =============================
	// === test name & namespace ===
	// =============================
	
	public void testParseNameAndNamespace1() throws Exception {
		ActionMapping actionMapping = new ActionMapping();
		
		DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
		defaultActionMapper.parseNameAndNamespace("someAction.action", actionMapping);
		
		assertEquals(actionMapping.getName(), "someAction");
		assertEquals(actionMapping.getNamespace(), "");
	}
	
	public void testParseNameAndNamespace2() throws Exception {
		ActionMapping actionMapping = new ActionMapping();
		
		DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
		defaultActionMapper.parseNameAndNamespace("/someAction.action", actionMapping);
		
		assertEquals(actionMapping.getName(), "someAction");
		assertEquals(actionMapping.getNamespace(), "/");
	}
	
	public void testParseNameAndNamespace3() throws Exception {
		ActionMapping actionMapping = new ActionMapping();
		
		DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
		defaultActionMapper.parseNameAndNamespace("/one/two/three/someAction.action", actionMapping);

		assertEquals(actionMapping.getName(), "someAction");
		assertEquals(actionMapping.getNamespace(), "/one/two/three");
	}
	
	
	// ===========================
	// === test special prefix ===
	// ===========================
	public void testMethodPrefix() throws Exception {
		Map parameterMap = new HashMap();
		parameterMap.put(DefaultActionMapper.METHOD_PREFIX+"myMethod", "");
		
		StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
		request.setParameterMap(parameterMap);
		request.setupGetServletPath("/someServletPath.action");
		
		DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
		ActionMapping actionMapping = defaultActionMapper.getMapping(request);
		
		assertEquals(actionMapping.getMethod(), "myMethod");
	}
	
	public void testActionPrefix() throws Exception {
		Map parameterMap = new HashMap();
		parameterMap.put(DefaultActionMapper.ACTION_PREFIX+"myAction", "");
		
		StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
		request.setParameterMap(parameterMap);
		request.setupGetServletPath("/someServletPath.action");
		
		DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
		ActionMapping actionMapping = defaultActionMapper.getMapping(request);
		
		assertEquals(actionMapping.getName(), "myAction");
	}
	
	public void testRedirectPrefix() throws Exception {
		Map parameterMap = new HashMap();
		parameterMap.put(DefaultActionMapper.REDIRECT_PREFIX+"www.google.com", "");
		
		StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
		request.setupGetServletPath("/someServletPath.action");
		request.setParameterMap(parameterMap);
		
		DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
		ActionMapping actionMapping = defaultActionMapper.getMapping(request);
		
		Result result = actionMapping.getResult();
		assertNotNull(result);
		assertTrue(result instanceof ServletRedirectResult);
		
		//TODO: need to test location but there's noaccess to the property/method, unless we use reflection
	}
	
	public void testRedirectActionPrefix() throws Exception {
		Map parameterMap = new HashMap();
		parameterMap.put(DefaultActionMapper.REDIRECT_ACTION_PREFIX+"myAction", "");
		
		StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
		request.setupGetServletPath("/someServletPath.action");
		request.setParameterMap(parameterMap);
		
		DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
		ActionMapping actionMapping = defaultActionMapper.getMapping(request);
		
		Result result = actionMapping.getResult();
		assertNotNull(result);
		assertTrue(result instanceof ServletRedirectResult);
		
		// TODO: need to test location but there's noaccess to the property/method, unless we use reflection
	}
	
	
	
	// ==========================
	// === test action!method ===
	// ==========================
	public void testActionBangMethod() throws Exception {
		
		StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
		request.setupGetServletPath("/someName!someMethod.action");
		
		DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
		ActionMapping actionMapping = defaultActionMapper.getMapping(request);
		
		assertEquals(actionMapping.getMethod(), "someMethod");
		assertEquals(actionMapping.getName(), "someName");
	}
	
	
	public void testGetUriFromActionMapper1() throws Exception {
		DefaultActionMapper mapper = new DefaultActionMapper();
		ActionMapping actionMapping = new ActionMapping();
		actionMapping.setMethod("myMethod");
		actionMapping.setName("myActionName");
		actionMapping.setNamespace("/myNamespace");
		String uri = mapper.getUriFromActionMapping(actionMapping);
		
		assertEquals("/myNamespace/myActionName!myMethod.action", uri);
	}
	
	public void testGetUriFromActionMapper2() throws Exception {
		DefaultActionMapper mapper = new DefaultActionMapper();
		ActionMapping actionMapping = new ActionMapping();
		actionMapping.setMethod("myMethod");
		actionMapping.setName("myActionName");
		actionMapping.setNamespace("/");
		String uri = mapper.getUriFromActionMapping(actionMapping);
		
		assertEquals("/myActionName!myMethod.action", uri);
	}
	
	public void testGetUriFromActionMapper3() throws Exception {
		DefaultActionMapper mapper = new DefaultActionMapper();
		ActionMapping actionMapping = new ActionMapping();
		actionMapping.setMethod("myMethod");
		actionMapping.setName("myActionName");
		actionMapping.setNamespace("");
		String uri = mapper.getUriFromActionMapping(actionMapping);
		
		assertEquals("/myActionName!myMethod.action", uri);
	}
	
	
	public void testGetUriFromActionMapper4() throws Exception {
		DefaultActionMapper mapper = new DefaultActionMapper();
		ActionMapping actionMapping = new ActionMapping();
		actionMapping.setName("myActionName");
		actionMapping.setNamespace("");
		String uri = mapper.getUriFromActionMapping(actionMapping);
		
		assertEquals("/myActionName.action", uri);
	}
	
	public void testGetUriFromActionMapper5() throws Exception {
		DefaultActionMapper mapper = new DefaultActionMapper();
		ActionMapping actionMapping = new ActionMapping();
		actionMapping.setName("myActionName");
		actionMapping.setNamespace("/");
		String uri = mapper.getUriFromActionMapping(actionMapping);
		
		assertEquals("/myActionName.action", uri);
	}

    //
    public void testGetUriFromActionMapper6() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper7() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper8() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action?test=bla", uri);
    }


    public void testGetUriFromActionMapper9() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper10() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

}
