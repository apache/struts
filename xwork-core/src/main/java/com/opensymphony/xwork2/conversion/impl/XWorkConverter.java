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
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.XWorkMessages;
import com.opensymphony.xwork2.conversion.ConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.ConversionFileProcessor;
import com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    private static final Logger LOG = LoggerFactory.getLogger(XWorkConverter.class);

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

    private TypeConverter defaultTypeConverter;
    private FileManager fileManager;
    private boolean reloadingConfigs;

    private ConversionFileProcessor fileProcessor;
    private ConversionAnnotationProcessor annotationProcessor;

    private TypeConverterHolder converterHolder;

    protected XWorkConverter() {
    }

    @Inject
    public void setDefaultTypeConverter(XWorkBasicConverter conv) {
        this.defaultTypeConverter = conv;
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    @Inject(value = XWorkConstants.RELOAD_XML_CONFIGURATION, required = false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadingConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    @Inject
    public void setConversionPropertiesProcessor(ConversionPropertiesProcessor propertiesProcessor) {
        // note: this file is deprecated
        propertiesProcessor.process("xwork-default-conversion.properties");
        propertiesProcessor.process("xwork-conversion.properties");
    }

    @Inject
    public void setConversionFileProcessor(ConversionFileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

    @Inject
    public void setConversionAnnotationProcessor(ConversionAnnotationProcessor annotationProcessor) {
        this.annotationProcessor = annotationProcessor;
    }

    @Inject
    public void setTypeConverterHolder(TypeConverterHolder converterHolder) {
        this.converterHolder = converterHolder;
    }

    public static String getConversionErrorMessage(String propertyName, ValueStack stack) {
        String defaultMessage = LocalizedTextUtil.findDefaultText(XWorkMessages.DEFAULT_INVALID_FIELDVALUE,
                ActionContext.getContext().getLocale(),
                new Object[]{
                        propertyName
                });

        List<String> indexValues = getIndexValues(propertyName);

        propertyName = removeAllIndexesInProperytName(propertyName);

        String getTextExpression = "getText('" + CONVERSION_ERROR_PROPERTY_PREFIX + propertyName + "','" + defaultMessage + "')";
        String message = (String) stack.findValue(getTextExpression);

        if (message == null) {
            message = defaultMessage;
        } else {
            message = MessageFormat.format(message, indexValues.toArray());
        }

        return message;
    }

    private static String removeAllIndexesInProperytName(String propertyName) {
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

    public String buildConverterFilename(Class clazz) {
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
    public TypeConverter lookup(String className, boolean isPrimitive) {
        if (converterHolder.containsUnknownMapping(className) && !converterHolder.containsDefaultMapping(className)) {
            return null;
        }

        TypeConverter result = converterHolder.getDefaultMapping(className);

        //Looks for super classes
        if (result == null && !isPrimitive) {
            Class clazz = null;

            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException cnfe) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Cannot load class #0", cnfe, className);
                }
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
        TypeConverter result = lookup(clazz.getName(), clazz.isPrimitive());

        if (result == null && clazz.isPrimitive()) {
            /**
             * if it is primitive use default converter which allows to define different converters per type
             * @see XWorkBasicConverter
             */
            return defaultTypeConverter;
        }

        return result;
    }

    protected Object getConverter(Class clazz, String property) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving convert for class [#0] and property [#1]", clazz, property);
        }
        synchronized (clazz) {
            if ((property != null) && !converterHolder.containsNoMapping(clazz)) {
                try {
                    Map<String, Object> mapping = converterHolder.getMapping(clazz);

                    if (mapping == null) {
                        mapping = buildConverterMapping(clazz);
                    } else {
                        mapping = conditionalReload(clazz, mapping);
                    }

                    Object converter = mapping.get(property);
                    if (LOG.isDebugEnabled() && converter == null) {
                        LOG.debug("Converter is null for property [#0]. Mapping size [#1]:", property, mapping.size());
                        for (String next : mapping.keySet()) {
                            LOG.debug(next + ":" + mapping.get(next));
                        }
                    }
                    return converter;
                } catch (Throwable t) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Got exception trying to resolve convert for class [#0] and property [#1]", t, clazz, property);
                    }
                    converterHolder.addNoMapping(clazz);
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
        converterHolder.addDefaultMapping(className, converter);
    }

    public synchronized void registerConverterNotFound(String className) {
        converterHolder.addUnknownMapping(className);
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
        // Process <clazz>-conversion.properties file
        String converterFilename = buildConverterFilename(clazz);
        fileProcessor.process(mapping, clazz, converterFilename);

        // Process annotations
        Annotation[] annotations = clazz.getAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation instanceof Conversion) {
                Conversion conversion = (Conversion) annotation;
                for (TypeConversion tc : conversion.conversions()) {
                    if (mapping.containsKey(tc.key())) {
                        break;
                    }
                    if (LOG.isDebugEnabled()) {
                        if (StringUtils.isEmpty(tc.key())) {
                            LOG.debug("WARNING! key of @TypeConversion [#0] applied to [#1] is empty!", tc.converter(), clazz.getName());
                        } else {
                            LOG.debug("TypeConversion [#0] with key: [#1]", tc.converter(), tc.key());
                        }
                    }
                    annotationProcessor.process(mapping, tc, tc.key());
                }
            }
        }

        // Process annotated methods
        for (Method method : clazz.getMethods()) {
            annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof TypeConversion) {
                    TypeConversion tc = (TypeConversion) annotation;
                    if (mapping.containsKey(tc.key())) {
                        break;
                    }
                    String key = tc.key();
                    // Default to the property name
                    if (StringUtils.isEmpty(key)) {
                        key = AnnotationUtils.resolvePropertyName(method);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Retrieved key [#0] from method name [#1]", key, method.getName());
                        }
                    }
                    annotationProcessor.process(mapping, tc, key);
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
            converterHolder.addMapping(clazz, mapping);
        } else {
            converterHolder.addNoMapping(clazz);
        }

        return mapping;
    }

    private Map<String, Object> conditionalReload(Class clazz, Map<String, Object> oldValues) throws Exception {
        Map<String, Object> mapping = oldValues;

        if (reloadingConfigs) {
            URL fileUrl = ClassLoaderUtil.getResource(buildConverterFilename(clazz), clazz);
            if (fileManager.fileNeedsReloading(fileUrl)) {
                mapping = buildConverterMapping(clazz);
            }
        }

        return mapping;
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
            result = converterHolder.getDefaultMapping(clazz.getName());

            if (result == null) {
                // Looks for direct interfaces (depth = 1 )
                Class[] interfaces = clazz.getInterfaces();

                for (Class anInterface : interfaces) {
                    if (converterHolder.containsDefaultMapping(anInterface.getName())) {
                        result = converterHolder.getDefaultMapping(anInterface.getName());
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
