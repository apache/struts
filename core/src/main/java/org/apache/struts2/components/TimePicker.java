/*
 * $Id: DatePicker.java 424152 2006-07-21 01:04:41Z husted $
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
package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.OgnlValueStack;

/**
 * @version $Date$ $Id$
 */
public class TimePicker extends TextField {

	final public static String TEMPLATE = "timepicker";
	
	protected String format;
	protected String templatePath;
	protected String templateCssPath;
	protected String timeIconPath;
	protected String size;
	
	public TimePicker(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
		super(stack, request, response);
	}

	protected void evaluateExtraParams() {
		super.evaluateExtraParams();
		
		if (format != null) {
            addParameter("format", findString(format));
        }
        if (timeIconPath != null) {
        	addParameter("timeIconPath", timeIconPath);
        }
        if (templatePath != null) {
        	addParameter("templatePath", templatePath);
        }
        if (templateCssPath != null) {
        	addParameter("templateCssPath", templateCssPath);
        }
        if (size != null) {
        	addParameter("size", findValue(size, Integer.class));
        }
	}
	
	protected String getDefaultTemplate() {
        return TEMPLATE;
    }
	
	/**
     * The format to use for time field.
     * @a2.tagattribute required="false" type="String" default="Dateformat specified by language preset (%Y/%m/%d for en)"
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    /**
     * The time picker icon path
     * @a2.tagattribute required="false" type="String" default="/struts/dojo/struts/widgets/dateIcon.gif"
     */
    public void setTimeIconPath(String timeIconPath) {
    	this.timeIconPath = timeIconPath;
    }
    
    /**
     * The time picker template path.
     * @a2.tagattribute required="false" type="String" 
     */
    public void setTemplatePath(String templatePath) {
    	this.templatePath = templatePath;
    }
    
    /**
     * The time picker template css path.
     * @a2.tagattribute required="false" type="String"
     */
    public void setTemplateCssPath(String templateCssPath) {
    	this.templateCssPath = templateCssPath;
    }
    
    /**
     * The time picker text field size.
     * @a2.tagattribute required="false" type="String"
     */
    public void setSize(String size) {
    	this.size = size;
    }
	
}
