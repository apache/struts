/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Submit;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Submit
 */
public class SubmitTag extends AbstractUITag {
    protected String action;
    protected String method;
    protected String align;
    protected String resultDivId;
    protected String onLoadJS;
    protected String notifyTopics;
    protected String listenTopics;
    protected String preInvokeJS;
    protected String type;
    protected String src;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Submit(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Submit submit = ((Submit) component);
        submit.setAction(action);
        submit.setMethod(method);
        submit.setAlign(align);
        submit.setResultDivId(resultDivId);
        submit.setOnLoadJS(onLoadJS);
        submit.setNotifyTopics(notifyTopics);
        submit.setListenTopics(listenTopics);
        submit.setPreInvokeJS(preInvokeJS);
        submit.setType(type);
        submit.setSrc(src);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public void setResultDivId(String resultDivId) {
        this.resultDivId = resultDivId;
    }

    public void setOnLoadJS(String onLoadJS) {
        this.onLoadJS = onLoadJS;
    }

    public void setNotifyTopics(String notifyTopics) {
        this.notifyTopics = notifyTopics;
    }

    public void setListenTopics(String listenTopics) {
        this.listenTopics = listenTopics;
    }

    public void setPreInvokeJS(String preInvokeJS) {
        this.preInvokeJS = preInvokeJS;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
