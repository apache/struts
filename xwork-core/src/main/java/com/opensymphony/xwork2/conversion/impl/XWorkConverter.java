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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkMessages;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.ConversionRule;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.XWorkTypeConverterWrapper;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.MessageFormat;


/**
 * XWorkConverter is a singleton used by many of the Struts 2's Ognl extention points,
 * such as InstantiatingNullHandler, XWorkListPropertyAccessor etc to do object
 * conversion.
 * <p/>
 * <!-- START SNIPPET: javadoc -->
 * <p/>
 * Type conversion is great for situations where you need to turn a String in to a more complex object. Because the web
 * is type-agnostic (everything is a string in HTTP), Struts 2's type conversion features are very useful. For instance,
 * if you were prompting a user to enter in coordinates in the form of a string (such as "3, 22"), you could have
 * Struts 2 do the conversion both from String to Point and from Point to String.
 * <p/>
 * <p/> Using this "point" example, if your action (or another compound object in which you are setting properties on)
 * has a corresponding ClassName-conversion.properties file, Struts 2 will use the configured type converters for
 * conversion to and from strings. So turning "3, 22" in to new Point(3, 22) is done by merely adding the following
 * entry to <b>ClassName-conversion.properties</b> (Note that the PointConverter should impl the TypeConverter
 * interface):
 * <p/>
 * <p/><b>point = com.acme.PointConverter</b>
 * <p/>
 * <p/> Your type converter should be sure to check what class type it is being requested to convert. Because it is used
 * for both to and from strings, you will need to split the conversion method in to two parts: one that turns Strings in
 * to Points, and one that turns Points in to Strings.
 * <p/>
 * <p/> After this is done, you can now reference your point (using &lt;s:property value="point"/&gt; in JSP or ${point}
 * in FreeMarker) and it will be printed as "3, 22" again. As such, if you submit this back to an action, it will be
 * converted back to a Point once again.
 * <p/>
 * <p/> In some situations you may wish to apply a type converter globally. This can be done by editing the file
 * <b>xwork-conversion.properties</b> in the root of your class path (typically WEB-INF/classes) and providing a
 * property in the form of the class name of the object you wish to convert on the left hand side and the class name of
 * the type converter on the right hand side. For example, providing a type converter for all Point objects would mean
 * adding the following entry:
 * <p/>
 * <p/><b>com.acme.Point = com.acme.PointConverter</b>
 * <p/>
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: i18n-note -->
 * <p/>
 * Type conversion should not be used as a substitute for i18n. It is not recommended to use this feature to print out
 * properly formatted dates. Rather, you should use the i18n features of Struts 2 (and consult the JavaDocs for JDK's
 * MessageFormat object) to see how a properly formatted date should be displayed.
 * <p/>
 * <!-- END SNIPPET: i18n-note -->
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: error-reporting -->
 * <p/>
 * Any error that occurs during type conversion may or may not wish to be reported. For example, reporting that the
 * input "abc" could not be converted to a number might be important. On the other hand, reporting that an empty string,
 * "", cannot be converted to a number might not be important - especially in a web environment where it is hard to
 * distinguish between a user not entering a value vs. entering a blank value.
 * <p/>
 * <p/> By default, all conversion errors are reported using the generic i18n key <b>xwork.default.invalid.fieldvalue</b>,
 * which you can override (the default text is <i>Invalid field value for field "xxx"</i>, where xxx is the field name)
 * in your global i18n resource bundle.
 * <p/>
 * <p/>However, sometimes you may wish to override this message on a per-field basis. You can do this by adding an i18n
 * key associated with just your action (Action.properties) using the pattern <b>invalid.fieldvalue.xxx</b>, where xxx
 * is the field name.
 * <p/>
 * <p/>It is important to know that none of these errors are actually reported directly. Rather, they are added to a map
 * called <i>conversionErrors</i> in the ActionContext. There are several ways this map can then be accessed and the
 * errors can be reported accordingly.
 * <p/>
 * <!-- END SNIPPET: error-reporting -->
 *
 * @author <a href="mailto:plightbo@gmail.com">Pat Lightbody</a>
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * @author tm_jee
 * @version $Date$ $Id$
 * @see XWorkBasicConverter
 */
public class XWorkConverter extends DefaultTypeConverter {

