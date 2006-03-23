/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.mockobjects.servlet.MockJspWriter;
import com.mockobjects.servlet.MockPageContext;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.WebWorkTestCase;
import com.opensymphony.webwork.WebWorkConstants;
import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.jsp.JspException;


/**
 * PropertyTag test case.
 * 
 * @author $Author: plightbo $
 * @version $Revision: 1.13 $
 */
public class PropertyTagTest extends WebWorkTestCase {

    WebWorkMockHttpServletRequest request = new WebWorkMockHttpServletRequest();
    OgnlValueStack stack = new OgnlValueStack();


    public void testDefaultValue() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();

        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("TEST");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        tag.setPageContext(pageContext);
        tag.setValue("title");
        tag.setDefault("TEST");

        try {
            tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }

    public void testNull() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();

        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        tag.setPageContext(pageContext);
        tag.setValue("title");

        try {
            tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }

    public void testSimple() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();
        foo.setTitle("test");

        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("test");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        tag.setPageContext(pageContext);
        tag.setValue("title");

        try {
            tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }

    public void testTopOfStack() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();
        foo.setTitle("test");

        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("Foo is: test");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        tag.setPageContext(pageContext);

        try {
            tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }


    public void testWithAltSyntax1() throws Exception {
        // setups
        Configuration.set(WebWorkConstants.WEBWORK_TAG_ALTSYNTAX, "true");
        assertEquals(Configuration.get(WebWorkConstants.WEBWORK_TAG_ALTSYNTAX), "true");

        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("Foo is: tm_jee");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPageContext(pageContext);
        tag.setValue("%{toString()}");
        tag.doStartTag();
        tag.doEndTag();}

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }

    public void testWithAltSyntax2() throws Exception {
        // setups
        Configuration.set(WebWorkConstants.WEBWORK_TAG_ALTSYNTAX, "true");
        assertEquals(Configuration.get(WebWorkConstants.WEBWORK_TAG_ALTSYNTAX), "true");

        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("Foo is: tm_jee");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPageContext(pageContext);
        tag.setValue("toString()");
        tag.doStartTag();
        tag.doEndTag();}

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }

    public void testWithoutAltSyntax1() throws Exception {
        //      setups
        Configuration.set(WebWorkConstants.WEBWORK_TAG_ALTSYNTAX, "false");
        assertEquals(Configuration.get(WebWorkConstants.WEBWORK_TAG_ALTSYNTAX), "false");

        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("Foo is: tm_jee");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPageContext(pageContext);
        tag.setValue("toString()");
        tag.doStartTag();
        tag.doEndTag();}

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }


    public void testWithoutAltSyntax2() throws Exception {
        //      setups
        Configuration.set(WebWorkConstants.WEBWORK_TAG_ALTSYNTAX, "false");
        assertEquals(Configuration.get(WebWorkConstants.WEBWORK_TAG_ALTSYNTAX), "false");

        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPageContext(pageContext);
        tag.setValue("%{toString()}");
        tag.doStartTag();
        tag.doEndTag();}

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }


    protected void setUp() throws Exception {
        super.setUp();
        ActionContext.getContext().setValueStack(stack);
        request.setAttribute(ServletActionContext.WEBWORK_VALUESTACK_KEY, stack);
    }


    public class Foo {
        private String title;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public String toString() {
            return "Foo is: " + title;
        }
    }
}
