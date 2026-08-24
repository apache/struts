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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlControlTypeTest {

    @Test
    public void resolvesKnownTypes() {
        assertThat(HtmlControlType.from("text")).isEqualTo(HtmlControlType.TEXT);
        assertThat(HtmlControlType.from("number")).isEqualTo(HtmlControlType.NUMBER);
        assertThat(HtmlControlType.from("datetime-local")).isEqualTo(HtmlControlType.DATETIME_LOCAL);
    }

    @Test
    public void isLenientAboutCaseAndWhitespace() {
        assertThat(HtmlControlType.from("  NuMbEr ")).isEqualTo(HtmlControlType.NUMBER);
    }

    @Test
    public void neverThrowsOnUnusableInput() {
        assertThat(HtmlControlType.from(null)).isEqualTo(HtmlControlType.OTHER);
        assertThat(HtmlControlType.from("")).isEqualTo(HtmlControlType.OTHER);
        assertThat(HtmlControlType.from("   ")).isEqualTo(HtmlControlType.OTHER);
        assertThat(HtmlControlType.from("supercolor")).isEqualTo(HtmlControlType.OTHER);
    }

    @Test
    public void otherSupportsNothing() {
        assertThat(HtmlControlType.OTHER.supportsPattern()).isFalse();
        assertThat(HtmlControlType.OTHER.supportsLength()).isFalse();
        assertThat(HtmlControlType.OTHER.supportsRange()).isFalse();
    }

    @Test
    public void patternIsTextEntryOnly() {
        assertThat(HtmlControlType.TEXT.supportsPattern()).isTrue();
        assertThat(HtmlControlType.PASSWORD.supportsPattern()).isTrue();
        assertThat(HtmlControlType.NUMBER.supportsPattern()).isFalse();
        assertThat(HtmlControlType.TEXTAREA.supportsPattern()).isFalse();
        assertThat(HtmlControlType.SELECT.supportsPattern()).isFalse();
    }

    @Test
    public void lengthIsTextEntryPlusTextarea() {
        assertThat(HtmlControlType.TEXT.supportsLength()).isTrue();
        assertThat(HtmlControlType.TEXTAREA.supportsLength()).isTrue();
        assertThat(HtmlControlType.NUMBER.supportsLength()).isFalse();
        assertThat(HtmlControlType.CHECKBOX.supportsLength()).isFalse();
    }

    @Test
    public void rangeIsNumericAndTemporalOnly() {
        assertThat(HtmlControlType.NUMBER.supportsRange()).isTrue();
        assertThat(HtmlControlType.RANGE.supportsRange()).isTrue();
        assertThat(HtmlControlType.DATE.supportsRange()).isTrue();
        assertThat(HtmlControlType.TEXT.supportsRange()).isFalse();
    }
}
