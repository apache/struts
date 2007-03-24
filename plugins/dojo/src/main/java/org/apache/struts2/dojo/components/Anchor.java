/*
 * $Id: Anchor.java 508285 2007-02-16 02:42:24Z musachy $
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
 * <pre>
 * <!-- START SNIPPET: examples -->
 * <b>Update target content with html returned from an action:</b>
 * 
 * &lt;div id="div1"&gt;Div 1&lt;/div&gt;
 * &lt;s:url id="ajaxTest" value="/AjaxTest.action"/&gt;
 * 
 * &lt;sx:a id="link1" href="%{ajaxTest}" target="div1"&gt;
 *      Update Content
 * &lt;/sx:a&gt;
 * 
 * <b>Submit form(anchor inside the form):</b>
 * 
 * &lt;s:form id="form" action="AjaxTest"&gt;
 *      &lt;input type="textbox" name="data"&gt;
 *      &lt;sx:a&gt;Submit form&lt;/s:a&gt;           
 * &lt;/s:form&gt;
 * 
 * <b>Submit form(anchor outside the form)</b>
 * 
 * &lt;s:form id="form" action="AjaxTest"&gt;
 *      &lt;input type="textbox" name="data"&gt;   
 * &lt;/s:form&gt;
 * 
 * &lt;sx:a formId="form"&gt;Submit form&lt;/sx:a&gt;
 * 
 * <b>Using topics:</b>
 * <script type="text/javascript">
 * dojo.event.topic.subscribe("/before", function(data, type, e){
 *      alert('inside a topic event. before request');
 *      //data : source element id
 *      //type : "before"
 *      //e    : request object
 *      //set e.cancel = true to cancel request
 * });
 * </script>         
 * 
 * &lt;sx:a beforeNotifyTopics="/before"&gt;Publish topics&lt;/s:a&gt; 
 * <!-- END SNIPPET: examples -->
 * </pre>
 */
@StrutsTag(name="a", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.AnchorTag", description="Renders an HTML anchor element that when clicked calls a URL via remote XMLHttpRequest and updates " +
                "its targets content")
public class Anchor extends AbstractRemoteCallUIBean {
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

        if(targets != null)
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
