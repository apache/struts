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
        
        // Use always matcher as we can not determine the exact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);
        
        replay(mockRequestDispatcher);

        tag.setValue("person/list.jsp");
        tag.doStartTag();
        tag.doEndTag();
        
        assertEquals("/person/list.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());
        
        verify(mockRequestDispatcher);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IncludeTag freshTag = new IncludeTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeNoParam_clearTagStateSet() throws Exception {

        // Use always matcher as we can not determine the exact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);

        replay(mockRequestDispatcher);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("person/list.jsp");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals("/person/list.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());

        verify(mockRequestDispatcher);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IncludeTag freshTag = new IncludeTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeWithParameters() throws Exception {
       
        // Use always matcher as we can not determine the exact objects used in mock.include(request, response) call
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IncludeTag freshTag = new IncludeTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeWithParameters_clearTagStateSet() throws Exception {

        // Use always matcher as we can not determine the exact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);

        replay(mockRequestDispatcher);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("person/create.jsp");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        // adding param must be done after doStartTag()
        Include include = (Include) tag.getComponent();
        include.addParameter("user", "Santa Claus");
        tag.doEndTag();

        assertEquals("/person/create.jsp?user=Santa+Claus", request.getRequestDispatherString());
        assertEquals("", writer.toString());

        verify(mockRequestDispatcher);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IncludeTag freshTag = new IncludeTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeRelative2Dots() throws Exception {
        // TODO: we should test for .. in unit test - is this test correct?
        // Use always matcher as we can not determine the exact objects used in mock.include(request, response) call
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IncludeTag freshTag = new IncludeTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeRelative2Dots_clearTagStateSet() throws Exception {
        // TODO: we should test for .. in unit test - is this test correct?
        // Use always matcher as we can not determine the exact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);

        replay(mockRequestDispatcher);

        request.setupGetServletPath("app/manager");
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("../car/view.jsp");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();


        assertEquals("/car/view.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());

        verify(mockRequestDispatcher);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IncludeTag freshTag = new IncludeTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeSetUseResponseEncodingTrue() throws Exception {
        // TODO: If possible in future mock-test an actual content-includes with various encodings
        //   while setting the response encoding to match.  Doesn't appear to be possible
        //   right now in unit-test form.
        // Seems that the best we can do is verify the setUseResponseEncoding() doesn't fail...

        // Use always matcher as we can not determine the exact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);

        replay(mockRequestDispatcher);

        tag.setValue("person/create.jsp");
        response.setCharacterEncoding("UTF-8");
        tag.doStartTag();
        // Manipulate after doStartTag to ensure the tag component has undergone injection
        Include include = (Include) tag.getComponent();
        include.setUseResponseEncoding("true");
        tag.doEndTag();

        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals("/person/create.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());  // Nothing gets written for mock-include

        verify(mockRequestDispatcher);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IncludeTag freshTag = new IncludeTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeSetUseResponseEncodingTrue_clearTagStateSet()  throws Exception {
        // TODO: If possible in future mock-test an actual content-includes with various encodings
        //   while setting the response encoding to match.  Doesn't appear to be possible
        //   right now in unit-test form.
        // Seems that the best we can do is verify the setUseResponseEncoding() doesn't fail...

        // Use always matcher as we can not determine the exact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);

        replay(mockRequestDispatcher);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("person/create.jsp");
        response.setCharacterEncoding("UTF-8");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        // Manipulate after doStartTag to ensure the tag component has undergone injection
        Include include = (Include) tag.getComponent();
        include.setUseResponseEncoding("true");
        tag.doEndTag();

        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals("/person/create.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());  // Nothing gets written for mock-include

        verify(mockRequestDispatcher);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IncludeTag freshTag = new IncludeTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeSetUseResponseEncodingFalse() throws Exception {
        // TODO: If possible in future mock-test an actual content-includes with various encodings
        //   while setting the response encoding to match.  Doesn't appear to be possible
        //   right now in unit-test form.
        // Seems that the best we can do is verify the setUseResponseEncoding() doesn't fail...

        // Use always matcher as we can not determine the exact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);

        replay(mockRequestDispatcher);

        tag.setValue("person/create.jsp");
        response.setCharacterEncoding("UTF-8");
        tag.doStartTag();
        // Manipulate after doStartTag to ensure the tag component has undergone injection
        Include include = (Include) tag.getComponent();
        include.setUseResponseEncoding("false");
        tag.doEndTag();

        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals("/person/create.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());  // Nothing gets written for mock-include

        verify(mockRequestDispatcher);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IncludeTag freshTag = new IncludeTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeSetUseResponseEncodingFalse_clearTagStateSet()  throws Exception {
        // TODO: If possible in future mock-test an actual content-includes with various encodings
        //   while setting the response encoding to match.  Doesn't appear to be possible
        //   right now in unit-test form.
        // Seems that the best we can do is verify the setUseResponseEncoding() doesn't fail...

        // Use always matcher as we can not determine the exact objects used in mock.include(request, response) call
        mockRequestDispatcher.include(anyObject(ServletRequest.class), anyObject(ServletResponse.class));
        expectLastCall().times(1);

        replay(mockRequestDispatcher);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("person/create.jsp");
        response.setCharacterEncoding("UTF-8");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        // Manipulate after doStartTag to ensure the tag component has undergone injection
        Include include = (Include) tag.getComponent();
        include.setUseResponseEncoding("false");
        tag.doEndTag();

        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals("/person/create.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());  // Nothing gets written for mock-include

        verify(mockRequestDispatcher);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IncludeTag freshTag = new IncludeTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        request.setupGetRequestDispatcher(new MockRequestDispatcher());
        tag = new IncludeTag();

        mockRequestDispatcher = (RequestDispatcher) createMock(RequestDispatcher.class);

        request.setupGetRequestDispatcher(mockRequestDispatcher);
        tag.setPageContext(pageContext);
        tag.setPageContext(pageContext);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        tag = null;
        mockRequestDispatcher = null;
    }

}
