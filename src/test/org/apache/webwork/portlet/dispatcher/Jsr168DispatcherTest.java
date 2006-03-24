/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.portlet.dispatcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import junit.textui.TestRunner;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Constraint;

import org.apache.struts.action2.portlet.PortletActionConstants;
import org.apache.struts.action2.portlet.context.ServletContextHolderListener;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * Jsr168DispatcherTest. Insert description.
 * 
 * @author Nils-Helge Garli
 * @version $Revision: 1.5 $ $Date: 2006/03/13 07:24:07 $
 */
public class Jsr168DispatcherTest extends MockObjectTestCase implements PortletActionConstants {
 
    Jsr168Dispatcher dispatcher = null;
    Mock mockConfig = null;
    Mock mockCtx = null;
    Mock mockRequest = null;
    Mock mockSession = null;
    Mock mockActionFactory = null;
    Mock mockActionProxy = null;
    Mock mockAction = null;
    Mock mockInvocation = null;
    
    public void setUp() {
        dispatcher = new Jsr168Dispatcher();
    }
    
    private void initPortletConfig(final Map initParams, final Map attributes) {
    	mockConfig = mock(PortletConfig.class);
    	mockCtx = mock(PortletContext.class);
    	mockConfig.stubs().method(ANYTHING);
    	setupStub(initParams, mockConfig, "getInitParameter");
    	mockCtx.stubs().method("getAttributeNames").will(returnValue(Collections.enumeration(attributes.keySet())));
    	setupStub(attributes, mockCtx, "getAttribute");
    	mockConfig.stubs().method("getPortletContext").will(returnValue(mockCtx.proxy()));
    	mockCtx.stubs().method("getInitParameterNames").will(returnValue(Collections.enumeration(initParams.keySet())));
    	setupStub(initParams, mockCtx, "getInitParameter");
    	
    	mockConfig.stubs().method("getResourceBundle").will(returnValue(new ListResourceBundle() {
            protected Object[][] getContents() {
                return new String[][]{{"javax.portlet.title", "MyTitle"}};
            }
        }));
    }

    private void setupActionFactory(String namespace, String actionName, String result, OgnlValueStack stack) {
    	if(mockActionFactory == null) {
    		mockActionFactory = mock(ActionProxyFactory.class);
    	}
    	mockAction = mock(Action.class);
    	mockActionProxy = mock(ActionProxy.class);
    	mockInvocation = mock(ActionInvocation.class);
    	
    	mockActionFactory.expects(once()).method("createActionProxy").with(new Constraint[]{eq(namespace), eq(actionName), isA(Map.class)}).will(returnValue(mockActionProxy.proxy()));
    	mockActionProxy.stubs().method("getAction").will(returnValue(mockAction.proxy()));
    	mockActionProxy.expects(once()).method("execute").will(returnValue(result));
    	mockActionProxy.expects(once()).method("getInvocation").will(returnValue(mockInvocation.proxy()));
    	mockInvocation.stubs().method("getStack").will(returnValue(stack));
    	
    }

    public void testRender_ok() {
        final Mock mockResponse = mock(RenderResponse.class);
        mockResponse.stubs().method(ANYTHING);
        final Mock servletContext = mock(ServletContext.class);
        servletContext.stubs().method(ANYTHING);
        ServletContextEvent event = new ServletContextEvent((ServletContext)servletContext.proxy());
        new ServletContextHolderListener().contextInitialized(event);
        String actionName = "testAction";
        PortletMode mode = PortletMode.VIEW;

        Map requestParams = new HashMap();
        requestParams.put(PortletActionConstants.ACTION_PARAM, new String[]{"/view/testAction"});
        requestParams.put(EVENT_ACTION, new String[]{"true"});
        requestParams.put(PortletActionConstants.MODE_PARAM, new String[]{mode.toString()});
        
        Map sessionMap = new HashMap();
        
        
        
        Map initParams = new HashMap();
        initParams.put("viewNamespace", "/view");

        initPortletConfig(initParams, new HashMap());
        initRequest(requestParams, new HashMap(), sessionMap, new HashMap(), PortletMode.VIEW, WindowState.NORMAL, false, null);
        setupActionFactory("/view", "testAction", "success", new OgnlValueStack());

        mockInvocation.expects(once()).method("getStack").will(
                returnValue(null));
        //mockSession.expects(once()).method("setAttribute").with(new Constraint[]{eq(PortletActionConstants.LAST_MODE), eq(PortletMode.VIEW)});
        try {
            dispatcher
                    .setActionProxyFactory((ActionProxyFactory) mockActionFactory
                            .proxy());
            dispatcher.init((PortletConfig) mockConfig.proxy());
            dispatcher.render((RenderRequest) mockRequest.proxy(),
                    (RenderResponse) mockResponse.proxy());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error occured");
        }
    }

    public void testProcessAction_ok() {
        final Mock mockResponse = mock(ActionResponse.class);

        String actionName = "testAction";
        PortletMode mode = PortletMode.VIEW;
        Map initParams = new HashMap();
        initParams.put("viewNamespace", "/view");
        
        Map requestParams = new HashMap();
        requestParams.put(PortletActionConstants.ACTION_PARAM, new String[]{"/view/testAction"});
        requestParams.put(PortletActionConstants.MODE_PARAM, new String[]{mode.toString()});
        
        initPortletConfig(initParams, new HashMap());
        initRequest(requestParams, new HashMap(), new HashMap(), new HashMap(), PortletMode.VIEW, WindowState.NORMAL, true, null);
        setupActionFactory("/view", "testAction", "success", new OgnlValueStack());
        Constraint[] paramConstraints = new Constraint[] {
                eq(PortletActionConstants.EVENT_ACTION), same(mockActionProxy.proxy()) };

        mockSession.expects(once()).method("setAttribute").with(
                paramConstraints);

        mockResponse.expects(once()).method("setRenderParameter").with(
                new Constraint[] { eq(PortletActionConstants.EVENT_ACTION),
                        eq("true") });

        //mockSession.expects(once()).method("setAttribute").with(new Constraint[]{eq(PortletActionConstants.LAST_MODE), eq(PortletMode.VIEW)});
        try {
            dispatcher
                    .setActionProxyFactory((ActionProxyFactory) mockActionFactory
                            .proxy());
            dispatcher.init((PortletConfig) mockConfig.proxy());
            dispatcher.processAction((ActionRequest) mockRequest.proxy(),
                    (ActionResponse) mockResponse.proxy());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error occured");
        }
    }
    
