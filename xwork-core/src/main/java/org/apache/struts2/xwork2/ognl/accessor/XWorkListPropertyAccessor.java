/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.ListPropertyAccessor;
import ognl.OgnlException;
import ognl.PropertyAccessor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Overrides the list property accessor so in the case of trying
 * to add properties of a given bean and the JavaBean is not present,
 * this class will create the necessary blank JavaBeans.
 *
 * @author Gabriel Zimmerman
 */
public class XWorkListPropertyAccessor extends ListPropertyAccessor {

    private XWorkCollectionPropertyAccessor _sAcc = new XWorkCollectionPropertyAccessor();
    
    private XWorkConverter xworkConverter;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;
    private OgnlUtil ognlUtil;
    
    @Inject("java.util.Collection")
    public void setXWorkCollectionPropertyAccessor(PropertyAccessor acc) {
        this._sAcc = (XWorkCollectionPropertyAccessor) acc;
    }
    
    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }
    
    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }
    
    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer ot) {
        this.objectTypeDeterminer = ot;
    }
    
    @Inject
    public void setOgnlUtil(OgnlUtil util) {
        this.ognlUtil = util;
    }

    @Override
    public Object getProperty(Map context, Object target, Object name)
            throws OgnlException {

        if (ReflectionContextState.isGettingByKeyProperty(context)
                || name.equals(XWorkCollectionPropertyAccessor.KEY_PROPERTY_FOR_CREATION)) {
            return _sAcc.getProperty(context, target, name);
        }	else if (name instanceof String) {
            return super.getProperty(context, target, name);
        }
        ReflectionContextState.updateCurrentPropertyPath(context, name);
        //System.out.println("Entering XWorkListPropertyAccessor. Name: " + name);
        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        
        if (name instanceof Number
                && ReflectionContextState.isCreatingNullObjects(context)
                && objectTypeDeterminer.shouldCreateIfNew(lastClass,lastProperty,target,null,true)) {

            //System.out.println("Getting index from List");
            List list = (List) target;
            int index = ((Number) name).intValue();
            int listSize = list.size();

            if (lastClass == null || lastProperty == null) {
                return super.getProperty(context, target, name);
            }
            Class beanClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, name);
            if (listSize <= index) {
                Object result = null;

                for (int i = listSize; i < index; i++) {

                    list.add(null);

                }
                try {
                    list.add(index, result = objectFactory.buildBean(beanClass, context));
                } catch (Exception exc) {
                    throw new XWorkException(exc);
                }
                return result;
            } else if (list.get(index) == null) {
                Object result = null;
                try {
                    list.set(index, result = objectFactory.buildBean(beanClass, context));
                } catch (Exception exc) {
                    throw new XWorkException(exc);
                }
                return result;
            }
        }
        return super.getProperty(context, target, name);
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value)
            throws OgnlException {

        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        Class convertToClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, name);

        if (name instanceof String && value.getClass().isArray()) {
            // looks like the input game in the form of "someList.foo" and
            // we are expected to define the index values ourselves.
            // So let's do it:

            Collection c = (Collection) target;
            Object[] values = (Object[]) value;
            for (Object v : values) {
                try {
                    Object o = objectFactory.buildBean(convertToClass, context);
                    ognlUtil.setValue((String) name, context, o, v);
                    c.add(o);
                } catch (Exception e) {
                    throw new OgnlException("Error converting given String values for Collection.", e);
                }
            }

            // we don't want to do the normal list property setting now, since we've already done the work
            // just return instead
            return;
        }

        Object realValue = getRealValue(context, value, convertToClass);

        if (target instanceof List && name instanceof Number) {
            //make sure there are enough spaces in the List to set
            List list = (List) target;
            int listSize = list.size();
            int count = ((Number) name).intValue();
            if (count >= listSize) {
                for (int i = listSize; i <= count; i++) {
                    list.add(null);
                }
            }
        }

        super.setProperty(context, target, name, realValue);
    }

    private Object getRealValue(Map context, Object value, Class convertToClass) {
        if (value == null || convertToClass == null) {
            return value;
        }
        return xworkConverter.convertValue(context, value, convertToClass);
    }
}
