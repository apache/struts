/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 2/10/2003
 *
 */
package com.opensymphony.webwork.dispatcher;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletOutputStream;
import com.opensymphony.webwork.WebWorkTestCase;
import com.opensymphony.webwork.views.jsp.WebWorkMockHttpServletRequest;
import com.opensymphony.webwork.views.jsp.WebWorkMockHttpServletResponse;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author CameronBraid
 */
public abstract class AbstractServletDispatcherTestCase extends WebWorkTestCase {

    public String getConfigFilename() {
        return "xwork.xml";
    }

    public abstract ServletDispatcher getServletDispatcher();

    public abstract String getServletPath();

    public void testServletDispatcher() throws ServletException, IOException {
        service(getServletDispatcher());
    }

    protected Map getParameterMap() {
        return new HashMap();
    }

    protected void setUp() throws Exception {
        super.setUp();

        loadConfig();
    }

    protected void loadConfig() {
        XmlConfigurationProvider c = new XmlConfigurationProvider(getConfigFilename());
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.destroyConfiguration();
        ConfigurationManager.addConfigurationProvider(c);
        ConfigurationManager.getConfiguration();
    }

    protected void tearDown() throws Exception {
        ConfigurationManager.destroyConfiguration();
    }

    private void service(HttpServlet servlet) throws ServletException, IOException {
        Mock dispatcherMock = new Mock(RequestDispatcher.class);
        dispatcherMock.expect("include", C.ANY_ARGS);

        MockHttpSession session = new MockHttpSession();

        WebWorkMockHttpServletRequest request = new WebWorkMockHttpServletRequest();
        request.setSession(session);
        request.setupAddHeader("Content-Type", "dunno what this should be... just not multipart !");
        request.setParameterMap(getParameterMap());
        request.setupGetServletPath(getServletPath());
        request.setupGetPathInfo(getServletPath());
        request.setupGetContentType("text/plain");

        MockHttpServletResponse response = new WebWorkMockHttpServletResponse();
        response.setupOutputStream(new MockServletOutputStream());

        Mock servletContextDMock = new Mock(ServletContext.class);
        servletContextDMock.matchAndReturn("getRealPath", C.args(C.eq("")), "");
        servletContextDMock.matchAndReturn("getRealPath", C.args(C.eq("velocity.properties")), null);
        servletContextDMock.matchAndReturn("getRealPath", C.args(C.eq("/WEB-INF/velocity.properties")), null);
        servletContextDMock.matchAndReturn("log", C.ANY_ARGS, null);

        ServletContext servletContextMock = (ServletContext) servletContextDMock.proxy();
        servletContextDMock.expectAndReturn("getServerInfo", "Resin");
        servletContextDMock.expectAndReturn("getServerInfo", "Resin");

        MockServletConfig servletConfigMock = new MockServletConfig();
        servletConfigMock.setServletContext(servletContextMock);

        servlet.init(servletConfigMock);
        servlet.service(request, response);
    }
}
