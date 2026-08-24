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
