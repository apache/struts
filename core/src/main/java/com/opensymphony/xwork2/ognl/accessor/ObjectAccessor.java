/**
 * 
 */
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import java.util.Map;

public class ObjectAccessor extends ObjectPropertyAccessor {
    @Override
    public Object getProperty(Map map, Object o, Object o1) throws OgnlException {
        Object obj = super.getProperty(map, o, o1);

        map.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, o.getClass());
        map.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, o1.toString());
        ReflectionContextState.updateCurrentPropertyPath(map, o1);
        return obj;
    }

    @Override
    public void setProperty(Map map, Object o, Object o1, Object o2) throws OgnlException {
        super.setProperty(map, o, o1, o2);
    }
}