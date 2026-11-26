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
package org.apache.struts2.components.escape;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.Level;
import org.apache.struts2.test.LogCapture;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StrutsTagEscaperTest {

    private static final String VALUE = "\"x' & <b></b>";

    private final StrutsTagEscaper escaper = new StrutsTagEscaper();

    private static EscapeConfig.Builder config() {
        return EscapeConfig.builder(TagOrigin.PROPERTY);
    }

    private static EscapeConfig allFlags() {
        return config().html(true).javaScript(true).xml(true).csv(true).build();
    }

    @Test
    public void noFlagsReturnsValueUnchanged() {
        assertThat(escaper.escape(config().build(), VALUE)).isEqualTo(VALUE);
    }

    @Test
    public void htmlOnly() {
        assertThat(escaper.escape(config().html(true).build(), VALUE))
                .isEqualTo("&quot;x' &amp; &lt;b&gt;&lt;/b&gt;");
    }

    @Test
    public void javaScriptOnly() {
        assertThat(escaper.escape(config().javaScript(true).build(), VALUE))
                .isEqualTo("\\\"x\\' & <b><\\/b>");
    }

    @Test
    public void htmlThenJavaScript() {
        assertThat(escaper.escape(config().html(true).javaScript(true).build(), VALUE))
                .isEqualTo("&quot;x\\' &amp; &lt;b&gt;&lt;\\/b&gt;");
    }

    @Test
    public void xmlSupersedesHtml() {
        assertThat(escaper.escape(config().html(true).xml(true).build(), VALUE))
                .isEqualTo("&quot;x&apos; &amp; &lt;b&gt;&lt;/b&gt;");
    }

    @Test
    public void xmlSupersedesHtmlAndStillChainsCsv() {
        assertThat(escaper.escape(config().html(true).xml(true).csv(true).build(), VALUE))
                .isEqualTo(StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeXml10(VALUE)));
    }

    @Test
    public void xmlSupersedingHtmlLogsNoWarning() {
        try (LogCapture log = new LogCapture(StrutsTagEscaper.class)) {
            escaper.escape(config().html(true).xml(true).build(), VALUE);

            assertThat(log.messages(Level.WARN)).isEmpty();
        }
    }

    @Test
    public void htmlThenCsv() {
        assertThat(escaper.escape(config().html(true).csv(true).build(), VALUE))
                .isEqualTo("&quot;x' &amp; &lt;b&gt;&lt;/b&gt;");
    }

    @Test
    public void csvOnly() {
        assertThat(escaper.escape(config().csv(true).build(), VALUE))
                .isEqualTo("\"\"\"x' & <b></b>\"");
    }

    @Test
    public void nullValueReturnsNull() {
        assertThat(escaper.escape(allFlags(), null)).isNull();
    }

    @Test
    public void emptyValueReturnsEmpty() {
        assertThat(escaper.escape(allFlags(), "")).isEmpty();
    }

    @Test
    public void subclassCanReplaceHtmlStep() {
        StrutsTagEscaper custom = new StrutsTagEscaper() {
            @Override
            protected String escapeHtml(String value) {
                return "<html:" + value + ">";
            }
        };

        assertThat(custom.escape(config().html(true).javaScript(true).build(), VALUE))
                .isEqualTo(StringEscapeUtils.escapeEcmaScript("<html:" + VALUE + ">"));
    }
}
