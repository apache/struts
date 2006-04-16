/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.dispatcher;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletOutputStream;
import org.apache.struts.action2.StrutsTestCase;
import org.apache.struts.action2.views.jsp.StrutsMockHttpServletRequest;
import org.apache.struts.action2.views.jsp.StrutsMockHttpServletResponse;
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
 */
public abstract class AbstractServletDispatcherTestCase extends StrutsTestCase {

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

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setSession(session);
        request.setupAddHeader("Content-Type", "dunno what this should be... just not multipart !");
        request.setParameterMap(getParameterMap());
        request.setupGetServletPath(getServletPath());
        request.setupGetPathInfo(getServletPath());
        request.setupGetContentType("text/plain");

        MockHttpServletResponse response = new StrutsMockHttpServletResponse();
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
