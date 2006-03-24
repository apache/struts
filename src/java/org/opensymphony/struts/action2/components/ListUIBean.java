package com.opensymphony.webwork.components;

import com.opensymphony.webwork.util.ContainUtil;
import com.opensymphony.webwork.util.MakeIterator;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * DoubleListUIBean is the standard superclass of all webwork list handling components.
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision: 1.10 $
 * @since 2.2
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
     * @ww.tagattribute required="true"
     */
    public void setList(Object list) {
        this.list = list;
    }

    /**
     * Property of list objects to get field value from
     * @ww.tagattribute required="false"
     */
    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    /**
     * Property of list objects to get field content from
     * @ww.tagattribute required="false"
      */
    public void setListValue(String listValue) {
        this.listValue = listValue;
    }
    
    
    public void setThrowExceptionOnNullValueAttribute(boolean throwExceptionOnNullValueAttribute) {
    	this.throwExceptionOnNullValueAttribute = throwExceptionOnNullValueAttribute;
    }
}
