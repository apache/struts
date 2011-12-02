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
import org.apache.struts2.dojo.components.DateTimePicker;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see DateTimePicker
 */
public class DateTimePickerTag extends AbstractUITag {

    private static final long serialVersionUID = 4054114507143447232L;

    protected String displayWeeks;
    protected String adjustWeeks;
    protected String startDate;
    protected String endDate;
    protected String weekStartsOn;
    protected String staticDisplay;
    protected String dayWidth;
    protected String language;
    
    protected String iconPath;
    protected String formatLength;
    protected String displayFormat;
    protected String toggleType;
    protected String toggleDuration;
    protected String type;
    protected String templateCssPath;
    protected String valueNotifyTopics;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DateTimePicker(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        final DateTimePicker dateTimePicker = (DateTimePicker) component;
        dateTimePicker.setAdjustWeeks(adjustWeeks);
        dateTimePicker.setDayWidth(dayWidth);
        dateTimePicker.setDisplayWeeks(displayWeeks);
        dateTimePicker.setEndDate(endDate);
        dateTimePicker.setStartDate(startDate);
        dateTimePicker.setStaticDisplay(staticDisplay);
        dateTimePicker.setWeekStartsOn(weekStartsOn);
        dateTimePicker.setLanguage(language);
        dateTimePicker.setIconPath(iconPath);
        dateTimePicker.setFormatLength(formatLength);
        dateTimePicker.setDisplayFormat(displayFormat);
        dateTimePicker.setToggleType(toggleType);
        dateTimePicker.setToggleDuration(toggleDuration);
        dateTimePicker.setType(type);
        dateTimePicker.setTemplateCssPath(templateCssPath);
        dateTimePicker.setValueNotifyTopics(valueNotifyTopics);
        dateTimePicker.setDisabled(disabled);
    }

    public void setAdjustWeeks(String adjustWeeks) {
        this.adjustWeeks = adjustWeeks;
    }

    public void setDayWidth(String dayWidth) {
        this.dayWidth = dayWidth;
    }

    public void setDisplayWeeks(String displayWeeks) {
        this.displayWeeks = displayWeeks;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStaticDisplay(String staticDisplay) {
        this.staticDisplay = staticDisplay;
    }

    public void setWeekStartsOn(String weekStartsOn) {
        this.weekStartsOn = weekStartsOn;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public void setToggleDuration(String toggleDuration) {
        this.toggleDuration = toggleDuration;
    }

    public void setToggleType(String toggleType) {
        this.toggleType = toggleType;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setTemplateCssPath(String templateCssPath) {
        this.templateCssPath = templateCssPath;
    }

    public void setValueNotifyTopics(String valueNotifyTopics) {
        this.valueNotifyTopics = valueNotifyTopics;
    }
}
