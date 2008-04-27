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

import org.apache.struts2.dojo.components.RemoteBean;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

public abstract class AbstractRemoteTag extends AbstractClosingTag {

    protected String href;
    protected String listenTopics;
    protected String notifyTopics;
    protected String loadingText;
    protected String errorText;
    protected String executeScripts;
    protected String handler;
    protected String formId;
    protected String formFilter;
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
    
    protected void populateParams() {
        super.populateParams();

        RemoteBean remote = (RemoteBean) component;
        remote.setHref(href);
        remote.setListenTopics(listenTopics);
        remote.setLoadingText(loadingText);
        remote.setErrorText(errorText);
        remote.setExecuteScripts(executeScripts);
        remote.setHandler(handler);
        remote.setFormFilter(formFilter);
        remote.setFormId(formId);
        remote.setNotifyTopics(notifyTopics);
        remote.setShowErrorTransportText(showErrorTransportText);
        remote.setIndicator(indicator);
        remote.setShowLoadingText(showLoadingText);
        remote.setAfterNotifyTopics(afterNotifyTopics);
        remote.setBeforeNotifyTopics(beforeNotifyTopics);
        remote.setErrorNotifyTopics(errorNotifyTopics);
        remote.setHighlightColor(highlightColor);
        remote.setHighlightDuration(highlightDuration);
        remote.setSeparateScripts(separateScripts);
        remote.setTransport(transport);
        remote.setParseContent(parseContent);
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    public void setListenTopics(String listenTopics) {
        this.listenTopics = listenTopics;
    }

    public void setExecuteScripts(String executeScripts) {
        this.executeScripts = executeScripts;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public void setFormFilter(String formFilter) {
        this.formFilter = formFilter;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public void setNotifyTopics(String notifyTopics) {
        this.notifyTopics = notifyTopics;
    }

    public void setShowErrorTransportText(String showErrorTransportText) {
        this.showErrorTransportText = showErrorTransportText;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public void setShowLoadingText(String showLoadingText) {
        this.showLoadingText = showLoadingText;
    }

    public void setAfterNotifyTopics(String afterNotifyTopics) {
        this.afterNotifyTopics = afterNotifyTopics;
    }

    public void setBeforeNotifyTopics(String beforeNotifyTopics) {
        this.beforeNotifyTopics = beforeNotifyTopics;
    }

    public void setErrorNotifyTopics(String errorNotifyTopics) {
        this.errorNotifyTopics = errorNotifyTopics;
    }

    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }

    public void setHighlightDuration(String highlightDuration) {
        this.highlightDuration = highlightDuration;
    }

    public void setSeparateScripts(String separateScripts) {
        this.separateScripts = separateScripts;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public void setParseContent(String parseContent) {
        this.parseContent = parseContent;
    }
}
