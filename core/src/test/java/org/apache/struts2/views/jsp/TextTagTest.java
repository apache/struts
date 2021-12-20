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

import com.mockobjects.servlet.MockJspWriter;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.TestAction;
import org.apache.struts2.components.Text;
import org.apache.struts2.views.jsp.ui.StrutsBodyContent;
import org.apache.struts2.views.jsp.ui.TestAction1;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertNotEquals;


/**
 * TextTagTest
 */
public class TextTagTest extends AbstractTagTest {

    private String fooValue = "org.apache.struts2.views.jsp.TextTagTest.fooValue";
    private TextTag tag;


    @Override
    public Action getAction() {
        TestAction action = new TestAction();
        action.setFoo(fooValue);

        return action;
    }

    public void testDefaultMessageOk() throws Exception {
        // NOTE:
        // simulate the condition
        // <s:text name="some.invalid.key">My Default Message</s:text>

        StrutsMockBodyContent mockBodyContent = new StrutsMockBodyContent(new MockJspWriter());
        mockBodyContent.setString("Sample Of Default Message");
        tag.setBodyContent(mockBodyContent);
        tag.setName("some.invalid.key.so.we.should.get.the.default.message");
        int startStatus = tag.doStartTag();
        tag.doEndTag();

        assertEquals(startStatus, BodyTag.EVAL_BODY_BUFFERED);
        assertEquals("Sample Of Default Message", writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDefaultMessageOk_clearTagStateSet() throws Exception {
        // NOTE:
        // simulate the condition
        // <s:text name="some.invalid.key">My Default Message</s:text>

        StrutsMockBodyContent mockBodyContent = new StrutsMockBodyContent(new MockJspWriter());
        mockBodyContent.setString("Sample Of Default Message");
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setBodyContent(mockBodyContent);
        tag.setName("some.invalid.key.so.we.should.get.the.default.message");
        int startStatus = tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(startStatus, BodyTag.EVAL_BODY_BUFFERED);
        assertEquals("Sample Of Default Message", writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testExpressionsEvaluated() throws Exception {
        String key = "expressionKey";
        String value = "Foo is " + fooValue;
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testExpressionsEvaluated_clearTagStateSet() throws Exception {
        String key = "expressionKey";
        String value = "Foo is " + fooValue;
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testCorrectI18NKey() throws Exception {
        String key = "foo.bar.baz";
        String value = "This should start with foo";
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testCorrectI18NKey_clearTagStateSet() throws Exception {
        String key = "foo.bar.baz";
        String value = "This should start with foo";
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testCorrectI18NKey2() throws Exception {
        String key = "bar.baz";
        String value = "No foo here";
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testCorrectI18NKey2_clearTagStateSet() throws Exception {
        String key = "bar.baz";
        String value = "No foo here";
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testMessageFormatWorks() throws Exception {
        String key = "messageFormatKey";
        String pattern = "Params are {0} {1} {2}";
        Object param1 = 12;
        Object param2 = new Date();
        Object param3 = "StringVal";
        List<Object> params = new ArrayList<>();
        params.add(param1);
        params.add(param2);
        params.add(param3);

        MessageFormat format = new MessageFormat(pattern, ActionContext.getContext().getLocale());
        String expected = format.format(params.toArray());

        tag.setName(key);
        tag.doStartTag();
        ((Text) tag.component).addParameter(param1);
        ((Text) tag.component).addParameter(param2);
        ((Text) tag.component).addParameter(param3);
        tag.doEndTag();
        assertEquals(expected, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testMessageFormatWorks_clearTagStateSet() throws Exception {
        String key = "messageFormatKey";
        String pattern = "Params are {0} {1} {2}";
        Object param1 = new Integer(12);
        Object param2 = new Date();
        Object param3 = "StringVal";
        List params = new ArrayList();
        params.add(param1);
        params.add(param2);
        params.add(param3);

        MessageFormat format = new MessageFormat(pattern, ActionContext.getContext().getLocale());
        String expected = format.format(params.toArray());

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        ((Text) tag.component).addParameter(param1);
        ((Text) tag.component).addParameter(param2);
        ((Text) tag.component).addParameter(param3);
        tag.doEndTag();
        assertEquals(expected, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleKeyValueWorks() throws JspException {
        String key = "simpleKey";
        String value = "Simple Message";
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleKeyValueWorks_clearTagStateSet() throws JspException {
        String key = "simpleKey";
        String value = "Simple Message";
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    private Locale getForeignLocale() {
        if (Locale.getDefault().getLanguage().equals("de")) {
            return Locale.FRANCE;
        } else {
            return Locale.GERMANY;
        }
    }

    private Locale getDefaultLocale() {
        if (Locale.getDefault().getLanguage().equals("de")) {
            return Locale.GERMANY;
        } else if (Locale.getDefault().getLanguage().equals("fr")) {
            return Locale.FRANCE;
        } else {
            return Locale.US;
        }
    }

    private String getLocalizedMessage(Locale locale) {
        if (locale.getLanguage().equals("de")) {
            return "This is TestBean1 in German";
        } else if (locale.getLanguage().equals("fr")) {
            return "This is TestBean1 in French";
        } else {
            return "This is TestBean1";
        }
    }

    public void testTextTagUsesValueStackInRequestNotActionContext() throws JspException {
        String key = "simpleKey";
        String value1 = "Simple Message";
        Locale foreignLocale = getForeignLocale();
        String value2 = getLocalizedMessage(foreignLocale);
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value1, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

        final StringBuffer buffer = writer.getBuffer();
        buffer.delete(0, buffer.length());
        ValueStack newStack = container.getInstance(ValueStackFactory.class).createValueStack();
        newStack.getActionContext().withLocale(foreignLocale).withContainer(container);
        newStack.push(container.inject(TestAction1.class));
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, newStack);
        assertNotSame(ActionContext.getContext().getValueStack().peek(), newStack.peek());
        tag.setName(key);  // Required as WW-5124 fix clears tag state.
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value2, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testTextTagUsesValueStackInRequestNotActionContext_clearTagStateSet() throws JspException {
        String key = "simpleKey";
        String value1 = "Simple Message";
        Locale foreignLocale = getForeignLocale();
        String value2 = getLocalizedMessage(foreignLocale);
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value1, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

        final StringBuffer buffer = writer.getBuffer();
        buffer.delete(0, buffer.length());
        ValueStack newStack = container.getInstance(ValueStackFactory.class).createValueStack();
        newStack.getContext().put(ActionContext.LOCALE, foreignLocale);
        newStack.getContext().put(ActionContext.CONTAINER, container);
        newStack.push(container.inject(TestAction1.class));
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, newStack);
        assertNotSame(ActionContext.getContext().getValueStack().peek(), newStack.peek());
        tag.setName(key);  // Required as WW-5124 fix clears tag state.
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value2, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testTextTagUsesLocaleFromValueStack() throws JspException {
        stack.pop();
        stack.push(container.inject(TestAction1.class));

        Locale defaultLocale = getDefaultLocale();
        Locale foreignLocale = getForeignLocale();
        assertNotSame(defaultLocale, foreignLocale);

        ActionContext.getContext().withLocale(defaultLocale);
        String key = "simpleKey";
        String value_default = getLocalizedMessage(defaultLocale);
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value_default, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

        final StringBuffer buffer = writer.getBuffer();
        buffer.delete(0, buffer.length());
        String value_int = getLocalizedMessage(foreignLocale);
        assertNotEquals(value_default, value_int);
        ValueStack newStack = container.getInstance(ValueStackFactory.class).createValueStack(stack);
        newStack.getActionContext().withLocale(foreignLocale).withContainer(container);
        assertNotSame(newStack.getActionContext().getLocale(), ActionContext.getContext().getLocale());
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, newStack);
        assertEquals(ActionContext.getContext().getValueStack().peek(), newStack.peek());
        tag.setName(key);  // Required as WW-5124 fix clears tag state.
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value_int, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testTextTagUsesLocaleFromValueStack_clearTagStateSet() throws JspException {
        stack.pop();
        stack.push(container.inject(TestAction1.class));

        Locale defaultLocale = getDefaultLocale();
        Locale foreignLocale = getForeignLocale();
        assertNotSame(defaultLocale, foreignLocale);

        ActionContext.getContext().setLocale(defaultLocale);
        String key = "simpleKey";
        String value_default = getLocalizedMessage(defaultLocale);
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value_default, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

        final StringBuffer buffer = writer.getBuffer();
        buffer.delete(0, buffer.length());
        String value_int = getLocalizedMessage(foreignLocale);
        assertFalse(value_default.equals(value_int));
        ValueStack newStack = container.getInstance(ValueStackFactory.class).createValueStack(stack);
        newStack.getContext().put(ActionContext.LOCALE, foreignLocale);
        newStack.getContext().put(ActionContext.CONTAINER, container);
        assertNotSame(newStack.getContext().get(ActionContext.LOCALE), ActionContext.getContext().getLocale());
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, newStack);
        assertEquals(ActionContext.getContext().getValueStack().peek(), newStack.peek());
        tag.setName(key);  // Required as WW-5124 fix clears tag state.
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value_int, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testWithNoMessageAndBodyIsNotEmptyBodyIsReturned() throws Exception {
        final String key = "key.does.not.exist";
        final String bodyText = "body text";
        tag.setName(key);

        StrutsBodyContent bodyContent = new StrutsBodyContent(null);
        bodyContent.print(bodyText);
        tag.setBodyContent(bodyContent);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(bodyText, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testWithNoMessageAndBodyIsNotEmptyBodyIsReturned_clearTagStateSet() throws Exception {
        final String key = "key.does.not.exist";
        final String bodyText = "body text";
        tag.setName(key);

        StrutsBodyContent bodyContent = new StrutsBodyContent(null);
        bodyContent.print(bodyText);
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setBodyContent(bodyContent);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(bodyText, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testWithNoMessageAndNoDefaultKeyReturned() throws JspException {
        final String key = "key.does.not.exist";
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(key, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testWithNoMessageAndNoDefaultKeyReturned_clearTagStateSet() throws JspException {
        final String key = "key.does.not.exist";
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(key, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testNoNameDefined() throws Exception {
        String msg = "tag 'text', field 'name': You must specify the i18n key. Example: welcome.header";
        try {
            tag.doStartTag();
            tag.doEndTag();
            fail("Should have thrown a RuntimeException");
        } catch (StrutsException e) {
            assertEquals(msg, e.getMessage());
        }

        // The doEndTag() call is expected not to complete.  Cannot perform basic sanity check of clearTagStateForTagPoolingServers() behaviour.
    }

    public void testBlankNameDefined() throws Exception {
        tag.setName("");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("", writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testBlankNameDefined_clearTagStateSet()  throws Exception {
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName("");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals("", writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testPutId() throws Exception {
        assertNull(stack.findString("myId")); // nothing in stack
        tag.setVar("myId");
        tag.setName("bar.baz");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("", writer.toString());
        assertEquals("No foo here", stack.findString("myId")); // is in stack now

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testPutId_clearTagStateSet()  throws Exception {
        assertEquals(null, stack.findString("myId")); // nothing in stack
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setVar("myId");
        tag.setName("bar.baz");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals("", writer.toString());
        assertEquals("No foo here", stack.findString("myId")); // is in stack now

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
         freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testEscapeHtml() throws Exception {
        final String key = "foo.escape.html";
        final String value = "1 &lt; 2";
        tag.setName(key);
        tag.setEscapeHtml(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testEscapeHtml_clearTagStateSet() throws Exception {
        final String key = "foo.escape.html";
        final String value = "1 &lt; 2";
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.setEscapeHtml(true);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testEscapeXml() throws Exception {
        final String key = "foo.escape.xml";
        final String value = "&lt;&gt;&apos;&quot;&amp;";
        tag.setName(key);
        tag.setEscapeXml(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testEscapeXml_clearTagStateSet() throws Exception {
        final String key = "foo.escape.xml";
        final String value = "&lt;&gt;&apos;&quot;&amp;";
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.setEscapeXml(true);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testEscapeJavaScript() throws Exception {
        final String key = "foo.escape.javascript";
        final String value = "\\t\\b\\n\\f\\r\\\"\\'\\/\\\\";
        tag.setName(key);
        tag.setEscapeJavaScript(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testEscapeJavaScript_clearTagStateSet() throws Exception {
        final String key = "foo.escape.javascript";
        final String value = "\\t\\b\\n\\f\\r\\\"\\\'\\/\\\\";
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.setEscapeJavaScript(true);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testEscapeCsv() throws Exception {
        final String key = "foo.escape.csv";
        final String value = "\"something,\"\",\"\"\"";
        tag.setName(key);
        tag.setEscapeCsv(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testEscapeCsv_clearTagStateSet() throws Exception {
        final String key = "foo.escape.csv";
        final String value = "\"something,\"\",\"\"\"";
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setName(key);
        tag.setEscapeCsv(true);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(value, writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextTag freshTag = new TextTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * todo remove ActionContext set after LocalizedTextUtil is fixed to not use ThreadLocal
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tag = new TextTag();
        tag.setPageContext(pageContext);
        ActionContext.of(stack.getContext()).bind();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
