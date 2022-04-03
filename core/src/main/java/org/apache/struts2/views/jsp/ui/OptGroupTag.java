/*
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

import org.apache.struts2.components.Component;
import org.apache.struts2.components.OptGroup;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.opensymphony.xwork2.util.ValueStack;

public class OptGroupTag extends ComponentTagSupport {

    private static final long serialVersionUID = 7367401003498678762L;

    protected String list;
    protected String label;
    protected String disabled;
    protected String listKey;
    protected String listValue;
    protected String listCssClass;
    protected String listCssStyle;
    protected String listTitle;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new OptGroup(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();

        OptGroup optGroup = (OptGroup) component;
        optGroup.setList(list);
        optGroup.setLabel(label);
        optGroup.setDisabled(disabled);
        optGroup.setListKey(listKey);
        optGroup.setListValue(listValue);
        optGroup.setListCssClass(listCssClass);
        optGroup.setListCssStyle(listCssStyle);
        optGroup.setListTitle(listTitle);
    }

    public void setList(String list) {
        this.list = list;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    public void setListCssClass(String listCssClass) {
        this.listCssClass = listCssClass;
    }

    public void setListCssStyle(String listCssStyle) {
        this.listCssStyle = listCssStyle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    @Override
    /**
     * Must declare the setter at the descendant Tag class level in order for the tag handler to locate the method.
     */
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
       if (getPerformClearTagStateForTagPoolingServers() == false) {
            return;  // If flag is false (default setting), do not perform any state clearing.
        }
        super.clearTagStateForTagPoolingServers();
        this.list = null;
        this.label = null;
        this.disabled = null;
        this.listKey = null;
        this.listValue = null;
        this.listCssClass = null;
        this.listCssStyle = null;
        this.listTitle = null;
    }

}
