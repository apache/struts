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

package org.apache.struts2.dojo.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.dojo.components.Div;

import com.opensymphony.xwork2.util.ValueStack;

public class DivTag extends AbstractRemoteTag {

    private static final long serialVersionUID = 5309231035916461758L;

    protected String updateFreq;
    protected String autoStart;
    protected String delay;
    protected String startTimerListenTopics;
    protected String stopTimerListenTopics;
    protected String refreshOnShow;
    protected String separateScripts;
    protected String closable;
    protected String preload;
    
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Div(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Div div = (Div) component;
        div.setUpdateFreq(updateFreq);
        div.setAutoStart(autoStart);
        div.setDelay(delay);
        div.setStartTimerListenTopics(startTimerListenTopics);
        div.setStopTimerListenTopics(stopTimerListenTopics);
        div.setRefreshOnShow(refreshOnShow);
        div.setSeparateScripts(separateScripts);
        div.setClosable(closable);
        div.setPreload(preload);
    }

    public void setAutoStart(String autoStart) {
        this.autoStart = autoStart;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public void setUpdateFreq(String updateInterval) {
        this.updateFreq = updateInterval;
    }

    public void setStartTimerListenTopics(String startTimerListenTopic) {
        this.startTimerListenTopics = startTimerListenTopic;
    }

    public void setStopTimerListenTopics(String stopTimerListenTopic) {
        this.stopTimerListenTopics = stopTimerListenTopic;
    }

    public void setRefreshOnShow(String refreshOnShow) {
        this.refreshOnShow = refreshOnShow;
    }

    public void setSeparateScripts(String separateScripts) {
        this.separateScripts = separateScripts;
    }

    public void setClosable(String closable) {
        this.closable = closable;
    }
    
    public void setPreload(String preload) {
        this.preload = preload;
    }
}
