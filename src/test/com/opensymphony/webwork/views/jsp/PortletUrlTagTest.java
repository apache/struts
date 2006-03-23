/*
 * Copyright 2004 BEKK Consulting
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.opensymphony.webwork.views.jsp;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import junit.textui.TestRunner;


import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Constraint;

import com.mockobjects.servlet.MockJspWriter;
import com.opensymphony.webwork.components.URL;
import com.opensymphony.webwork.portlet.PortletActionConstants;
import com.opensymphony.webwork.portlet.util.PortletUrlHelper;
import com.opensymphony.webwork.views.jsp.ParamTag;
import com.opensymphony.webwork.dispatcher.DispatcherUtils;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * @author Nils-Helge Garli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PortletUrlTagTest extends MockObjectTestCase {

	URLTag tag = new URLTag();

	Mock mockHttpReq = null;

	Mock mockHttpRes = null;

	Mock mockPortletReq = null;

	Mock mockPortletRes = null;

	Mock mockPageCtx = null;

	Mock mockPortletUrl = null;
	
	MockJspWriter mockJspWriter = null;

	OgnlValueStack stack = null;

	public static void main(String[] args) {
		TestRunner.run(PortletUrlTagTest.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		
		mockPortletApiAvailable();
		
		stack = new OgnlValueStack();

		
		mockHttpReq = mock(HttpServletRequest.class);
		mockHttpRes = mock(HttpServletResponse.class);
		mockPortletReq = mock(RenderRequest.class);
		mockPortletRes = mock(RenderResponse.class);
		mockPageCtx = mock(PageContext.class);
		mockPortletUrl = mock(PortletURL.class);
		mockJspWriter = new MockJspWriter();

		mockPageCtx.stubs().method("getRequest").will(
				returnValue((HttpServletRequest) mockHttpReq.proxy()));
		mockPageCtx.stubs().method("getResponse").will(
				returnValue((HttpServletResponse) mockHttpRes.proxy()));
		mockPageCtx.stubs().method("getOut").will(returnValue(mockJspWriter));
		
		mockHttpReq.stubs().method("getScheme").will(returnValue("http"));
		mockHttpReq.stubs().method("getAttribute").with(
				eq("webwork.valueStack")).will(returnValue(stack));
		mockHttpReq.stubs().method("getAttribute").with(
				eq("javax.portlet.response")).will(
				returnValue((PortletResponse) mockPortletRes.proxy()));
		mockHttpReq.stubs().method("getAttribute").with(
				eq("javax.portlet.request")).will(
				returnValue((PortletRequest) mockPortletReq.proxy()));
		
		mockPortletReq.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));
		mockPortletReq.stubs().method("getWindowState").will(returnValue(WindowState.NORMAL));
		mockPortletReq.stubs().method("getContextPath").will(returnValue("/contextPath"));

		tag.setPageContext((PageContext) mockPageCtx.proxy());
		
		Map modeMap = new HashMap();
		modeMap.put(PortletMode.VIEW, "/view");
		modeMap.put(PortletMode.HELP, "/help");
		modeMap.put(PortletMode.EDIT, "/edit");
		Map sessionMap = new HashMap();
		Map contextMap = new HashMap();
		contextMap.put(ActionContext.SESSION, sessionMap);
		contextMap.put(PortletActionConstants.REQUEST, mockPortletReq.proxy());
		contextMap.put(PortletActionConstants.RESPONSE, mockPortletRes.proxy());
		contextMap.put(PortletActionConstants.PHASE, PortletActionConstants.RENDER_PHASE);
		contextMap.put(PortletActionConstants.MODE_NAMESPACE_MAP, modeMap);
		ActionContext ctx = new ActionContext(contextMap);
		ctx.setValueStack(stack);
		ActionContext.setContext(ctx);
    }

	/**
     * 
     */
    private void mockPortletApiAvailable() {
        try {
            Field field = DispatcherUtils.class.getDeclaredField("portletSupportActive");
            field.setAccessible(true);
            field.set(null, Boolean.TRUE);
        }
        catch(Exception e) {
            
        }
        
    }

    public void testEnsureParamsAreStringArrays() {
		Map params = new HashMap();
		params.put("param1", "Test1");
		params.put("param2", new String[] { "Test2" });

		Map result = PortletUrlHelper.ensureParamsAreStringArrays(params);
		assertEquals(2, result.size());
		assertTrue(result.get("param1") instanceof String[]);
	}

	public void testSetWindowState() throws Exception {

	    PortletMode mode = PortletMode.VIEW;
	    
		mockHttpReq.stubs().method("getQueryString").will(returnValue(""));

		mockPortletRes.expects(once()).method("createRenderURL").will(
				returnValue((PortletURL) mockPortletUrl.proxy()));
		
		Map paramMap = new HashMap();
		paramMap.put(PortletActionConstants.ACTION_PARAM, new String[]{"/view/testAction"});
		paramMap.put(PortletActionConstants.MODE_PARAM, new String[]{mode.toString()});
		
		mockPortletUrl.expects(once()).method("setParameters").with(new ParamMapConstraint(paramMap));
		mockPortletUrl.expects(once()).method("setWindowState").with(eq(WindowState.MAXIMIZED));
		mockPortletUrl.expects(once()).method("setPortletMode").with(eq(PortletMode.VIEW));
		
		tag.setAction("testAction");
		tag.setWindowState("maximized");
		tag.doStartTag();
		tag.doEndTag();

	}
	
	public void testSetPortletMode() throws Exception  {
	    
	    PortletMode mode = PortletMode.HELP;
	    WindowState state = WindowState.NORMAL;
	    
	    Mock mockSession = mock(PortletSession.class);
	    
		mockHttpReq.stubs().method("getQueryString").will(returnValue(""));

		mockPortletRes.expects(once()).method("createRenderURL").will(
				returnValue((PortletURL) mockPortletUrl.proxy()));
		
		Map paramMap = new HashMap();
		paramMap.put(PortletActionConstants.ACTION_PARAM, new String[]{"/help/testAction"});
		paramMap.put(PortletActionConstants.MODE_PARAM, new String[]{mode.toString()});
		
		mockPortletUrl.expects(once()).method("setParameters").with(new ParamMapConstraint(paramMap));
		mockPortletUrl.expects(once()).method("setPortletMode").with(eq(PortletMode.HELP));
		mockPortletUrl.expects(once()).method("setWindowState").with(eq(WindowState.NORMAL));
		
		tag.setAction("testAction");
		tag.setPortletMode("help");
		tag.doStartTag();
		tag.doEndTag();
	}
	
	public void testUrlWithQueryParams() throws Exception {
	    
	    PortletMode mode = PortletMode.VIEW;
	    
		mockHttpReq.stubs().method("getQueryString").will(returnValue(""));

		mockPortletRes.expects(once()).method("createRenderURL").will(
				returnValue((PortletURL) mockPortletUrl.proxy()));
		
		Map paramMap = new HashMap();
		paramMap.put(PortletActionConstants.ACTION_PARAM, new String[]{"/view/testAction"});
		paramMap.put("testParam1", new String[]{"testValue1"});
		paramMap.put(PortletActionConstants.MODE_PARAM, new String[]{mode.toString()});
		
		mockPortletUrl.expects(once()).method("setParameters").with(new ParamMapConstraint(paramMap));
		mockPortletUrl.expects(once()).method("setPortletMode").with(eq(PortletMode.VIEW));
		mockPortletUrl.expects(once()).method("setWindowState").with(eq(WindowState.NORMAL));
		
		tag.setAction("testAction?testParam1=testValue1");
		tag.doStartTag();
		tag.doEndTag();
	}
	
	public void testActionUrl() throws Exception {
	    
	    PortletMode mode = PortletMode.VIEW;
	    
		mockHttpReq.stubs().method("getQueryString").will(returnValue(""));

		mockPortletRes.expects(once()).method("createActionURL").will(
				returnValue((PortletURL) mockPortletUrl.proxy()));
		
		Map paramMap = new HashMap();
		paramMap.put(PortletActionConstants.ACTION_PARAM, new String[]{"/view/testAction"});
		paramMap.put(PortletActionConstants.MODE_PARAM, new String[]{mode.toString()});
		
		mockPortletUrl.expects(once()).method("setParameters").with(new ParamMapConstraint(paramMap));
		mockPortletUrl.expects(once()).method("setPortletMode").with(eq(PortletMode.VIEW));
		mockPortletUrl.expects(once()).method("setWindowState").with(eq(WindowState.NORMAL));
		
		tag.setAction("testAction");
		tag.setPortletUrlType("action");
		tag.doStartTag();
		tag.doEndTag();
	}
	
	public void testResourceUrl() throws Exception {
		mockHttpReq.stubs().method("getQueryString").will(returnValue(""));
		mockPortletRes.expects(once()).method("encodeURL").will(returnValue("/contextPath/image.gif"));
		mockJspWriter.setExpectedData("/contextPath/image.gif");
		tag.setValue("image.gif");
		tag.doStartTag();
		tag.doEndTag();
		mockJspWriter.verify();
	}
	
	public void testResourceUrlWithNestedParam() throws Exception {
		mockHttpReq.stubs().method("getQueryString").will(returnValue(""));
		mockPortletRes.expects(once()).method("encodeURL").with(eq("/contextPath/image.gif?testParam1=testValue1")).will(returnValue("/contextPath/image.gif?testParam1=testValue1"));
		mockJspWriter.setExpectedData("/contextPath/image.gif?testParam1=testValue1");
		
		ParamTag paramTag = new ParamTag();
		paramTag.setPageContext((PageContext)mockPageCtx.proxy());
		paramTag.setParent(tag);
		paramTag.setName("testParam1");
		paramTag.setValue("'testValue1'");
		tag.setValue("image.gif");
		tag.doStartTag();
		paramTag.doStartTag();
		paramTag.doEndTag();
		tag.doEndTag();
		mockJspWriter.verify();
	}
	
	public void testResourceUrlWithTwoNestedParam() throws Exception {
		mockHttpReq.stubs().method("getQueryString").will(returnValue(""));
		mockPortletRes.expects(once()).method("encodeURL").with(eq("/contextPath/image.gif?testParam1=testValue1&testParam2=testValue2")).will(returnValue("/contextPath/image.gif?testParam1=testValue1&testParam2=testValue2"));
		mockJspWriter.setExpectedData("/contextPath/image.gif?testParam1=testValue1&testParam2=testValue2");
		
		ParamTag paramTag = new ParamTag();
		paramTag.setPageContext((PageContext)mockPageCtx.proxy());
		paramTag.setParent(tag);
		paramTag.setName("testParam1");
		paramTag.setValue("'testValue1'");
		ParamTag paramTag2 = new ParamTag();
		paramTag2.setPageContext((PageContext)mockPageCtx.proxy());
		paramTag2.setParent(tag);
		paramTag2.setName("testParam2");
		paramTag2.setValue("'testValue2'");
		tag.setValue("image.gif");
		tag.doStartTag();
		paramTag.doStartTag();
		paramTag.doEndTag();
		paramTag2.doStartTag();
		paramTag2.doEndTag();
		tag.doEndTag();
		mockJspWriter.verify();
	}

	private static class ParamMapConstraint implements Constraint {

		private Map myExpectedMap = null;
		private Map myActualMap = null;
		
		public ParamMapConstraint(Map expectedMap) {
			if(expectedMap == null) {
				throw new IllegalArgumentException("Use an isNull constraint instead!");
			}
			myExpectedMap = expectedMap;
		}

		/* (non-Javadoc)
		 * @see org.jmock.core.Constraint#eval(java.lang.Object)
		 */
		public boolean eval(Object val) {
			myActualMap = (Map)val;
			boolean result = false;
			if(val != null) {
				if(myExpectedMap.size() == myActualMap.size()) {
					Iterator keys = myExpectedMap.keySet().iterator();
					boolean allSame = true;
					while(keys.hasNext()) {
						Object key = keys.next();
						if(!myActualMap.containsKey(key)) {
							allSame = false;
							break;
						}
						else {
							String[] expected = (String[])myExpectedMap.get(key);
							String[] actual = (String[])myActualMap.get(key);
							if(!Arrays.equals(expected, actual)) {
								allSame = false;
								break;
							}
						}
					}
					result = allSame;
				}
			}
			return result;
		}

		/* (non-Javadoc)
		 * @see org.jmock.core.SelfDescribing#describeTo(java.lang.StringBuffer)
		 */
		public StringBuffer describeTo(StringBuffer sb) {
			return sb.append(myExpectedMap);
		}
		
		

	}

}
