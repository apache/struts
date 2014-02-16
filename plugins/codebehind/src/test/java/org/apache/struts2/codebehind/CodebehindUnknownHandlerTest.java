/*
 * $Id$
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

package org.apache.struts2.codebehind;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.util.XWorkTestCaseHelper;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

public class CodebehindUnknownHandlerTest extends StrutsTestCase {

    CodebehindUnknownHandler handler;
    Mock mockServletContext;
    
    public void setUp() throws Exception {
        configurationManager = XWorkTestCaseHelper.setUp();
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        actionProxyFactory = container.getInstance(ActionProxyFactory.class);
        servletContext = new MockServletContext();
        initDispatcher(Collections.singletonMap("actionPackages", "foo.bar"));
        mockServletContext = new Mock(ServletContext.class);
        handler = new CodebehindUnknownHandler("codebehind-default", configuration);
        handler.setPathPrefix("/");
        handler.setObjectFactory(container.getInstance(ObjectFactory.class));
        handler.setServletContext((ServletContext)mockServletContext.proxy());
    }

    public void testBuildResult() {
        ActionContext ctx = new ActionContext(new HashMap());
        ResultTypeConfig config = new ResultTypeConfig.Builder("null", SomeResult.class.getName()).defaultResultParam("location").build();
        
        Result result = handler.buildResult("/foo.jsp", "success", config, ctx);
        assertNotNull(result);
        assertTrue(result instanceof SomeResult);
        assertEquals("/foo.jsp", ((SomeResult) result).location);
        
    }

    public void testString() {
        assertEquals("foo.bar.jim", handler.string("foo", ".", "bar", ".", "jim"));
    }

    public void testDeterminePath() {
        assertEquals("/", handler.determinePath("/", ""));
        assertEquals("/", handler.determinePath("/", null));
        assertEquals("/", handler.determinePath("/", "/"));
        assertEquals("/foo/", handler.determinePath("/", "/foo"));
        assertEquals("/foo/", handler.determinePath("/", "/foo/"));
        assertEquals("/foo/", handler.determinePath("/", "foo"));
        assertEquals("/", handler.determinePath("", ""));
        assertEquals("/foo/", handler.determinePath("", "foo"));
        assertEquals("/foo/", handler.determinePath("", "/foo/"));
    }
    
    public void testLocateTemplate() throws MalformedURLException {
        URL url = new URL("file:/foo.xml");
        mockServletContext.expectAndReturn("getResource", C.args(C.eq("/foo.xml")), url);
        assertEquals(url, handler.locateTemplate("/foo.xml"));
        mockServletContext.verify();
        
    }
    
    public void testLocateTemplateFromClasspath() throws MalformedURLException {
        mockServletContext.expectAndReturn("getResource", C.args(C.eq("struts-plugin.xml")), null);
        URL url = handler.locateTemplate("struts-plugin.xml");
        assertNotNull(url);
        assertTrue(url.toString().endsWith("struts-plugin.xml"));
        mockServletContext.verify();
    }

    /**
     * Assert that an unknown action like /foo maps to ActionSupport with a ServletDispatcherResult to /foo.jsp
     */
    public void testBuildActionConfigForUnknownAction() throws MalformedURLException {
        URL url = new URL("file:/foo.jsp");
        mockServletContext.expectAndReturn("getResource", C.args(C.eq("/foo.jsp")), url);
        ActionConfig actionConfig = handler.handleUnknownAction("/", "foo");
        // we need a package
        assertEquals("codebehind-default", actionConfig.getPackageName());
        // a non-empty interceptor stack
        assertTrue(actionConfig.getInterceptors().size() > 0);
        // ActionSupport as the implementation
        assertEquals(ActionSupport.class.getName(), actionConfig.getClassName());
        // with one result
        assertEquals(1, actionConfig.getResults().size());
        // named success
        assertNotNull(actionConfig.getResults().get("success"));
        // of ServletDispatcherResult type
        assertEquals(ServletDispatcherResult.class.getName(), actionConfig.getResults().get("success").getClassName());
        // and finally pointing to foo.jsp!
        assertEquals("/foo.jsp", actionConfig.getResults().get("success").getParams().get("location"));
    }

    public static class SomeResult implements Result {

        public String location;
        public void setLocation(String loc) {
            this.location = loc;
        }
        
        public void execute(ActionInvocation invocation) throws Exception {
        }
        
    }

}
