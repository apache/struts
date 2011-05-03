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
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CreateIfNull;
import com.opensymphony.xwork2.util.Element;
import com.opensymphony.xwork2.util.Key;
import com.opensymphony.xwork2.util.KeyProperty;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * This {@link ObjectTypeDeterminer} looks at the <b>Class-conversion.properties</b> for entries that indicated what
 * objects are contained within Maps and Collections. For Collections, such as Lists, the element is specified using the
 * pattern <b>Element_xxx</b>, where xxx is the field name of the collection property in your action or object. For
 * Maps, both the key and the value may be specified by using the pattern <b>Key_xxx</b> and <b>Element_xxx</b>,
 * respectively.
 *
 * <p/> From WebWork 2.1.x, the <b>Collection_xxx</b> format is still supported and honored, although it is deprecated
 * and will be removed eventually.
 *
 * <!-- END SNIPPET: javadoc -->
 * 
 *
 * @author Gabriel Zimmerman
 */
public class DefaultObjectTypeDeterminer implements ObjectTypeDeterminer {
    

    protected static final Logger LOG = LoggerFactory.getLogger(DefaultObjectTypeDeterminer.class);

    public static final String KEY_PREFIX = "Key_";
    public static final String ELEMENT_PREFIX = "Element_";
    public static final String KEY_PROPERTY_PREFIX = "KeyProperty_";
    public static final String CREATE_IF_NULL_PREFIX = "CreateIfNull_";
    public static final String DEPRECATED_ELEMENT_PREFIX = "Collection_";
    
    private ReflectionProvider reflectionProvider;
    private XWorkConverter xworkConverter;
    
    @Inject
    public DefaultObjectTypeDeterminer(@Inject XWorkConverter conv, @Inject ReflectionProvider prov) {
        this.reflectionProvider = prov;
        this.xworkConverter = conv;
        
    }
    
    /**
     * Determines the key class by looking for the value of @Key annotation for the given class.
     * If no annotation is found, the key class is determined by using the generic parametrics.
     *
     * As fallback, it determines the key class by looking for the value of Key_${property} in the properties
     * file for the given class.
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @see com.opensymphony.xwork2.conversion.ObjectTypeDeterminer#getKeyClass(Class, String)
     */
    public Class getKeyClass(Class parentClass, String property) {
        Key annotation = getAnnotation(parentClass, property, Key.class);

        if (annotation != null) {
            return annotation.value();
        }

        Class clazz = getClass(parentClass, property, false);

        if (clazz != null) {
            return clazz;
        }

        return (Class) xworkConverter.getConverter(parentClass, KEY_PREFIX + property);
    }


    /**
     * Determines the element class by looking for the value of @Element annotation for the given
     * class.
     * If no annotation is found, the element class is determined by using the generic parametrics.
     *
     * As fallback, it determines the key class by looking for the value of Element_${property} in the properties
     * file for the given class. Also looks for the deprecated Collection_${property}
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @see com.opensymphony.xwork2.conversion.ObjectTypeDeterminer#getElementClass(Class, String, Object)
     */
    public Class getElementClass(Class parentClass, String property, Object key) {
        Element annotation = getAnnotation(parentClass, property, Element.class);

        if (annotation != null) {
            return annotation.value();
        }

        Class clazz = getClass(parentClass, property, true);

        if (clazz != null) {
            return clazz;
        }

        clazz = (Class) xworkConverter.getConverter(parentClass, ELEMENT_PREFIX + property);

        if (clazz == null) {
            clazz = (Class) xworkConverter
                    .getConverter(parentClass, DEPRECATED_ELEMENT_PREFIX + property);

            if (LOG.isInfoEnabled() && clazz != null) {
                LOG.info("The Collection_xxx pattern for collection type conversion is deprecated. Please use Element_xxx!");
            }
        }
        return clazz;

    }


    /**
     * Determines the key property for a Collection by getting it from the @KeyProperty annotation.
     *
     * As fallback, it determines the String key property for a Collection by getting it from the conversion properties
     * file using the KeyProperty_ prefix. KeyProperty_${property}=somePropertyOfBeansInTheSet
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @see com.opensymphony.xwork2.conversion.ObjectTypeDeterminer#getKeyProperty(Class, String)
     */
    public String getKeyProperty(Class parentClass, String property) {
        KeyProperty annotation = getAnnotation(parentClass, property, KeyProperty.class);

        if (annotation != null) {
            return annotation.value();
        }

        return (String) xworkConverter.getConverter(parentClass, KEY_PROPERTY_PREFIX + property);
    }