    protected static final Logger LOG = LoggerFactory.getLogger(XWorkConverter.class);
    public static final String REPORT_CONVERSION_ERRORS = "report.conversion.errors";
    public static final String CONVERSION_PROPERTY_FULLNAME = "conversion.property.fullName";
    public static final String CONVERSION_ERROR_PROPERTY_PREFIX = "invalid.fieldvalue.";
    public static final String CONVERSION_COLLECTION_PREFIX = "Collection_";

    public static final String LAST_BEAN_CLASS_ACCESSED = "last.bean.accessed";
    public static final String LAST_BEAN_PROPERTY_ACCESSED = "last.property.accessed";
    public static final String MESSAGE_INDEX_PATTERN = "\\[\\d+\\]\\.";
    public static final String MESSAGE_INDEX_BRACKET_PATTERN = "[\\[\\]\\.]";
    public static final String PERIOD = ".";
    public static final Pattern messageIndexPattern = Pattern.compile(MESSAGE_INDEX_PATTERN); 

    /**
     * Target class conversion Mappings.
     * <pre>
     * Map<Class, Map<String, Object>>
     *  - Class -> convert to class
     *  - Map<String, Object>
     *    - String -> property name
     *                eg. Element_property, property etc.
     *    - Object -> String to represent properties
     *                eg. value part of
     *                    KeyProperty_property=id
     *             -> TypeConverter to represent an Ognl TypeConverter
     *                eg. value part of
     *                    property=foo.bar.MyConverter
     *             -> Class to represent a class
     *                eg. value part of
     *                    Element_property=foo.bar.MyObject
     * </pre>
     */
    protected HashMap<Class, Map<String, Object>> mappings = new HashMap<Class, Map<String, Object>>(); // action

    /**
     * Unavailable target class conversion mappings, serves as a simple cache.
     */
    protected HashSet<Class> noMapping = new HashSet<Class>(); // action

    /**
     * Record class and its type converter mapping.
     * <pre>
     * - String - classname as String
     * - TypeConverter - instance of TypeConverter
     * </pre>
     */
    protected HashMap<String, TypeConverter> defaultMappings = new HashMap<String, TypeConverter>();  // non-action (eg. returned value)

    /**
     * Record classes that doesn't have conversion mapping defined.
     * <pre>
     * - String -> classname as String
     * </pre>
     */
    protected HashSet<String> unknownMappings = new HashSet<String>();     // non-action (eg. returned value)

    private TypeConverter defaultTypeConverter;
    private ObjectFactory objectFactory;


    protected XWorkConverter() {
    }

    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
        // note: this file is deprecated
        loadConversionProperties("xwork-default-conversion.properties");

