/*
 * $Id: DatePickerTag.java 451544 2006-09-30 05:38:02Z mrdon $
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
package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.DropdownDateTimePicker;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see DropdownDateTimePicker
 */
public class DropdownDateTimePickerTag extends DatePickerTag {

    private static final long serialVersionUID = -988878415165982315L;

    protected String iconPath;
    protected String formatLength;
    protected String displayFormat;
    protected String saveFormat;
    protected String toggleType;
    protected String toggleDuration;
    protected String type;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DropdownDateTimePicker(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        DropdownDateTimePicker picker = (DropdownDateTimePicker) component;
        picker.setIconPath(iconPath);
        picker.setFormatLength(formatLength);
        picker.setDisplayFormat(displayFormat);
        picker.setSaveFormat(saveFormat);
        picker.setToggleType(toggleType);
        picker.setToggleDuration(toggleDuration);
        picker.setType(type);
    }

    public void setDisplayFormat(String displayFormat) {
        this.displayFormat = displayFormat;
    }

    public void setFormatLength(String formatLength) {
        this.formatLength = formatLength;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setSaveFormat(String saveFormat) {
        this.saveFormat = saveFormat;
    }

    public void setToggleDuration(String toggleDuration) {
        this.toggleDuration = toggleDuration;
    }

    public void setToggleType(String toggleType) {
        this.toggleType = toggleType;
    }

    public void setType(String type) {
        this.type = type;
    }

}
