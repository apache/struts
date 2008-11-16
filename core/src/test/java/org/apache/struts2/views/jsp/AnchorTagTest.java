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

import java.io.StringWriter;

import javax.servlet.jsp.JspWriter;

import org.apache.struts2.views.jsp.ui.AnchorTag;
import org.apache.struts2.views.jsp.ui.StrutsBodyContent;


/**
 * Unit test for {@ link AnchorTag}.
 */
public class AnchorTagTest extends AbstractUITagTest {
    private StringWriter writer = new StringWriter();
    private AnchorTag tag;

    protected void setUp() throws Exception {
        super.setUp();

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);

        tag = new AnchorTag();
        tag.setPageContext(pageContext);
        JspWriter jspWriter = new StrutsMockJspWriter(writer);
        pageContext.setJspWriter(jspWriter);
    }

    public void testActionURL() throws Exception {
        tag.setHref("TestAction.action");
        tag.doStartTag();
        tag.doEndTag();
        assertTrue(writer.toString().indexOf("href=\"TestAction.action\"") > -1);
        assertEquals("<a href=\"TestAction.action\"></a>", writer.toString());
    }

    public void testNoNewLineAtEnd() throws Exception {
        tag.setHref("TestAction.action");
        tag.doStartTag();
        tag.doEndTag();
        assertFalse(writer.toString().endsWith("\n"));
    }

    public void testAccessKey() throws Exception {
        tag.setHref("TestAction.action");
        tag.setAccesskey("T");
        tag.doStartTag();
        tag.doEndTag();
        assertTrue(writer.toString().indexOf("accesskey=\"T\"") > -1);
        assertFalse(writer.toString().endsWith("\n"));
    }

    public void testId() throws Exception {
        tag.setId("home&improvements");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("<a id=\"home&amp;improvements\"></a>", writer.toString());
        assertFalse(writer.toString().endsWith("\n"));
    }

    public void testTitle() throws Exception {
    	tag.setHref("home.ftl");
        tag.setTitle("home & improvements");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("<a href=\"home.ftl\" title=\"home &amp; improvements\"></a>", writer.toString());
        assertFalse(writer.toString().endsWith("\n"));
    }

    public void testOnMouseOver() throws Exception {
        tag.setHref("TestAction.action");
        tag.setOnmouseover("over");
        tag.doStartTag();
        tag.doEndTag();
        assertTrue(writer.toString().indexOf("onmouseover=\"over\"") > -1);
        assertFalse(writer.toString().endsWith("\n"));
    }

    public void testOnMouseOverAndFocus() throws Exception {
        tag.setHref("TestAction.action");
        tag.setOnmouseover("overme");
        tag.setOnfocus("focusme");
        tag.doStartTag();
        tag.doEndTag();
        assertTrue(writer.toString().indexOf("onmouseover=\"overme\"") > -1);
        assertTrue(writer.toString().indexOf("onfocus=\"focusme\"") > -1);
        assertFalse(writer.toString().endsWith("\n"));
    }

    public void testWithContent() throws Exception {
        tag.setHref("TestAction.action");
        tag.doStartTag();
        writer.write("Home");       
        tag.doEndTag();
        assertEquals("<a href=\"TestAction.action\">Home</a>", writer.toString());
        assertFalse(writer.toString().endsWith("\n"));
    }

    public void testAddParameters() throws Exception {
        tag.setHref("/TestAction.action");
        String bodyText = "<img src=\"#\"/>";
        StrutsBodyContent bodyContent = new StrutsBodyContent(null);
        bodyContent.print(bodyText);
        tag.setBodyContent(bodyContent);

        tag.doStartTag();
        tag.doEndTag();
    }

}
