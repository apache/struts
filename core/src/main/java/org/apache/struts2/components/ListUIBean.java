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
import org.apache.struts2.util.ContainUtil;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * DoubleListUIBean is the standard superclass of all Struts list handling components.
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: javadoc -->
 * <p/>
 * Note that the listkey and listvalue attribute will default to "key" and "value"
 * respectively only when the list attribute is evaluated to a Map or its decendant.
 * Everything else will result in listkey and listvalue to be null and not used.
 * <p/>
 * <!-- END SNIPPET: javadoc -->
 */
public abstract class ListUIBean extends UIBean {
    protected Object list;
    protected String listKey;
    protected String listValueKey;
    protected String listValue;
    protected String listLabelKey;
    protected String listCssClass;
    protected String listCssStyle;
    protected String listTitle;

    // indicate if an exception is to be thrown when value attribute is null
    protected boolean throwExceptionOnNullValueAttribute = false;

    protected ListUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public void evaluateExtraParams() {
        Object value = null;

        if (list == null) {
            list = parameters.get("list");
        }

        if (list instanceof String) {
            value = findValue((String) list);
        } else if (list instanceof Collection) {
            value = list;
        } else if (MakeIterator.isIterable(list)) {
            value = MakeIterator.convert(list);
        }
        if (value == null) {
            if (throwExceptionOnNullValueAttribute) {
                // will throw an exception if not found
                value = findValue((list == null) ? (String) list : list.toString(), "list",
                        "The requested list key '" + list + "' could not be resolved as a collection/array/map/enumeration/iterator type. " +
                                "Example: people or people.{name}");
            } else {
                // ww-1010, allows value with null value to be compatible with ww
                // 2.1.7 behaviour
                value = findValue((list == null) ? (String) list : list.toString());
            }
        }

        if (value instanceof Collection) {
            addParameter("list", value);
        } else {
            addParameter("list", MakeIterator.convert(value));
        }

        if (value instanceof Collection) {
            addParameter("listSize", Integer.valueOf(((Collection) value).size()));
        } else if (value instanceof Map) {
            addParameter("listSize", Integer.valueOf(((Map) value).size()));
        } else if (value != null && value.getClass().isArray()) {
            addParameter("listSize", Integer.valueOf(Array.getLength(value)));
        }

        if (listKey != null) {
            listKey = stripExpressionIfAltSyntax(listKey);
            addParameter("listKey", listKey);
        } else if (value instanceof Map) {
            addParameter("listKey", "key");
        }

        if (listValueKey != null) {
            listValueKey = stripExpressionIfAltSyntax(listValueKey);
            addParameter("listValueKey", listValueKey);
        }

        if (listValue != null) {
            listValue = stripExpressionIfAltSyntax(listValue);
            addParameter("listValue", listValue);
        } else if (value instanceof Map) {
            addParameter("listValue", "value");
        }

        if (listLabelKey != null) {
            listLabelKey = stripExpressionIfAltSyntax(listLabelKey);
            addParameter("listLabelKey", listLabelKey);
        }

        if (listCssClass != null && listCssClass.trim().length() > 0) {
            addParameter("listCssClass", listCssClass);
        }

        if (listCssStyle != null && listCssStyle.trim().length() > 0) {
            addParameter("listCssStyle", listCssStyle);
        }

        if (listTitle != null && listTitle.trim().length() > 0) {
            addParameter("listTitle", listTitle);
        }
    }

    public boolean contains(Object obj1, Object obj2) {
        return ContainUtil.contains(obj1, obj2);
    }

    protected Class getValueClassType() {
        return null; // don't convert nameValue to anything, we need the raw value
    }

    @StrutsTagAttribute(description = "Iterable source to populate from. If the list is a Map (key, value), the Map key will become the option 'value'" +
            " parameter and the Map value will become the option body.", required = true)
    public void setList(Object list) {
        this.list = list;
    }

    @StrutsTagAttribute(description = " Property of list objects to get field value from")
    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    @StrutsTagAttribute(description = " Property of list objects to get field value label from")
    public void setListValueKey(String listValueKey) {
        this.listValueKey = listValueKey;
    }

    @StrutsTagAttribute(description = "Property of list objects to get field content from")
    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    @StrutsTagAttribute(description = "Property of list objects to be used to lookup for localised version of field label")
    public void setListLabelKey(String listLabelKey) {
        this.listLabelKey = listLabelKey;
    }

    @StrutsTagAttribute(description = "Property of list objects to get css class from")
    public void setListCssClass(String listCssClass) {
        this.listCssClass = listCssClass;
    }

    @StrutsTagAttribute(description = "Property of list objects to get css style from")
    public void setListCssStyle(String listCssStyle) {
        this.listCssStyle = listCssStyle;
    }

    @StrutsTagAttribute(description = "Property of list objects to get title from")
    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }


    public void setThrowExceptionOnNullValueAttribute(boolean throwExceptionOnNullValueAttribute) {
        this.throwExceptionOnNullValueAttribute = throwExceptionOnNullValueAttribute;
    }
}
