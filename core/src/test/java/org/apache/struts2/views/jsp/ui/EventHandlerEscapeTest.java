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

import org.apache.struts2.views.jsp.AbstractUITagTest;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.Tag;

public class EventHandlerEscapeTest extends AbstractUITagTest {

    private static final String PAYLOAD = "x\" autofocus onfocus=\"alert(1)";
    private static final String ESCAPED = "x&quot; autofocus onfocus=&quot;alert(1)";
    private static final String[] THEMES = {"simple", "xhtml", "css_xhtml", "html5"};

    public void testScriptingEventsAreEscapedInEveryTheme() throws Exception {
        for (String theme : THEMES) {
            TextFieldTag tag = new TextFieldTag();
            tag.setTheme(theme);
            tag.setName("foo");
            tag.setTitle(PAYLOAD);
            tag.setOnclick(PAYLOAD);
            tag.setOnchange("a && b < c");

            String out = render(tag);

            assertTrue(theme + ": " + out, out.contains("title=\"" + ESCAPED + "\""));
            assertTrue(theme + ": " + out, out.contains("onclick=\"" + ESCAPED + "\""));
            assertTrue(theme + ": " + out, out.contains("onchange=\"a &amp;&amp; b &lt; c\""));
        }
    }

    public void testLegitimateScriptingEventIsRendered() throws Exception {
        for (String theme : THEMES) {
            TextFieldTag tag = new TextFieldTag();
            tag.setTheme(theme);
            tag.setName("foo");
            tag.setOnclick("doIt();");

            assertTrue(theme, render(tag).contains("onclick=\"doIt();\""));
        }
    }

    public void testFormEventsAreEscapedInEveryTheme() throws Exception {
        for (String theme : THEMES) {
            FormTag tag = new FormTag();
            tag.setTheme(theme);
            tag.setOnsubmit(PAYLOAD);
            tag.setOnreset(PAYLOAD);

            String out = render(tag);

            assertTrue(theme + ": " + out, out.contains("onsubmit=\"" + ESCAPED + "\""));
            assertTrue(theme + ": " + out, out.contains("onreset=\"" + ESCAPED + "\""));
        }
    }

    public void testDoubleSelectEventsAreEscaped() throws Exception {
        DoubleSelectTag tag = new DoubleSelectTag();
        tag.setName("foo");
        tag.setDoubleName("bar");
        tag.setList("{'a'}");
        tag.setDoubleList("{'b'}");
        tag.setFormName("inputForm");
        tag.setDoubleOnclick(PAYLOAD);

        assertTrue(render(tag).contains("onclick=\"" + ESCAPED + "\""));
    }

    public void testOptionTransferSelectEventsAreEscaped() throws Exception {
        OptionTransferSelectTag tag = new OptionTransferSelectTag();
        tag.setName("foo");
        tag.setList("{'a'}");
        tag.setDoubleName("bar");
        tag.setDoubleList("{'b'}");
        tag.setDoubleOnclick(PAYLOAD);
        tag.setDoubleAccesskey(PAYLOAD);

        String out = render(tag);

        assertTrue(out, out.contains("onclick=\"" + ESCAPED + "\""));
        assertTrue(out, out.contains("accesskey=\"" + ESCAPED + "\""));
    }

    public void testJsTooltipIsEscaped() throws Exception {
        FormTag formTag = new FormTag();
        formTag.setTheme("xhtml");
        formTag.setPageContext(pageContext);
        formTag.setAction("testAction");

        TextFieldTag tag = new TextFieldTag();
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setName("foo");
        tag.setLabel("foo");
        tag.setTooltip(PAYLOAD);
        tag.setTooltipConfig("#{'jsTooltipEnabled':'true'}");

        formTag.doStartTag();
        tag.doStartTag();
        tag.doEndTag();
        formTag.doEndTag();

        String out = writer.toString();
        assertTrue(out, out.contains("'content', '" + ESCAPED + "'"));
    }

    private String render(Tag tag) throws JspException {
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();
        return writer.toString();
    }
}
