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
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import ognl.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Utility class that provides common access to the Ognl APIs for
 * setting and getting properties from objects (usually Actions).
 *
 * @author Jason Carreira
 */
public class OgnlUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OgnlUtil.class);
    private ConcurrentHashMap<String, Object> expressions = new ConcurrentHashMap<String, Object>();
    private final ConcurrentHashMap<Class, BeanInfo> beanInfoCache = new ConcurrentHashMap<Class, BeanInfo>();

    private TypeConverter defaultConverter;
    static boolean devMode = false;
    static boolean enableExpressionCache = true;

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.defaultConverter = new OgnlTypeConverterWrapper(conv);
    }

    @Inject("devMode")
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }

    @Inject("enableOGNLExpressionCache")
    public static void setEnableExpressionCache(String cache) {
       enableExpressionCache = "true".equals(cache);
    }

    /**
     * Sets the object's properties using the default type converter, defaulting to not throw
     * exceptions for problems setting the properties.
     *
     * @param props   the properties being set
     * @param o       the object
     * @param context the action context
     */
    public void setProperties(Map<String, ?> props, Object o, Map<String, Object> context) {
        setProperties(props, o, context, false);
    }

    /**
     * Sets the object's properties using the default type converter.
     *
     * @param props                   the properties being set
     * @param o                       the object
     * @param context                 the action context
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the properties
     */
    public void setProperties(Map<String, ?> props, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws ReflectionException{
        if (props == null) {
            return;
        }

        Ognl.setTypeConverter(context, getTypeConverterFromContext(context));

        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, o);

        for (Map.Entry<String, ?> entry : props.entrySet()) {
            String expression = entry.getKey();
            internalSetProperty(expression, entry.getValue(), o, context, throwPropertyExceptions);
        }

        Ognl.setRoot(context, oldRoot);
    }

    /**
     * Sets the properties on the object using the default context, defaulting to not throwing
     * exceptions for problems setting the properties.
     *
     * @param properties
     * @param o
     */
    public void setProperties(Map<String, ?> properties, Object o) {
        setProperties(properties, o, false);
    }

    /**
     * Sets the properties on the object using the default context.
     *
     * @param properties              the property map to set on the object
     * @param o                       the object to set the properties into
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the properties
     */
    public void setProperties(Map<String, ?> properties, Object o, boolean throwPropertyExceptions) {
        Map context = Ognl.createDefaultContext(o);
        setProperties(properties, o, context, throwPropertyExceptions);
    }

    /**
     * Sets the named property to the supplied value on the Object, defaults to not throwing
     * property exceptions.
     *
     * @param name    the name of the property to be set
     * @param value   the value to set into the named property
     * @param o       the object upon which to set the property
     * @param context the context which may include the TypeConverter
     */
    public void setProperty(String name, Object value, Object o, Map<String, Object> context) {
        setProperty(name, value, o, context, false);
    }

    /**
     * Sets the named property to the supplied value on the Object.
     *
     * @param name                    the name of the property to be set
     * @param value                   the value to set into the named property
     * @param o                       the object upon which to set the property
     * @param context                 the context which may include the TypeConverter
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the property
     */
    public void setProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) {
        Ognl.setTypeConverter(context, getTypeConverterFromContext(context));

        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, o);

        internalSetProperty(name, value, o, context, throwPropertyExceptions);

        Ognl.setRoot(context, oldRoot);
    }

    /**
     * Looks for the real target with the specified property given a root Object which may be a
     * CompoundRoot.
     *
     * @return the real target or null if no object can be found with the specified property
     */
    public Object getRealTarget(String property, Map<String, Object> context, Object root) throws OgnlException {
        //special keyword, they must be cutting the stack
        if ("top".equals(property)) {
            return root;
        }

        if (root instanceof CompoundRoot) {
            // find real target
            CompoundRoot cr = (CompoundRoot) root;

            try {
                for (Object target : cr) {
                    if (
                            OgnlRuntime.hasSetProperty((OgnlContext) context, target, property)
                                    ||
                                    OgnlRuntime.hasGetProperty((OgnlContext) context, target, property)
                                    ||
                                    OgnlRuntime.getIndexedPropertyType((OgnlContext) context, target.getClass(), property) != OgnlRuntime.INDEXED_PROPERTY_NONE
                            ) {
                        return target;
                    }
                }
            } catch (IntrospectionException ex) {
                throw new ReflectionException("Cannot figure out real target class", ex);
            }

            return null;
        }

        return root;
    }


    /**
     * Wrapper around Ognl.setValue() to handle type conversion for collection elements.
     * Ideally, this should be handled by OGNL directly.
     */
    public void setValue(String name, Map<String, Object> context, Object root, Object value) throws OgnlException {
        Ognl.setValue(compile(name), context, root, value);
    }

    public Object getValue(String name, Map<String, Object> context, Object root) throws OgnlException {
        return Ognl.getValue(compile(name), context, root);
    }

    public Object getValue(String name, Map<String, Object> context, Object root, Class resultType) throws OgnlException {
        return Ognl.getValue(compile(name), context, root, resultType);
    }


    public Object compile(String expression) throws OgnlException {
        if (enableExpressionCache) {
            Object o = expressions.get(expression);
            if (o == null) {
                o = Ognl.parseExpression(expression);
                expressions.put(expression, o);
            }
            return o;
        } else
            return Ognl.parseExpression(expression);
    }

    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * using specified type converter, or {@link com.opensymphony.xwork2.conversion.impl.XWorkConverter} if none
     * is specified.
     *
     * @param from       the source object
     * @param to         the target object
     * @param context    the action context we're running under
     * @param exclusions collection of method names to excluded from copying ( can be null)
     * @param inclusions collection of method names to included copying  (can be null)
     *                   note if exclusions AND inclusions are supplied and not null nothing will get copied.
     */
    public void copy(Object from, Object to, Map<String, Object> context, Collection<String> exclusions, Collection<String> inclusions) {
        if (from == null || to == null) {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("Attempting to copy from or to a null source. This is illegal and is bein skipped. This may be due to an error in an OGNL expression, action chaining, or some other event.");
            }

            return;
        }

        TypeConverter conv = getTypeConverterFromContext(context);
        Map contextFrom = Ognl.createDefaultContext(from);
        Ognl.setTypeConverter(contextFrom, conv);
        Map contextTo = Ognl.createDefaultContext(to);
        Ognl.setTypeConverter(contextTo, conv);

        PropertyDescriptor[] fromPds;
        PropertyDescriptor[] toPds;

        try {
            fromPds = getPropertyDescriptors(from);
            toPds = getPropertyDescriptors(to);
        } catch (IntrospectionException e) {
            LOG.error("An error occured", e);

            return;
        }

        Map<String, PropertyDescriptor> toPdHash = new HashMap<String, PropertyDescriptor>();

        for (PropertyDescriptor toPd : toPds) {
            toPdHash.put(toPd.getName(), toPd);
        }

        for (PropertyDescriptor fromPd : fromPds) {
            if (fromPd.getReadMethod() != null) {
                boolean copy = true;
                if (exclusions != null && exclusions.contains(fromPd.getName())) {
                    copy = false;
                } else if (inclusions != null && !inclusions.contains(fromPd.getName())) {
                    copy = false;
                }

                if (copy == true) {
                    PropertyDescriptor toPd = toPdHash.get(fromPd.getName());
                    if ((toPd != null) && (toPd.getWriteMethod() != null)) {
                        try {
                            Object expr = compile(fromPd.getName());
                            Object value = Ognl.getValue(expr, contextFrom, from);
                            Ognl.setValue(expr, contextTo, to, value);
                        } catch (OgnlException e) {
                            // ignore, this is OK
                        }
                    }

                }

            }

        }
    }


    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * using specified type converter, or {@link com.opensymphony.xwork2.conversion.impl.XWorkConverter} if none
     * is specified.
     *
     * @param from    the source object
     * @param to      the target object
     * @param context the action context we're running under
     */
    public void copy(Object from, Object to, Map<String, Object> context) {
        copy(from, to, context, null, null);
    }

    /**
     * Get's the java beans property descriptors for the given source.
     *
     * @param source the source object.
     * @return property descriptors.
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    public PropertyDescriptor[] getPropertyDescriptors(Object source) throws IntrospectionException {
        BeanInfo beanInfo = getBeanInfo(source);
        return beanInfo.getPropertyDescriptors();
    }


    /**
     * Get's the java beans property descriptors for the given class.
     *
     * @param clazz the source object.
     * @return property descriptors.
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    public PropertyDescriptor[] getPropertyDescriptors(Class clazz) throws IntrospectionException {
        BeanInfo beanInfo = getBeanInfo(clazz);
        return beanInfo.getPropertyDescriptors();
    }

    /**
     * Creates a Map with read properties for the given source object.
     * <p/>
     * If the source object does not have a read property (i.e. write-only) then
     * the property is added to the map with the value <code>here is no read method for property-name</code>.
     *
     * @param source the source object.
     * @return a Map with (key = read property name, value = value of read property).
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     * @throws OgnlException          is thrown by OGNL if the property value could not be retrieved
     */
    public Map getBeanMap(Object source) throws IntrospectionException, OgnlException {
        Map beanMap = new HashMap();
        Map sourceMap = Ognl.createDefaultContext(source);
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(source);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getDisplayName();
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                Object expr = compile(propertyName);
                Object value = Ognl.getValue(expr, sourceMap, source);
                beanMap.put(propertyName, value);
            } else {
                beanMap.put(propertyName, "There is no read method for " + propertyName);
            }
        }
        return beanMap;
    }

    /**
     * Get's the java bean info for the given source object. Calls getBeanInfo(Class c).
     *
     * @param from the source object.
     * @return java bean info.
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    public BeanInfo getBeanInfo(Object from) throws IntrospectionException {
        return getBeanInfo(from.getClass());
    }


    /**
     * Get's the java bean info for the given source.
     *
     * @param clazz the source class.
     * @return java bean info.
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    public BeanInfo getBeanInfo(Class clazz) throws IntrospectionException {
        synchronized (beanInfoCache) {
            BeanInfo beanInfo;
            beanInfo = beanInfoCache.get(clazz);
            if (beanInfo == null) {
                beanInfo = Introspector.getBeanInfo(clazz, Object.class);
                beanInfoCache.put(clazz, beanInfo);
            }
            return beanInfo;
        }
    }

    void internalSetProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws ReflectionException{
        try {
            setValue(name, context, o, value);
        } catch (OgnlException e) {
            Throwable reason = e.getReason();
            String msg = "Caught OgnlException while setting property '" + name + "' on type '" + o.getClass().getName() + "'.";
            Throwable exception = (reason == null) ? e : reason;

            if (throwPropertyExceptions) {
                throw new ReflectionException(msg, exception);
            } else {
                if (devMode) {
                    LOG.warn(msg, exception);
                }
            }
        }
    }

    TypeConverter getTypeConverterFromContext(Map<String, Object> context) {
        /*ValueStack stack = (ValueStack) context.get(ActionContext.VALUE_STACK);
        Container cont = (Container)stack.getContext().get(ActionContext.CONTAINER);
        if (cont != null) {
            return new OgnlTypeConverterWrapper(cont.getInstance(XWorkConverter.class));
        } else {
            throw new IllegalArgumentException("Cannot find type converter in context map");
        }
        */
        return defaultConverter;
    }
}
