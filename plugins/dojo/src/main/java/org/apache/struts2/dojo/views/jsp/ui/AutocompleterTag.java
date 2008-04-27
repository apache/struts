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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.dojo.components.Autocompleter;
import org.apache.struts2.views.jsp.ui.ComboBoxTag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see Autocompleter
 */
public class AutocompleterTag extends ComboBoxTag {
    private static final long serialVersionUID = -1112470447573172581L;

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
    protected String beforeNotifyTopics;
    protected String afterNotifyTopics;
    protected String errorNotifyTopics;
    protected String valueNotifyTopics;
    protected String resultsLimit;
    protected String transport;
    protected String preload;
    protected String keyValue;
    
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Autocompleter(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Autocompleter autocompleter = (Autocompleter) component;
        autocompleter.setAutoComplete(autoComplete);
        autocompleter.setDisabled(disabled);
        autocompleter.setForceValidOption(forceValidOption);
        autocompleter.setHref(href);
        autocompleter.setDelay(delay);
        autocompleter.setSearchType(searchType);
        autocompleter.setDropdownHeight(dropdownHeight);
        autocompleter.setDropdownWidth(dropdownWidth);
        autocompleter.setFormFilter(formFilter);
        autocompleter.setFormId(formId);
        autocompleter.setListenTopics(listenTopics);
        autocompleter.setNotifyTopics(notifyTopics);
        autocompleter.setIndicator(indicator);
        autocompleter.setLoadMinimumCount(loadMinimumCount);
        autocompleter.setLoadOnTextChange(loadOnTextChange);
        autocompleter.setShowDownArrow(showDownArrow);
        autocompleter.setTemplateCssPath(templateCssPath);
        autocompleter.setIconPath(iconPath);
        autocompleter.setKeyName(keyName);
        autocompleter.setDataFieldName(dataFieldName);
        autocompleter.setAfterNotifyTopics(afterNotifyTopics);
        autocompleter.setBeforeNotifyTopics(beforeNotifyTopics);
        autocompleter.setErrorNotifyTopics(errorNotifyTopics);
        autocompleter.setValueNotifyTopics(valueNotifyTopics);
        autocompleter.setResultsLimit(resultsLimit);
        autocompleter.setTransport(transport);
        autocompleter.setPreload(preload);
        autocompleter.setKeyValue(keyValue);
    }

    public void setAutoComplete(String autoComplete) {
        this.autoComplete = autoComplete;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setForceValidOption(String forceValidOption) {
        this.forceValidOption = forceValidOption;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setDelay(String searchDelay) {
        this.delay = searchDelay;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public void setDropdownHeight(String height) {
        this.dropdownHeight = height;
    }

    public void setDropdownWidth(String width) {
        this.dropdownWidth = width;
    }

    public void setFormFilter(String formFilter) {
      this.formFilter = formFilter;
    }

    public void setFormId(String formId) {
      this.formId = formId;
    }

    public void setListenTopics(String listenTopics) {
      this.listenTopics = listenTopics;
    }

    public void setNotifyTopics(String onValueChangedPublishTopic) {
      this.notifyTopics = onValueChangedPublishTopic;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public void setLoadMinimumCount(String loadMinimumCount) {
        this.loadMinimumCount = loadMinimumCount;
    }

    public String getLoadMinimumCount() {
        return loadMinimumCount;
    }

    public void setLoadOnTextChange(String loadOnTextChange) {
        this.loadOnTextChange = loadOnTextChange;
    }

    public void setShowDownArrow(String showDownArrow) {
        this.showDownArrow = showDownArrow;
    }

    public void setTemplateCssPath(String templateCssPath) {
        this.templateCssPath = templateCssPath;
    }
    
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setDataFieldName(String dataFieldName) {
        this.dataFieldName = dataFieldName;
    }
    
    public void setAfterNotifyTopics(String afterNotifyTopics) {
        this.afterNotifyTopics = afterNotifyTopics;
    }

    public void setBeforeNotifyTopics(String beforeNotifyTopics) {
        this.beforeNotifyTopics = beforeNotifyTopics;
    }

    public void setErrorNotifyTopics(String errorNotifyTopics) {
        this.errorNotifyTopics = errorNotifyTopics;
    }

    public void setValueNotifyTopics(String valueNotifyTopics) {
        this.valueNotifyTopics = valueNotifyTopics;
    }

    public void setResultsLimit(String resultsLimit) {
        this.resultsLimit = resultsLimit;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public void setPreload(String preload) {
        this.preload = preload;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
}
