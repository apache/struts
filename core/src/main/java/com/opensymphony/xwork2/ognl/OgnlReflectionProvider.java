package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class OgnlReflectionProvider implements ReflectionProvider {
    
    private OgnlUtil ognlUtil;
    
    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }

    public Field getField(Class inClass, String name) {
        return OgnlRuntime.getField(inClass, name);
    }

    public Method getGetMethod(Class targetClass, String propertyName)
            throws IntrospectionException, ReflectionException {
        try {
            return OgnlRuntime.getGetMethod(null, targetClass, propertyName);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    public Method getSetMethod(Class targetClass, String propertyName)
            throws IntrospectionException, ReflectionException {
        try {
            return OgnlRuntime.getSetMethod(null, targetClass, propertyName);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    public void setProperties(Map<String, ?> props, Object o, Map<String, Object> context) {
        ognlUtil.setProperties(props, o, context);
    }

    public void setProperties(Map<String, ?> props, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws ReflectionException{
        ognlUtil.setProperties(props, o, context, throwPropertyExceptions);
        
    }

    public void setProperties(Map<String, ?> properties, Object o) {
        ognlUtil.setProperties(properties, o);
    }

    public PropertyDescriptor getPropertyDescriptor(Class targetClass,
            String propertyName) throws IntrospectionException,
            ReflectionException {
        try {
            return OgnlRuntime.getPropertyDescriptor(targetClass, propertyName);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    public void copy(Object from, Object to, Map<String, Object> context,
            Collection<String> exclusions, Collection<String> inclusions) {
        ognlUtil.copy(from, to, context, exclusions, inclusions);
    }

    public Object getRealTarget(String property, Map<String, Object> context, Object root)
            throws ReflectionException {
        try {
            return ognlUtil.getRealTarget(property, context, root);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    public void setProperty(String name, Object value, Object o, Map<String, Object> context) {
        ognlUtil.setProperty(name, value, o, context);
    }

    public void setProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) {
        ognlUtil.setProperty(name, value, o, context, throwPropertyExceptions);
    }

    public Map getBeanMap(Object source) throws IntrospectionException,
            ReflectionException {
        try {
            return ognlUtil.getBeanMap(source);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    public Object getValue(String expression, Map<String, Object> context, Object root)
            throws ReflectionException {
        try {
            return ognlUtil.getValue(expression, context, root);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    public void setValue(String expression, Map<String, Object> context, Object root,
            Object value) throws ReflectionException {
        try {
            Ognl.setValue(expression, context, root, value);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    public PropertyDescriptor[] getPropertyDescriptors(Object source)
            throws IntrospectionException {
        return ognlUtil.getPropertyDescriptors(source);
    }

}
