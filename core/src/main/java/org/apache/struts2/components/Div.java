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

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * The div tag when used on the ajax theme, provides a remote call
 * from the current page to update a section of content without having to refresh the entire page.<p/>
 *
 * It creates a HTML &lt;DIV /&gt; that obtains it's content via a remote XMLHttpRequest call via
 * the dojo framework.<p/>
 *
 * </p>
 * <!-- START SNIPPET: ajaxJavadoc -->
 * <B>THE FOLLOWING IS ONLY VALID WHEN AJAX IS CONFIGURED</B>
 * <ul>
 *      <li>href</li>
 *      <li>errorText</li>
 *      <li>afterLoading</li>
 *      <li>executeScripts</li>
 *      <li>loadingText</li>
 *      <li>listenTopics</li>
 *      <li>handler</li>
 *      <li>formId</li>
 *      <li>formFilter</li>
 *      <li>targets</li>
 *      <li>notifyTopics</li>
 *      <li>showErrorTransportText</li>
 *      <li>indicator</li>
 * </ul>
 * 'targets' is a list of element ids whose content will be updated with the
 * text returned from request.<p/>
 * 'href' needs to be set as an url tag reference value.<p/>
 * 'errorText' is the text that will be displayed when there is an error making the request.<p/>
 * 'afterLoading' Deprecated. Use 'notifyTopics'.<p/>
 * 'executeScripts' if set to true will execute javascript sections in the returned text.<p/>
 * 'loadingText' is the text that will be displayed on the 'targets' elements while making the
 * request.<p/>
 * 'handler' is the name of the function that will take care of making the AJAX request. Dojo's widget
 * and dom node are passed as parameters).<p/>
 * 'formId' is the id of the html form whose fields will be seralized and passed as parameters
 * in the request.<p/>
 * 'formFilter' is the name of a function which will be used to filter the fields that will be
 * seralized. This function takes as a parameter the element and returns true if the element
 * should be included.<p/>
 * 'updateFreq' sets(in milliseconds) the update interval.
 * 'autoStart' if set to true(true by default) starts the timer automatically
 * 'startTimerListenTopics' is a comma-separated list of topics used to start the timer
 * 'stopTimerListenTopics' is a comma-separated list of topics used to stop the timer
 * 'listenTopics' comma separated list of topics names, that will trigger a request
 * 'indicator' element to be shown while the request executing
 * 'showErrorTransportText': whether errors should be displayed (on 'targets')</p>
 * 'notifyTopics' comma separated list of topics names, that will be published. Three parameters are passed:<p>
 * <ul>
 *      <li>data: html or json object when type='load' or type='error'</li>
 *      <li>type: 'before' before the request is made, 'load' when the request succeeds, or 'error' when it fails</li>
 *      <li>request: request javascript object, when type='load' or type='error'</li>
 * <ul>

 * <!-- END SNIPPET: javadoc -->
 * <p/> <b>Examples</b>
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
 *
 * @s.tag name="div" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.DivTag"
 *        description="Render HTML div providing content from remote call via AJAX"
 */
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
    }

    /**
     * Start timer automatically
     *
     * @s.tagattribute required="false" type="Boolean" default="true"
     */
    public void setAutoStart(String autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * How long to wait before fetching the content (in milliseconds)
     *
     * @s.tagattribute required="false" type="Integer" default="0"
     */
    public void setDelay(String delay) {
        this.delay = delay;
    }

    /**
     * How often to re-fetch the content (in milliseconds)
     *
     * @s.tagattribute required="false" type="Integer" default="0"
     */
    public void setUpdateFreq(String updateInterval) {
        this.updateFreq = updateInterval;
    }

    /**
     * Topic that will start the timer (for autoupdate)
     *
     * @s.tagattribute required="false" type="String"
     */
    public void setStartTimerListenTopics(String startTimerListenTopic) {
        this.startTimerListenTopics = startTimerListenTopic;
    }

    /**
     * Topic that will stop the timer (for autoupdate)
     *
     * @s.tagattribute required="false" type="String"
     */
    public void setStopTimerListenTopics(String stopTimerListenTopic) {
        this.stopTimerListenTopics = stopTimerListenTopic;
    }

    /**
     * Content will be loaded when div becomes visible
     *
     * @s.tagattribute required="false" type="String" default="false"
     */
    public void setRefreshOnShow(String refreshOnShow) {
        this.refreshOnShow = refreshOnShow;
    }
}
