/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp;

import javax.servlet.jsp.JspWriter;

/**
 * Unit test for {@link URLTag}.
 *
 * @author Brock Bulger (brockman_bulger@hotmail.com)
 * @version $Date: 2006/03/19 12:50:49 $ $Id: URLTagTest.java,v 1.19 2006/03/19 12:50:49 davsclaus Exp $
 */
public class URLTagTest extends AbstractUITagTest {

    private URLTag tag;

    public void testActionURL() throws Exception {
        tag.setValue("TestAction.action");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals("TestAction.action", writer.toString());
    }

    public void testAddParameters() throws Exception {
        request.setAttribute("struts.request_uri", "/Test.action");

        request.setAttribute("struts.request_uri", "/TestAction.action");
        request.setQueryString("param0=value0");

        tag.doStartTag();
        tag.component.addParameter("param1", "value1");
        tag.component.addParameter("param2", "value2");
        tag.doEndTag();
        assertEquals("/TestAction.action?param2=value2&amp;param0=value0&amp;param1=value1", writer.toString());
    }

    public void testEvaluateValue() throws Exception {
        Foo foo = new Foo();
        foo.setTitle("test");
        stack.push(foo);
        tag.setValue("%{title}");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals("test", writer.toString());
    }

    public void testHttps() throws Exception {
        request.setScheme("https");
        request.setServerName("localhost");
        request.setServerPort(443);

        tag.setValue("list-members.action");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals("list-members.action", writer.toString());
    }

    public void testAnchor() throws Exception {
        request.setScheme("https");
        request.setServerName("localhost");
        request.setServerPort(443);

        tag.setValue("list-members.action");
        tag.setAnchor("test");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals("list-members.action#test", writer.toString());
    }
    
    public void testParamPrecedence() throws Exception {
    	request.setRequestURI("/context/someAction.action");
    	request.setQueryString("id=22&name=John");
    	
    	URLTag urlTag = new URLTag();
    	urlTag.setPageContext(pageContext);
    	urlTag.setIncludeParams("get");
    	urlTag.setEncode("%{false}");
    	
    	ParamTag paramTag = new ParamTag();
    	paramTag.setPageContext(pageContext);
    	paramTag.setName("id");
    	paramTag.setValue("%{'33'}");
    	
    	urlTag.doStartTag();
    	paramTag.doStartTag();
    	paramTag.doEndTag();
    	urlTag.doEndTag();
    	
    	assertEquals(writer.getBuffer().toString(), "/context/someAction.action?name=John&amp;id=33");
    }

    public void testParamPrecedenceWithAnchor() throws Exception {
    	request.setRequestURI("/context/someAction.action");
    	request.setQueryString("id=22&name=John");

    	URLTag urlTag = new URLTag();
    	urlTag.setPageContext(pageContext);
    	urlTag.setIncludeParams("get");
    	urlTag.setEncode("%{false}");
        urlTag.setAnchor("testAnchor");

        ParamTag paramTag = new ParamTag();
    	paramTag.setPageContext(pageContext);
    	paramTag.setName("id");
    	paramTag.setValue("%{'33'}");

    	urlTag.doStartTag();
    	paramTag.doStartTag();
    	paramTag.doEndTag();
    	urlTag.doEndTag();

    	assertEquals(writer.getBuffer().toString(), "/context/someAction.action?name=John&amp;id=33#testAnchor");
    }

    public void testPutId() throws Exception {
        tag.setValue("/public/about");
        assertEquals(null, stack.findString("myId")); // nothing in stack
        tag.setId("myId");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("", writer.toString());
        assertEquals("/public/about", stack.findString("myId")); // is in stack now
    }
    
    public void testUsingValueOnly() throws Exception {
        tag.setValue("/public/about/team.jsp");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("/public/about/team.jsp", writer.toString());
    }

    public void testRequestURIActionIncludeNone() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("none");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/team.action", writer.toString());
    }

    public void testRequestURIActionIncludeGet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/team.action?section=team&amp;company=acme+inc", writer.toString());
    }

    public void testRequestURINoActionIncludeNone() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction(null);
        tag.setIncludeParams("none");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/public/about", writer.toString());
    }

    public void testNoActionIncludeGet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction(null);
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/public/about?section=team&amp;company=acme+inc", writer.toString());
    }

    public void testRequestURIActionIncludeAll() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("all");

        tag.doStartTag();

        // include nested param tag
        ParamTag paramTag = new ParamTag();
        paramTag.setPageContext(pageContext);
        paramTag.setName("year");
        paramTag.setValue("2006");
        paramTag.doStartTag();
        paramTag.doEndTag();

        tag.doEndTag();

        assertEquals("/team.action?section=team&amp;year=2006&amp;company=acme+inc", writer.toString());
    }

    public void testRequestURINoActionIncludeAll() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction(null);
        tag.setIncludeParams("all");

        tag.doStartTag();

        // include nested param tag
        ParamTag paramTag = new ParamTag();
        paramTag.setPageContext(pageContext);
        paramTag.setName("year");
        paramTag.setValue("2006");
        paramTag.doStartTag();
        paramTag.doEndTag();

        tag.doEndTag();

        assertEquals("/public/about?section=team&amp;year=2006&amp;company=acme+inc", writer.toString());
    }

    public void testUnknownIncludeParam() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team");

        tag.setIncludeParams("unknown"); // will log at WARN level
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("/public/about", writer.toString()); // should not add any request parameters
    }

    public void testRequestURIWithAnchor() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("company=acme inc#canada");

        tag.setAction("company");
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/company.action?company=acme+inc", writer.toString()); // will always chop anchor if using requestURI
    }

    public void testIncludeContext() throws Exception {
        request.setupGetContext("/myapp");

        tag.setIncludeContext("true");
        tag.setAction("company");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/myapp/company.action", writer.toString());
    }

    protected void setUp() throws Exception {
        super.setUp();

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);

        tag = new URLTag();
        tag.setPageContext(pageContext);
        JspWriter jspWriter = new StrutsMockJspWriter(writer);
        pageContext.setJspWriter(jspWriter);
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
