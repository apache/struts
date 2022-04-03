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

import java.util.Map;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;


/**
 */
public class TextareaTest extends AbstractUITagTest {

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextareaTag tag = new TextareaTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");
        tag.setRows("30");
        tag.setCols("20");
        tag.setTitle("mytitle");
        tag.setDisabled("true");
        tag.setTabindex("5");
        tag.setOnchange("alert('goodbye');");
        tag.setOnclick("alert('onclick');");
        tag.setId("the_id");
        tag.setOnkeyup("alert('hello');");
        tag.setReadonly("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextareaTag.class.getResource("Textarea-1.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextareaTag freshTag = new TextareaTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimple_clearTagStateSet() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextareaTag tag = new TextareaTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");
        tag.setRows("30");
        tag.setCols("20");
        tag.setTitle("mytitle");
        tag.setDisabled("true");
        tag.setTabindex("5");
        tag.setOnchange("alert('goodbye');");
        tag.setOnclick("alert('onclick');");
        tag.setId("the_id");
        tag.setOnkeyup("alert('hello');");
        tag.setReadonly("true");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(TextareaTag.class.getResource("Textarea-1.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextareaTag freshTag = new TextareaTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testNoColsAndRows() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextareaTag tag = new TextareaTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");
        tag.setTitle("mytitle");
        tag.setDisabled("true");
        tag.setTabindex("5");
        tag.setOnchange("alert('goodbye');");
        tag.setOnclick("alert('onclick');");
        tag.setId("the_id");
        tag.setOnkeyup("alert('hello');");
        tag.setReadonly("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextareaTag.class.getResource("Textarea-2.txt"));
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
    protected Map<String, PropertyHolder> initializedGenericTagTestProperties() {
        Map<String, PropertyHolder> result = super.initializedGenericTagTestProperties();
        new PropertyHolder("cols", "10").addToMap(result);
        new PropertyHolder("rows", "11").addToMap(result);
        new PropertyHolder("readonly", "true", "readonly=\"readonly\"").addToMap(result);
        new PropertyHolder("wrap", "soft").addToMap(result);
        return result;
    }

    public void testGenericSimple() throws Exception {
        TextareaTag tag = new TextareaTag();
        verifyGenericProperties(tag, "simple", new String[] {"value"});
    }

    public void testGenericXhtml() throws Exception {
        TextareaTag tag = new TextareaTag();
        verifyGenericProperties(tag, "xhtml", new String[] {"value"});
    }
}
