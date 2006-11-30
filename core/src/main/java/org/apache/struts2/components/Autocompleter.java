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
package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.util.UrlHelper;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <p>The autocomplete tag is a combobox that can autocomplete text entered on the input box.
 * When used on the "simple" theme, the autocompleter can be used like the ComboBox.
 * When used on the "ajax" theme, the list can be retieved from an action. This action must
 * return a JSON list in the format:</p>
 * <pre>
 * [
 *   ["Text 1","Value1"],
 *   ["Text 2","Value2"],
 *   ["Text 3","Value3"]
 * ]
 * </pre>
 * <B>THE FOLLOWING IS ONLY VALID WHEN AJAX IS CONFIGURED</B>
 * <ul>
 *      <li>href</li>
 * </ul>
 * @s.tag name="autocompleter" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.AutocompleterTag"
 *        description="Renders a combobox with autocomplete and AJAX capabilities"
 */
public class Autocompleter extends ComboBox {
    public static final String TEMPLATE = "autocompleter";
    final private static String COMPONENT_NAME = Autocompleter.class.getName();

    protected String forceValidOption;
    protected String searchType;
    protected String autoComplete;
    protected String delay;
    protected String disabled;
    protected String href;
    protected String dropdownWidth;
    protected String dropdownHeight;
    protected String formId;
    protected String formFilter;
    protected String refreshListenTopic;
    protected String onValueChangedPublishTopic;
    protected String afterLoading;
    protected String beforeLoading;

    public Autocompleter(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public String getComponentName() {
        return COMPONENT_NAME;
    }

    public void evaluateParams() {
        super.evaluateParams();

        if (forceValidOption != null)
            addParameter("forceValidOption", findValue(forceValidOption,
                    Boolean.class));
        if (searchType != null)
            addParameter("searchType", findString(searchType));
        if (autoComplete != null)
            addParameter("autoComplete", findValue(autoComplete, Boolean.class));
        if (delay != null)
            addParameter("delay", findValue(delay, Integer.class));
        if (disabled != null)
            addParameter("disabled", findValue(disabled, Boolean.class));
        if (href != null) {
            addParameter("href", UrlHelper.buildUrl(findString(href), request,
                    response, null));
            addParameter("mode", "remote");
        }
        if (dropdownHeight != null)
            addParameter("dropdownHeight", findValue(dropdownHeight, Integer.class));
        if (dropdownWidth != null)
            addParameter("dropdownWidth", findValue(dropdownWidth, Integer.class));
        if (formFilter != null)
          addParameter("formFilter", findString(formFilter));
        if (formId != null)
          addParameter("formId", findString(formId));
        if (refreshListenTopic != null)
          addParameter("refreshListenTopic", findString(refreshListenTopic));
        if (onValueChangedPublishTopic != null)
          addParameter("onValueChangedPublishTopic", findString(onValueChangedPublishTopic));
        if (afterLoading != null)
          addParameter("afterLoading", findString(afterLoading));
        if (beforeLoading != null)
          addParameter("beforeLoading", findString(beforeLoading));
    }


    protected Object findListValue() {
        return (list != null) ? findValue(list, Object.class) : null;
    }

    /**
     * sets if combobox must perform autocomplete
     * @s.tagattribute required="false" type="Boolean" default="true"
     */
    public void setAutoComplete(String autoComplete) {
        this.autoComplete = autoComplete;
    }

    /**
     * sets combobox to enabled or disabled
     * @s.tagattribute required="false" type="Boolean" default="false"
     */
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    /**
     * sets if user can enter a value that is not in a list
     * @s.tagattribute required="false" type="Boolean" default="false"
     */
    public void setForceValidOption(String forceValidOption) {
        this.forceValidOption = forceValidOption;
    }

    /**
     * The URL to call to obtain the content
     * @s.tagattribute required="false" type="String"
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * set delay before making the search
     * @s.tagattribute required="false" type="Integer" default="100"
     */
    public void setDelay(String searchDelay) {
        this.delay = searchDelay;
    }


    /**
     * set how the serach bust be preformed, optionas are: "startstring", "startword" and "substring"
     * @s.tagattribute required="false" default="stringstart" type="String"
     */
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    /**
     * set the height of the dropdown in pixels
     * @s.tagattribute required="false" default="120" type="Integer"
     */
    public void setDropdownHeight(String height) {
        this.dropdownHeight = height;
    }

    /**
     * set the width of the drodown, by default the same as the combobox
     * @s.tagattribute required="false" type="Integer"
     */
    public void setDropdownWidth(String width) {
        this.dropdownWidth = width;
    }

    /**
     * Function name used to filter the fields of the form.
     * This function takes as a parameter the element and returns true if the element
     * must be included.
     * @s.tagattribute required="false" type="String"
     */
    public void setFormFilter(String formFilter) {
      this.formFilter = formFilter;
    }

    /**
     * Form id whose fields will be serialized and passed as parameters
     *
     * @s.tagattribute required="false" type="String"
     */
    public void setFormId(String formId) {
      this.formId = formId;
    }

    /**
     * Topic that will trigger a re-fetch
     *
     * @s.tagattribute required="false" type="String"
     */
    public void setRefreshListenTopic(String refreshListenTopic) {
      this.refreshListenTopic = refreshListenTopic;
    }

    /**
     * Topic that will be published when content is fetched.
     * New Value is passed as parameter.
     * @s.tagattribute required="false" type="String"
     */
    public void setOnValueChangedPublishTopic(String onValueChangedPublishTopic) {
      this.onValueChangedPublishTopic = onValueChangedPublishTopic;
    }

    /**
     * Javascript code name that will be executed after the content has been fetched
     * @s.tagattribute required="false" type="String"
     */
    public void setAfterLoading(String afterLoading) {
      this.afterLoading = afterLoading;
    }

    /**
     * Javascript code that will be executed before the content has been fetched
     * @s.tagattribute required="false" type="String"
     */
    public void setBeforeLoading(String beforeLoading) {
      this.beforeLoading = beforeLoading;
    }
}
