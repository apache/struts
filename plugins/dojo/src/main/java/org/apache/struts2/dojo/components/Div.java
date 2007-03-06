/*
 * $Id: Div.java 511300 2007-02-24 16:41:38Z musachy $
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
 * The div tag when used on the ajax theme, provides a remote call
 * from the current page to update a section of content without having to refresh the entire page.
 * <p>
 * It creates a HTML &lt;DIV /&gt; that obtains it's content via a remote XMLHttpRequest call via
 * the dojo framework.
 * </p>
 * <div>
 * 
 * </div><p> <b>Examples</b>
 *
 * <pre>
 *       <!-- START SNIPPET: example -->
 * &lt;s:url id="url" action="AjaxTest" />
 * &lt;s:div
 *    id=&quot;once&quot;
 *    theme=&quot;ajax&quot;
 *    href=&quot;%{url}&quot;
 *    loadingText=&quot;Loading...&quot;
 *    listenTopics=&quot;/refresh&quot;
 *    updateFreq=&quot;3000&quot;
 *    autoStart=&quot;true&quot;
 *    formId=&quot;form&quot;
 *&gt;&lt;/s:div&gt;
 *       <!-- END SNIPPET: example -->
 * </pre>
 * </p>
 *
 */
@StrutsTag(name="div", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.DivTag", description="Render HTML div providing content from remote call via AJAX")
public class Div extends AbstractRemoteCallUIBean {

    public static final String TEMPLATE = "div";
    public static final String TEMPLATE_CLOSE = "div-close";
    public static final String COMPONENT_NAME = Div.class.getName();

    protected String updateFreq;
    protected String autoStart;
    protected String delay;
    protected String startTimerListenTopics;
    protected String stopTimerListenTopics;
    protected String refreshOnShow;
    protected String separateScripts;

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

    @StrutsTagAttribute(description="Content will be loaded when div becomes visible, used only inside tabbedPanel", type="Boolean", defaultValue="false")
    public void setRefreshOnShow(String refreshOnShow) {
        this.refreshOnShow = refreshOnShow;
    }

    @StrutsTagAttribute(description="Deprecated. Use 'notifyTopics'. Javascript code execute after reload")
    public void setAfterLoading(String afterLoading) {
        this.afterLoading = afterLoading;
    }

    @StrutsTagAttribute(description="Run scripts in a separate scope, unique for each Div", defaultValue="true")
    public void setSeparateScripts(String separateScripts) {
        this.separateScripts = separateScripts;
    }
}
