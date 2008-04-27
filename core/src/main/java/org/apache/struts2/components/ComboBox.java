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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.util.MakeIterator;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * The combo box is basically an HTML INPUT of type text and HTML SELECT grouped together to give you a combo box
 * functionality. You can place text in the INPUT control by using the SELECT control or type it in directly in
 * the text field.<p/>
 *
 * In this example, the SELECT will be populated from id=year attribute. Counter is itself an Iterator. It will
 * span from first to last. The population is done via javascript, and requires that this tag be surrounded by a
 * &lt;form&gt;.<p/>
 *
 * Note that unlike the &lt;s:select/&gt; tag, there is no ability to define the individual &lt;option&gt; tags' id attribute
 * or content separately. Each of these is simply populated from the toString() method of the list item. Presumably
 * this is because the select box isn't intended to actually submit useful data, but to assist the user in filling
 * out the text field.<p/>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * JSP:
 * &lt;-- Example One --&gt;
 * &lt;s:bean name="struts.util.Counter" var="year"&gt;
 *   &lt;s:param name="first" value="text('firstBirthYear')"/&gt;
 *   &lt;s:param name="last" value="2000"/&gt;
 *
 *   &lt;s:combobox label="Birth year" size="6" maxlength="4" name="birthYear" list="#year"/&gt;
 * &lt;/s:bean&gt;
 *
 * &lt;-- Example Two --&gt;
 * <s:combobox
 *     label="My Favourite Fruit"
 *     name="myFavouriteFruit"
 *     list="{'apple','banana','grape','pear'}"
 *     headerKey="-1"
 *     headerValue="--- Please Select ---"
 *     emptyOption="true"
 *     value="banana" />
 *
 * &lt;-- Example Two --&gt;
 * <s:combobox
 *    label="My Favourite Color"
 *    name="myFavouriteColor"
 *    list="#{'red':'red','green':'green','blue':'blue'}"
 *    headerKey="-1"
 *    headerValue="--- Please Select ---"
 *    emptyOption="true"
 *    value="green" />
 *
 * Velocity:
 * #tag( ComboBox "label=Birth year" "size=6" "maxlength=4" "name=birthYear" "list=#year" )
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(name="combobox", tldTagClass="org.apache.struts2.views.jsp.ui.ComboBoxTag", description="Widget that fills a text box from a select")
public class ComboBox extends TextField {
    final public static String TEMPLATE = "combobox";

    protected String list;
    protected String listKey;
    protected String listValue;
    protected String headerKey;
    protected String headerValue;
    protected String emptyOption;


    public ComboBox(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        Object value = findListValue();

        if (headerKey != null) {
            addParameter("headerKey", findString(headerKey));
        }
        if (headerValue != null) {
            addParameter("headerValue", findString(headerValue));
        }
        if (emptyOption != null) {
            addParameter("emptyOption", findValue(emptyOption, Boolean.class));
        }

        if (value != null) {
            if (value instanceof Collection) {
                Collection tmp = (Collection) value;
                addParameter("list", tmp);
                if (listKey != null) {
                    addParameter("listKey", listKey);
                }
                if (listValue != null) {
                    addParameter("listValue", listValue);
                }
            } else if (value instanceof Map) {
                Map tmp = (Map) value;
                addParameter("list", MakeIterator.convert(tmp));
                addParameter("listKey", "key");
                addParameter("listValue", "value");                
            } else { // also covers "if (value.getClass().isArray())"
                Iterator i = MakeIterator.convert(value);
                addParameter("list", i);
                if (listKey != null) {
                    addParameter("listKey", listKey);
                }
                if (listValue != null) {
                    addParameter("listValue", listValue);
                }
            }
        }
    }

    protected Object findListValue() {
        return findValue(list, "list",
                "You must specify a collection/array/map/enumeration/iterator. " +
                "Example: people or people.{name}");
    }

    @StrutsTagAttribute(description="Iteratable source to populate from. " +
                "If this is missing, the select widget is simply not displayed.", required=true)
    public void setList(String list) {
        this.list = list;
    }

    @StrutsTagAttribute(description="Decide if an empty option is to be inserted. Default false.")
    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    @StrutsTagAttribute(description="Set the header key for the header option.")
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    @StrutsTagAttribute(description="Set the header value for the header option.")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @StrutsTagAttribute(description="Set the key used to retrive the option key.")
    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    @StrutsTagAttribute(description="Set the value used to retrive the option value.")
    public void setListValue(String listValue) {
        this.listValue = listValue;
    }


}
