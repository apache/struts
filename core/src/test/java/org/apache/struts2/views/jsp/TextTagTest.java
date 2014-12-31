/*
 * $Id$
 *
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.TestAction;
import org.apache.struts2.components.Text;
import org.apache.struts2.views.jsp.ui.StrutsBodyContent;
import org.apache.struts2.views.jsp.ui.TestAction1;

import com.mockobjects.servlet.MockJspWriter;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;


/**
 * TextTagTest
 *
 */
public class TextTagTest extends AbstractTagTest {

    private String fooValue = "org.apache.struts2.views.jsp.TextTagTest.fooValue";
    private TextTag tag;


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
    }

    public void testExpressionsEvaluated() throws Exception {
        String key = "expressionKey";
        String value = "Foo is " + fooValue;
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());
    }

    public void testCorrectI18NKey() throws Exception {
        String key = "foo.bar.baz";
        String value = "This should start with foo";
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());
    }

    public void testCorrectI18NKey2() throws Exception {
        String key = "bar.baz";
        String value = "No foo here";
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());
    }

    public void testMessageFormatWorks() throws Exception {
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

        tag.setName(key);
        tag.doStartTag();
        ((Text) tag.component).addParameter(param1);
        ((Text) tag.component).addParameter(param2);
        ((Text) tag.component).addParameter(param3);
        tag.doEndTag();
        assertEquals(expected, writer.toString());
    }

    public void testSimpleKeyValueWorks() throws JspException {
        String key = "simpleKey";
        String value = "Simple Message";
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value, writer.toString());
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
        final StringBuffer buffer = writer.getBuffer();
        buffer.delete(0, buffer.length());
        ValueStack newStack = container.getInstance(ValueStackFactory.class).createValueStack();
        newStack.getContext().put(ActionContext.LOCALE, foreignLocale);
        newStack.getContext().put(ActionContext.CONTAINER, container);
        newStack.push(new TestAction1());
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, newStack);
        assertNotSame(ActionContext.getContext().getValueStack().peek(), newStack.peek());

        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value2, writer.toString());
    }

    public void testTextTagUsesLocaleFromValueStack() throws JspException {
        stack.pop();
        stack.push(new TestAction1());

        Locale defaultLocale = getDefaultLocale();
        Locale foreignLocale = getForeignLocale();
        assertNotSame(defaultLocale, foreignLocale);

        ActionContext.getContext().setLocale(defaultLocale);
        String key = "simpleKey";
        String value_default = getLocalizedMessage(defaultLocale);
        tag.setName(key);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value_default, writer.toString());

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
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value_int, writer.toString());
    }

     public void testTextTagSearchesStackByDefault() throws JspException {
        String key = "result";

        tag.setName(key);
        final StringBuffer buffer = writer.getBuffer();
        buffer.delete(0, buffer.length());
        ValueStack newStack = container.getInstance(ValueStackFactory.class).createValueStack();
        newStack.getContext().put(ActionContext.CONTAINER, container);
        TestAction testAction = new TestAction();
        testAction.setResult("bar");
        newStack.push(testAction);
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, newStack);


        tag.doStartTag();
        tag.doEndTag();
        assertEquals("bar", writer.toString());
    }

    public void testTextTagDoNotSearchStack() throws JspException {
        String key = "result";

        tag.setName(key);
        tag.setSearchValueStack("false");
        final StringBuffer buffer = writer.getBuffer();
        buffer.delete(0, buffer.length());
        ValueStack newStack = container.getInstance(ValueStackFactory.class).createValueStack();
        newStack.getContext().put(ActionContext.CONTAINER, container);
        TestAction testAction = new TestAction();
        testAction.setResult("bar");
        newStack.push(testAction);
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, newStack);


        tag.doStartTag();
        tag.doEndTag();
        assertEquals("result", writer.toString());
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
    }

    public void testWithNoMessageAndNoDefaultKeyReturned() throws JspException {
        final String key = "key.does.not.exist";
        tag.setName("'" + key + "'");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(key, writer.toString());
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
    }

    public void testBlankNameDefined() throws Exception {
        tag.setName("");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("", writer.toString());
    }

    public void testPutId() throws Exception {
        assertEquals(null, stack.findString("myId")); // nothing in stack
        tag.setId("myId");
        tag.setName("bar.baz");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("", writer.toString());
        assertEquals("No foo here", stack.findString("myId")); // is in stack now
    }

    /**
     * todo remove ActionContext set after LocalizedTextUtil is fixed to not use ThreadLocal
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        tag = new TextTag();
        tag.setPageContext(pageContext);
        ActionContext.setContext(new ActionContext(stack.getContext()));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
