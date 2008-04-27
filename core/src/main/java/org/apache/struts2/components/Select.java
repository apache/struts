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

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Render an HTML input tag of type select.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;s:select label="Pets"
 *        name="petIds"
 *        list="petDao.pets"
 *        listKey="id"
 *        listValue="name"
 *        multiple="true"
 *        size="3"
 *        required="true"
 *        value="%{petDao.pets.{id}}"
 * /&gt;
 *
 * &lt;s:select label="Months"
 *        name="months"
 *        headerKey="-1" headerValue="Select Month"
 *        list="#{'01':'Jan', '02':'Feb', [...]}"
 *        value="selectedMonth"
 *        required="true"
 * /&gt;
 *
 * // The month id (01, 02, ...) returned by the getSelectedMonth() call
 * // against the stack will be auto-selected
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <p/>
 *
 * <!-- START SNIPPET: exnote -->
 *
 * Note: For any of the tags that use lists (select probably being the most ubiquitous), which uses the OGNL list
 * notation (see the "months" example above), it should be noted that the map key created (in the months example,
 * the '01', '02', etc.) is typed. '1' is a char, '01' is a String, "1" is a String. This is important since if
 * the value returned by your "value" attribute is NOT the same type as the key in the "list" attribute, they
 * WILL NOT MATCH, even though their String values may be equivalent. If they don't match, nothing in your list
 * will be auto-selected.<p/>
 *
 * <!-- END SNIPPET: exnote -->
 *
 */
@StrutsTag(
    name="select",
    tldTagClass="org.apache.struts2.views.jsp.ui.SelectTag",
    description="Render a select element",
    allowDynamicAttributes=true)
public class Select extends ListUIBean {
    final public static String TEMPLATE = "select";

    protected String emptyOption;
    protected String headerKey;
    protected String headerValue;
    protected String multiple;
    protected String size;

    public Select(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (emptyOption != null) {
            addParameter("emptyOption", findValue(emptyOption, Boolean.class));
        }

        if (multiple != null) {
            addParameter("multiple", findValue(multiple, Boolean.class));
        }

        if (size != null) {
            addParameter("size", findString(size));
        }

        if ((headerKey != null) && (headerValue != null)) {
            addParameter("headerKey", findString(headerKey));
            addParameter("headerValue", findString(headerValue));
        }
    }

    @StrutsTagAttribute(description="Whether or not to add an empty (--) option after the header option", type="Boolean", defaultValue="false")
    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    @StrutsTagAttribute(description=" Key for first item in list. Must not be empty! '-1' and '' is correct, '' is bad.")
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    @StrutsTagAttribute(description="Value expression for first item in list")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @StrutsTagAttribute(description=" Creates a multiple select. The tag will pre-select multiple values" +
                " if the values are passed as an Array or a Collection(of appropriate types) via the value attribute. If one of the keys equals" +
                " one of the values in the Collection or Array it wil be selected", type="Boolean", defaultValue="false")
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    @StrutsTagAttribute(description="Size of the element box (# of elements to show)", type="Integer")
    public void setSize(String size) {
        this.size = size;
    }
}
