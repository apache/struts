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
package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class HiddenTest extends AbstractUITagTest {

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        HiddenTag tag = new HiddenTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Hidden-1.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        HiddenTag freshTag = new HiddenTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimple_clearTagStateSet() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        HiddenTag tag = new HiddenTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Hidden-1.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        HiddenTag freshTag = new HiddenTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDisabled() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        HiddenTag tag = new HiddenTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");
        tag.setDisabled("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Hidden-2.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        HiddenTag freshTag = new HiddenTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDisabled_clearTagStateSet() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        HiddenTag tag = new HiddenTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");
        tag.setDisabled("true");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Hidden-2.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        HiddenTag freshTag = new HiddenTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDynamicAttributesWithActionInvocation() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setId(27357L);

        MockActionInvocation ai = new MockActionInvocation();
        ai.setAction(action);
        ActionContext.getContext().setActionInvocation(ai);

        HiddenTag tag = new HiddenTag();
        tag.setPageContext(pageContext);
        tag.setId("einszwei");
        tag.setName("first");
        tag.setValue("%{id}");
        tag.setDynamicAttribute("", "data-wuffmiauww", "%{id}");

        tag.doStartTag();
        tag.doEndTag();

        assertSame(stack.pop(), testAction);
        assertNotSame(stack.pop(), tag);

        verify(TextFieldTag.class.getResource("Hidden-3.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        HiddenTag freshTag = new HiddenTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDynamicAttributesWithActionInvocation_clearTagStateSet() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setId(27357L);

        MockActionInvocation ai = new MockActionInvocation();
        ai.setAction(action);
        ActionContext.getContext().setActionInvocation(ai);

        HiddenTag tag = new HiddenTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setId("einszwei");
        tag.setName("first");
        tag.setValue("%{id}");
        tag.setDynamicAttribute("", "data-wuffmiauww", "%{id}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertSame(stack.pop(), testAction);
        assertNotSame(stack.pop(), tag);

        verify(TextFieldTag.class.getResource("Hidden-3.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        HiddenTag freshTag = new HiddenTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDynamicAttributesWithStack() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setId(27357L);

        HiddenTag tag = new HiddenTag();
        tag.setPageContext(pageContext);
        tag.setId("einszwei");
        tag.setName("first");
        tag.setValue("%{id}");
        tag.setDynamicAttribute("", "data-wuffmiauww", "%{id}");

        tag.doStartTag();
        tag.doEndTag();

        assertSame(stack.pop(), testAction);
        assertNotSame(stack.pop(), tag);

        verify(TextFieldTag.class.getResource("Hidden-3.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        HiddenTag freshTag = new HiddenTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDynamicAttributesWithStack_clearTagStateSet() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setId(27357L);

        HiddenTag tag = new HiddenTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setId("einszwei");
        tag.setName("first");
        tag.setValue("%{id}");
        tag.setDynamicAttribute("", "data-wuffmiauww", "%{id}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertSame(stack.pop(), testAction);
        assertNotSame(stack.pop(), tag);

        verify(TextFieldTag.class.getResource("Hidden-3.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        HiddenTag freshTag = new HiddenTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * Initialize a map of {@link org.apache.struts2.views.jsp.AbstractUITagTest.PropertyHolder} for generic tag
     * property testing. Will be used when calling {@link #verifyGenericProperties(org.apache.struts2.views.jsp.ui.AbstractUITag,
     * String, String[])} as properties to verify.<br> This implementation extends testdata from AbstractUITag.
     *
     * @return A Map of PropertyHolders values bound to {@link org.apache.struts2.views.jsp.AbstractUITagTest.PropertyHolder#getName()}
     *         as key.
     */
    @Override
    protected Map initializedGenericTagTestProperties() {
        Map result = new HashMap();
        new PropertyHolder("name", "someName").addToMap(result);
        new PropertyHolder("value", "someValue").addToMap(result);
        new PropertyHolder("cssClass", "cssClass1", "class=\"cssClass1\"").addToMap(result);
        new PropertyHolder("cssStyle", "cssStyle1", "style=\"cssStyle1\"").addToMap(result);
        new PropertyHolder("id", "someId").addToMap(result);
        return result;
    }

    public void testGenericSimple() throws Exception {
        HiddenTag tag = new HiddenTag();
        verifyGenericProperties(tag, "simple", null);
    }

    public void testGenericXhtml() throws Exception {
        HiddenTag tag = new HiddenTag();
        verifyGenericProperties(tag, "xhtml", null);
    }
}
