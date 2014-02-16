/*
 * $Id: PortletUrlTagTest.java 609901 2008-01-08 08:18:23Z nilsga $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.views.jsp;

import com.mockobjects.servlet.MockJspWriter;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import junit.textui.TestRunner;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.portlet.PortletConstants;
import org.apache.struts2.portlet.PortletPhase;
import org.apache.struts2.portlet.util.PortletUrlHelper;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.springframework.mock.web.MockServletContext;

import javax.portlet.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.*;

import static org.apache.struts2.StrutsStatics.STRUTS_PORTLET_CONTEXT;

/**
 */
@SuppressWarnings("unchecked")
public class PortletUrlTagTest extends MockObjectTestCase {

    URLTag tag = new URLTag();

    Mock mockHttpReq = null;

    Mock mockHttpRes = null;

    Mock mockPortletReq = null;

    Mock mockPortletRes = null;

    Mock mockPageCtx = null;

    Mock mockPortletUrl = null;

    MockJspWriter mockJspWriter = null;

    Mock mockCtx = null;

    ValueStack stack = null;
    
    Mock mockActionProxy = null;

	Mock mockActionInvocation = null;

    private Dispatcher dispatcher;

    public static void main(String[] args) {
        TestRunner.run(PortletUrlTagTest.class);
    }


