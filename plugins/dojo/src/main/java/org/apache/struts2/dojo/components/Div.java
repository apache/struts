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

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * This tag generates an HTML div that loads its content using an XMLHttpRequest call, via
 * the dojo framework. When the "updateFreq" is set the built in timer will start automatically and 
 * reload the div content with the value of "updateFreq" as the refresh period(in milliseconds).
 * Topics can be used to stop(stopTimerListenTopics) and start(startTimerListenTopics) this timer.  
 * </p>
 * <p>
 * When used inside a "tabbedpanel" tag, each div becomes a tab. Some attributes are specific
 * to this use case, like:
 * <ul>
 *   <li>refreshOnShow: div content is realoded when tab is selected</li>
 *   <li>closable: Tab will have close button</li>
 *   <li>preload: load div content after page is loaded</li>
 * </ul>
 * </p>
 * <!-- END SNIPPET: javadoc -->
 * 
 * <p>Examples</p>
 * <!-- START SNIPPET: example1 -->
 * &lt;sx:div href="%{#url}"&gt;Initial Content&lt;/sx:div&gt;
 * <!-- END SNIPPET: example1 -->
 * 
 * <!-- START SNIPPET: example2 -->
 * &lt;img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" style="display:none"/&gt;
 * &lt;sx:div href="%{#url}" updateFreq="2000" indicator="indicator"&gt;
 *   Initial Content
 * &lt;/sx:div&gt;
 * <!-- END SNIPPET: example2 -->
 * 
 * <!-- START SNIPPET: example3 -->
 * &lt;form id="form"&gt;
 *   &lt;label for="textInput"&gt;Text to be submited when div reloads&lt;/label&gt;
 *   &lt;input type=textbox id="textInput" name="data"&gt;
 * &lt;/form&gt;
 * &lt;sx:div 
 *      href="%{#url}" 
 *      updateFreq="3000"
 *      listenTopics="/refresh"
 *      startTimerListenTopics="/startTimer"
 *      stopTimerListenTopics="/stopTimer"
 *      highlightColor="red"
 *      formId="form"&gt;
 *  Initial Content
 * &lt;/sx:div&gt;
 * <!-- END SNIPPET: example3 -->
 */
@StrutsTag(name="div", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.DivTag", description="Render HTML div providing content from remote call via AJAX")
public class Div extends AbstractRemoteBean {

    public static final String TEMPLATE = "div";
    public static final String TEMPLATE_CLOSE = "div-close";
    public static final String COMPONENT_NAME = Div.class.getName();

    protected String updateFreq;
    protected String autoStart;
    protected String delay;
    protected String startTimerListenTopics;
    protected String stopTimerListenTopics;
    protected String refreshOnShow;
    protected String closable;
    protected String preload;
    
    public Div(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE_CLOSE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (updateFreq != null)
            addParameter("updateFreq", findValue(updateFreq, Integer.class));
        if (autoStart != null)
            addParameter("autoStart", findValue(autoStart, Boolean.class));
        if (refreshOnShow != null)
            addParameter("refreshOnShow", findValue(refreshOnShow, Boolean.class));
        if (delay != null)
            addParameter("delay", findValue(delay, Integer.class));
        if (startTimerListenTopics != null)
            addParameter("startTimerListenTopics", findString(startTimerListenTopics));
        if (stopTimerListenTopics != null)
            addParameter("stopTimerListenTopics", findString(stopTimerListenTopics));
        if (separateScripts != null)
            addParameter("separateScripts", findValue(separateScripts, Boolean.class));
        if (closable != null)
            addParameter("closable", findValue(closable, Boolean.class));
        if (preload != null)
            addParameter("preload", findValue(preload, Boolean.class));
    }

    @StrutsTagAttribute(description="Start timer automatically", type="Boolean", defaultValue="true")
    public void setAutoStart(String autoStart) {
        this.autoStart = autoStart;
    }

    @StrutsTagAttribute(description="How long to wait before fetching the content (in milliseconds)",  type="Integer")
    public void setDelay(String delay) {
        this.delay = delay;
    }

    @StrutsTagAttribute(description="How often to reload the content (in milliseconds)", type="Integer")
    public void setUpdateFreq(String updateInterval) {
        this.updateFreq = updateInterval;
    }

    @StrutsTagAttribute(description="Topics that will start the timer (for autoupdate)")
    public void setStartTimerListenTopics(String startTimerListenTopic) {
        this.startTimerListenTopics = startTimerListenTopic;
    }

    @StrutsTagAttribute(description="Topics that will stop the timer (for autoupdate)")
    public void setStopTimerListenTopics(String stopTimerListenTopic) {
        this.stopTimerListenTopics = stopTimerListenTopic;
    }

    @StrutsTagAttribute(description="Content will be loaded when div becomes visible, used only inside the tabbedpanel tag", type="Boolean", defaultValue="false")
    public void setRefreshOnShow(String refreshOnShow) {
        this.refreshOnShow = refreshOnShow;
    }

    @StrutsTagAttribute(description="Show a close button when the div is inside a 'tabbedpanel'", defaultValue="false")
    public void setClosable(String closable) {
        this.closable = closable;
    }

    @StrutsTagAttribute(description="Load content when page is loaded", type="Boolean", defaultValue="true")
    public void setPreload(String preload) {
        this.preload = preload;
    }
    
    @StrutsTagAttribute(description = "Color used to perform a highlight effect on this element", 
        defaultValue = "none")
    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }
}
