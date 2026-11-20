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
            assertFalse(theme + ": " + out, out.contains("title=\"" + PAYLOAD + "\""));
            assertTrue(theme + ": " + out, out.contains("onclick=\"" + ESCAPED + "\""));
            assertFalse(theme + ": " + out, out.contains("onclick=\"" + PAYLOAD + "\""));
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
            assertFalse(theme + ": " + out, out.contains("onsubmit=\"" + PAYLOAD + "\""));
            assertFalse(theme + ": " + out, out.contains("onreset=\"" + PAYLOAD + "\""));
            assertEquals(theme + ": " + out, 1, countOccurrences(out, "onreset="));
        }
    }

    public void testFormRendersSingleOnresetInEveryTheme() throws Exception {
        for (String theme : THEMES) {
            for (boolean validate : new boolean[]{true, false}) {
                FormTag withHandler = new FormTag();
                withHandler.setTheme(theme);
                withHandler.setValidate(String.valueOf(validate));
                withHandler.setOnreset("doIt();");
                String out = render(withHandler);
                assertEquals(theme + " validate=" + validate + ": " + out, 1, countOccurrences(out, "onreset="));
                assertTrue(theme + " validate=" + validate + ": " + out, out.contains("onreset=\"doIt();\""));

                FormTag withoutHandler = new FormTag();
                withoutHandler.setTheme(theme);
                withoutHandler.setValidate(String.valueOf(validate));
                out = render(withoutHandler);
                boolean xhtmlDefault = validate && (theme.equals("xhtml") || theme.equals("css_xhtml"));
                assertEquals(theme + " validate=" + validate + ": " + out, xhtmlDefault ? 1 : 0, countOccurrences(out, "onreset="));
                assertEquals(theme + " validate=" + validate + ": " + out, xhtmlDefault,
                        out.contains("onreset=\"clearErrorMessages(this);clearErrorLabels(this);\""));
            }
        }
    }

    public void testAlreadyEncodedScriptingEventsAreEncodedAgain() throws Exception {
        for (String theme : THEMES) {
            TextFieldTag tag = new TextFieldTag();
            tag.setTheme(theme);
            tag.setName("foo");
            tag.setOnclick("if (a &amp;&amp; b) go()");
            tag.setOnchange("show('O&#39;Brien')");

            String out = render(tag);

            assertTrue(theme + ": " + out, out.contains("onclick=\"if (a &amp;amp;&amp;amp; b) go()\""));
            assertTrue(theme + ": " + out, out.contains("onchange=\"show(&#39;O&amp;#39;Brien&#39;)\""));
        }
    }

    public void testAlreadyEncodedFormEventsAreEncodedAgain() throws Exception {
        for (String theme : THEMES) {
            FormTag tag = new FormTag();
            tag.setTheme(theme);
            tag.setOnsubmit("if (a &amp;&amp; b) go()");
            tag.setOnreset("if (a &amp;&amp; b) go()");

            String out = render(tag);

            assertTrue(theme + ": " + out, out.contains("onsubmit=\"if (a &amp;amp;&amp;amp; b) go()\""));
            assertTrue(theme + ": " + out, out.contains("onreset=\"if (a &amp;amp;&amp;amp; b) go()\""));
        }
    }

    public void testAlreadyEncodedDoubleSelectEventsAreEncodedAgain() throws Exception {
        DoubleSelectTag tag = new DoubleSelectTag();
        tag.setName("foo");
        tag.setDoubleName("bar");
        tag.setList("{'a'}");
        tag.setDoubleList("{'b'}");
        tag.setFormName("inputForm");
        tag.setDoubleOnclick("if (a &amp;&amp; b) go()");

        String out = render(tag);

        assertTrue(out, out.contains("onclick=\"if (a &amp;amp;&amp;amp; b) go()\""));
    }

    public void testAlreadyEncodedOptionTransferSelectEventsAreEncodedAgain() throws Exception {
        OptionTransferSelectTag tag = new OptionTransferSelectTag();
        tag.setName("foo");
        tag.setList("{'a'}");
        tag.setDoubleName("bar");
        tag.setDoubleList("{'b'}");
        tag.setDoubleOnclick("if (a &amp;&amp; b) go()");

        String out = render(tag);

        assertTrue(out, out.contains("onclick=\"if (a &amp;amp;&amp;amp; b) go()\""));
    }

    public void testDoubleSelectEventsAreEscaped() throws Exception {
        DoubleSelectTag tag = new DoubleSelectTag();
        tag.setName("foo");
        tag.setDoubleName("bar");
        tag.setList("{'a'}");
        tag.setDoubleList("{'b'}");
        tag.setFormName("inputForm");
        tag.setDoubleOnclick(PAYLOAD);

        String out = render(tag);

        assertTrue(out, out.contains("onclick=\"" + ESCAPED + "\""));
        assertFalse(out, out.contains("onclick=\"" + PAYLOAD + "\""));
    }

    public void testOptionTransferSelectEventsAreEscaped() throws Exception {
        OptionTransferSelectTag tag = new OptionTransferSelectTag();
        tag.setName("foo");
        tag.setList("{'a'}");
        tag.setDoubleName("bar");
        tag.setDoubleList("{'b'}");
        tag.setDoubleOnclick(PAYLOAD);
        tag.setDoubleOnkeydown(PAYLOAD);
        tag.setDoubleAccesskey(PAYLOAD);

        String out = render(tag);

        assertTrue(out, out.contains("onclick=\"" + ESCAPED + "\""));
        assertTrue(out, out.contains("onkeydown=\"" + ESCAPED + "\""));
        assertTrue(out, out.contains("accesskey=\"" + ESCAPED + "\""));
        assertFalse(out, out.contains("onclick=\"" + PAYLOAD + "\""));
        assertFalse(out, out.contains("onkeydown=\"" + PAYLOAD + "\""));
        assertFalse(out, out.contains("accesskey=\"" + PAYLOAD + "\""));
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
        // ?js_string backslash-escapes the quote before the HTML entity encoding applies on top.
        assertTrue(out, out.contains("'content', 'x\\&quot; autofocus onfocus=\\&quot;alert(1)'"));
    }

    public void testJsTooltipApostropheCannotBreakOutOfTheJsString() throws Exception {
        String payload = "it's <script>alert(1)</script>";

        FormTag formTag = new FormTag();
        formTag.setTheme("xhtml");
        formTag.setPageContext(pageContext);
        formTag.setAction("testAction");

        TextFieldTag tag = new TextFieldTag();
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setName("foo");
        tag.setLabel("foo");
        tag.setTooltip(payload);
        tag.setTooltipConfig("#{'jsTooltipEnabled':'true'}");

        formTag.doStartTag();
        tag.doStartTag();
        tag.doEndTag();
        formTag.doEndTag();

        String out = writer.toString();
        // The raw apostrophe must not reach the markup unescaped: that would close the
        // JS string literal early and let the rest run as script on mouseover.
        assertFalse(out, out.contains("'it's"));
        assertTrue(out, out.contains("'content', 'it\\&#39;s &lt;script&gt;alert(1)&lt;\\/script&gt;'"));
    }

    public void testJsTooltipDelayApostropheCannotBreakOutOfTheJsString() throws Exception {
        String payload = "1000'+alert(1)+'";

        FormTag formTag = new FormTag();
        formTag.setTheme("xhtml");
        formTag.setPageContext(pageContext);
        formTag.setAction("testAction");

        TextFieldTag tag = new TextFieldTag();
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setName("foo");
        tag.setLabel("foo");
        tag.setTooltip("foo");
        tag.setTooltipConfig("#{'jsTooltipEnabled':'true'}");
        tag.setTooltipDelay(payload);

        formTag.doStartTag();
        tag.doStartTag();
        tag.doEndTag();
        formTag.doEndTag();

        String out = writer.toString();
        assertFalse(out, out.contains("'1000'+"));
        assertTrue(out, out.contains("'delay', '1000\\&#39;+alert(1)+\\&#39;'"));
    }

    public void testJsTooltipCssClassApostropheCannotBreakOutOfTheJsString() throws Exception {
        String payload = "x'+alert(1)+'";

        FormTag formTag = new FormTag();
        formTag.setTheme("xhtml");
        formTag.setPageContext(pageContext);
        formTag.setAction("testAction");

        TextFieldTag tag = new TextFieldTag();
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setName("foo");
        tag.setLabel("foo");
        tag.setTooltip("foo");
        tag.setTooltipConfig("#{'jsTooltipEnabled':'true'}");
        tag.setTooltipCssClass(payload);

        formTag.doStartTag();
        tag.doStartTag();
        tag.doEndTag();
        formTag.doEndTag();

        String out = writer.toString();
        assertFalse(out, out.contains("'x'+"));
        assertTrue(out, out.contains("'styleClass', 'x\\&#39;+alert(1)+\\&#39;'"));
    }

    private static int countOccurrences(String text, String token) {
        int count = 0;
        for (int i = text.indexOf(token); i >= 0; i = text.indexOf(token, i + token.length())) {
            count++;
        }
        return count;
    }

    private String render(Tag tag) throws JspException {
        // the writer is shared by the whole test, so drop what earlier renders wrote
        writer.getBuffer().setLength(0);
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();
        return writer.toString();
    }
}
