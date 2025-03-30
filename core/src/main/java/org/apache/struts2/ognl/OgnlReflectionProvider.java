/*
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
package org.apache.struts2.ognl;

import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.reflection.ReflectionException;
import org.apache.struts2.util.reflection.ReflectionProvider;
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

    @Override
    public Field getField(Class inClass, String name) {
        return OgnlRuntime.getField(inClass, name);
    }

    @Override
    public Method getGetMethod(Class targetClass, String propertyName)
            throws IntrospectionException, ReflectionException {
        return OgnlRuntime.getGetMethod(targetClass, propertyName);
    }

    @Override
    public Method getSetMethod(Class targetClass, String propertyName)
            throws IntrospectionException, ReflectionException {
        return OgnlRuntime.getSetMethod(null, targetClass, propertyName);
    }

    @Override
    public void setProperties(Map<String, ?> props, Object o, StrutsContext context) {
        ognlUtil.setProperties(props, o, context);
    }

    @Override
    public void setProperties(Map<String, ?> props, Object o, StrutsContext context, boolean throwPropertyExceptions) throws ReflectionException{
        ognlUtil.setProperties(props, o, context, throwPropertyExceptions);
    }

    @Override
    public void setProperties(Map<String, ?> properties, Object o) {
        ognlUtil.setProperties(properties, o);
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(Class targetClass,
            String propertyName) throws IntrospectionException,
            ReflectionException {
        try {
            return OgnlRuntime.getPropertyDescriptor(targetClass, propertyName);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public void copy(Object from, Object to, StrutsContext context,
            Collection<String> exclusions, Collection<String> inclusions) {
        copy(from, to, context, exclusions, inclusions, null);
    }

    @Override
    public void copy(Object from, Object to, StrutsContext context,
                     Collection<String> exclusions, Collection<String> inclusions, Class<?> editable) {
        ognlUtil.copy(from, to, context, exclusions, inclusions, editable);
    }

    @Override
    public Object getRealTarget(String property, StrutsContext context, Object root)
            throws ReflectionException {
        try {
            return ognlUtil.getRealTarget(property, context, root);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public void setProperty(String name, Object value, Object o, StrutsContext context) {
        ognlUtil.setProperty(name, value, o, context);
    }

    @Override
    public void setProperty(String name, Object value, Object o, StrutsContext context, boolean throwPropertyExceptions) {
        ognlUtil.setProperty(name, value, o, context, throwPropertyExceptions);
    }

    @Override
    public Map getBeanMap(Object source) throws IntrospectionException,
            ReflectionException {
        try {
            return ognlUtil.getBeanMap(source);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public Object getValue(String expression, StrutsContext context, Object root)
            throws ReflectionException {
        try {
            return ognlUtil.getValue(expression, context, root);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public void setValue(String expression, StrutsContext context, Object root,
            Object value) throws ReflectionException {
        try {
            ognlUtil.setValue(expression, context, root, value);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors(Object source) throws IntrospectionException {
        return ognlUtil.getPropertyDescriptors(source);
    }

}