    /**
     * Initialize the mock request (and as a result, the mock session)
     * @param requestParams The request parameters
     * @param requestAttributes The request attributes
     * @param sessionParams The session attributes
     * @param renderParams The render parameters. Will only be set if <code>isEvent</code> is <code>true</code>
     * @param mode The portlet mode
     * @param state The portlet window state
     * @param isEvent <code>true</code> when the request is an ActionRequest.
     * @param locale The locale. If <code>null</code>, the request will return <code>Locale.getDefault()</code>
     */
    private void initRequest(Map requestParams, Map requestAttributes, Map sessionParams, Map renderParams, PortletMode mode, WindowState state, boolean isEvent, Locale locale) {
    	mockRequest = isEvent ? mock(ActionRequest.class) : mock(RenderRequest.class);
    	mockSession = mock(PortletSession.class);
    	mockSession.stubs().method(ANYTHING);
    	mockRequest.stubs().method(ANYTHING);
    	setupStub(sessionParams, mockSession, "getAttribute");
    	mockSession.stubs().method("getAttributeNames").will(returnValue(Collections.enumeration(sessionParams.keySet())));
    	setupParamStub(requestParams, mockRequest, "getParameter");
    	setupStub(requestAttributes, mockRequest, "getAttribute");
    	mockRequest.stubs().method("getAttributeNames").will(returnValue(Collections.enumeration(requestAttributes.keySet())));
    	mockRequest.stubs().method("getParameterMap").will(returnValue(requestParams));
    	mockRequest.stubs().method("getParameterNames").will(returnValue(Collections.enumeration(requestParams.keySet())));
    	mockRequest.stubs().method("getPortletSession").will(returnValue(mockSession.proxy()));
    	if(locale != null) {
    		mockRequest.stubs().method("getLocale").will(returnValue(locale));
    	}
    	else {
    		mockRequest.stubs().method("getLocale").will(returnValue(Locale.getDefault()));
    	}
    	mockRequest.stubs().method("getPortletMode").will(returnValue(mode));
    	mockRequest.stubs().method("getWindowState").will(returnValue(state));
    }
    
    /**
     * @param requestParams
     * @param mockRequest2
     * @param string
     */
    private void setupParamStub(Map requestParams, Mock mockRequest, String method) {
        Map newMap = new HashMap();
        Iterator it = requestParams.keySet().iterator();
        while(it.hasNext()) {
            Object key = it.next();
            String[] val = (String[])requestParams.get(key);
            newMap.put(key, val[0]);
        }
        setupStub(newMap, mockRequest, method);
        
    }

    /**
     * Set up stubs for the mock.
     * @param map The map containing the <code>key</code> and <code>values</code>. The key is the 
     * expected parameter to <code>method</code>, and value is the value that should be returned from
     * the stub.
     * @param mock The mock to initialize.
     * @param method The name of the method to stub.
     */
    private void setupStub(Map map, Mock mock, String method) {
    	Iterator it = map.keySet().iterator();
    	while(it.hasNext()) {
    		Object key = it.next();
    		Object val = map.get(key);
    		mock.stubs().method(method).with(eq(key)).will(returnValue(val));
    	}
    }
    
    public void testModeChangeUsingPortletWidgets() {
        final Mock mockResponse = mock(RenderResponse.class);
        mockResponse.stubs().method(ANYTHING);
        String actionName = "testAction";
        PortletMode mode = PortletMode.EDIT;

        Map requestParams = new HashMap();
        requestParams.put(PortletActionConstants.ACTION_PARAM, new String[]{"/view/testAction"});
        requestParams.put(EVENT_ACTION, new String[]{"false"});
        requestParams.put(PortletActionConstants.MODE_PARAM, new String[]{PortletMode.VIEW.toString()});
        
        Map sessionMap = new HashMap();
        
        Map initParams = new HashMap();
        initParams.put("viewNamespace", "/view");
        initParams.put("editNamespace", "/edit");

        initPortletConfig(initParams, new HashMap());
        initRequest(requestParams, new HashMap(), sessionMap, new HashMap(), mode, WindowState.NORMAL, false, null);
        setupActionFactory("/edit", "default", "success", new OgnlValueStack());

        mockInvocation.expects(once()).method("getStack").will(
                returnValue(null));
        //mockSession.expects(once()).method("setAttribute").with(new Constraint[]{eq(PortletActionConstants.LAST_MODE), eq(PortletMode.VIEW)});
        try {
            dispatcher
                    .setActionProxyFactory((ActionProxyFactory) mockActionFactory
                            .proxy());
            dispatcher.init((PortletConfig) mockConfig.proxy());
            dispatcher.render((RenderRequest) mockRequest.proxy(),
                    (RenderResponse) mockResponse.proxy());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error occured");
        }
    }
    
    public static void main(String[] args) {
    	TestRunner.run(Jsr168DispatcherTest.class);
    }

}
