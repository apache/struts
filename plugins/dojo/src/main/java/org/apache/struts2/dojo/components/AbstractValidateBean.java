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

package org.apache.struts2.dojo.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Form;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Base class for tags that perform AJAX validation
 */
public abstract class AbstractValidateBean extends AbstractRemoteBean {
    protected String validate;
    protected String ajaxAfterValidation;
    
    public AbstractValidateBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (validate != null) 
            addParameter("validate", findValue(validate, Boolean.class));
        if (ajaxAfterValidation != null)
            addParameter("ajaxAfterValidation", findValue(ajaxAfterValidation, Boolean.class));
        
        Form form = (Form) findAncestor(Form.class);
        if (form != null) 
            addParameter("parentTheme", form.getTheme());
    }
    
    @StrutsTagAttribute(description = "Perform Ajax validation. 'ajaxValidation' interceptor must be applied to action", type="Boolean", 
        defaultValue = "false")
    public void setValidate(String validate) {
        this.validate = validate;
    }
    
    @StrutsTagAttribute(description = "Make an asynchronous request if validation succeeds. Only valid if 'validate' is 'true'", type="Boolean", 
        defaultValue = "false")
    public void setAjaxAfterValidation(String ajaxAfterValidation) {
        this.ajaxAfterValidation = ajaxAfterValidation;
    }
}