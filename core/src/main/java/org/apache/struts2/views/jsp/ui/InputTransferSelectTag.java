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

import org.apache.struts2.components.Component;
import org.apache.struts2.components.InputTransferSelect;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * InputTransferSelect jsp tag.
 */
public class InputTransferSelectTag extends AbstractListTag {

    private static final long serialVersionUID = 250474334495763536L;

    protected String size;
    protected String multiple;

    protected String allowRemoveAll;
    protected String allowUpDown;

    protected String leftTitle;
    protected String rightTitle;

    protected String buttonCssClass;
    protected String buttonCssStyle;

    protected String addLabel;
    protected String removeLabel;
    protected String removeAllLabel;
    protected String upLabel;
    protected String downLabel;

    protected String headerKey;
    protected String headerValue;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new InputTransferSelect(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        InputTransferSelect inputTransferSelect = (InputTransferSelect) component;
        inputTransferSelect.setSize(size);
        inputTransferSelect.setMultiple(multiple);
        inputTransferSelect.setAllowRemoveAll(allowRemoveAll);
        inputTransferSelect.setAllowUpDown(allowUpDown);
        inputTransferSelect.setLeftTitle(leftTitle);
        inputTransferSelect.setRightTitle(rightTitle);

        inputTransferSelect.setButtonCssClass(buttonCssClass);
        inputTransferSelect.setButtonCssStyle(buttonCssStyle);

        inputTransferSelect.setAddLabel(addLabel);
        inputTransferSelect.setRemoveLabel(removeLabel);
        inputTransferSelect.setRemoveAllLabel(removeAllLabel);
        inputTransferSelect.setUpLabel(upLabel);
        inputTransferSelect.setDownLabel(downLabel);
        inputTransferSelect.setHeaderKey(headerKey);
        inputTransferSelect.setHeaderValue(headerValue);
    }


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMultiple() {
        return multiple;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public String getAllowRemoveAll() {
        return allowRemoveAll;
    }

    public void setAllowRemoveAll(String allowRemoveAll) {
        this.allowRemoveAll = allowRemoveAll;
    }

    public String getAllowUpDown() {
        return allowUpDown;
    }

    public void setAllowUpDown(String allowUpDown) {
        this.allowUpDown = allowUpDown;
    }

    public String getLeftTitle() {
        return leftTitle;
    }

    public void setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
    }

    public String getRightTitle() {
        return rightTitle;
    }

    public void setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
    }

    public String getButtonCssClass() {
        return buttonCssClass;
    }

    public void setButtonCssClass(String buttonCssClass) {
        this.buttonCssClass = buttonCssClass;
    }

    public String getButtonCssStyle() {
        return buttonCssStyle;
    }

    public void setButtonCssStyle(String buttonCssStyle) {
        this.buttonCssStyle = buttonCssStyle;
    }

    public String getAddLabel() {
        return addLabel;
    }

    public void setAddLabel(String addLabel) {
        this.addLabel = addLabel;
    }

    public String getRemoveLabel() {
        return removeLabel;
    }

    public void setRemoveLabel(String removeLabel) {
        this.removeLabel = removeLabel;
    }

    public String getRemoveAllLabel() {
        return removeAllLabel;
    }

    public void setRemoveAllLabel(String removeAllLabel) {
        this.removeAllLabel = removeAllLabel;
    }

    public String getUpLabel() {
        return upLabel;
    }

    public void setUpLabel(String upLabel) {
        this.upLabel = upLabel;
    }

    public String getDownLabel() {
        return downLabel;
    }

    public void setDownLabel(String downLabel) {
        this.downLabel = downLabel;
    }

    public String getHeaderKey() {
        return headerKey;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }
}
