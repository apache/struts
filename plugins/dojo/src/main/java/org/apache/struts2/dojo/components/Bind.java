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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * This tag will generate event listeners for multiple events on multiple sources,
 * making an asynchronous request to the specified href, and updating multiple targets.
 * </p>
 * <!-- END SNIPPET: javadoc -->
 * 
 * <p>Examples</p>
 * 
 * <!-- START SNIPPET: example0 -->
 * &lt;sx:bind href="%{#ajaxTest}" listenTopics="/makecall"/&gt;
 * &lt;s:submit onclick="dojo.event.topic.publish('/makecall')"/&gt;
 * <!-- END SNIPPET: example0 -->
 * 
 * <!-- START SNIPPET: example1 -->
 * &lt;img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/&gt;
 * &lt;sx:bind id="ex1" href="%{#ajaxTest}" sources="button" targets="div1" events="onclick" indicator="indicator" /&gt;
 * &lt;s:submit theme="simple" type="submit" value="submit" id="button"/&gt;
 * <!-- END SNIPPET: example1 -->
 * 
 * <!-- START SNIPPET: example2 -->
 * &lt;sx:bind id="ex3" href="%{#ajaxTest}" sources="chk1" targets="div1" events="onchange" formId="form1" /&gt;
 * &lt;form id="form1"&gt;
 *     &lt;s:checkbox name="data" label="Hit me" id="chk1"/&gt;
 * &lt;/form>
 * <!-- END SNIPPET: example2 -->
 * 
 * <!-- START SNIPPET: example4 -->
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/before", function(event, widget){
 *     alert('inside a topic event. before request');
 *     //event: set event.cancel = true, to cancel request
 *     //widget: widget that published the topic
 * });
 * &lt;/script&gt;         
 * 
 * &lt;input type="button" id="button"&gt; 
 * &lt;sx:bind id="ex1" href="%{#ajaxTest}" beforeNotifyTopics="/before" sources="button" events="onclick"/&gt; 
 * <!-- END SNIPPET: example4 -->
 * 
 * <!-- START SNIPPET: example5 -->
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/after", function(data, request, widget){
 *     alert('inside a topic event. after request');
 *     //data : text returned from request(the html)
 *     //request: XMLHttpRequest object
 *     //widget: widget that published the topic
 * });
 * &lt;/script&gt;        
 * 
 * &lt;input type="button" id="button"&gt;
 * &lt;sx:bind id="ex1" href="%{#ajaxTest}" highlightColor="red" afterNotifyTopics="/after" sources="button" events="onclick"/&gt;
 * <!-- END SNIPPET: example5 -->
 * 
 * <!-- START SNIPPET: example6 -->
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/error", function(error, request, widget){
 *     alert('inside a topic event. on error');
 *     //error : error object (error.message has the error message)
 *     //request: XMLHttpRequest object
 *     //widget: widget that published the topic
 * });
 * &lt;/script&gt;         
 * 
 * &lt;input type="button" id="button"&gt;
 * &lt;img id="ind1" src="${pageContext.request.contextPath}/images/indicator.gif" style="display:none"/&gt;
 * &lt;sx:bind href="%{#ajaxTest}" indicator="ind1" errorNotifyTopics="/error" sources="button" events="onclick"/&gt;
 * <!-- END SNIPPET: example6 -->
 */
@StrutsTag(name="bind", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.BindTag", description="Attach event listeners to elements to make AJAX calls")
@StrutsTagSkipInheritance
public class Bind extends AbstractValidateBean {
    public static final String TEMPLATE = "bind-close";
    public static final String OPEN_TEMPLATE = "bind";

    protected String targets;
    protected String sources;
    protected String events;

    public Bind(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (targets != null)
            addParameter("targets", findString(targets));
        if (sources != null)
            addParameter("sources", findString(sources));
        if (events != null)
            addParameter("events", findString(events));
    }
    
    @StrutsTagAttribute(description="Comma delimited list of event names to attach to")
    public void setEvents(String events) {
        this.events = events;
    }

    @StrutsTagAttribute(description="Comma delimited list of ids of the elements to attach to")
    public void setSources(String sources) {
        this.sources = sources;
    }

    @StrutsTagAttribute(description="Comma delimited list of ids of the elements whose content will be updated")
    public void setTargets(String targets) {
        this.targets = targets;
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

    //these attributes are overwritten here just for the TLD generation
    
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

    @StrutsTagSkipInheritance
    public void setCssClass(String cssClass) {
        super.setCssClass(cssClass);
    }

    @StrutsTagSkipInheritance
    public void setCssStyle(String cssStyle) {
        super.setCssStyle(cssStyle);
    }

    @StrutsTagSkipInheritance
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
    
    @StrutsTagAttribute(description="The id to use for the element")
    public void setId(String id) {
        super.setId(id);
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
    
    @StrutsTagAttribute(description = "Perform Ajax validation. 'ajaxValidation' interceptor must be applied to action", type="Boolean", 
        defaultValue = "false")
    public void setValidate(String validate) {
        this.validate = validate;
    }
    
    @StrutsTagAttribute(description = "Make an asynchronous request if validation succeeds. Only valid is 'validate' is 'true'", type="Boolean", 
        defaultValue = "false")
    public void setAjaxAfterValidation(String ajaxAfterValidation) {
        this.ajaxAfterValidation = ajaxAfterValidation;
    }
    
    @StrutsTagAttribute(description="Run scripts in a separate scope, unique for each tag", defaultValue="true")
    public void setSeparateScripts(String separateScripts) {
        this.separateScripts = separateScripts;
    }
    
    @StrutsTagAttribute(description="Transport used by Dojo to make the request", defaultValue="XMLHTTPTransport")
    public void setTransport(String transport) {
        this.transport = transport;
    }
}