    /**
     * Determines the createIfNull property for a Collection or Map by getting it from the @CreateIfNull annotation.
     *
     * As fallback, it determines the boolean CreateIfNull property for a Collection or Map by getting it from the
     * conversion properties file using the CreateIfNull_ prefix. CreateIfNull_${property}=true|false
     *
     * @param parentClass     the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property        the property of the Map or Collection for the given parent class
     * @param target          the target object
     * @param keyProperty     the keyProperty value
     * @param isIndexAccessed <tt>true</tt>, if the collection or map is accessed via index, <tt>false</tt> otherwise.
     * @return <tt>true</tt>, if the Collection or Map should be created, <tt>false</tt> otherwise.
     * @see ObjectTypeDeterminer#getKeyProperty(Class, String)
     */
    public boolean shouldCreateIfNew(Class parentClass,
                                     String property,
                                     Object target,
                                     String keyProperty,
                                     boolean isIndexAccessed) {

        CreateIfNull annotation = getAnnotation(parentClass, property, CreateIfNull.class);

        if (annotation != null) {
            return annotation.value();
        }

        String configValue = (String) xworkConverter.getConverter(parentClass, CREATE_IF_NULL_PREFIX + property);
        //check if a value is in the config
        if (configValue!=null) {
            if ("true".equalsIgnoreCase(configValue)) {
                return true;
            }
            if ("false".equalsIgnoreCase(configValue)) {
                return false;
            }
        }

        //default values depend on target type
        //and whether this is accessed by an index
        //in the case of List
        if ((target instanceof Map) || isIndexAccessed) {
            return true;
        }	else {
            return false;
        }

    }

    /**
     * Retrieves an annotation for the specified property of field, setter or getter.
     *
     * @param <T>             the annotation type to be retrieved
     * @param parentClass     the class
     * @param property        the property
     * @param annotationClass the annotation
     * @return the field or setter/getter annotation or <code>null</code> if not found
     */
    protected <T extends Annotation> T getAnnotation(Class parentClass, String property, Class<T> annotationClass) {
        T annotation = null;
        Field field = reflectionProvider.getField(parentClass, property);

        if (field != null) {
            annotation = field.getAnnotation(annotationClass);
        }
        if (annotation == null) { // HINT: try with setter
            annotation = getAnnotationFromSetter(parentClass, property, annotationClass);
        }
        if (annotation == null) { // HINT: try with getter
            annotation = getAnnotationFromGetter(parentClass, property, annotationClass);
        }

        return annotation;
    }

    /**
     * Retrieves an annotation for the specified field of getter.
     *
     * @param parentClass     the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property        the property of the Map or Collection for the given parent class
     * @param annotationClass The annotation
     * @return concrete Annotation instance or <tt>null</tt> if none could be retrieved.
     */
    private <T extends Annotation>T getAnnotationFromGetter(Class parentClass, String property, Class<T> annotationClass) {
        try {
            Method getter = reflectionProvider.getGetMethod(parentClass, property);

            if (getter != null) {
                return getter.getAnnotation(annotationClass);
            }
        }
        catch (ReflectionException ognle) {
            ; // ignore
        }
        catch (IntrospectionException ie) {
            ; // ignore
        }
        return null;
    }

    /**
     * Retrieves an annotation for the specified field of setter.
     *
     * @param parentClass     the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property        the property of the Map or Collection for the given parent class
     * @param annotationClass The annotation
     * @return concrete Annotation instance or <tt>null</tt> if none could be retrieved.
     */
    private <T extends Annotation>T getAnnotationFromSetter(Class parentClass, String property, Class<T> annotationClass) {
        try {
            Method setter = reflectionProvider.getSetMethod(parentClass, property);

            if (setter != null) {
                return setter.getAnnotation(annotationClass);
            }
        }
        catch (ReflectionException ognle) {
            ; // ignore
        }
        catch (IntrospectionException ie) {
            ; // ignore
        }
        return null;
    }

    /**
     * Returns the class for the given field via generic type check.
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @param element     <tt>true</tt> for indexed types and Maps.
     * @return Class of the specified field.
     */
    private Class getClass(Class parentClass, String property, boolean element) {


        try {

            Field field = reflectionProvider.getField(parentClass, property);

            Type genericType = null;

            // Check fields first
            if (field != null) {
                genericType = field.getGenericType();
            }

            // Try to get ParameterType from setter method
            if (genericType == null || !(genericType instanceof ParameterizedType)) {
                try {
                    Method setter = reflectionProvider.getSetMethod(parentClass, property);
                    genericType = setter.getGenericParameterTypes()[0];
                }
                catch (ReflectionException ognle) {
                    ; // ignore
                }
                catch (IntrospectionException ie) {
                    ; // ignore
                }
            }

            // Try to get ReturnType from getter method
            if (genericType == null || !(genericType instanceof ParameterizedType)) {
                try {
                    Method getter = reflectionProvider.getGetMethod(parentClass, property);
                    genericType = getter.getGenericReturnType();
                }
                catch (ReflectionException ognle) {
                    ; // ignore
                }
                catch (IntrospectionException ie) {
                    ; // ignore
                }
            }

            if (genericType instanceof ParameterizedType) {


                ParameterizedType type = (ParameterizedType) genericType;

                int index = (element && type.getRawType().toString().contains(Map.class.getName())) ? 1 : 0;

                Type resultType = type.getActualTypeArguments()[index];

                if ( resultType instanceof ParameterizedType) {
                    return (Class) ((ParameterizedType) resultType).getRawType();
                }
                return (Class) resultType;

            }
        } catch (Exception e) {
            if ( LOG.isDebugEnabled()) {
                LOG.debug("Error while retrieving generic property class for property=" + property, e);
            }
        }
        return null;
    }
}
