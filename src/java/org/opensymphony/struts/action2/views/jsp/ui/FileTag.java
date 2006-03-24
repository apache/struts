/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.File;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see File
 */
public class FileTag extends AbstractUITag {
    protected String accept;
    protected String size;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new File(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        File file = ((File) component);
        file.setAccept(accept);
        file.setSize(size);
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
