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
 * <!-- START SNIPPET: javadoc --> The div tag is primarily an AJAX tag, providing a remote call
 * from the current page to update a section of content without having to refresh the entire page.<p/>
 *
 * It creates a HTML &lt;DIV /&gt; that obtains it's content via a remote XMLHttpRequest call via
 * the dojo framework.<p/>
 *
 * If a "refreshListenTopic" is supplied, it will listen to that topic and refresh it's content when any
 * message is received.<p/> <!-- END SNIPPET: javadoc -->
 *
 * <b>Important:</b> Be sure to setup the page containing this tag to be Configured for AJAX
 * </p>
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 *       &lt;!-- START SNIPPET: example --&gt;
 *       &lt;s:div ... /&gt;
 *       &lt;!-- END SNIPPET: example --&gt;
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
