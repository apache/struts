/*
 * $Id: AbstractRemoteCallUIBean.java 508285 2007-02-16 02:42:24Z musachy $
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
package org.apache.struts2.dojo.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * AbstractRemoteCallUIBean is superclass for all components dealing with remote
 * calls.
 */
public abstract class AbstractRemoteCallUIBean extends ClosingUIBean implements RemoteUICallBean {

    protected String href;
    protected String errorText;
    protected String afterLoading;
    protected String beforeLoading;
    protected String executeScripts;
    protected String loadingText;
    protected String listenTopics;
    protected String handler;
    protected String formId;
    protected String formFilter;
    protected String notifyTopics;
    protected String showErrorTransportText;
    protected String indicator;
    protected String showLoadingText;

    public AbstractRemoteCallUIBean(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
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
        if (listenTopics != null)
            addParameter("listenTopics", findValue(listenTopics, String.class));
        if (notifyTopics != null)
            addParameter("notifyTopics", findValue(notifyTopics, String.class));
        if (handler != null)
            addParameter("handler", findString(handler));
        if (formId != null)
            addParameter("formId", findString(formId));
        if (formFilter != null)
            addParameter("formFilter", findString(formFilter));
        if (indicator != null)
            addParameter("indicator", findString(indicator));
        if (showErrorTransportText != null)
            addParameter("showErrorTransportText", findValue(showErrorTransportText, Boolean.class));
        else
            addParameter("showErrorTransportText", true);
        if (showLoadingText != null)
            addParameter("showLoadingText", findString(showLoadingText));
    }

    @Override
    @StrutsTagSkipInheritance
    public void setTheme(String theme) {
        super.setTheme(theme);
    }
    
    @Override
    public String getTheme() {
        return "ajax";
    }

    @StrutsTagAttribute(description="Topic that will trigger the remote call")
    public void setListenTopics(String listenTopics) {
        this.listenTopics = listenTopics;
    }

    @StrutsTagAttribute(description="The URL to call to obtain the content. Note: If used with ajax context, the value must be set as an url tag value.")
    public void setHref(String href) {
        this.href = href;
    }


    @StrutsTagAttribute(description="The text to display to the user if the is an error fetching the content")
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


    @StrutsTagAttribute(description="Javascript code in the fetched content will be executed", type="Boolean", defaultValue="false")
    public void setExecuteScripts(String executeScripts) {
        this.executeScripts = executeScripts;
    }

    @StrutsTagAttribute(description="Text to be shown while content is being fetched", defaultValue="Loading...")
    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }


    @StrutsTagAttribute(description="Javascript function name that will make the request")
    public void setHandler(String handler) {
        this.handler = handler;
    }


    @StrutsTagAttribute(description="Function name used to filter the fields of the form.")
    public void setFormFilter(String formFilter) {
        this.formFilter = formFilter;
    }

    @StrutsTagAttribute(description="Form id whose fields will be serialized and passed as parameters")
    public void setFormId(String formId) {
        this.formId = formId;
    }

    @StrutsTagAttribute(description="Topics that will published when the remote call completes")
    public void setNotifyTopics(String notifyTopics) {
        this.notifyTopics = notifyTopics;
    }


    @StrutsTagAttribute(description="Set whether errors will be shown or not", type="Boolean", defaultValue="true")
    public void setShowErrorTransportText(String showError) {
        this.showErrorTransportText = showError;
    }

    @StrutsTagAttribute(description="Id of element that will be shown while making request")
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    @StrutsTagAttribute(description="Show loading text on targets", type="Boolean", defaultValue="true")
    public void setShowLoadingText(String showLoadingText) {
        this.showLoadingText = showLoadingText;
    }

    @StrutsTagAttribute(description="The css class to use for element")
    public void setCssClass(String cssClass) {
        super.setCssClass(cssClass);
    }

    @StrutsTagAttribute(description="The css style to use for element")
    public void setCssStyle(String cssStyle) {
        super.setCssStyle(cssStyle);
    }

    @StrutsTagAttribute(description="The id to use for the element")
    public void setId(String id) {
        super.setId(id);
    }

    @StrutsTagAttribute(description="The name to set for element")
    public void setName(String name) {
        super.setName(name);
    }
}
