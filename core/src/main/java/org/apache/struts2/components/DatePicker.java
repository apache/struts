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
package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Renders datepicker element.</p>
 * Format supported by this component are:-
 * <table border="1">
 *   <tr>
 *   	<td>Format</td>
 *    	<td>Description</td>
 *   </tr>
 *   <tr>
 *   	<td>#dd</td>
 *   	<td>Display day in two digits format</td>
 *   </tr>	
 *   <tr>
 *   	<td>#d</td>
 *   	<td>Try to display day in one digit format, if cannot use 2 digit format</td>
 *   </tr>
 *   <tr>
 *   	<td>#MM</td>
 *      <td>Display month in two digits format</td>
 *   </tr>
 *   <tr>
 *   	<td>#M</td>
 *   	<td>Try to display month in one digits format, if cannot use 2 digit format</td>
 *   </tr>
 *   <tr>
 *   	<td>#yyyy</td>
 *      <td>Display year in four digits format</td>
 *   </tr>
 *   <tr>
 *   	<td>#yy</td>
 *      <td>Display the last two digits of the yaer</td>
 *   </tr>
 *   <tr>
 *   	<td>#y</td>
 *      <td>Display the last digits of the year</td>
 *   </tr>
 * </table>
 *
 * <p/>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: expl1 -->
 *
 * Example 1:
 *     &lt;s:datepicker name="order.date" label="Order Date" /&gt;
 * Example 2:
 *     &lt;s:datepicker name="delivery.date" label="Delivery Date" format="#yyyy-#MM-#dd"  /&gt;
 *     
 * <!-- END SNIPPET: expl1 -->
 * </pre>
 * <p/>
 *
 * <!-- START SNIPPET: expldesc2 -->
 *
 * The css could be changed by using the following :-
 *
 * <!-- END SNIPPET: expldesc2 -->
 * 
 * <pre>
 * <!-- START SNIPPET: expl2 -->
 * 
 * &lt;s:datepicker name="birthday" label="Birthday" templateCss="...." /&gt;
 * 
 * <!-- END SNIPPET: expl2 -->
 * </pre>
 *
 * @s.tag name="datepicker" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.DatePickerTag"
 * description="Render datepicker"
 */
public class DatePicker extends TextField {

    final public static String TEMPLATE = "datepicker";

    protected String format;
    protected String dateIconPath;
    protected String templatePath;
    protected String templateCssPath;
    protected String size;

    public DatePicker(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        super.evaluateParams();

        if (format != null) {
            addParameter("format", findString(format));
        }
        if (dateIconPath != null) {
        	addParameter("dateIconPath", dateIconPath);
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

    /**
     * The format to use for date field.
     * @s.tagattribute required="false" type="String" default="Dateformat specified by language preset (%Y/%m/%d for en)"
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    /**
     * The date picker icon path
     * @s.tagattribute required="false" type="String" default="/struts/dojo/struts/widgets/dateIcon.gif"
     */
    public void setDateIconPath(String dateIconPath) {
    	this.dateIconPath = dateIconPath;
    }
    
    /**
     * The datepicker template path.
     * @s.tagattribute required="false" type="String"
     */
    public void setTemplatePath(String templatePath) {
    	this.templatePath = templatePath;
    }
    
    /**
     * The datepicker template css path.
     * @s.tagattribute required="false" type="String"
     */
    public void setTemplateCssPath(String templateCssPath) {
    	this.templateCssPath = templateCssPath;
    }
    
    /**
     * The datepicker text field size.
     * @s.tagattribute required="false" type="String"
     */
    public void setSize(String size) {
    	this.size = size;
    }
}
