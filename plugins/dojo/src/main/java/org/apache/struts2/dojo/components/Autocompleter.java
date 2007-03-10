/*
 * $Id: Autocompleter.java 510785 2007-02-23 03:05:33Z musachy $
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
package org.apache.struts2.dojo.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ComboBox;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>The autocomplete tag is a combobox that can autocomplete text entered on the input box.
 * When used on the "simple" theme, the autocompleter can be used like the ComboBox.
 * When used on the "ajax" theme, the list can be retieved from an action. </p>
 *<!-- END SNIPPET: javadoc -->
 *
 *<!-- START SNIPPET: example -->
 *<p>Autocompleter that gets its list from an action:</p>
 *&lt;s:autocompleter name="test"  href="%{jsonList}" autoComplete="false"/&gt;
 *<br/>
 **<p>Autocompleter that uses a list:</p>
 *&lt;s:autocompleter name="test"  list="{'apple','banana','grape','pear'}" autoComplete="false"/&gt;
 *<br/>
 *<!-- END SNIPPET: example -->
 */
@StrutsTag(name="autocompleter", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.AutocompleterTag", description="Renders a combobox with autocomplete and AJAX capabilities")
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
    protected String loadOnTextChange;
    protected String loadMinimumCount;
    protected String showDownArrow;
    protected String templateCssPath;
    protected String iconPath;
    protected String keyName;
    protected String dataFieldName;
    
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
        if (searchType != null) {
            String type =  findString(searchType);
            if(type != null)
                addParameter("searchType", type.toUpperCase());
        }
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
        if (loadOnTextChange != null)
            addParameter("loadOnTextChange", findValue(loadOnTextChange, Boolean.class));
        if (loadMinimumCount != null)
            addParameter("loadMinimumCount", findValue(loadMinimumCount, Integer.class));
        if (showDownArrow != null)
            addParameter("showDownArrow", findValue(showDownArrow, Boolean.class));
        else
            addParameter("showDownArrow", Boolean.TRUE);
        if(templateCssPath != null)
            addParameter("templateCssPath", findString(templateCssPath));
        if(iconPath != null)
            addParameter("iconPath", findString(iconPath));
        if(dataFieldName != null)
        addParameter("dataFieldName", findString(dataFieldName));
        if(keyName != null)
            addParameter("keyName", findString(keyName));
        else {
            keyName = name + "Key";
            addParameter("keyName", findString(keyName));
        }
        
        String keyNameExpr = "%{" + keyName + "}";
        addParameter("key", findString(keyNameExpr));
    }

    @Override
    @StrutsTagSkipInheritance
    public void setTheme(String theme) {
        super.setTheme(theme);
    }
    
    @Override
    public String getTheme() {
        return "ajax";
    }
    
    protected Object findListValue() {
        return (list != null) ? findValue(list, Object.class) : null;
    }

    @StrutsTagAttribute(description="Whether autocompleter should make suggestion on the textbox", type="Boolean", defaultValue="false")
    public void setAutoComplete(String autoComplete) {
        this.autoComplete = autoComplete;
    }

    @StrutsTagAttribute(description="Enable or disable autocompleter", type="Boolean", defaultValue="false")
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    @StrutsTagAttribute(description="Force selection to be one of the options", type="Boolean", defaultValue="false")
    public void setForceValidOption(String forceValidOption) {
        this.forceValidOption = forceValidOption;
    }

    @StrutsTagAttribute(description="The URL used to load the options")
    public void setHref(String href) {
        this.href = href;
    }

    @StrutsTagAttribute(description="Delay before making the search", type="Integer", defaultValue="100")
    public void setDelay(String searchDelay) {
        this.delay = searchDelay;
    }

    @StrutsTagAttribute(description="how the search must be performed, options are: 'startstring', 'startword' " +
                "and 'substring'", defaultValue="stringstart")
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    @StrutsTagAttribute(description="Dropdown's height in pixels", type="Integer", defaultValue="120")
    public void setDropdownHeight(String height) {
        this.dropdownHeight = height;
    }

    @StrutsTagAttribute(description="Dropdown's width", type="Integer", defaultValue="same as textbox")
    public void setDropdownWidth(String width) {
        this.dropdownWidth = width;
    }

    @StrutsTagAttribute(description="Function name used to filter the fields of the form")
    public void setFormFilter(String formFilter) {
      this.formFilter = formFilter;
    }

    @StrutsTagAttribute(description="Form id whose fields will be serialized and passed as parameters")
    public void setFormId(String formId) {
      this.formId = formId;
    }

    @StrutsTagAttribute(description="Topic that will trigger a reload")
    public void setListenTopics(String listenTopics) {
      this.listenTopics = listenTopics;
    }

    @StrutsTagAttribute(description="Topics that will be published when content is reloaded")
    public void setNotifyTopics(String onValueChangedPublishTopic) {
      this.notifyTopics = onValueChangedPublishTopic;
    }

    @StrutsTagAttribute(description="Id of element that will be shown while request is made")
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    @StrutsTagAttribute(description="Minimum number of characters that will force the content to be loaded", type="Integer", defaultValue="3")
    public void setLoadMinimumCount(String loadMinimumCount) {
        this.loadMinimumCount = loadMinimumCount;
    }

    @StrutsTagAttribute(description="Options will be reloaded everytime a character is typed on the textbox", type="Boolean", defaultValue="true")
    public void setLoadOnTextChange(String loadOnType) {
        this.loadOnTextChange = loadOnType;
    }

    @StrutsTagAttribute(description="Show or hide the down arrow button", type="Boolean", defaultValue="true")
    public void setShowDownArrow(String showDownArrow) {
        this.showDownArrow = showDownArrow;
    }

    // Override as not required
    @StrutsTagAttribute(description="Iteratable source to populate from.")
    public void setList(String list) {
        super.setList(list);
    }
    
    @StrutsTagAttribute(description="Template css path")
    public void setTemplateCssPath(String templateCssPath) {
        this.templateCssPath = templateCssPath;
    }
    
    @StrutsTagAttribute(description="Path to icon used for the dropdown")
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
    
    @StrutsTagAttribute(description="Name of the field to which the selected key will be assigned")
    public void setKeyName(String keyName) {
       this.keyName = keyName;
    }

    @StrutsTagAttribute(description="Name of the field in the returned JSON object that contains the data array", defaultValue="Value specified in 'name'")
    public void setDataFieldName(String dataFieldName) {
        this.dataFieldName = dataFieldName;
    }
    
    @StrutsTagAttribute(description="The css class to use for element")
    public void setCssClass(String cssClass) {
        super.setCssClass(cssClass);
    }

    @StrutsTagAttribute(description="The css style to use for element")
    public void setCssStyle(String cssStyle) {
        super.setCssStyle(cssStyle);
    }

    @StrutsTagAttribute(description="The id to use for the element")
    public void setId(String id) {
        super.setId(id);
    }

    @StrutsTagAttribute(description="The name to set for element")
    public void setName(String name) {
        super.setName(name);
    }

    @StrutsTagAttribute(description="Preset the value of input element")
    public void setValue(String arg0) {
        super.setValue(arg0);
    }
}
