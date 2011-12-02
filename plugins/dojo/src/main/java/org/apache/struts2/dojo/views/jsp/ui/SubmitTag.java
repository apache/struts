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
import org.apache.struts2.dojo.components.Submit;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see Submit
 */
public class SubmitTag extends AbstractUITag {

    private static final long serialVersionUID = 2179281109958301343L;

    protected String method;
    protected String align;
    protected String type;
    protected String href;
    protected String listenTopics;
    protected String notifyTopics;
    protected String loadingText;
    protected String errorText;
    protected String executeScripts;
    protected String handler;
    protected String formId;
    protected String formFilter;
    protected String src;
    protected String showErrorTransportText;
    protected String indicator;
    protected String showLoadingText;
    protected String targets;
    protected String beforeNotifyTopics;
    protected String afterNotifyTopics;
    protected String errorNotifyTopics;
    protected String highlightColor;
    protected String highlightDuration;
    protected String validate;
    protected String ajaxAfterValidation;
    protected String separateScripts;
    protected String transport;
    protected String parseContent;
    
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Submit(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Submit submit = ((Submit) component);
        submit.setMethod(method);
        submit.setAlign(align);
        submit.setType(type);
        submit.setHref(href);
        submit.setListenTopics(listenTopics);
        submit.setLoadingText(loadingText);
        submit.setErrorText(errorText);
        submit.setExecuteScripts(executeScripts);
        submit.setHandler(handler);
        submit.setFormFilter(formFilter);
        submit.setFormId(formId);
        submit.setSrc(src);
        submit.setTargets(targets);
        submit.setNotifyTopics(notifyTopics);
        submit.setShowErrorTransportText(showErrorTransportText);
        submit.setIndicator(indicator);
        submit.setShowLoadingText(showLoadingText);
        submit.setAfterNotifyTopics(afterNotifyTopics);
        submit.setBeforeNotifyTopics(beforeNotifyTopics);
        submit.setErrorNotifyTopics(errorNotifyTopics);
        submit.setHighlightColor(highlightColor);
        submit.setHighlightDuration(highlightDuration);
        submit.setValidate(validate);
        submit.setAjaxAfterValidation(ajaxAfterValidation);
        submit.setSeparateScripts(separateScripts);
        submit.setTransport(transport);
        submit.setParseContent(parseContent);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setSrc(String src) {
        this.src = src;
    }

    public void setTargets(String targets) {
        this.targets = targets;
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

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public void setAjaxAfterValidation(String ajaxAfterValidation) {
        this.ajaxAfterValidation = ajaxAfterValidation;
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
