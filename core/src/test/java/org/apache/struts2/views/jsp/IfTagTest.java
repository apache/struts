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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;

import com.mockobjects.servlet.MockJspWriter;
import com.mockobjects.servlet.MockPageContext;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;


/**
 */
public class IfTagTest extends StrutsInternalTestCase {

    IfTag tag;
    MockPageContext pageContext;
    ValueStack stack;


    public void testNonBooleanTest() {
        // set up the stack
        Foo foo = new Foo();
        foo.setNum(1);
        stack.push(foo);

        // set up the test
        tag.setTest("num");

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
        IfTag freshTag = new IfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testNonBooleanTest_clearTagStateSet() {
        // set up the stack
        Foo foo = new Foo();
        foo.setNum(1);
        stack.push(foo);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        // set up the test
        tag.setTest("num");

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
        IfTag freshTag = new IfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testTestError() {
        // set up the stack
        Foo foo = new Foo();
        foo.setNum(2);
        stack.push(foo);

        // set up the test
        tag.setTest("nuuuuum == 2");

        int result = 0;

        try {
            result = tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.SKIP_BODY, result);

        try {
            result = tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testTestError_clearTagStateSet() {
        // set up the stack
        Foo foo = new Foo();
        foo.setNum(2);
        stack.push(foo);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        // set up the test
        tag.setTest("nuuuuum == 2");

        int result = 0;

        try {
            result = tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.SKIP_BODY, result);

        try {
            result = tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testTestFalse() {
        // set up the stack
        Foo foo = new Foo();
        foo.setNum(2);
        stack.push(foo);

        // set up the test
        tag.setTest("num != 2");

        int result = 0;

        try {
            result = tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.SKIP_BODY, result);

        try {
            result = tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testTestFalse_clearTagStateSet() {
        // set up the stack
        Foo foo = new Foo();
        foo.setNum(2);
        stack.push(foo);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        // set up the test
        tag.setTest("num != 2");

        int result = 0;

        try {
            result = tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.SKIP_BODY, result);

        try {
            result = tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testTestTrue() {
        // set up the stack
        Foo foo = new Foo();
        foo.setNum(2);
        stack.push(foo);

        // set up the test
        tag.setTest("num == 2");

        int result = 0;
        //tag.setPageContext(pageContext);

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
        IfTag freshTag = new IfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testTestTrue_clearTagStateSet() {
        // set up the stack
        Foo foo = new Foo();
        foo.setNum(2);
        stack.push(foo);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        // set up the test
        tag.setTest("num == 2");

        int result = 0;
        //tag.setPageContext(pageContext);

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
        IfTag freshTag = new IfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testIfElse1() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPageContext(pageContext);
        ifTag.setTest("true");

        ElseTag elseTag = new ElseTag();
        elseTag.setPageContext(pageContext);

        int r1 = ifTag.doStartTag();
        ifTag.doEndTag();
        int r2 = elseTag.doStartTag();
        elseTag.doEndTag();

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r1);
        assertEquals(TagSupport.SKIP_BODY, r2);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseTag freshTag2 = new ElseTag();
        freshTag2.setPageContext(pageContext);
        // ElseTag has no additional state, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseTag, freshTag2));
    }

    public void testIfElse1_clearTagStateSet() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        ifTag.setPageContext(pageContext);
        ifTag.setTest("true");

        ElseTag elseTag = new ElseTag();
        elseTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseTag.setPageContext(pageContext);

        int r1 = ifTag.doStartTag();
        setComponentTagClearTagState(ifTag, true);  // Ensure component tag state clearing is set true (to match tag).
        ifTag.doEndTag();
        int r2 = elseTag.doStartTag();
        setComponentTagClearTagState(elseTag, true);  // Ensure component tag state clearing is set true (to match tag).
        elseTag.doEndTag();

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r1);
        assertEquals(TagSupport.SKIP_BODY, r2);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseTag freshTag2 = new ElseTag();
        freshTag2.setPerformClearTagStateForTagPoolingServers(true);
        freshTag2.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseTag, freshTag2));
    }

    public void testIfElse2() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPageContext(pageContext);
        ifTag.setTest("false");

        ElseTag elseTag = new ElseTag();
        elseTag.setPageContext(pageContext);

        int r1 = ifTag.doStartTag();
        ifTag.doEndTag();
        int r2 = elseTag.doStartTag();
        elseTag.doEndTag();

        assertEquals(TagSupport.SKIP_BODY, r1);
        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r2);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseTag freshTag2 = new ElseTag();
        freshTag2.setPageContext(pageContext);
        // ElseTag has no additional state, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseTag, freshTag2));
    }

    public void testIfElse2_clearTagStateSet() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        ifTag.setPageContext(pageContext);
        ifTag.setTest("false");

        ElseTag elseTag = new ElseTag();
        elseTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseTag.setPageContext(pageContext);

        int r1 = ifTag.doStartTag();
        setComponentTagClearTagState(ifTag, true);  // Ensure component tag state clearing is set true (to match tag).
        ifTag.doEndTag();
        int r2 = elseTag.doStartTag();
        setComponentTagClearTagState(elseTag, true);  // Ensure component tag state clearing is set true (to match tag).
        elseTag.doEndTag();

        assertEquals(TagSupport.SKIP_BODY, r1);
        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r2);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseTag freshTag2 = new ElseTag();
        freshTag2.setPerformClearTagStateForTagPoolingServers(true);
        freshTag2.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseTag, freshTag2));
    }

