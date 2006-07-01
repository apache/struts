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

import org.apache.struts2.util.ContainUtil;
import org.apache.struts2.util.MakeIterator;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * DoubleListUIBean is the standard superclass of all Struts Action Framework list handling components.
 *
 * <p/>
 * 
 * <!-- START SNIPPET: javadoc -->
 * 
 * Note that the listkey and listvalue attribute will default to "key" and "value"
 * respectively only when the list attribute is evaluated to a Map or its decendant.
 * Other thing else, will result in listkey and listvalue to be null and not used.
 * 
 * <!-- END SNIPPET: javadoc -->
 *
 */
public abstract class ListUIBean extends UIBean {
    protected Object list;
    protected String listKey;
    protected String listValue;
    
    // indicate if an exception is to be thrown when value attribute is null
    protected boolean throwExceptionOnNullValueAttribute = false; 

    protected ListUIBean(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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
                    "You must specify a collection/array/map/enumeration/iterator. " +
                    "Example: people or people.{name}");
        	}
        	else {
        		// ww-1010, allows value with null value to be compatible with ww 
        		// 2.1.7 behaviour
        		value = findValue((list == null)?(String) list:list.toString());
        	}
        }

        if (value instanceof Collection) {
            addParameter("list", value);
        } else {
            addParameter("list", MakeIterator.convert(value));
        }

        if (value instanceof Collection) {
            addParameter("listSize", new Integer(((Collection) value).size()));
        } else if (value instanceof Map) {
            addParameter("listSize", new Integer(((Map) value).size()));
        } else if (value != null && value.getClass().isArray()) {
            addParameter("listSize", new Integer(Array.getLength(value)));
        }

        if (listKey != null) {
            addParameter("listKey", listKey);
        } else if (value instanceof Map) {
            addParameter("listKey", "key");
        }

        if (listValue != null) {
            addParameter("listValue", listValue);
        } else if (value instanceof Map) {
            addParameter("listValue", "value");
        }
    }

    public boolean contains(Object obj1, Object obj2) {
        return ContainUtil.contains(obj1, obj2);
    }

    protected Class getValueClassType() {
        return null; // don't convert nameValue to anything, we need the raw value
    }

    /**
     * Iterable source to populate from. If the list is a Map (key, value), the Map key will become the option "value" parameter and the Map value will become the option body.
     * @a2.tagattribute required="true"
     */
    public void setList(Object list) {
        this.list = list;
    }

    /**
     * Property of list objects to get field value from
     * @a2.tagattribute required="false"
     */
    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    /**
     * Property of list objects to get field content from
     * @a2.tagattribute required="false"
      */
    public void setListValue(String listValue) {
        this.listValue = listValue;
    }
    
    
    public void setThrowExceptionOnNullValueAttribute(boolean throwExceptionOnNullValueAttribute) {
    	this.throwExceptionOnNullValueAttribute = throwExceptionOnNullValueAttribute;
    }
}
