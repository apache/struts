/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.TestAction;
import com.opensymphony.webwork.components.Text;
import com.opensymphony.webwork.views.jsp.ui.TestAction1;
import com.opensymphony.webwork.views.jsp.ui.WebWorkBodyContent;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.jsp.JspException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * TextTagTest
 *
 * @author jcarreira
 * @author Rainer Hermanns
 */
public class TextTagTest extends AbstractTagTest {

    private String fooValue = "com.opensymphony.webwork.views.jsp.TextTagTest.fooValue";
    private TextTag tag;


    public Action getAction() {
        TestAction action = new TestAction();
        action.setFoo(fooValue);

        return action;
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

        String expected = MessageFormat.format(pattern, params.toArray());
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
        OgnlValueStack newStack = new OgnlValueStack();
        newStack.getContext().put(ActionContext.LOCALE, foreignLocale);
        newStack.push(new TestAction1());
        request.setAttribute(ServletActionContext.WEBWORK_VALUESTACK_KEY, newStack);
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
        OgnlValueStack newStack = new OgnlValueStack(stack);
        newStack.getContext().put(ActionContext.LOCALE, foreignLocale);
        assertNotSame(newStack.getContext().get(ActionContext.LOCALE), ActionContext.getContext().getLocale());
        request.setAttribute(ServletActionContext.WEBWORK_VALUESTACK_KEY, newStack);
        assertEquals(ActionContext.getContext().getValueStack().peek(), newStack.peek());
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(value_int, writer.toString());
    }

    public void testWithNoMessageAndBodyIsNotEmptyBodyIsReturned() throws Exception {
        final String key = "key.does.not.exist";
        final String bodyText = "body text";
        tag.setName(key);

        WebWorkBodyContent bodyContent = new WebWorkBodyContent(null);
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
        String msg = "tag text, field name: You must specify the i18n key. Example: welcome.header";
        try {
            tag.doStartTag();
            tag.doEndTag();
            fail("Should have thrown a RuntimeException");
        } catch (RuntimeException e) {
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
        OgnlValueStack valueStack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(valueStack.getContext()));
    }
}
