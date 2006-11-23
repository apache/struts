/*
 * $Id$
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
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.RemoteUICallBean;

public abstract class AbstractRemoteCallUITag extends AbstractClosingTag {

    protected String href;
    protected String refreshListenTopic;
    protected String loadingText;
    protected String errorText;
    protected String beforeLoading;
    protected String afterLoading;
    protected String executeScripts;
    protected String handler;
    protected String formId;
    protected String formFilter;

    protected void populateParams() {
        super.populateParams();

        RemoteUICallBean remote = (RemoteUICallBean) component;
        remote.setHref(href);
        remote.setRefreshListenTopic(refreshListenTopic);
        remote.setLoadingText(loadingText);
        remote.setErrorText(errorText);
        remote.setAfterLoading(afterLoading);
        remote.setBeforeLoading(beforeLoading);
        remote.setExecuteScripts(executeScripts);
        remote.setHandler(handler);
        remote.setFormFilter(formFilter);
        remote.setFormId(formId);
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

    public void setRefreshListenTopic(String refreshListenTopic) {
        this.refreshListenTopic = refreshListenTopic;
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
}
