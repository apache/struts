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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 *      <li>beforeLoading</li>
 *      <li>executeScripts</li>
 *      <li>loadingText</li>
 *      <li>refreshListenTopic</li>
 *      <li>handler</li>
 *      <li>formId</li>
 *      <li>formFilter</li>
 *      <li>targets</li>
 * </ul>
 * 'targets' is a list of element ids whose content will be updated with the
 * text returned from request.<p/>
 * 'href' needs to be set as an url tag reference value.<p/>
 * 'errorText' is the text that will be displayed when there is an error making the request.<p/>
 * 'afterLoading' is the name of a function that will be called after the request.<p/>
 * 'beforeLoading' is the name of a function that will be called before the request.<p/>
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
 * 'updateInterval' sets(in milliseconds) the update interval.
 * 'autoStart' if set to true(true by default) starts the timer automatically
 * 'startTimerListenTopic' is the topic used to start the timer
 * 'stopTimerListenTopic' is the topic used to stop the timer
 * 'refreshListenTopic' is the topic that forces an update
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
 *    refreshListenTopic=&quot;/refresh&quot;
 *    updateInterval=&quot;3000&quot;
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

    private static final Log _log = LogFactory.getLog(Div.class);

    public static final String TEMPLATE = "div";
    public static final String TEMPLATE_CLOSE = "div-close";
    public static final String COMPONENT_NAME = Div.class.getName();

    protected String updateInterval;
    protected String autoStart;
    protected String delay;
    protected String startTimerListenTopic;
    protected String stopTimerListenTopic;
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

        if (updateInterval != null)
            addParameter("updateInterval", findValue(updateInterval, Integer.class));
        if (autoStart != null)
            addParameter("autoStart", findValue(autoStart, Boolean.class));
        if (refreshOnShow != null)
            addParameter("refreshOnShow", findValue(refreshOnShow, Boolean.class));
        if (delay != null)
            addParameter("delay", findValue(delay, Integer.class));
        if (startTimerListenTopic != null)
            addParameter("startTimerListenTopic", findValue(startTimerListenTopic, String.class));
        if (stopTimerListenTopic != null)
            addParameter("stopTimerListenTopic", findValue(stopTimerListenTopic, String.class));
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
    public void setUpdateInterval(String updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * Topic that will start the timer (for autoupdate)
     *
     * @s.tagattribute required="false" type="String"
     */
    public void setStartTimerListenTopic(String startTimerListenTopic) {
        this.startTimerListenTopic = startTimerListenTopic;
    }

    /**
     * Topic that will stop the timer (for autoupdate)
     *
     * @s.tagattribute required="false" type="String"
     */
    public void setStopTimerListenTopic(String stopTimerListenTopic) {
        this.stopTimerListenTopic = stopTimerListenTopic;
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
