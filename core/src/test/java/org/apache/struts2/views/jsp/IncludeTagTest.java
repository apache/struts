/*
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

import static org.easymock.EasyMock.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.struts2.StrutsException;
import org.apache.struts2.components.Include;

import com.mockobjects.servlet.MockRequestDispatcher;

/**
 * Unit test of {@link IncludeTag}.
 *
 */
public class IncludeTagTest extends AbstractTagTest {

    private RequestDispatcher mockRequestDispatcher;

    private IncludeTag tag;

    public void testNoURL() throws Exception {
        try {
            tag.doStartTag();
            tag.doEndTag();
            fail("Should have thrown exception as no URL is specified in setValue");
        } catch (StrutsException e) {
            assertEquals("tag 'include', field 'value': You must specify the URL to include. Example: /foo.jsp", e.getMessage());
        }
    }

    public void testIncludeNoParam() throws Exception {
        
        // use always matcher as we can not determine the excact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);
        
        replay(mockRequestDispatcher);

        tag.setValue("person/list.jsp");
        tag.doStartTag();
        tag.doEndTag();
        
        assertEquals("/person/list.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());
        
        verify(mockRequestDispatcher);
    }

    public void testIncludeWithParameters() throws Exception {
       
        // use always matcher as we can not determine the excact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);
        
        replay(mockRequestDispatcher);

        tag.setValue("person/create.jsp");
        tag.doStartTag();
        // adding param must be done after doStartTag()
        Include include = (Include) tag.getComponent();
        include.addParameter("user", "Santa Claus");
        tag.doEndTag();

        assertEquals("/person/create.jsp?user=Santa+Claus", request.getRequestDispatherString());
        assertEquals("", writer.toString());
        
        verify(mockRequestDispatcher);
    }

    public void testIncludeRelative2Dots() throws Exception {
        // TODO: we should test for .. in unit test - is this test correct?
        // use always matcher as we can not determine the exact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);
        
        replay(mockRequestDispatcher);

        request.setupGetServletPath("app/manager");
        tag.setValue("../car/view.jsp");
        tag.doStartTag();
        tag.doEndTag();


        assertEquals("/car/view.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());
        
        verify(mockRequestDispatcher);        
    }

    protected void setUp() throws Exception {
        super.setUp();
        request.setupGetRequestDispatcher(new MockRequestDispatcher());
        tag = new IncludeTag();

        mockRequestDispatcher = (RequestDispatcher) createMock(RequestDispatcher.class);

        request.setupGetRequestDispatcher(mockRequestDispatcher);
        tag.setPageContext(pageContext);
        tag.setPageContext(pageContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        tag = null;
        mockRequestDispatcher = null;
    }

}
