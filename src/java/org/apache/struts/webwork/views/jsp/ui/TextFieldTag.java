/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.TextField;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see TextField
 */
public class TextFieldTag extends AbstractUITag {
	
	private static final long serialVersionUID = 5811285953670562288L;

	protected String maxlength;
    protected String readonly;
    protected String size;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TextField(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        TextField textField = ((TextField) component);
        textField.setMaxlength(maxlength);
        textField.setReadonly(readonly);
        textField.setSize(size);
    }

    /**
     * @deprecated please use {@link #setMaxlength} instead
     */
    public void setMaxLength(String maxlength) {
        this.maxlength = maxlength;
    }

    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
