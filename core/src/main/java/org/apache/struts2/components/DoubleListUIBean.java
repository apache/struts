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

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * DoubleListUIBean is the standard superclass of all Struts double list handling components.
 *
 * <p/>
 *
 * <!-- START SNIPPET: javadoc -->
 *
 * Note that the doublelistkey and doublelistvalue attribute will default to "key" and "value"
 * respectively only when the doublelist attribute is evaluated to a Map or its decendant.
 * Other thing else, will result in doublelistkey and doublelistvalue to be null and not used.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 */
public abstract class DoubleListUIBean extends ListUIBean {

    protected String emptyOption;
    protected String headerKey;
    protected String headerValue;
    protected String multiple;
    protected String size;

    protected String doubleList;
    protected String doubleListKey;
    protected String doubleListValue;
    protected String doubleListCssClass;
    protected String doubleListCssStyle;
    protected String doubleListTitle;
    protected String doubleName;
    protected String doubleValue;
    protected String formName;

    protected String doubleId;
    protected String doubleDisabled;
    protected String doubleMultiple;
    protected String doubleSize;
    protected String doubleHeaderKey;
    protected String doubleHeaderValue;
    protected String doubleEmptyOption;

    protected String doubleCssClass;
    protected String doubleCssStyle;

    protected String doubleOnclick;
    protected String doubleOndblclick;
    protected String doubleOnmousedown;
    protected String doubleOnmouseup;
    protected String doubleOnmouseover;
    protected String doubleOnmousemove;
    protected String doubleOnmouseout;
    protected String doubleOnfocus;
    protected String doubleOnblur;
    protected String doubleOnkeypress;
    protected String doubleOnkeydown;
    protected String doubleOnkeyup;
    protected String doubleOnselect;
    protected String doubleOnchange;

    protected String doubleAccesskey;


    public DoubleListUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        //Object doubleName = null;

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


        if (doubleMultiple != null) {
            addParameter("doubleMultiple", findValue(doubleMultiple, Boolean.class));
        }

        if (doubleSize != null) {
            addParameter("doubleSize", findString(doubleSize));
        }

        if (doubleDisabled != null) {
            addParameter("doubleDisabled", findValue(doubleDisabled, Boolean.class));
        }

        if (doubleName != null) {
            addParameter("doubleName", findString(this.doubleName));
        }

        if (doubleList != null) {
            addParameter("doubleList", doubleList);
        }

        Object tmpDoubleList = findValue(doubleList);
        if (doubleListKey != null) {
            addParameter("doubleListKey", doubleListKey);
        }else if (tmpDoubleList instanceof Map) {
            addParameter("doubleListKey", "key");
        }

        if (doubleListValue != null) {
        	doubleListValue = stripExpressionIfAltSyntax(doubleListValue);

            addParameter("doubleListValue", doubleListValue);
        }else if (tmpDoubleList instanceof Map) {
            addParameter("doubleListValue", "value");
        }
        if (doubleListCssClass != null) {
            addParameter("doubleListCssClass", findString(doubleListCssClass));
        }
        if (doubleListCssStyle!= null) {
            addParameter("doubleListCssStyle", findString(doubleListCssStyle));
        }
        if (doubleListTitle != null) {
            addParameter("doubleListTitle", findString(doubleListTitle));
        }


        if (formName != null) {
            addParameter("formName", findString(formName));
        } else {
            // ok, let's look it up
            Component form = findAncestor(Form.class);
            if (form != null) {
                addParameter("formName", form.getParameters().get("name"));
            }
        }

        Class valueClazz = getValueClassType();

        if (valueClazz != null) {
            if (doubleValue != null) {
                addParameter("doubleNameValue", findValue(doubleValue, valueClazz));
            } else if (doubleName != null) {
                addParameter("doubleNameValue", findValue(doubleName, valueClazz));
            }
        } else {
            if (doubleValue != null) {
                addParameter("doubleNameValue", findValue(doubleValue));
            } else if (doubleName != null) {
                addParameter("doubleNameValue", findValue(doubleName));
            }
        }

