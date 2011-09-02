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

package org.apache.struts2.dojo.components;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ComboBox;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>The autocomplete tag is a combobox that can autocomplete text entered on the input box. If an action
 * is used to populate the autocompleter, the output of the action must be a well formed JSON string. </p>
 * <p>The autocompleter follows this rule to find its datasource:<p>
 * <p>1. If the response is an array, assume that it contains 2-dimension array elements, like:
 * <pre>
 * [
 *      ["Alabama", "AL"],
 *      ["Alaska", "AK"]
 * ]
 * </pre>
 * <p>2. If a value is specified in the "dataFieldName" attribute, and the response has a field with that
 * name, assume that's the datasource, which can be an array of 2-dimension array elements, or a map, 
 * like (assuming dataFieldName="state"):</p>
 * <pre>
 * {
 *      "state" : [
 *           ["Alabama","AL"],
 *           ["Alaska","AK"]
 *      ]
 * }     
 * or
 * {
 *      "state" : {
 *            "Alabama" : "AL",
 *            "Alaska" : "AK"
 *      }
 * }
 * </pre>
 * </pre>
 * <p>3. If there is a field that starts with the value specified on the "name" attribute, assume 
 * that's the datasource, like (assuming name="state"):</p>
 * <pre>
 * {
 *      "states" : [
 *           ["Alabama","AL"],
 *           ["Alaska","AK"]
 *      ]
 * }
 * </pre>
 * <p>4. Use first array that is found, like:<p>
 * <pre>
 * {
 *      "anything" : [
 *            ["Alabama", "AL"],
 *            ["Alaska", "AK"]
 *     ]       
 * }
 * <p>5. If the response is a map, use it (recommended as it is the easiest one to generate):
 * <pre>
 * {
 *      "Alabama" : "AL",
 *      "Alaska" : "AK"
 * }
 * </pre>
 * <!-- END SNIPPET: javadoc -->
 * <p>Examples</p>
 * <!-- START SNIPPET: example1 -->
 * &lt;sx:autocompleter name="autocompleter1" href="%{jsonList}"/&gt;
 * <!-- END SNIPPET: example1 -->
 * 
 * <!-- START SNIPPET: example2 -->
 * &lt;s:autocompleter name="test"  list="{'apple','banana','grape','pear'}" autoComplete="false"/&gt;
 * <!-- END SNIPPET: example2 -->
 * 
 * <!-- START SNIPPET: example3 -->
 * &lt;sx:autocompleter name="mvc" href="%{jsonList}" loadOnTextChange="true" loadMinimumCount="3"/&gt;
 * 
 * The text entered on the autocompleter is passed as a parameter to the url specified in "href", like (text is "struts"):
 *  
 * http://host/example/myaction.do?mvc=struts
 * <!-- END SNIPPET: example3 -->
 * 
 * <!-- START SNIPPET: example4 -->
 * &lt;form id="selectForm"&gt;
 *      &lt;sx:autocompleter  name="select" list="{'fruits','colors'}"  valueNotifyTopics="/changed" /&gt;
 * &lt;/form&gt;  
 * &lt;sx:autocompleter  href="%{jsonList}" formId="selectForm" listenTopics="/changed"/&gt;
 * <!-- END SNIPPET: example4 -->
 * 
 * <!-- START SNIPPET: example5 -->
 * &lt;sx:autocompleter  href="%{jsonList}" id="auto"/&gt;
 * &lt;script type="text/javascript"&gt;
 *   function getValues() {
 *      var autoCompleter = dojo.widget.byId("auto");
 *      
 *      //key (in the states example above, "AL")
 *      var key = autoCompleter.getSelectedKey();
 *      alert(key);
 *      
 *      //value (in the states example above, "Alabama")
 *      var value = autoCompleter.getSelectedValue();
 *      alert(value);
 *      
 *      //text currently on the textbox (anything the user typed)
 *      var text = autoCompleter.getText();
 *      alert(text);
 *   }
 * 
 *   function setValues() {
 *      var autoCompleter = dojo.widget.byId("auto");
 *      
 *      //key (key will be set to "AL" and value to "Alabama")
 *      autoCompleter.setSelectedKey("AL");
 *      
 *      //value (key will be set to "AL" and value to "Alabama")
 *      autoCompleter.setAllValues("AL", "Alabama");
 *   }
 * &lt;/script&gt;
 * <!-- END SNIPPET: example5 -->
 * 
 * <!-- START SNIPPET: example6 -->
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/before", function(event, widget){
 *     alert('inside a topic event. before request');
 *     //event: set event.cancel = true, to cancel request
 *     //widget: widget that published the topic
 * });
 * &lt;/script&gt;         
 * 
 * &lt;sx:autocompleter beforeNotifyTopics="/before" href="%{#ajaxTest} /&gt;
 * <!-- END SNIPPET: example6 -->
 * 
 * <!-- START SNIPPET: example7 -->
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/after", function(data, request, widget){
 *     alert('inside a topic event. after request');
 *     //data : JavaScript object from parsing response
 *     //request: XMLHttpRequest object
 *     //widget: widget that published the topic
 * });
 * &lt;/script&gt;        
 * 
 * &lt;sx:autocompleter afterNotifyTopics="/after" href="%{#ajaxTest}" /&gt;
 * <!-- END SNIPPET: example7 -->
 * 
 * <!-- START SNIPPET: example8-->
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/error", function(error, request, widget){
 *     alert('inside a topic event. on error');
 *     //error : error object (error.message has the error message)
 *     //request: XMLHttpRequest object
 *     //widget: widget that published the topic
 * });
 * &lt;/script&gt;
 * 
 * &lt;sx:autocompleter errorNotifyTopics="/error" href="%{#ajaxTest}" /&gt;
 * <!-- END SNIPPET: example8 -->
 * 
 * <!-- START SNIPPET: example9 -->
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/value", function(value, key, text, widget){
 *     alert('inside a topic event. after value changed');
 *     //value : selected value (like "Florida" in example above)
 *     //key: selected key (like "FL" in example above)
 *     //text: text typed into textbox
 *     //widget: widget that published the topic
 * });
 * &lt;/script&gt;   
 * 
 * &lt;sx:autocompleter valueNotifyTopics="/value" href="%{#ajaxTest}" /&gt;
 * <!-- END SNIPPET: example9 -->
 */
