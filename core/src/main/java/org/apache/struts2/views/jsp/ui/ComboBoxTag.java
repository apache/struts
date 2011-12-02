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

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ComboBox;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see ComboBox
 */
public class ComboBoxTag extends TextFieldTag {

    private static final long serialVersionUID = 3509392460170385605L;

    protected String list;
    protected String listKey;
    protected String listValue;
    protected String headerKey;
    protected String headerValue;
    protected String emptyOption;

    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ComboBox(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((ComboBox) component).setList(list);
        ((ComboBox) component).setListKey(listKey);
        ((ComboBox) component).setListValue(listValue);
        ((ComboBox) component).setHeaderKey(headerKey);
        ((ComboBox) component).setHeaderValue(headerValue);
        ((ComboBox) component).setEmptyOption(emptyOption);
    }

    public void setList(String list) {
        this.list = list;
    }
}
