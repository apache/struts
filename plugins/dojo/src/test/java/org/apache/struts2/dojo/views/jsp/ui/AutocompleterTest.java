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

package org.apache.struts2.dojo.views.jsp.ui;

/**
 * @see org.apache.struts2.components.Autocompleter
 */
public class AutocompleterTest extends AbstractUITagTest {

    public void testAjax() throws Exception {
        AutocompleterTag tag = new AutocompleterTag();
        tag.setPageContext(pageContext);
        tag.setAutoComplete("true");
        tag.setDisabled("false");
        tag.setForceValidOption("false");
        tag.setHref("a");
        tag.setDropdownWidth("10");
        tag.setDropdownHeight("10");
        tag.setDelay("100");
        tag.setSearchType("b");
        tag.setDisabled("c");
        tag.setName("f");
        tag.setId("f");
        tag.setValue("g");
        tag.setIndicator("h");
        tag.setKeyName("i");
        tag.setLoadOnTextChange("true");
        tag.setLoadMinimumCount("3");
        tag.setShowDownArrow("false");
        tag.setIconPath("i");
        tag.setTemplateCssPath("j");
        tag.setDataFieldName("k");
        tag.setValueNotifyTopics("l");
        tag.setResultsLimit("2");
        tag.setTransport("m");
        tag.setPreload("true");
        tag.setKeyValue("key");
        tag.doStartTag();
        tag.doEndTag();

        verify(AutocompleterTest.class.getResource("Autocompleter-1.txt"));
    }

    public void testSimple() throws Exception {
        AutocompleterTag tag = new AutocompleterTag();
        tag.setPageContext(pageContext);
        tag.setAutoComplete("true");
        tag.setDisabled("false");
        tag.setForceValidOption("false");
        tag.setList("{'d','e'}");
        tag.setDropdownWidth("10");
        tag.setDropdownHeight("10");
        tag.setDelay("100");
        tag.setSearchType("b");
        tag.setDisabled("c");
        tag.setName("f");
        tag.setId("f");
        tag.setIconPath("i");
        tag.setTemplateCssPath("j");
        tag.setValueNotifyTopics("k");
        tag.setResultsLimit("2");
        tag.doStartTag();
        tag.doEndTag();

        verify(AutocompleterTest.class.getResource("Autocompleter-2.txt"));
    }

}