@StrutsTag(name="autocompleter", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.AutocompleterTag", description="Renders a combobox with autocomplete and AJAX capabilities")
public class Autocompleter extends ComboBox {
    public static final String TEMPLATE = "autocompleter";
    final private static String COMPONENT_NAME = Autocompleter.class.getName();
    private final static transient Random RANDOM = new Random();

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
        if (templateCssPath != null)
            addParameter("templateCssPath", findString(templateCssPath));
        if (iconPath != null)
            addParameter("iconPath", findString(iconPath));
        if (dataFieldName != null)
            addParameter("dataFieldName", findString(dataFieldName));
        if (keyName != null)
            addParameter("keyName", findString(keyName));
        else {
            keyName = name + "Key";
            addParameter("keyName", findString(keyName));
        }
        if (transport != null)
            addParameter("transport", findString(transport));
        if (preload != null)
            addParameter("preload", findValue(preload, Boolean.class));
        
        if (keyValue != null)
            addParameter("nameKeyValue", findString(keyValue));
        else {
            String keyNameExpr = "%{" + keyName + "}";
            addParameter("nameKeyValue", findString(keyNameExpr));
        }
        
        
        if (beforeNotifyTopics != null)
            addParameter("beforeNotifyTopics", findString(beforeNotifyTopics));
        if (afterNotifyTopics != null)
            addParameter("afterNotifyTopics", findString(afterNotifyTopics));
        if (errorNotifyTopics != null)
            addParameter("errorNotifyTopics", findString(errorNotifyTopics));
        if (valueNotifyTopics != null)
            addParameter("valueNotifyTopics", findString(valueNotifyTopics));
        if (resultsLimit != null)
            addParameter("searchLimit", findString(resultsLimit));
        
        // generate a random ID if not explicitly set and not parsing the content
        Boolean parseContent = (Boolean)stack.getContext().get(Head.PARSE_CONTENT);
        boolean generateId = (parseContent != null ? !parseContent : true);
        
        addParameter("pushId", generateId);
        if ((this.id == null || this.id.length() == 0) && generateId) {
            // resolves Math.abs(Integer.MIN_VALUE) issue reported by FindBugs 
            // http://findbugs.sourceforge.net/bugDescriptions.html#RV_ABSOLUTE_VALUE_OF_RANDOM_INT
            int nextInt = RANDOM.nextInt();
            nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);  
            this.id = "widget_" + String.valueOf(nextInt);
        }
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
    
    @StrutsTagAttribute(description="Comma delimmited list of topics that will published after the request(if the request succeeds)")
    public void setAfterNotifyTopics(String afterNotifyTopics) {
        this.afterNotifyTopics = afterNotifyTopics;
    }

    @StrutsTagAttribute(description="Comma delimmited list of topics that will published before the request")
    public void setBeforeNotifyTopics(String beforeNotifyTopics) {
        this.beforeNotifyTopics = beforeNotifyTopics;
    }

    @StrutsTagAttribute(description="Comma delimmited list of topics that will published after the request(if the request fails)")
    public void setErrorNotifyTopics(String errorNotifyTopics) {
        this.errorNotifyTopics = errorNotifyTopics;
    }

    @StrutsTagAttribute(description="Comma delimmited list of topics that will published when a value is selected")
    public void setValueNotifyTopics(String valueNotifyTopics) {
        this.valueNotifyTopics = valueNotifyTopics;
    }
    
    @StrutsTagAttribute(description="Limit how many results are shown as autocompletion options, set to -1 for unlimited results", defaultValue="30", type = "Integer")
    public void setResultsLimit(String resultsLimit) {
        this.resultsLimit = resultsLimit;
    }
    
    @StrutsTagAttribute(description="Transport used by Dojo to make the request", defaultValue="XMLHTTPTransport")
    public void setTransport(String transport) {
        this.transport = transport;
    }
    
    @StrutsTagAttribute(description="Load options when page is loaded", type="Boolean", defaultValue="true")
    public void setPreload(String preload) {
        this.preload = preload;
    }

    @StrutsTagAttribute(description="Initial key value")
    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
}
