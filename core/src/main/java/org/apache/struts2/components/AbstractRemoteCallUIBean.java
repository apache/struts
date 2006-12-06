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
package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * AbstractRemoteCallUIBean is superclass for all components dealing with remote calls.
 *
 */
public abstract class AbstractRemoteCallUIBean extends ClosingUIBean implements RemoteUICallBean {

    protected String href;
    protected String errorText;
    protected String afterLoading;
    protected String beforeLoading;
    protected String executeScripts;
    protected String loadingText;
    protected String refreshListenTopic;
    protected String handler;
    protected String formId;
    protected String formFilter;

    public AbstractRemoteCallUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (href != null)
            addParameter("href", findString(href));
        if (errorText != null)
            addParameter("errorText", findString(errorText));
        if (loadingText != null)
            addParameter("loadingText", findString(loadingText));
        if (afterLoading != null)
            addParameter("afterLoading", findString(afterLoading));
        if (beforeLoading != null)
            addParameter("beforeLoading", findString(beforeLoading));
        if (executeScripts != null)
            addParameter("executeScripts", findValue(executeScripts, Boolean.class));
        if (refreshListenTopic != null)
            addParameter("refreshListenTopic", findValue(refreshListenTopic, String.class));
        if (handler != null)
            addParameter("handler", findString(handler));
        if (formId != null)
            addParameter("formId", findString(formId));
        if (formFilter != null)
            addParameter("formFilter", findString(formFilter));
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setRefreshListenTopic(java.lang.String)
     */
    public void setRefreshListenTopic(String refreshListenTopic) {
        this.refreshListenTopic = refreshListenTopic;
    }

    /**
     * The theme to use for the element. <b>This tag will usually use the ajax theme.</b>
     * @s.tagattribute required="false" type="String"
     */
    public void setTheme(String theme) {
        super.setTheme(theme);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setHref(java.lang.String)
     */
    public void setHref(String href) {
        this.href = href;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setErrorText(java.lang.String)
     */
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setAfterLoading(java.lang.String)
     */
    public void setAfterLoading(String afterLoading) {
        this.afterLoading = afterLoading;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setBeforeLoading(java.lang.String)
     */
    public void setBeforeLoading(String beforeLoading) {
        this.beforeLoading = beforeLoading;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setExecuteScripts(java.lang.String)
     */
    public void setExecuteScripts(String executeScripts) {
        this.executeScripts = executeScripts;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setLoadingText(java.lang.String)
     */
    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setHandler(java.lang.String)
     */
    public void setHandler(String handler) {
        this.handler = handler;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setFormFilter(java.lang.String)
     */
    public void setFormFilter(String formFilter) {
        this.formFilter = formFilter;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setFormId(java.lang.String)
     */
    public void setFormId(String formId) {
        this.formId = formId;
    }
}
