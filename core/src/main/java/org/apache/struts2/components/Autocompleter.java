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
 * <!-- START SNIPPET: ajaxJavadoc -->
 * <B>THE FOLLOWING IS ONLY VALID WHEN AJAX IS CONFIGURED</B>
 * <ul>
 *      <li>href</li>
 *      <li>errorText</li>
 *      <li>listenTopics</li>
 *      <li>notifyTopics</li>
 *      <li>listenTopics</li>
 *      <li>formId</li>
 *      <li>formFilter</li>
 *      <li>indicator</li>
 * </ul>
 * 'dropdownWidth' width in pixels of the drodpdown, same as autocompleter's width by default<p/>
 * 'dropdownHeight' height in pixels of the drodown, 120 px by default<p/>
 * 'forceValidOption' if invalid option is selected, clear autocompleter's text when focus is lost<p/>
 * 'autoComplete', if true, make suggestions on the textbox<p/>
 * 'formId' is the id of the html form whose fields will be seralized and passed as parameters
 * in the request.<p/>
 * 'formFilter' is the name of a function which will be used to filter the fields that will be
 * seralized. This function takes as a parameter the element and returns true if the element
 * should be included.<p/>
 * 'listenTopics' comma separated list of topics names, that will trigger a request
 * 'indicator' element to be shown while the request executing
 * 'showErrorTransportText': whether errors should be displayed (on 'targets')<p/>
 * 'notifyTopics' comma separated list of topics names, that will be published. Three parameters are passed:<p/>
 * <ul>
 *      <li>data: selected value when type='valuechanged'</li>
 *      <li>type: 'before' before the request is made, 'valuechanged' when selection changes, 'load' when the request succeeds, or 'error' when it fails</li>
 *      <li>request: request javascript object, when type='load' or type='error'</li>
 * <ul>
 *
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
    protected String listenTopics;
    protected String notifyTopics;
    protected String indicator;

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


    public void evaluateExtraParams() {
        super.evaluateExtraParams();

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
            addParameter("href", findString(href));
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
        if (listenTopics != null)
          addParameter("listenTopics", findString(listenTopics));
        if (notifyTopics != null)
          addParameter("notifyTopics", findString(notifyTopics));
        if (indicator != null)
            addParameter("indicator", findString(indicator));
        //get the key value
        if(name != null) {
            String keyNameExpr = "%{" + name + "Key}";
            addParameter("key", findString(keyNameExpr));
        }
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
    public void setListenTopics(String listenTopics) {
      this.listenTopics = listenTopics;
    }

    /**
     * Topic that will be published when content is fetched.
     * New Value is passed as parameter.
     * @s.tagattribute required="false" type="String"
     */
    public void setNotifyTopics(String onValueChangedPublishTopic) {
      this.notifyTopics = onValueChangedPublishTopic;
    }

    /**
     * Id of element that will be shown while request is made
     * @s.tagattribute required="false" type="String"
     */
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }
}
