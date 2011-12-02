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

import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * AbstractRemoteCallUIBean is superclass for all components dealing with remote
 * calls.
 */
public abstract class AbstractRemoteBean extends ClosingUIBean implements RemoteBean {

    final private static transient Random RANDOM = new Random();    

    protected String href;
    protected String errorText;
    protected String executeScripts;
    protected String loadingText;
    protected String listenTopics;
    protected String handler;
    protected String formId;
    protected String formFilter;
    protected String notifyTopics;
    protected String showErrorTransportText;
    protected String indicator;
    protected String showLoadingText;
    protected String beforeNotifyTopics;
    protected String afterNotifyTopics;
    protected String errorNotifyTopics;
    protected String highlightColor;
    protected String highlightDuration;
    protected String separateScripts;
    protected String transport;
    protected String parseContent;
    
    public AbstractRemoteBean(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (href != null)
            addParameter("href", findString(href));
        if (errorText != null)
            addParameter("errorText", findString(errorText));
        if (loadingText != null)
            addParameter("loadingText", findString(loadingText));
        if (executeScripts != null)
            addParameter("executeScripts", findValue(executeScripts, Boolean.class));
        if (listenTopics != null)
            addParameter("listenTopics", findValue(listenTopics, String.class));
        if (notifyTopics != null)
            addParameter("notifyTopics", findValue(notifyTopics, String.class));
        if (handler != null)
            addParameter("handler", findString(handler));
        if (formId != null)
            addParameter("formId", findString(formId));
        if (formFilter != null)
            addParameter("formFilter", findString(formFilter));
        if (indicator != null)
            addParameter("indicator", findString(indicator));
        if (showErrorTransportText != null)
            addParameter("showErrorTransportText", findValue(showErrorTransportText, Boolean.class));
        else
            addParameter("showErrorTransportText", true);
        if (showLoadingText != null)
            addParameter("showLoadingText", findString(showLoadingText));
        if (beforeNotifyTopics != null)
            addParameter("beforeNotifyTopics", findString(beforeNotifyTopics));
        if (afterNotifyTopics != null)
            addParameter("afterNotifyTopics", findString(afterNotifyTopics));
        if (errorNotifyTopics != null)
            addParameter("errorNotifyTopics", findString(errorNotifyTopics));
        if (highlightColor != null)
            addParameter("highlightColor", findString(highlightColor));
        if (highlightDuration != null)
            addParameter("highlightDuration", findString(highlightDuration));
        if (separateScripts != null)
            addParameter("separateScripts", findValue(separateScripts, Boolean.class));
        if (transport != null)
            addParameter("transport", findString(transport));
        if (parseContent != null)
            addParameter("parseContent", findValue(parseContent, Boolean.class));
        else
            addParameter("parseContent", true);

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
            addParameter("id", this.id);
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

    @StrutsTagAttribute(description="Topic that will trigger the remote call")
    public void setListenTopics(String listenTopics) {
        this.listenTopics = listenTopics;
    }

    @StrutsTagAttribute(description="The URL to call to obtain the content. Note: If used with ajax context, the value must be set as an url tag value.")
    public void setHref(String href) {
        this.href = href;
    }


    @StrutsTagAttribute(description="The text to display to the user if the is an error fetching the content")
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    @StrutsTagAttribute(description="Javascript code in the fetched content will be executed", type="Boolean", defaultValue="false")
    public void setExecuteScripts(String executeScripts) {
        this.executeScripts = executeScripts;
    }

    @StrutsTagAttribute(description="Text to be shown while content is being fetched", defaultValue="Loading...")
    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }


    @StrutsTagAttribute(description="Javascript function name that will make the request")
    public void setHandler(String handler) {
        this.handler = handler;
    }


    @StrutsTagAttribute(description="Function name used to filter the fields of the form.")
    public void setFormFilter(String formFilter) {
        this.formFilter = formFilter;
    }

    @StrutsTagAttribute(description="Form id whose fields will be serialized and passed as parameters")
    public void setFormId(String formId) {
        this.formId = formId;
    }

    @StrutsTagAttribute(description="Comma delimmited list of topics that will published before and after the request, and on errors")
    public void setNotifyTopics(String notifyTopics) {
        this.notifyTopics = notifyTopics;
    }


    @StrutsTagAttribute(description="Set whether errors will be shown or not", type="Boolean", defaultValue="true")
    public void setShowErrorTransportText(String showError) {
        this.showErrorTransportText = showError;
    }

    @StrutsTagAttribute(description="Id of element that will be shown while making request")
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    @StrutsTagAttribute(description="Show loading text on targets", type="Boolean", defaultValue="false")
    public void setShowLoadingText(String showLoadingText) {
        this.showLoadingText = showLoadingText;
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

    @StrutsTagAttribute(description = "Color used to perform a highlight effect on the elements specified in the 'targets' attribute", 
        defaultValue = "none")
    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }

    @StrutsTagAttribute(description = "Duration of highlight effect in milliseconds. Only valid if 'highlightColor' attribute is set", 
        defaultValue = "2000", type="Integer")
    public void setHighlightDuration(String highlightDuration) {
        this.highlightDuration = highlightDuration;
    }
    
    @StrutsTagAttribute(description="Run scripts in a separate scope, unique for each tag", defaultValue="true")
    public void setSeparateScripts(String separateScripts) {
        this.separateScripts = separateScripts;
    }

    @StrutsTagAttribute(description="Transport used by Dojo to make the request", defaultValue="XMLHTTPTransport")
    public void setTransport(String transport) {
        this.transport = transport;
    }

    @StrutsTagAttribute(description="Parse returned HTML for Dojo widgets", defaultValue="true", type="Boolean")
    public void setParseContent(String parseContent) {
        this.parseContent = parseContent;
    }
}