    public void setUp() throws Exception {
        super.setUp();

        ServletContext servletContext = new MockServletContext();
        dispatcher = new Dispatcher(servletContext, new HashMap());
        dispatcher.init();
        Dispatcher.setInstance(dispatcher);

        stack = dispatcher.getContainer().getInstance(ValueStackFactory.class).createValueStack();
        stack.getContext().put(ActionContext.CONTAINER, dispatcher.getContainer());
        ActionContext context = new ActionContext(stack.getContext());
        ActionContext.setContext(context);

    	mockActionInvocation  = mock(ActionInvocation.class);
        mockActionProxy = mock(ActionProxy.class);
        mockHttpReq = mock(HttpServletRequest.class);
        mockHttpRes = mock(HttpServletResponse.class);
        mockPortletReq = mock(RenderRequest.class);
        mockPortletRes = mock(RenderResponse.class);
        mockPageCtx = mock(PageContext.class);
        mockPortletUrl = mock(PortletURL.class);
        mockJspWriter = new MockJspWriter();
        mockCtx = mock(PortletContext.class);

        mockActionProxy.stubs().method("getNamespace").will(returnValue("/view"));
        mockActionInvocation.stubs().method("getProxy").will(returnValue(
                mockActionProxy.proxy()));
        mockPageCtx.stubs().method("getRequest").will(
                returnValue(mockHttpReq.proxy()));
        mockPageCtx.stubs().method("getResponse").will(
                returnValue(mockHttpRes.proxy()));
        mockPageCtx.stubs().method("getOut").will(returnValue(mockJspWriter));

        mockHttpReq.stubs().method("getScheme").will(returnValue("http"));
        mockHttpReq.stubs().method("getAttribute").with(
                eq("struts.valueStack")).will(returnValue(stack));
        mockHttpReq.stubs().method("getAttribute").with(
                eq("javax.portlet.response")).will(
                returnValue(mockPortletRes.proxy()));
        mockHttpReq.stubs().method("getAttribute").with(
                eq("javax.portlet.request")).will(
                returnValue(mockPortletReq.proxy()));
        mockHttpReq.stubs().method("getAttribute").with(
                eq("javax.servlet.include.servlet_path")).will(
                returnValue("/servletPath"));
        mockHttpReq.stubs().method("getParameterMap").will(
                returnValue(Collections.emptyMap()));

        mockPortletReq.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));
        mockPortletReq.stubs().method("getWindowState").will(returnValue(WindowState.NORMAL));
        mockPortletReq.stubs().method("getContextPath").will(returnValue("/contextPath"));

        tag.setPageContext((PageContext) mockPageCtx.proxy());

        Map modeMap = new HashMap();
        modeMap.put(PortletMode.VIEW, "/view");
        modeMap.put(PortletMode.HELP, "/help");
        modeMap.put(PortletMode.EDIT, "/edit");
        Map<PortletMode,ActionMapping> actionMap = new HashMap<PortletMode,ActionMapping>();
        actionMap.put(PortletMode.VIEW, new ActionMapping("defaultView", "/view", "execute", new HashMap<String,Object>()));
        actionMap.put(PortletMode.HELP, new ActionMapping("defaultHelp", "/help", "execute", new HashMap<String,Object>()));
        actionMap.put(PortletMode.EDIT, new ActionMapping("defaultEdit", "/edit", "execute", new HashMap<String,Object>()));
        Map sessionMap = new HashMap();
        Map contextMap = new HashMap();
        contextMap.put(ActionContext.SESSION, sessionMap);
        contextMap.put(PortletConstants.REQUEST, mockPortletReq.proxy());
        contextMap.put(PortletConstants.RESPONSE, mockPortletRes.proxy());
        contextMap.put(PortletConstants.PHASE, PortletPhase.RENDER_PHASE);
        contextMap.put(PortletConstants.MODE_NAMESPACE_MAP, modeMap);
        contextMap.put(PortletConstants.DEFAULT_ACTION_MAP, actionMap);
        contextMap.put(STRUTS_PORTLET_CONTEXT, mockCtx.proxy());
        
        ActionContext ctx = new ActionContext(contextMap);
        ctx.setValueStack(stack);
        ActionInvocation ai = (ActionInvocation)mockActionInvocation.proxy();
    	stack.getContext().put(ActionContext.ACTION_INVOCATION, ai);
        ActionContext.setContext(ctx);
        
    }

    public void tearDown() throws Exception {
        super.tearDown();
        if (dispatcher != null && dispatcher.getConfigurationManager() != null) {
            dispatcher.cleanup();
            dispatcher = null;
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
                returnValue(mockPortletUrl.proxy()));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));

        Map paramMap = new HashMap();
        paramMap.put(PortletConstants.ACTION_PARAM, new String[]{"/view/testAction"});
        paramMap.put(PortletConstants.MODE_PARAM, new String[]{mode.toString()});

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

        mockHttpReq.stubs().method("getQueryString").will(returnValue(""));

        mockPortletRes.expects(once()).method("createRenderURL").will(
                returnValue(mockPortletUrl.proxy()));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));

        Map paramMap = new HashMap();
        paramMap.put(PortletConstants.ACTION_PARAM, new String[]{"/help/testAction"});
        paramMap.put(PortletConstants.MODE_PARAM, new String[]{mode.toString()});

        mockPortletUrl.expects(once()).method("setParameters").with(new ParamMapConstraint(paramMap));
        mockPortletUrl.expects(once()).method("setPortletMode").with(eq(PortletMode.HELP));
        mockPortletUrl.expects(once()).method("setWindowState").with(eq(WindowState.NORMAL));

        tag.setNamespace("/help");
        tag.setAction("testAction");
        tag.setPortletMode("help");
        tag.doStartTag();
        tag.doEndTag();
    }
    
    public void testWhenPortletModeDiffersFromCurrentAndNoParametersAreSetRenderTheDefaults()
    		 throws Exception {
        PortletMode mode = PortletMode.HELP;

        mockHttpReq.stubs().method("getQueryString").will(returnValue(""));

        mockPortletRes.expects(once()).method("createRenderURL").will(
                returnValue(mockPortletUrl.proxy()));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));

        Map paramMap = new HashMap();
        paramMap.put(PortletConstants.ACTION_PARAM, new String[]{"/help/defaultHelp"});
        paramMap.put(PortletConstants.MODE_PARAM, new String[]{mode.toString()});

        mockPortletUrl.expects(once()).method("setParameters").with(new ParamMapConstraint(paramMap));
        mockPortletUrl.expects(once()).method("setPortletMode").with(eq(PortletMode.HELP));
        mockPortletUrl.expects(once()).method("setWindowState").with(eq(WindowState.NORMAL));

        tag.setPortletMode("help");
        tag.doStartTag();
        tag.doEndTag();
    }

    public void testUrlWithQueryParams() throws Exception {

        PortletMode mode = PortletMode.VIEW;

        mockHttpReq.stubs().method("getQueryString").will(returnValue(""));

        mockPortletRes.expects(once()).method("createRenderURL").will(
                returnValue(mockPortletUrl.proxy()));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));

        Map paramMap = new HashMap();
        paramMap.put(PortletConstants.ACTION_PARAM, new String[]{"/view/testAction"});
        paramMap.put("testParam1", new String[]{"testValue1"});
        paramMap.put(PortletConstants.MODE_PARAM, new String[]{mode.toString()});

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
                returnValue(mockPortletUrl.proxy()));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));

        Map paramMap = new HashMap();
        paramMap.put(PortletConstants.ACTION_PARAM, new String[]{"/view/testAction"});
        paramMap.put(PortletConstants.MODE_PARAM, new String[]{mode.toString()});

        mockPortletUrl.expects(once()).method("setParameters").with(new ParamMapConstraint(paramMap));
        mockPortletUrl.expects(once()).method("setPortletMode").with(eq(PortletMode.VIEW));
        mockPortletUrl.expects(once()).method("setWindowState").with(eq(WindowState.NORMAL));

        tag.setNamespace("/view");
        tag.setAction("testAction");
        tag.setPortletUrlType("action");
        tag.doStartTag();
        tag.doEndTag();
    }

    public void testResourceUrl() throws Exception {
        mockHttpReq.stubs().method("getQueryString").will(returnValue(""));
        mockPortletRes.expects(once()).method("encodeURL").will(returnValue("/contextPath/image.gif"));
        mockJspWriter.setExpectedData("/contextPath/image.gif");
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));
        tag.setValue("image.gif");
        tag.doStartTag();
        tag.doEndTag();
        mockJspWriter.verify();
    }

    public void testResourceUrlWithNestedParam() throws Exception {
        mockHttpReq.stubs().method("getQueryString").will(returnValue(""));
        mockPortletRes.expects(once()).method("encodeURL").with(eq("/contextPath/image.gif?testParam1=testValue1")).will(returnValue("/contextPath/image.gif?testParam1=testValue1"));
        mockJspWriter.setExpectedData("/contextPath/image.gif?testParam1=testValue1");
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));

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
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));

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

    public void testUrlWithMethod() throws Exception {
    	PortletMode mode = PortletMode.VIEW;
    	mockHttpReq.stubs().method("getQueryString").will(returnValue(""));
        mockPortletRes.expects(once()).method("createRenderURL").will(
                returnValue(mockPortletUrl.proxy()));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));
    	tag.setAction("testAction");
    	Map paramMap = new HashMap();
        paramMap.put(PortletConstants.ACTION_PARAM, new String[]{"/view/testAction!input"});
        paramMap.put(PortletConstants.MODE_PARAM, new String[]{mode.toString()});
        mockPortletUrl.expects(once()).method("setParameters").with(new ParamMapConstraint(paramMap));
        mockPortletUrl.expects(once()).method("setPortletMode").with(eq(PortletMode.VIEW));
        mockPortletUrl.expects(once()).method("setWindowState").with(eq(WindowState.NORMAL));
    	tag.setMethod("input");
    	tag.doStartTag();
    	tag.doEndTag();
    }
    
    public void testUrlWithNoActionOrMethod() throws Exception {
    	PortletMode mode = PortletMode.VIEW;
    	mockHttpReq.stubs().method("getQueryString").will(returnValue(""));
        mockPortletRes.expects(once()).method("createRenderURL").will(
                returnValue(mockPortletUrl.proxy()));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));
    	Map paramMap = new HashMap();
    	
    	
    	mockActionProxy.stubs().method("getActionName").will(returnValue("currentExecutingAction"));
    	
    	
    	paramMap.put(PortletConstants.ACTION_PARAM, new String[]{"/view/currentExecutingAction"});
        paramMap.put(PortletConstants.MODE_PARAM, new String[]{mode.toString()});
        mockPortletUrl.expects(once()).method("setParameters").with(new ParamMapConstraint(paramMap));
        mockPortletUrl.expects(once()).method("setPortletMode").with(eq(PortletMode.VIEW));
        mockPortletUrl.expects(once()).method("setWindowState").with(eq(WindowState.NORMAL));
    	tag.doStartTag();
    	tag.doEndTag();    	
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
        	sb.append("\n Expected: ");
        	describeTo(myExpectedMap, sb);
        	sb.append("\n Actual: ");
        	describeTo(myActualMap, sb);
        	
            return sb;
        }
        
        private StringBuffer describeTo(Map map,StringBuffer sb) {
        	Iterator<String> it = map.keySet().iterator();
        	while(it.hasNext()) {
        		String key = it.next();
        		sb.append(key).append("=");
        		String[] value = (String[])map.get(key);
        		sb.append(value[0]);
        		if(it.hasNext()) {
        			sb.append(", ");
        		}
        	}
            return sb;
        }



    }

}
