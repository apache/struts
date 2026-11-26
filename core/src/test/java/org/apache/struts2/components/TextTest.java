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
package org.apache.struts2.components;

import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;
import org.apache.struts2.components.escape.EscapeConfig;
import org.apache.struts2.components.escape.TagOrigin;

import java.io.StringWriter;

public class TextTest extends StrutsInternalTestCase {

    public void testCustomTagEscaperReceivesTextConfig() {
        Text text = newText("unknown.text.key");
        PropertyTest.RecordingTagEscaper escaper = new PropertyTest.RecordingTagEscaper();
        text.setTagEscaper(escaper);
        text.setEscapeXml(true);

        assertEquals("[escaped]", render(text, ""));
        assertEquals(EscapeConfig.builder(TagOrigin.TEXT).xml(true).build(), escaper.config);
        assertEquals("unknown.text.key", escaper.value);
    }

    public void testTextXmlSupersedesHtml() {
        ActionContext.getContext().getValueStack().push(new TestAction());
        Text text = newText("foo.escape.html");
        text.setEscapeHtml(true);
        text.setEscapeXml(true);

        assertEquals("1 &lt; 2", render(text, ""));
    }

    private Text newText(String name) {
        Text text = new Text(ActionContext.getContext().getValueStack());
        container.inject(text);
        text.setName(name);
        return text;
    }

    private static String render(Text text, String body) {
        StringWriter out = new StringWriter();
        text.start(out);
        text.end(out, body);
        return out.toString();
    }
}
