package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.views.jsp.AbstractUITagTest;

/**
 * @see org.apache.struts2.components.Autocompleter
 */
public class AutocompleterTest extends AbstractUITagTest {

    public void testAjax() throws Exception {
        AutocompleterTag tag = new AutocompleterTag();
        tag.setPageContext(pageContext);
        tag.setTheme("ajax");
        tag.setAutoComplete("true");
        tag.setDisabled("false");
        tag.setForceValidOption("false");
        tag.setHref("a");
        tag.setDropdownWidth("10");
        tag.setDropdownHeight("10");
        tag.setSearchDelay("100");
        tag.setSearchType("b");
        tag.setDisabled("c");
        tag.setName("f");
        tag.setValue("g");
        tag.setBeforeLoading("h");
        tag.setAfterLoading("i");
        tag.setRefreshListenTopic("j");
        tag.setOnValueChangedPublishTopic("k");
        tag.doStartTag();
        tag.doEndTag();

        verify(AutocompleterTest.class.getResource("Autocompleter-1.txt"));
    }

    public void testSimple() throws Exception {
        AutocompleterTag tag = new AutocompleterTag();
        tag.setPageContext(pageContext);
        tag.setTheme("simple");
        tag.setAutoComplete("true");
        tag.setDisabled("false");
        tag.setForceValidOption("false");
        tag.setList("{'d','e'}");
        tag.setHref("a");
        tag.setDropdownWidth("10");
        tag.setDropdownHeight("10");
        tag.setSearchDelay("100");
        tag.setSearchType("b");
        tag.setDisabled("c");
        tag.setName("f");
        tag.doStartTag();
        tag.doEndTag();

        verify(AutocompleterTest.class.getResource("Autocompleter-2.txt"));
    }

}
