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

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.TestAction;
import org.apache.struts2.components.Anchor;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class AnchorTest extends AbstractUITagTest {

    public void testBeanInfo() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(AbstractUITag.class);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            System.out.println(pd.getName() + ": write = " + pd.getWriteMethod() + ", read = " + pd.getReadMethod());
        }
    }

    public void testSimple() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a");
        tag.doStartTag();
        tag.doEndTag();

        verifyResource("href-1.txt");

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimple_clearTagStateSet() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setHref("a");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verifyResource("href-1.txt");

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is unequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleBadQuote() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a\"");
        tag.doStartTag();
        tag.doEndTag();

        verifyResource("href-2.txt");

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleBadQuote_clearTagStateSet() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setHref("a\"");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verifyResource("href-2.txt");

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is unequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDynamicAttribute() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a");

        tag.setDynamicAttribute("uri", "dynAttrName", "dynAttrValue");

        tag.doStartTag();
        tag.doEndTag();

        verifyResource("Anchor-2.txt");

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDynamicAttribute_clearTagStateSet()  throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setHref("a");

        tag.setDynamicAttribute("uri", "dynAttrName", "dynAttrValue");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verifyResource("Anchor-2.txt");

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is unequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDynamicAttributeAsExpression() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a");

        tag.setDynamicAttribute("uri", "placeholder", "%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verifyResource("Anchor-3.txt");

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDynamicAttributeAsExpression_clearTagStateSet() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setHref("a");

        tag.setDynamicAttribute("uri", "placeholder", "%{foo}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verifyResource("Anchor-3.txt");

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is unequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    private void createAction() {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");
    }

    private AnchorTag createTag() {
        AnchorTag tag = new AnchorTag();
        tag.setPageContext(pageContext);

        tag.setId("mylink");
        return tag;
    }

    /**
     * Test anchor tag body supported
     */
    public void testSimpleWithBody() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a");

        StrutsBodyContent body = new StrutsBodyContent(null);
        body.print("normal body text with nothing to escape");
        tag.setBodyContent(body);

        tag.doStartTag();
        tag.doEndTag();

        verifyResource("href-3.txt");
    }

    /**
     * Test that by default anchor tag body is HTML-escaped.
     */
    public void testSimpleWithBodyHTMLEscaped() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a");

        StrutsBodyContent body = new StrutsBodyContent(null);
        body.print("should HTML escape: < & >");
        tag.setBodyContent(body);
        tag.setEscapeHtmlBody("true");

        tag.doStartTag();
        tag.doEndTag();

        verifyResource("href-4.txt");
    }

    /**
     * Test that with htmlEscapeBody false anchor tag body is not HTML-escaped.
     */
    public void testSimpleWithBodyNotHTMLEscaped() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a");
        tag.setEscapeHtmlBody("false");

        StrutsBodyContent body = new StrutsBodyContent(null);
        body.print("should not HTML escape: < & >");
        tag.setBodyContent(body);

        tag.doStartTag();
        tag.doEndTag();

        verifyResource("href-5.txt");
    }

    public void testInjectEscapeHtmlBodyFlag() throws Exception {
        // given
        initDispatcherWithConfigs("struts-default.xml, struts-escape-body.xml");
        String escapeHtmlBody = container.getInstance(String.class, StrutsConstants.STRUTS_UI_ESCAPE_HTML_BODY);
        assertEquals("true", escapeHtmlBody);

        createMocks();

        createAction();

        AnchorTag tag = createTag();

        // when
        tag.doStartTag();

        // then
        Anchor component = (Anchor) tag.getComponent();
        assertTrue(component.escapeHtmlBody());

        tag.doEndTag();
    }

    public void testTagAttributeTakesPrecedenceOverInjectEscapeHtmlBodyFlag() throws Exception {
        // given
        initDispatcherWithConfigs("struts-default.xml, struts-escape-body.xml");
        String escapeHtmlBody = container.getInstance(String.class, StrutsConstants.STRUTS_UI_ESCAPE_HTML_BODY);
        assertEquals("true", escapeHtmlBody);

        createMocks();

        createAction();

        AnchorTag tag = createTag();
        tag.setEscapeHtmlBody("false");

        // when
        tag.doStartTag();

        // then
        Anchor component = (Anchor) tag.getComponent();
        assertFalse(component.escapeHtmlBody());

        tag.doEndTag();
    }

}
