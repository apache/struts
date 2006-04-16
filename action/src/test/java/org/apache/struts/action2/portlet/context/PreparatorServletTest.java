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
package org.apache.struts.action2.portlet.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.MockControl;

import org.apache.struts.action2.ServletActionContext;

import junit.framework.TestCase;

/**
 * 
 * Test for the {@link PreparatorServletTest}
 * 
 */
public class PreparatorServletTest extends TestCase {

    /**
     * Test that the service method stores the request, response and servlet context
     * in the {@link com.opensymphony.xwork.ActionContext}
     */
    public void testServiceHttpServletRequestHttpServletResponse() throws Exception {
        MockControl mockRequest = MockControl.createNiceControl(HttpServletRequest.class);
        MockControl mockResponse = MockControl.createNiceControl(HttpServletResponse.class);
        MockControl mockContext = MockControl.createNiceControl(ServletContext.class);
        MockControl mockConfig = MockControl.createNiceControl(ServletConfig.class);
        
        HttpServletRequest req = (HttpServletRequest)mockRequest.getMock();
        HttpServletResponse res = (HttpServletResponse)mockResponse.getMock();
        ServletContext context = (ServletContext)mockContext.getMock();
        ServletConfig config = (ServletConfig)mockConfig.getMock();
        
        mockConfig.expectAndDefaultReturn(config.getServletContext(), context);
        mockConfig.replay();
        
        PreparatorServlet servlet = new PreparatorServlet();
        servlet.init(config);
        servlet.service(req, res);
        assertSame(req, ServletActionContext.getRequest());
        assertSame(res, ServletActionContext.getResponse());
        assertSame(context, ServletActionContext.getServletContext());
        
        mockConfig.verify();
    }

}