        Form form = (Form) findAncestor(Form.class);
        if (doubleId != null) {
            // this check is needed for backwards compatibility with 2.1.x
        	addParameter("doubleId", findStringIfAltSyntax(doubleId));
        } else if (form != null) {
            addParameter("doubleId", form.getParameters().get("id") + "_" +escape(doubleName !=null ? findString(doubleName) : null));
        } else {
            addParameter("doubleId", escape(doubleName != null ? findString(doubleName) : null));
        }

        if (doubleOnclick != null) {
            addParameter("doubleOnclick", findString(doubleOnclick));
        }

        if (doubleOndblclick != null) {
            addParameter("doubleOndblclick", findString(doubleOndblclick));
        }

        if (doubleOnmousedown != null) {
            addParameter("doubleOnmousedown", findString(doubleOnmousedown));
        }

        if (doubleOnmouseup != null) {
            addParameter("doubleOnmouseup", findString(doubleOnmouseup));
        }

        if (doubleOnmouseover != null) {
            addParameter("doubleOnmouseover", findString(doubleOnmouseover));
        }

        if (doubleOnmousemove != null) {
            addParameter("doubleOnmousemove", findString(doubleOnmousemove));
        }

        if (doubleOnmouseout != null) {
            addParameter("doubleOnmouseout", findString(doubleOnmouseout));
        }

        if (doubleOnfocus != null) {
            addParameter("doubleOnfocus", findString(doubleOnfocus));
        }

        if (doubleOnblur != null) {
            addParameter("doubleOnblur", findString(doubleOnblur));
        }

        if (doubleOnkeypress != null) {
            addParameter("doubleOnkeypress", findString(doubleOnkeypress));
        }

        if (doubleOnkeydown != null) {
            addParameter("doubleOnkeydown", findString(doubleOnkeydown));
        }

        if (doubleOnselect != null) {
            addParameter("doubleOnselect", findString(doubleOnselect));
        }

        if (doubleOnchange != null) {
            addParameter("doubleOnchange", findString(doubleOnchange));
        }

        if (doubleCssClass != null) {
            addParameter("doubleCss", findString(doubleCssClass));
        }

        if (doubleCssStyle != null) {
            addParameter("doubleStyle", findString(doubleCssStyle));
        }

        if (doubleHeaderKey != null && doubleHeaderValue != null) {
            addParameter("doubleHeaderKey", findString(doubleHeaderKey));
            addParameter("doubleHeaderValue", findString(doubleHeaderValue));
        }

        if (doubleEmptyOption != null) {
            addParameter("doubleEmptyOption", findValue(doubleEmptyOption, Boolean.class));
        }

