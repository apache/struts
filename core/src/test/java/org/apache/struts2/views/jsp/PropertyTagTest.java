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

import java.util.HashMap;

import javax.servlet.jsp.JspException;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;

import com.mockobjects.servlet.MockJspWriter;
import com.mockobjects.servlet.MockPageContext;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * PropertyTag test case.
 *
 */
public class PropertyTagTest extends StrutsInternalTestCase {

    StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
    ValueStack stack;


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
        initDispatcher(new HashMap() {{ put(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");}});

        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("Foo is: tm_jee");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        // test
        {
            PropertyTag tag = new PropertyTag();
            tag.setPageContext(pageContext);
            tag.setValue("%{formatTitle()}");
            tag.doStartTag();
            tag.doEndTag();
        }

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }

    public void testEscapeJavaScript() throws Exception {
        // setups
        initDispatcher(new HashMap() {{ put(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");}});

        Foo foo = new Foo();
        foo.setTitle("\t\b\n\f\r\"\'/\\");
        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("Foo is: \\t\\b\\n\\f\\r\\\"\\\'\\/\\\\");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setEscape(false);
        tag.setEscapeJavaScript(true);    
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        tag.doEndTag();}

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }

     public void testEscapeXml() throws Exception {
        // setups
        initDispatcher(new HashMap() {{ put(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");}});

        Foo foo = new Foo();
        foo.setTitle("<>'\"&");
        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("Foo is: &lt;&gt;&apos;&quot;&amp;");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setEscape(false);
        tag.setEscapeXml(true);
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        tag.doEndTag();}

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }

     public void testEscapeCsv() throws Exception {
        // setups
        initDispatcher(new HashMap() {{ put(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");}});

        Foo foo = new Foo();
        foo.setTitle("\"something,\",\"");
        stack.push(foo);

        MockJspWriter jspWriter = new MockJspWriter();
        jspWriter.setExpectedData("\"Foo is: \"\"something,\"\",\"\"\"");

        MockPageContext pageContext = new MockPageContext();
        pageContext.setJspWriter(jspWriter);
        pageContext.setRequest(request);

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setEscape(false);
        tag.setEscapeCsv(true);
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        tag.doEndTag();}

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }

    public void testWithAltSyntax2() throws Exception {
        // setups
        initDispatcher(new HashMap() {{ put(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");}});

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
        tag.setValue("formatTitle()");
        tag.doStartTag();
        tag.doEndTag();}

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }

    public void testWithoutAltSyntax1() throws Exception {
        //      setups
        initDispatcher(new HashMap() {{ put(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");}});

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
        tag.setValue("formatTitle()");
        tag.doStartTag();
        tag.doEndTag();}

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }


    public void testWithoutAltSyntax2() throws Exception {
        //      setups
        initDispatcher(new HashMap() {{ put(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");}});

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
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        tag.doEndTag();}

        // verify test
        request.verify();
        jspWriter.verify();
        pageContext.verify();
    }


    protected void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
    }


    public class Foo {
        private String title;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public String formatTitle() {
            return "Foo is: " + title;
        }

        public String toString() {
            return formatTitle();
        }
    }
}
