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
 * A tag that creates an HTML &lt;a/&gt; element, that when clicked makes an asynchronous request(XMLHttpRequest). The url
 * attribute must be build using the &lt;s:url/&gt; tag. 
 * </p>
 * <!-- END SNIPPET: javadoc -->
 * <p>Examples</p>
 * 
 * <!-- START SNIPPET: example1 -->
 * &lt;div id="div1"&gt;Div 1&lt;/div&gt;
 * &lt;s:url id="ajaxTest" value="/AjaxTest.action"/&gt;
 * 
 * &lt;sx:a id="link1" href="%{ajaxTest}" target="div1"&gt;
 *      Update Content
 * &lt;/sx:a&gt;
 * <!-- END SNIPPET: example1 -->
 * 
 * <!-- START SNIPPET: example2 -->
 * &lt;s:form id="form" action="AjaxTest"&gt;
 *      &lt;input type="textbox" name="data"&gt;
 *      &lt;sx:a&gt;Submit form&lt;/sx:a&gt;           
 * &lt;/s:form&gt;
 * <!-- END SNIPPET: example2 -->
 * 
 * <!-- START SNIPPET: example3 -->
 * &lt;s:form id="form" action="AjaxTest"&gt;
 *      &lt;input type="textbox" name="data"&gt;   
 * &lt;/s:form&gt;
 * 
 * &lt;sx:a formId="form"&gt;Submit form&lt;/sx:a&gt;
 * <!-- END SNIPPET: example3 -->
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
 * &lt;sx:a beforeNotifyTopics="/before"&gt;Publish topics&lt;/sx:a&gt;
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
 * &lt;sx:a afterNotifyTopics="/after" highlightColor="red" href="%{#ajaxTest}"&gt;Publish topics&lt;/sx:a&gt;
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
 * &lt;img id="ind1" src="${pageContext.request.contextPath}/images/indicator.gif" style="display:none"/&gt;
 * &lt;sx:a errorNotifyTopics="/error" indicator="ind1" href="%{#ajaxTest}"&gt;Publish topics&lt;/sx:a&gt;
 * <!-- END SNIPPET: example6 -->
 */
@StrutsTag(name="a", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.AnchorTag", description="Renders an HTML anchor element that when clicked calls a URL via remote XMLHttpRequest and updates " +
                "its targets content")
public class Anchor extends AbstractValidateBean {
    public static final String OPEN_TEMPLATE = "a";
    public static final String TEMPLATE = "a-close";
    public static final String COMPONENT_NAME = Anchor.class.getName();

    protected String targets;

    public Anchor(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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
    }
    
    @Override
    @StrutsTagSkipInheritance
    public void setTheme(String theme) {
        super.setTheme(theme);
    }

    @StrutsTagAttribute(description="Comma delimited list of ids of the elements whose content will be updated")
    public void setTargets(String targets) {
        this.targets = targets;
    }
}
