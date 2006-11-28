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

    private String forceValidOption;
    private String searchType;
    private String autoComplete;
    private String searchDelay;
    private String disabled;
    private String href;
    private String listLength;


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
        if (searchDelay != null)
            addParameter("searchDelay", findValue(searchDelay, Integer.class));
        if (disabled != null)
            addParameter("disabled", findValue(disabled, Boolean.class));
        if (href != null) {
            addParameter("href", UrlHelper.buildUrl(findString(href), request,
                    response, null));
            addParameter("mode", "remote");
        } 
        if (listLength != null)
            addParameter("listLength", findValue(listLength, Integer.class));
            
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
     * limits list of visible rows, scroll on rest 
     * @s.tagattribute required="false" type="Integer" default="8"
     */
    public void setListLength(String listLength) {
        this.listLength = listLength;
    }

    /**
     * set delay before making the search
     * @s.tagattribute required="false" type="Integer" default="100"
     */
    public void setSearchDelay(String searchDelay) {
        this.searchDelay = searchDelay;
    }


    /**
     * set how the serach bust be preformed, optionas are: "startstring", "startword" and "substring" 
     * @s.tagattribute required="false" default="stringstart" type="String"
     */
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }
}