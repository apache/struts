/*
 * $Id$
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

import org.apache.struts2.components.Component;
import org.apache.struts2.components.DatePicker;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see DatePicker
 */
public class DatePickerTag extends TextFieldTag {

	private static final long serialVersionUID = 4054114507143447232L;
	
	protected String language;
    protected String format;
    protected String showstime;
    protected String singleclick;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DatePicker(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        final DatePicker datePicker = (DatePicker) component;
        datePicker.setLanguage(language);
        datePicker.setFormat(format);
        datePicker.setShowstime(showstime);
        datePicker.setSingleclick(singleclick);
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setShowstime(String showstime) {
        this.showstime = showstime;
    }

    public void setSingleclick(String singleclick) {
        this.singleclick = singleclick;
    }
}
