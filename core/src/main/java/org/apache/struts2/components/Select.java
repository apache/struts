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

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * &lt;a:select label="Pets"
 *        name="petIds"
 *        list="petDao.pets"
 *        listKey="id"
 *        listValue="name"
 *        multiple="true"
 *        size="3"
 *        required="true"
 * /&gt;
 *
 * &lt;a:select label="Months"
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
 * @a2.tag name="select" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.SelectTag"
 * description="Render a select element"
 */
public class Select extends ListUIBean {
    final public static String TEMPLATE = "select";

    protected String emptyOption;
    protected String headerKey;
    protected String headerValue;
    protected String multiple;
    protected String size;

    public Select(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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

    /**
     * Whether or not to add an empty (--) option after the header option
     * @a2.tagattribute required="false" type="Boolean" default="false"
     */
    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    /**
     * Key for first item in list. Must not be empty! "'-1'" and "''" is correct, "" is bad.
     * @a2.tagattribute required="false"
     */
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    /**
     * Value expression for first item in list
     * @a2.tagattribute required="false"
     */
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    /**
     * Creates a multiple select. The tag will pre-select multiple values if the values are passed as an Array (of appropriate types) via the value attribute. Passing a Collection may work too? Haven't tested this.
     * @a2.tagattribute required="false"  type="Boolean" default="false"
     */
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    /**
     * Size of the element box (# of elements to show)
     * @a2.tagattribute required="false" type="Integer"
     */
    public void setSize(String size) {
        this.size = size;
    }
}
