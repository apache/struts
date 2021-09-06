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

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.components.If;

import com.mockobjects.servlet.MockJspWriter;
import com.mockobjects.servlet.MockPageContext;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

/**
 *
 */
public class ElseIfTagTest extends StrutsInternalTestCase {

    protected MockPageContext pageContext;
    protected MockJspWriter jspWriter;
    protected ValueStack stack;


    public void testIfIsFalseElseIfIsTrue() throws Exception {
        stack.getContext().put(If.ANSWER, Boolean.FALSE);

        ElseIfTag tag = new ElseIfTag();
        tag.setPageContext(pageContext);
        tag.setTest("true");

        int result = tag.doStartTag();
        tag.doEndTag();

        assertEquals(result, TagSupport.EVAL_BODY_INCLUDE);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag = new ElseIfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testIfIsFalseElseIfIsTrue_clearTagStateSet() throws Exception {
        stack.getContext().put(If.ANSWER, Boolean.FALSE);

        ElseIfTag tag = new ElseIfTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setTest("true");

        int result = tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(result, TagSupport.EVAL_BODY_INCLUDE);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag = new ElseIfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testIfIsFalseElseIfIsFalse() throws Exception {
        stack.getContext().put(If.ANSWER, Boolean.FALSE);

        ElseIfTag tag = new ElseIfTag();
        tag.setPageContext(pageContext);
        tag.setTest("false");

        int result = tag.doStartTag();
        tag.doEndTag();

        assertEquals(result, TagSupport.SKIP_BODY);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag = new ElseIfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testIfIsFalseElseIfIsFalse_clearTagStateSet() throws Exception {
        stack.getContext().put(If.ANSWER, Boolean.FALSE);

        ElseIfTag tag = new ElseIfTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setTest("false");

        int result = tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(result, TagSupport.SKIP_BODY);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag = new ElseIfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testIfIsTrueElseIfIsTrue() throws Exception {
        stack.getContext().put(If.ANSWER, Boolean.TRUE);

        ElseIfTag tag = new ElseIfTag();
        tag.setPageContext(pageContext);
        tag.setTest("true");

        int result = tag.doStartTag();
        tag.doEndTag();

        assertEquals(result, TagSupport.SKIP_BODY);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag = new ElseIfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testIfIsTrueElseIfIsTrue_clearTagStateSet() throws Exception {
        stack.getContext().put(If.ANSWER, Boolean.TRUE);

        ElseIfTag tag = new ElseIfTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setTest("true");

        int result = tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(result, TagSupport.SKIP_BODY);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag = new ElseIfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testIfIsTrueElseIfIsFalse() throws Exception {
        stack.getContext().put(If.ANSWER, Boolean.TRUE);

        ElseIfTag tag = new ElseIfTag();
        tag.setPageContext(pageContext);
        tag.setTest("false");

        int result = tag.doStartTag();
        tag.doEndTag();

        assertEquals(result, TagSupport.SKIP_BODY);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag = new ElseIfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testIfIsTrueElseIfIsFalse_clearTagStateSet() throws Exception {
        stack.getContext().put(If.ANSWER, Boolean.TRUE);

        ElseIfTag tag = new ElseIfTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setTest("false");

        int result = tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(result, TagSupport.SKIP_BODY);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag = new ElseIfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();

        jspWriter = new MockJspWriter();

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();

        StrutsMockServletContext servletContext = new StrutsMockServletContext();
        servletContext.setServletInfo("not-weblogic");

        pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);
        pageContext.setServletContext(servletContext);

        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
    }

    /**
     * Helper method to simplify setting the performClearTagStateForTagPoolingServers state for a 
     * {@link ComponentTagSupport} tag's {@link Component} to match expectations for the test.
     * 
     * The component reference is not available to the tag until after the doStartTag() method is called.
     * We need to ensure the component's {@link Component#performClearTagStateForTagPoolingServers} state matches
     * what we set for the Tag when a non-default (true) value is used, so this method accesses the component instance,
     * sets the value specified and forces the tag's parameters to be repopulated again.
     * 
     * @param tag The ComponentTagSupport tag upon whose component we will set the performClearTagStateForTagPoolingServers state.
     * @param performClearTagStateForTagPoolingServers true to clear tag state, false otherwise
     */
    protected void setComponentTagClearTagState(ComponentTagSupport tag, boolean performClearTagStateForTagPoolingServers) {
        tag.component.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
        //tag.populateParams();  // Not safe to call after doStartTag() ... breaks some tests.
        tag.populatePerformClearTagStateForTagPoolingServersParam();  // Only populate the performClearTagStateForTagPoolingServers parameter for the Tag.
    }

}