        if (doubleAccesskey != null) {
            addParameter("doubleAccesskey", findString(doubleAccesskey));
        }
    }

    @StrutsTagAttribute(description="The second iterable source to populate from.", required=true)
    public void setDoubleList(String doubleList) {
        this.doubleList = doubleList;
    }

    @StrutsTagAttribute(description="The key expression to use for second list")
    public void setDoubleListKey(String doubleListKey) {
        this.doubleListKey = doubleListKey;
    }

    @StrutsTagAttribute(description="The value expression to use for second list")
    public void setDoubleListValue(String doubleListValue) {
        this.doubleListValue = doubleListValue;
    }

    @StrutsTagAttribute(description = "Property of second list objects to get css class from")
     public void setDoubleListCssClass(String doubleListCssClass) {
        this.doubleListCssClass = doubleListCssClass;
    }

    @StrutsTagAttribute(description = "Property of second list objects to get css style from")
    public void setDoubleListCssStyle(String doubleListCssStyle) {
        this.doubleListCssStyle = doubleListCssStyle;
    }

    @StrutsTagAttribute(description = "Property of second list objects to get title from")
    public void setDoubleListTitle(String doubleListTitle) {
        this.doubleListTitle = doubleListTitle;
    }

    @StrutsTagAttribute(description="The name for complete component", required=true)
    public void setDoubleName(String doubleName) {
        this.doubleName = doubleName;
    }

    @StrutsTagAttribute(description="The value expression for complete component")
    public void setDoubleValue(String doubleValue) {
        this.doubleValue = doubleValue;
    }

    @StrutsTagAttribute(description="The form name this component resides in and populates to")
    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormName() {
        return formName;
    }

    @StrutsTagAttribute(description="The css class for the second list")
    public void setDoubleCssClass(String doubleCssClass) {
        this.doubleCssClass = doubleCssClass;
    }

    public String getDoubleCssClass() {
        return doubleCssClass;
    }

    @StrutsTagAttribute(description="The css style for the second list")
    public void setDoubleCssStyle(String doubleCssStyle) {
        this.doubleCssStyle = doubleCssStyle;
    }

    public String getDoubleCssStyle() {
        return doubleCssStyle;
    }

    @StrutsTagAttribute(description="The header key for the second list")
    public void setDoubleHeaderKey(String doubleHeaderKey) {
        this.doubleHeaderKey = doubleHeaderKey;
    }

    public String getDoubleHeaderKey() {
        return doubleHeaderKey;
    }

    @StrutsTagAttribute(description="The header value for the second list")
    public void setDoubleHeaderValue(String doubleHeaderValue) {
        this.doubleHeaderValue = doubleHeaderValue;
    }

    public String getDoubleHeaderValue() {
        return doubleHeaderValue;
    }

    @StrutsTagAttribute(description="Decides if the second list will add an empty option")
    public void setDoubleEmptyOption(String doubleEmptyOption) {
        this.doubleEmptyOption = doubleEmptyOption;
    }

    public String getDoubleEmptyOption() {
        return this.doubleEmptyOption;
    }


    public String getDoubleDisabled() {
        return doubleDisabled;
    }

    @StrutsTagAttribute(description="Decides if a disable attribute should be added to the second list")
    public void setDoubleDisabled(String doubleDisabled) {
        this.doubleDisabled = doubleDisabled;
    }

    public String getDoubleId() {
        return doubleId;
    }

    @StrutsTagAttribute(description="The id of the second list")
    public void setDoubleId(String doubleId) {
        this.doubleId = doubleId;
    }

    public String getDoubleMultiple() {
        return doubleMultiple;
    }

    @StrutsTagAttribute(description=" Decides if multiple attribute should be set on the second list")
    public void setDoubleMultiple(String doubleMultiple) {
        this.doubleMultiple = doubleMultiple;
    }

    public String getDoubleOnblur() {
        return doubleOnblur;
    }

    @StrutsTagAttribute(description="Set the onblur attribute of the second list")
    public void setDoubleOnblur(String doubleOnblur) {
        this.doubleOnblur = doubleOnblur;
    }

    public String getDoubleOnchange() {
        return doubleOnchange;
    }

    @StrutsTagAttribute(description="Set the onchange attribute of the second list")
    public void setDoubleOnchange(String doubleOnchange) {
        this.doubleOnchange = doubleOnchange;
    }

    public String getDoubleOnclick() {
        return doubleOnclick;
    }

    @StrutsTagAttribute(description="Set the onclick attribute of the second list")
    public void setDoubleOnclick(String doubleOnclick) {
        this.doubleOnclick = doubleOnclick;
    }

    public String getDoubleOndblclick() {
        return doubleOndblclick;
    }

    @StrutsTagAttribute(description="Set the ondbclick attribute of the second list")
    public void setDoubleOndblclick(String doubleOndblclick) {
        this.doubleOndblclick = doubleOndblclick;
    }

    public String getDoubleOnfocus() {
        return doubleOnfocus;
    }

    @StrutsTagAttribute(description="Set the onfocus attribute of the second list")
    public void setDoubleOnfocus(String doubleOnfocus) {
        this.doubleOnfocus = doubleOnfocus;
    }

    public String getDoubleOnkeydown() {
        return doubleOnkeydown;
    }

    @StrutsTagAttribute(description="Set the onkeydown attribute of the second list")
    public void setDoubleOnkeydown(String doubleOnkeydown) {
        this.doubleOnkeydown = doubleOnkeydown;
    }

    public String getDoubleOnkeypress() {
        return doubleOnkeypress;
    }

    @StrutsTagAttribute(description="Set the onkeypress attribute of the second list")
    public void setDoubleOnkeypress(String doubleOnkeypress) {
        this.doubleOnkeypress = doubleOnkeypress;
    }

    public String getDoubleOnkeyup() {
        return doubleOnkeyup;
    }

    @StrutsTagAttribute(description="Set the onkeyup attribute of the second list")
    public void setDoubleOnkeyup(String doubleOnkeyup) {
        this.doubleOnkeyup = doubleOnkeyup;
    }

    public String getDoubleOnmousedown() {
        return doubleOnmousedown;
    }

    @StrutsTagAttribute(description="Set the onmousedown attribute of the second list")
    public void setDoubleOnmousedown(String doubleOnmousedown) {
        this.doubleOnmousedown = doubleOnmousedown;
    }

    public String getDoubleOnmousemove() {
        return doubleOnmousemove;
    }

    @StrutsTagAttribute(description="Set the onmousemove attribute of the second list")
    public void setDoubleOnmousemove(String doubleOnmousemove) {
        this.doubleOnmousemove = doubleOnmousemove;
    }

    public String getDoubleOnmouseout() {
        return doubleOnmouseout;
    }

    @StrutsTagAttribute(description="Set the onmouseout attribute of the second list")
    public void setDoubleOnmouseout(String doubleOnmouseout) {
        this.doubleOnmouseout = doubleOnmouseout;
    }

    public String getDoubleOnmouseover() {
        return doubleOnmouseover;
    }

    @StrutsTagAttribute(description="Set the onmouseover attribute of the second list")
    public void setDoubleOnmouseover(String doubleOnmouseover) {
        this.doubleOnmouseover = doubleOnmouseover;
    }

    public String getDoubleOnmouseup() {
        return doubleOnmouseup;
    }

    @StrutsTagAttribute(description="Set the onmouseup attribute of the second list")
    public void setDoubleOnmouseup(String doubleOnmouseup) {
        this.doubleOnmouseup = doubleOnmouseup;
    }

    public String getDoubleOnselect() {
        return doubleOnselect;
    }

    @StrutsTagAttribute(description="Set the onselect attribute of the second list")
    public void setDoubleOnselect(String doubleOnselect) {
        this.doubleOnselect = doubleOnselect;
    }

    public String getDoubleSize() {
        return doubleSize;
    }

    @StrutsTagAttribute(description="Set the size attribute of the second list")
    public void setDoubleSize(String doubleSize) {
        this.doubleSize = doubleSize;
    }

    public String getDoubleList() {
        return doubleList;
    }

    public String getDoubleListKey() {
        return doubleListKey;
    }

    public String getDoubleListValue() {
        return doubleListValue;
    }

    public String getDoubleName() {
        return doubleName;
    }

    public String getDoubleValue() {
        return doubleValue;
    }

    @StrutsTagAttribute(description="Decides of an empty option is to be inserted in the second list", type="Boolean", defaultValue="false")
    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    @StrutsTagAttribute(description="Set the header key of the second list. Must not be empty! " +
                "'-1' and '' is correct, '' is bad.")
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    @StrutsTagAttribute(description=" Set the header value of the second list")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @StrutsTagAttribute(description="Creates a multiple select. " +
                "The tag will pre-select multiple values if the values are passed as an Array " +
                "(of appropriate types) via the value attribute.")
    public void setMultiple(String multiple) {
        // TODO: Passing a Collection may work too?
        this.multiple = multiple;
    }

    @StrutsTagAttribute(description="Size of the element box (# of elements to show)", type="Integer")
    public void setSize(String size) {
        this.size = size;
    }

    @StrutsTagAttribute(description="Set the html accesskey attribute.")
    public void setDoubleAccesskey(String doubleAccesskey) {
        this.doubleAccesskey = doubleAccesskey;
    }
}
