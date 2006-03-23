/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.TextArea;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see TextArea
 */
public class TextareaTag extends AbstractUITag {
    protected String cols;
    protected String readonly;
    protected String rows;
    protected String wrap;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TextArea(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        TextArea textArea = ((TextArea) component);
        textArea.setCols(cols);
        textArea.setReadonly(readonly);
        textArea.setRows(rows);
        textArea.setWrap(wrap);
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public void setWrap(String wrap) {
        this.wrap = wrap;
    }

}
