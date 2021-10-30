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

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import com.mockobjects.servlet.MockPageContext;
import com.mockobjects.servlet.MockJspWriter;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class I18nTagTest extends StrutsInternalTestCase {

    I18nTag tag;
    MockPageContext pageContext;
    ValueStack stack;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // create the needed objects
        tag = new I18nTag();
        stack = ActionContext.getContext().getValueStack();

        // create the mock http servlet request
        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

        // create the mock page context
        pageContext = new MockPageContext();
        pageContext.setRequest(request);
        pageContext.setJspWriter(new MockJspWriter());

        // associate the tag with the mock page request
        tag.setPageContext(pageContext);
    }

    public void testSimple() throws Exception {

        // set the resource bundle
        tag.setName("testmessages");

        int result = 0;

        try {
            result = tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, result);

        try {
            result = tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        I18nTag freshTag = new I18nTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testSimple_clearTagStateSet() throws Exception {

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        // set the resource bundle
        tag.setName("testmessages");

        int result = 0;

        try {
            result = tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, result);

        try {
            result = tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        I18nTag freshTag = new I18nTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    /**
     * Helper method to simplify setting the performClearTagStateForTagPoolingServers state for a 
     * {@link ComponentTagSupport} tag's {@link Component} to match expecations for a test.
     * 
     * Since the component is not available to the tag until after the doStartTag() method is called,
     * but we need to ensure the component's {@link Component#performClearTagStateForTagPoolingServers} state matches
     * what we set for the Tag when a non-default (true) value is used, this method retrieves the component instance,
     * sets the value specified and forces the parameters to be repopulated again.
     * 
     * @param tag The ComponentTagSupport tag upon whose component we will set the performClearTagStateForTagPoolingServers state.
     * @param performClearTagStateForTagPoolingServers true to clear tag state, false otherwise
     */
    protected void setComponentTagClearTagState(ComponentTagSupport tag, boolean performClearTagStateForTagPoolingServers) {
        tag.component.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
        //tag.populateParams();  // Not safe to call after doStartTag() ... breaks some tests.
        tag.populatePerformClearTagStateForTagPoolingServersParam();  // Only populate the performClearTagStateForTagPoolingServers parameter for the Tag.
    }

    /**
     * Asserts that an exception is thrown when something unexpected is popped off the stack by the closing tag
     *
     * @throws Exception
     */
    public void testUnexpectedPop() throws Exception {

         // set the resource bundle
        tag.setName("testmessages");

        int result = 0;

        try {
            result = tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        stack.push("An new object on top of the stack");

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, result);

        try {
            result = tag.doEndTag();
            fail();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        } catch (StrutsException e) {
            e.printStackTrace();
            // pass
        }

    }
}
