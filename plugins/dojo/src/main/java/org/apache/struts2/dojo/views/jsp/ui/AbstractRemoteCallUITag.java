/*
 * $Id: AbstractRemoteCallUITag.java 508285 2007-02-16 02:42:24Z musachy $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.dojo.views.jsp.ui;

import org.apache.struts2.dojo.components.RemoteUICallBean;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

public abstract class AbstractRemoteCallUITag extends AbstractClosingTag {

    protected String href;
    protected String listenTopics;
    protected String notifyTopics;
    protected String loadingText;
    protected String errorText;
    protected String beforeLoading;
    protected String afterLoading;
    protected String executeScripts;
    protected String handler;
    protected String formId;
    protected String formFilter;
    protected String showErrorTransportText;
    protected String indicator;
    protected String showLoadingText;

    protected void populateParams() {
        super.populateParams();

        RemoteUICallBean remote = (RemoteUICallBean) component;
        remote.setHref(href);
        remote.setListenTopics(listenTopics);
        remote.setLoadingText(loadingText);
        remote.setErrorText(errorText);
        remote.setAfterLoading(afterLoading);
        remote.setBeforeLoading(beforeLoading);
        remote.setExecuteScripts(executeScripts);
        remote.setHandler(handler);
        remote.setFormFilter(formFilter);
        remote.setFormId(formId);
        remote.setNotifyTopics(notifyTopics);
        remote.setShowErrorTransportText(showErrorTransportText);
        remote.setIndicator(indicator);
        remote.setShowLoadingText(showLoadingText);
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

    public void setAfterLoading(String afterLoading) {
        this.afterLoading = afterLoading;
    }

    public void setBeforeLoading(String beforeLoading) {
        this.beforeLoading = beforeLoading;
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

    /**
     * @param indicator The indicator to set.
     */
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public void setShowLoadingText(String showLoadingText) {
        this.showLoadingText = showLoadingText;
    }
}