    public void testIfElseIf() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPageContext(pageContext);
        ifTag.setTest("false");

        ElseIfTag elseIfTag1 = new ElseIfTag();
        elseIfTag1.setPageContext(pageContext);
        elseIfTag1.setTest("false");

        ElseIfTag elseIfTag2 = new ElseIfTag();
        elseIfTag2.setPageContext(pageContext);
        elseIfTag2.setTest("true");

        ElseIfTag elseIfTag3 = new ElseIfTag();
        elseIfTag3.setPageContext(pageContext);
        elseIfTag3.setTest("true");

        int r1 = ifTag.doStartTag();
        ifTag.doEndTag();
        int r2 = elseIfTag1.doStartTag();
        elseIfTag1.doEndTag();
        int r3 = elseIfTag2.doStartTag();
        elseIfTag2.doEndTag();
        int r4 = elseIfTag3.doStartTag();
        elseIfTag3.doEndTag();

        assertEquals(TagSupport.SKIP_BODY, r1);
        assertEquals(TagSupport.SKIP_BODY, r2);
        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r3);
        assertEquals(TagSupport.SKIP_BODY, r4);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag2 = new ElseIfTag();
        freshTag2.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag1, freshTag2));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag2, freshTag2));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag3, freshTag2));
    }

    public void testIfElseIf_clearTagStateSet() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        ifTag.setPageContext(pageContext);
        ifTag.setTest("false");

        ElseIfTag elseIfTag1 = new ElseIfTag();
        elseIfTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseIfTag1.setPageContext(pageContext);
        elseIfTag1.setTest("false");

        ElseIfTag elseIfTag2 = new ElseIfTag();
        elseIfTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseIfTag2.setPageContext(pageContext);
        elseIfTag2.setTest("true");

        ElseIfTag elseIfTag3 = new ElseIfTag();
        elseIfTag3.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseIfTag3.setPageContext(pageContext);
        elseIfTag3.setTest("true");

        int r1 = ifTag.doStartTag();
        setComponentTagClearTagState(ifTag, true);  // Ensure component tag state clearing is set true (to match tag).
        ifTag.doEndTag();
        int r2 = elseIfTag1.doStartTag();
        setComponentTagClearTagState(elseIfTag1, true);  // Ensure component tag state clearing is set true (to match tag).
        elseIfTag1.doEndTag();
        int r3 = elseIfTag2.doStartTag();
        setComponentTagClearTagState(elseIfTag2, true);  // Ensure component tag state clearing is set true (to match tag).
        elseIfTag2.doEndTag();
        int r4 = elseIfTag3.doStartTag();
        setComponentTagClearTagState(elseIfTag3, true);  // Ensure component tag state clearing is set true (to match tag).
        elseIfTag3.doEndTag();

        assertEquals(TagSupport.SKIP_BODY, r1);
        assertEquals(TagSupport.SKIP_BODY, r2);
        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r3);
        assertEquals(TagSupport.SKIP_BODY, r4);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag2 = new ElseIfTag();
        freshTag2.setPerformClearTagStateForTagPoolingServers(true);
        freshTag2.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag1, freshTag2));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag2, freshTag2));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag3, freshTag2));
    }

    public void testIfElseIfElse() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPageContext(pageContext);
        ifTag.setTest("false");

        ElseIfTag elseIfTag1 = new ElseIfTag();
        elseIfTag1.setPageContext(pageContext);
        elseIfTag1.setTest("false");

        ElseIfTag elseIfTag2 = new ElseIfTag();
        elseIfTag2.setPageContext(pageContext);
        elseIfTag2.setTest("false");

        ElseIfTag elseIfTag3 = new ElseIfTag();
        elseIfTag3.setPageContext(pageContext);
        elseIfTag3.setTest("false");

        ElseTag elseTag = new ElseTag();
        elseTag.setPageContext(pageContext);

        int r1 = ifTag.doStartTag();
        ifTag.doEndTag();
        int r2 = elseIfTag1.doStartTag();
        elseIfTag1.doEndTag();
        int r3 = elseIfTag2.doStartTag();
        elseIfTag2.doEndTag();
        int r4 = elseIfTag3.doStartTag();
        elseIfTag3.doEndTag();
        int r5 = elseTag.doStartTag();
        elseTag.doEndTag();

        assertEquals(TagSupport.SKIP_BODY, r1);
        assertEquals(TagSupport.SKIP_BODY, r2);
        assertEquals(TagSupport.SKIP_BODY, r3);
        assertEquals(TagSupport.SKIP_BODY, r4);
        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r5);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPageContext(pageContext);
                assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag2 = new ElseIfTag();
        freshTag2.setPageContext(pageContext);
                assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag1, freshTag2));
                assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag2, freshTag2));
                assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag3, freshTag2));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseTag freshTag3 = new ElseTag();
        freshTag3.setPageContext(pageContext);
        // ElseTag has no additional state, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseTag, freshTag3));
    }

    public void testIfElseIfElse_clearTagStateSet() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        ifTag.setPageContext(pageContext);
        ifTag.setTest("false");

        ElseIfTag elseIfTag1 = new ElseIfTag();
        elseIfTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseIfTag1.setPageContext(pageContext);
        elseIfTag1.setTest("false");

        ElseIfTag elseIfTag2 = new ElseIfTag();
        elseIfTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseIfTag2.setPageContext(pageContext);
        elseIfTag2.setTest("false");

        ElseIfTag elseIfTag3 = new ElseIfTag();
        elseIfTag3.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseIfTag3.setPageContext(pageContext);
        elseIfTag3.setTest("false");

        ElseTag elseTag = new ElseTag();
        elseTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseTag.setPageContext(pageContext);

        int r1 = ifTag.doStartTag();
        setComponentTagClearTagState(ifTag, true);  // Ensure component tag state clearing is set true (to match tag).
        ifTag.doEndTag();
        int r2 = elseIfTag1.doStartTag();
        setComponentTagClearTagState(elseIfTag1, true);  // Ensure component tag state clearing is set true (to match tag).
        elseIfTag1.doEndTag();
        int r3 = elseIfTag2.doStartTag();
        setComponentTagClearTagState(elseIfTag2, true);  // Ensure component tag state clearing is set true (to match tag).
        elseIfTag2.doEndTag();
        int r4 = elseIfTag3.doStartTag();
        setComponentTagClearTagState(elseIfTag3, true);  // Ensure component tag state clearing is set true (to match tag).
        elseIfTag3.doEndTag();
        int r5 = elseTag.doStartTag();
        setComponentTagClearTagState(elseTag, true);  // Ensure component tag state clearing is set true (to match tag).
        elseTag.doEndTag();

        assertEquals(TagSupport.SKIP_BODY, r1);
        assertEquals(TagSupport.SKIP_BODY, r2);
        assertEquals(TagSupport.SKIP_BODY, r3);
        assertEquals(TagSupport.SKIP_BODY, r4);
        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r5);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseIfTag freshTag2 = new ElseIfTag();
        freshTag2.setPerformClearTagStateForTagPoolingServers(true);
        freshTag2.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag1, freshTag2));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag2, freshTag2));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseIfTag3, freshTag2));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseTag freshTag3 = new ElseTag();
        freshTag3.setPerformClearTagStateForTagPoolingServers(true);
        freshTag3.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseTag, freshTag3));
    }

    public void testNestedIfElse1() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPageContext(pageContext);
        ifTag.setTest("true");

        IfTag nestedIfTag = new IfTag();
        nestedIfTag.setPageContext(pageContext);
        nestedIfTag.setTest("true");

        ElseTag elseTag = new ElseTag();
        elseTag.setPageContext(pageContext);

        int r1 = ifTag.doStartTag();
        int r2 = nestedIfTag.doStartTag();
        int r3 = nestedIfTag.doEndTag();
        int r4 = ifTag.doEndTag();
        int r5 = elseTag.doStartTag();
        int r6 = elseTag.doEndTag();

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r1);
        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r2);
        assertEquals(TagSupport.EVAL_PAGE, r3);
        assertEquals(TagSupport.EVAL_PAGE, r4);
        assertEquals(TagSupport.SKIP_BODY, r5);
        assertEquals(TagSupport.EVAL_PAGE, r6);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(nestedIfTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseTag freshTag2 = new ElseTag();
        freshTag2.setPageContext(pageContext);
        // ElseTag has no additional state, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseTag, freshTag2));
    }

    public void testNestedIfElse1_clearTagStateSet() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        ifTag.setPageContext(pageContext);
        ifTag.setTest("true");

        IfTag nestedIfTag = new IfTag();
        nestedIfTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        nestedIfTag.setPageContext(pageContext);
        nestedIfTag.setTest("true");

        ElseTag elseTag = new ElseTag();
        elseTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseTag.setPageContext(pageContext);

        int r1 = ifTag.doStartTag();
        setComponentTagClearTagState(ifTag, true);  // Ensure component tag state clearing is set true (to match tag).
        int r2 = nestedIfTag.doStartTag();
        setComponentTagClearTagState(nestedIfTag, true);  // Ensure component tag state clearing is set true (to match tag).
        int r3 = nestedIfTag.doEndTag();
        int r4 = ifTag.doEndTag();
        int r5 = elseTag.doStartTag();
        setComponentTagClearTagState(elseTag, true);  // Ensure component tag state clearing is set true (to match tag).
        int r6 = elseTag.doEndTag();

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r1);
        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r2);
        assertEquals(TagSupport.EVAL_PAGE, r3);
        assertEquals(TagSupport.EVAL_PAGE, r4);
        assertEquals(TagSupport.SKIP_BODY, r5);
        assertEquals(TagSupport.EVAL_PAGE, r6);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(nestedIfTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseTag freshTag2 = new ElseTag();
        freshTag2.setPerformClearTagStateForTagPoolingServers(true);
        freshTag2.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseTag, freshTag2));
    }

    public void testNestedIfElse2() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPageContext(pageContext);
        ifTag.setTest("true");

        IfTag nestedIfTag = new IfTag();
        nestedIfTag.setPageContext(pageContext);
        nestedIfTag.setTest("false");

        ElseTag elseTag = new ElseTag();
        elseTag.setPageContext(pageContext);

        int r1 = ifTag.doStartTag();
        int r2 = nestedIfTag.doStartTag();
        int r3 = nestedIfTag.doEndTag();
        int r4 = ifTag.doEndTag();
        int r5 = elseTag.doStartTag();
        int r6 = elseTag.doEndTag();

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r1);
        assertEquals(TagSupport.SKIP_BODY, r2);
        assertEquals(TagSupport.EVAL_PAGE, r3);
        assertEquals(TagSupport.EVAL_PAGE, r4);
        assertEquals(TagSupport.SKIP_BODY, r5);
        assertEquals(TagSupport.EVAL_PAGE, r6);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(nestedIfTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseTag freshTag2 = new ElseTag();
        freshTag2.setPageContext(pageContext);
        // ElseTag has no additional state, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseTag, freshTag2));
    }

    public void testNestedIfElse2_clearTagStateSet() throws Exception {
        IfTag ifTag = new IfTag();
        ifTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        ifTag.setPageContext(pageContext);
        ifTag.setTest("true");

        IfTag nestedIfTag = new IfTag();
        nestedIfTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        nestedIfTag.setPageContext(pageContext);
        nestedIfTag.setTest("false");

        ElseTag elseTag = new ElseTag();
        elseTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        elseTag.setPageContext(pageContext);

        int r1 = ifTag.doStartTag();
        setComponentTagClearTagState(ifTag, true);  // Ensure component tag state clearing is set true (to match tag).
        int r2 = nestedIfTag.doStartTag();
        setComponentTagClearTagState(nestedIfTag, true);  // Ensure component tag state clearing is set true (to match tag).
        int r3 = nestedIfTag.doEndTag();
        int r4 = ifTag.doEndTag();
        int r5 = elseTag.doStartTag();
        setComponentTagClearTagState(elseTag, true);  // Ensure component tag state clearing is set true (to match tag).
        int r6 = elseTag.doEndTag();

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, r1);
        assertEquals(TagSupport.SKIP_BODY, r2);
        assertEquals(TagSupport.EVAL_PAGE, r3);
        assertEquals(TagSupport.EVAL_PAGE, r4);
        assertEquals(TagSupport.SKIP_BODY, r5);
        assertEquals(TagSupport.EVAL_PAGE, r6);

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        IfTag freshTag = new IfTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(ifTag, freshTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(nestedIfTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ElseTag freshTag2 = new ElseTag();
        freshTag2.setPerformClearTagStateForTagPoolingServers(true);
        freshTag2.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(elseTag, freshTag2));
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // create the needed objects
        tag = new IfTag();
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


    class Foo {
        int num;

        public void setNum(int num) {
            this.num = num;
        }

        public int getNum() {
            return num;
        }
    }
}