        loadConversionProperties("xwork-conversion.properties");
    }

    @Inject
    public void setDefaultTypeConverter(XWorkBasicConverter conv) {
        this.defaultTypeConverter = conv;
    }

    public static String getConversionErrorMessage(String propertyName, ValueStack stack) {
        String defaultMessage = LocalizedTextUtil.findDefaultText(XWorkMessages.DEFAULT_INVALID_FIELDVALUE,
                ActionContext.getContext().getLocale(),
                new Object[]{
                        propertyName
                });

        List<String> indexValues = getIndexValues(propertyName);

        propertyName = removeAllIndexesInProperyName(propertyName);

        String getTextExpression = "getText('" + CONVERSION_ERROR_PROPERTY_PREFIX + propertyName + "','" + defaultMessage + "')";
        String message = (String) stack.findValue(getTextExpression);

        if (message == null) {
            message = defaultMessage;
        } else {
            message = MessageFormat.format(message, indexValues.toArray());
        }

        return message;
    }

    private static String removeAllIndexesInProperyName(String propertyName) {
        return propertyName.replaceAll(MESSAGE_INDEX_PATTERN, PERIOD);
    }

    private static List<String> getIndexValues(String propertyName) {
        Matcher matcher = messageIndexPattern.matcher(propertyName);
        List<String> indexes = new ArrayList<String>();
        while (matcher.find()) {
            Integer index = new Integer(matcher.group().replaceAll(MESSAGE_INDEX_BRACKET_PATTERN, "")) + 1;
            indexes.add(Integer.toString(index));
        }
        return indexes;
    }

    public static String buildConverterFilename(Class clazz) {
        String className = clazz.getName();
        return className.replace('.', '/') + "-conversion.properties";
    }

    @Override
    public Object convertValue(Map<String, Object> map, Object o, Class aClass) {
        return convertValue(map, null, null, null, o, aClass);
    }

    /**
     * Convert value from one form to another.
     * Minimum requirement of arguments:
     * <ul>
     * <li>supplying context, toClass and value</li>
     * <li>supplying context, target and value.</li>
     * </ul>
     *
     * @see TypeConverter#convertValue(java.util.Map, java.lang.Object, java.lang.reflect.Member, java.lang.String, java.lang.Object, java.lang.Class)
     */
    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String property, Object value, Class toClass) {
        //
        // Process the conversion using the default mappings, if one exists
        //
        TypeConverter tc = null;

        if ((value != null) && (toClass == value.getClass())) {
            return value;
        }

        // allow this method to be called without any context
        // i.e. it can be called with as little as "Object value" and "Class toClass"
        if (target != null) {
            Class clazz = target.getClass();

            Object[] classProp = null;

            // this is to handle weird issues with setValue with a different type
            if ((target instanceof CompoundRoot) && (context != null)) {
                classProp = getClassProperty(context);
            }

            if (classProp != null) {
                clazz = (Class) classProp[0];
                property = (String) classProp[1];
            }

            tc = (TypeConverter) getConverter(clazz, property);

            if (LOG.isDebugEnabled())
                LOG.debug("field-level type converter for property [" + property + "] = " + (tc == null ? "none found" : tc));
        }

        if (tc == null && context != null) {
            // ok, let's see if we can look it up by path as requested in XW-297
            Object lastPropertyPath = context.get(ReflectionContextState.CURRENT_PROPERTY_PATH);
            Class clazz = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            if (lastPropertyPath != null && clazz != null) {
                String path = lastPropertyPath + "." + property;
                tc = (TypeConverter) getConverter(clazz, path);
            }
        }

        if (tc == null) {
            if (toClass.equals(String.class) && (value != null) && !(value.getClass().equals(String.class) || value.getClass().equals(String[].class))) {
                // when converting to a string, use the source target's class's converter
                tc = lookup(value.getClass());
            } else {
                // when converting from a string, use the toClass's converter
                tc = lookup(toClass);
            }

            if (LOG.isDebugEnabled())
                LOG.debug("global-level type converter for property [" + property + "] = " + (tc == null ? "none found" : tc));
        }


        if (tc != null) {
            try {
                return tc.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                if (LOG.isDebugEnabled())
                    LOG.debug("unable to convert value using type converter [#0]", e, tc.getClass().getName());
                handleConversionException(context, property, value, target);

                return TypeConverter.NO_CONVERSION_POSSIBLE;
            }
        }

        if (defaultTypeConverter != null) {
            try {
                if (LOG.isDebugEnabled())
                    LOG.debug("falling back to default type converter [" + defaultTypeConverter + "]");
                return defaultTypeConverter.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                if (LOG.isDebugEnabled())
                    LOG.debug("unable to convert value using type converter [#0]", e, defaultTypeConverter.getClass().getName());
                handleConversionException(context, property, value, target);

                return TypeConverter.NO_CONVERSION_POSSIBLE;
            }
        } else {
            try {
                if (LOG.isDebugEnabled())
                    LOG.debug("falling back to Ognl's default type conversion");
                return super.convertValue(value, toClass);
            } catch (Exception e) {
                if (LOG.isDebugEnabled())
                    LOG.debug("unable to convert value using type converter [#0]", e, super.getClass().getName());
                handleConversionException(context, property, value, target);

                return TypeConverter.NO_CONVERSION_POSSIBLE;
            }
        }
    }

    /**
     * Looks for a TypeConverter in the default mappings.
     *
     * @param className name of the class the TypeConverter must handle
     * @return a TypeConverter to handle the specified class or null if none can be found
     */
    public TypeConverter lookup(String className) {
        if (unknownMappings.contains(className) && !defaultMappings.containsKey(className)) {
            return null;
        }

        TypeConverter result = defaultMappings.get(className);

        //Looks for super classes
        if (result == null) {
            Class clazz = null;

            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException cnfe) {
                //swallow
            }

            result = lookupSuper(clazz);

            if (result != null) {
                //Register now, the next lookup will be faster
                registerConverter(className, result);
            } else {
                // if it isn't found, never look again (also faster)
                registerConverterNotFound(className);
            }
        }

        return result;
    }

    /**
     * Looks for a TypeConverter in the default mappings.
     *
     * @param clazz the class the TypeConverter must handle
     * @return a TypeConverter to handle the specified class or null if none can be found
     */
    public TypeConverter lookup(Class clazz) {
        return lookup(clazz.getName());
    }

    protected Object getConverter(Class clazz, String property) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Property: " + property);
            LOG.debug("Class: " + clazz.getName());
        }
        synchronized (clazz) {
            if ((property != null) && !noMapping.contains(clazz)) {
                try {
                    Map<String, Object> mapping = mappings.get(clazz);

                    if (mapping == null) {
                        mapping = buildConverterMapping(clazz);
                    } else {
                        mapping = conditionalReload(clazz, mapping);
                    }

                    Object converter = mapping.get(property);
                    if (LOG.isDebugEnabled() && converter == null) {
                        LOG.debug("converter is null for property " + property + ". Mapping size: " + mapping.size());
                        for (String next : mapping.keySet()) {
                            LOG.debug(next + ":" + mapping.get(next));
                        }
                    }
                    return converter;
                } catch (Throwable t) {
                    noMapping.add(clazz);
                }
            }
        }

        return null;
    }

    protected void handleConversionException(Map<String, Object> context, String property, Object value, Object object) {
        if (context != null && (Boolean.TRUE.equals(context.get(REPORT_CONVERSION_ERRORS)))) {
            String realProperty = property;
            String fullName = (String) context.get(CONVERSION_PROPERTY_FULLNAME);

            if (fullName != null) {
                realProperty = fullName;
            }

            Map<String, Object> conversionErrors = (Map<String, Object>) context.get(ActionContext.CONVERSION_ERRORS);

            if (conversionErrors == null) {
                conversionErrors = new HashMap<String, Object>();
                context.put(ActionContext.CONVERSION_ERRORS, conversionErrors);
            }

            conversionErrors.put(realProperty, value);
        }
    }

    public synchronized void registerConverter(String className, TypeConverter converter) {
        defaultMappings.put(className, converter);
        if (unknownMappings.contains(className)) {
            unknownMappings.remove(className);
        }
    }

    public synchronized void registerConverterNotFound(String className) {
        unknownMappings.add(className);
    }

    private Object[] getClassProperty(Map<String, Object> context) {
        Object lastClass = context.get(LAST_BEAN_CLASS_ACCESSED);
        Object lastProperty = context.get(LAST_BEAN_PROPERTY_ACCESSED);
        return (lastClass != null && lastProperty != null) ? new Object[] {lastClass, lastProperty} : null;
    }

    /**
     * Looks for converter mappings for the specified class and adds it to an existing map.  Only new converters are
     * added.  If a converter is defined on a key that already exists, the converter is ignored.
     *
     * @param mapping an existing map to add new converter mappings to
     * @param clazz   class to look for converter mappings for
     */
    protected void addConverterMapping(Map<String, Object> mapping, Class clazz) {
        try {
            String converterFilename = buildConverterFilename(clazz);
            InputStream is = FileManager.loadFile(converterFilename, clazz);

            if (is != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("processing conversion file [" + converterFilename + "] [class=" + clazz + "]");
                }

                Properties prop = new Properties();
                prop.load(is);

                for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                    String key = (String) entry.getKey();

                    if (mapping.containsKey(key)) {
                        break;
                    }
                    // for keyProperty of Set
                    if (key.startsWith(DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX)
                            || key.startsWith(DefaultObjectTypeDeterminer.CREATE_IF_NULL_PREFIX)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as String]");
                        }
                        mapping.put(key, entry.getValue());
                    }
                    //for properties of classes
                    else if (!(key.startsWith(DefaultObjectTypeDeterminer.ELEMENT_PREFIX) ||
                            key.startsWith(DefaultObjectTypeDeterminer.KEY_PREFIX) ||
                            key.startsWith(DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX))
                            ) {
                        TypeConverter _typeConverter = createTypeConverter((String) entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as TypeConverter " + _typeConverter + "]");
                        }
                        mapping.put(key, _typeConverter);
                    }
                    //for keys of Maps
                    else if (key.startsWith(DefaultObjectTypeDeterminer.KEY_PREFIX)) {

                        Class converterClass = Thread.currentThread().getContextClassLoader().loadClass((String) entry.getValue());

                        //check if the converter is a type converter if it is one
                        //then just put it in the map as is. Otherwise
                        //put a value in for the type converter of the class
                        if (converterClass.isAssignableFrom(TypeConverter.class)) {
                            TypeConverter _typeConverter = createTypeConverter((String) entry.getValue());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as TypeConverter " + _typeConverter + "]");
                            }
                            mapping.put(key, _typeConverter);
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as Class " + converterClass + "]");
                            }
                            mapping.put(key, converterClass);
                        }
                    }
                    //elements(values) of maps / lists
                    else {
                        Class _c = Thread.currentThread().getContextClassLoader().loadClass((String) entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as Class " + _c + "]");
                        }
                        mapping.put(key, _c);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Problem loading properties for " + clazz.getName(), ex);
        }

        // Process annotations
        Annotation[] annotations = clazz.getAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation instanceof Conversion) {
                Conversion conversion = (Conversion) annotation;

                for (TypeConversion tc : conversion.conversions()) {

                    String key = tc.key();

                    if (mapping.containsKey(key)) {
                        break;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(key + ":" + key);
                    }

                    if (key != null) {
                        try {
                            if (tc.type() == ConversionType.APPLICATION) {
                                defaultMappings.put(key, createTypeConverter(tc.converter()));
                            } else {
                                if (tc.rule().toString().equals(ConversionRule.KEY_PROPERTY) || tc.rule().toString().equals(ConversionRule.CREATE_IF_NULL)) {
                                    mapping.put(key, tc.value());
                                }
                                //for properties of classes
                                else if (!(tc.rule().toString().equals(ConversionRule.ELEMENT.toString())) ||
                                        tc.rule().toString().equals(ConversionRule.KEY.toString()) ||
                                        tc.rule().toString().equals(ConversionRule.COLLECTION.toString())
                                        ) {
                                    mapping.put(key, createTypeConverter(tc.converter()));


                                }
                                //for keys of Maps
                                else if (tc.rule().toString().equals(ConversionRule.KEY.toString())) {
                                    Class converterClass = Thread.currentThread().getContextClassLoader().loadClass(tc.converter());
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Converter class: " + converterClass);
                                    }
                                    //check if the converter is a type converter if it is one
                                    //then just put it in the map as is. Otherwise
                                    //put a value in for the type converter of the class
                                    if (converterClass.isAssignableFrom(TypeConverter.class)) {
                                        mapping.put(key, createTypeConverter(tc.converter()));
                                    } else {
                                        mapping.put(key, converterClass);
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("Object placed in mapping for key "
                                                    + key
                                                    + " is "
                                                    + mapping.get(key));
                                        }

                                    }

                                }
                                //elements(values) of maps / lists
                                else {
                                    mapping.put(key, Thread.currentThread().getContextClassLoader().loadClass(tc.converter()));
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

        Method[] methods = clazz.getMethods();

        for (Method method : methods) {

            annotations = method.getAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation instanceof TypeConversion) {
                    TypeConversion tc = (TypeConversion) annotation;

                    String key = tc.key();
                    if (mapping.containsKey(key)) {
                        break;
                    }
                    // Default to the property name
                    if (key != null && key.length() == 0) {
                        key = AnnotationUtils.resolvePropertyName(method);
                        LOG.debug("key from method name... " + key + " - " + method.getName());
                    }


                    if (LOG.isDebugEnabled()) {
                        LOG.debug(key + ":" + key);
                    }

                    if (key != null) {
                        try {
                            if (tc.type() == ConversionType.APPLICATION) {
                                defaultMappings.put(key, createTypeConverter(tc.converter()));
                            } else {
                                if (tc.rule().toString().equals(ConversionRule.KEY_PROPERTY)) {
                                    mapping.put(key, tc.value());
                                }
                                //for properties of classes
                                else if (!(tc.rule().toString().equals(ConversionRule.ELEMENT.toString())) ||
                                        tc.rule().toString().equals(ConversionRule.KEY.toString()) ||
                                        tc.rule().toString().equals(ConversionRule.COLLECTION.toString())
                                        ) {
                                    mapping.put(key, createTypeConverter(tc.converter()));
                                }
                                //for keys of Maps
                                else if (tc.rule().toString().equals(ConversionRule.KEY.toString())) {
                                    Class converterClass = Thread.currentThread().getContextClassLoader().loadClass(tc.converter());
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Converter class: " + converterClass);
                                    }
                                    //check if the converter is a type converter if it is one
                                    //then just put it in the map as is. Otherwise
                                    //put a value in for the type converter of the class
                                    if (converterClass.isAssignableFrom(TypeConverter.class)) {
                                        mapping.put(key, createTypeConverter(tc.converter()));
                                    } else {
                                        mapping.put(key, converterClass);
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("Object placed in mapping for key "
                                                    + key
                                                    + " is "
                                                    + mapping.get(key));
                                        }

                                    }

                                }
                                //elements(values) of maps / lists
                                else {
                                    mapping.put(key, Thread.currentThread().getContextClassLoader().loadClass(tc.converter()));
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    /**
     * Looks for converter mappings for the specified class, traversing up its class hierarchy and interfaces and adding
     * any additional mappings it may find.  Mappings lower in the hierarchy have priority over those higher in the
     * hierarcy.
     *
     * @param clazz the class to look for converter mappings for
     * @return the converter mappings
     */
    protected Map<String, Object> buildConverterMapping(Class clazz) throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();

        // check for conversion mapping associated with super classes and any implemented interfaces
        Class curClazz = clazz;

        while (!curClazz.equals(Object.class)) {
            // add current class' mappings
            addConverterMapping(mapping, curClazz);

            // check interfaces' mappings
            Class[] interfaces = curClazz.getInterfaces();

            for (Class anInterface : interfaces) {
                addConverterMapping(mapping, anInterface);
            }

            curClazz = curClazz.getSuperclass();
        }

        if (mapping.size() > 0) {
            mappings.put(clazz, mapping);
        } else {
            noMapping.add(clazz);
        }

        return mapping;
    }

    private Map<String, Object> conditionalReload(Class clazz, Map<String, Object> oldValues) throws Exception {
        Map<String, Object> mapping = oldValues;

        if (FileManager.isReloadingConfigs()) {
            if (FileManager.fileNeedsReloading(buildConverterFilename(clazz), clazz)) {
                mapping = buildConverterMapping(clazz);
            }
        }

        return mapping;
    }

    TypeConverter createTypeConverter(String className) throws Exception {
        // type converters are used across users
        Object obj = objectFactory.buildBean(className, null);
        if (obj instanceof TypeConverter) {
            return (TypeConverter) obj;

            // For backwards compatibility
        } else if (obj instanceof ognl.TypeConverter) {
            return new XWorkTypeConverterWrapper((ognl.TypeConverter) obj);
        } else {
            throw new IllegalArgumentException("Type converter class " + obj.getClass() + " doesn't implement com.opensymphony.xwork2.conversion.TypeConverter");
        }
    }

    public void loadConversionProperties(String propsName) {
        loadConversionProperties(propsName, false);
    }

    public void loadConversionProperties(String propsName, boolean require) {
        try {
            Iterator<URL> resources = ClassLoaderUtil.getResources(propsName, getClass(), true);
            while (resources.hasNext()) {
                URL url = resources.next();
                Properties props = new Properties();
                props.load(url.openStream());

                if (LOG.isDebugEnabled()) {
                    LOG.debug("processing conversion file [" + propsName + "]");
                }

                for (Object o : props.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    String key = (String) entry.getKey();

                    try {
                        TypeConverter _typeConverter = createTypeConverter((String) entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + " [treated as TypeConverter " + _typeConverter + "]");
                        }
                        defaultMappings.put(key, _typeConverter);
                    } catch (Exception e) {
                        LOG.error("Conversion registration error", e);
                    }
                }
            }
        } catch (IOException ex) {
            if (require) {
                throw new XWorkException("Cannot load conversion properties file: "+propsName, ex);
            } else {
                LOG.debug("Cannot load conversion properties file: "+propsName, ex);
            }
        }
    }

    /**
     * Recurses through a class' interfaces and class hierarchy looking for a TypeConverter in the default mapping that
     * can handle the specified class.
     *
     * @param clazz the class the TypeConverter must handle
     * @return a TypeConverter to handle the specified class or null if none can be found
     */
    TypeConverter lookupSuper(Class clazz) {
        TypeConverter result = null;

        if (clazz != null) {
            result = defaultMappings.get(clazz.getName());

            if (result == null) {
                // Looks for direct interfaces (depth = 1 )
                Class[] interfaces = clazz.getInterfaces();

                for (Class anInterface : interfaces) {
                    if (defaultMappings.containsKey(anInterface.getName())) {
                        result = (TypeConverter) defaultMappings.get(anInterface.getName());
                        break;
                    }
                }

                if (result == null) {
                    // Looks for the superclass
                    // If 'clazz' is the Object class, an interface, a primitive type or void then clazz.getSuperClass() returns null
                    result = lookupSuper(clazz.getSuperclass());
                }
            }
        }

        return result;
    }


}
