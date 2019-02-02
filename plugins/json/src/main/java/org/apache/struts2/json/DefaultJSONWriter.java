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
package org.apache.struts2.json;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ProxyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.apache.struts2.json.annotations.JSONFieldBridge;
import org.apache.struts2.json.annotations.JSONParameter;
import org.apache.struts2.json.bridge.FieldBridge;
import org.apache.struts2.json.bridge.ParameterizedBridge;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * <p>
 * Serializes an object into JavaScript Object Notation (JSON). If cyclic
 * references are detected they will be nulled out.
 * </p>
 */
public class DefaultJSONWriter implements JSONWriter {

    private static final Logger LOG = LogManager.getLogger(DefaultJSONWriter.class);

    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    private static final ConcurrentMap<Class<?>, BeanInfo> BEAN_INFO_CACHE_IGNORE_HIERARCHY = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Class<?>, BeanInfo> BEAN_INFO_CACHE = new ConcurrentHashMap<>();

    private boolean ignoreHierarchy = true;
    private DateFormat formatter;
    private boolean enumAsBean = ENUM_AS_BEAN_DEFAULT;
    private boolean cacheBeanInfo = true;
    private boolean excludeProxyProperties;


    /**
     * Set whether to exclude proxy properties or not.
     * 
     * @param excludeProxyProperties
     */
    @Inject(value = JSONConstants.RESULT_EXCLUDE_PROXY_PROPERTIES, required = false)
    public void setExcludeProxyProperties(String excludeProxyProperties) {
        setExcludeProxyProperties(Boolean.parseBoolean(excludeProxyProperties));
    }

    /**
     * Perform a write (serialize) of the given object into JSON.
     * 
     * @param object Object to be serialized into JSON
     * 
     * @return JSON string for object
     * @throws JSONException in case of error during serialize
     */
    @Override
    public String write(Object object) throws JSONException {
        return this.write(object, null, null, false);
    }

    /**
     * Perform a write (serialize) of the given object into JSON,
     * using the provided parameters.
     * 
     * @param object
     *            Object to be serialized into JSON
     * @param excludeProperties
     *            Patterns matching properties to ignore
     * @param includeProperties
     *            Patterns matching properties to include
     * @param excludeNullProperties
     *            enable/disable excluding of null properties
     * @return JSON string for object
     * @throws JSONException in case of error during serialize
     */
    @Override
    public String write(Object object, Collection<Pattern> excludeProperties,
                        Collection<Pattern> includeProperties, boolean excludeNullProperties) throws JSONException {
        final JSONWriterProcessingState jsonWriterProcessingState = new JSONWriterProcessingState(object,
                excludeProperties, includeProperties, excludeNullProperties,
                excludeProxyProperties, enumAsBean, ignoreHierarchy, formatter);

        jsonWriterProcessingState.addValue(object, null);
        return jsonWriterProcessingState.getBufAsString();
     }

    /**
     * Retrieve bean information for class (ignoring hierarchy).
     * 
     * @param clazz
     * 
     * @return
     * @throws IntrospectionException
     */
    protected static BeanInfo getBeanInfoIgnoreHierarchy(final Class<?> clazz) throws IntrospectionException {
        BeanInfo beanInfo = BEAN_INFO_CACHE_IGNORE_HIERARCHY.get(clazz);
        if (beanInfo != null) {
            return beanInfo;
        }
        beanInfo = Introspector.getBeanInfo(clazz, clazz.getSuperclass());
        BEAN_INFO_CACHE_IGNORE_HIERARCHY.put(clazz, beanInfo);
        return beanInfo;
    }

    /**
     * Retrieve bean information for class.
     * 
     * @param clazz
     * 
     * @return
     * @throws IntrospectionException
     */
    protected static BeanInfo getBeanInfo(final Class<?> clazz) throws IntrospectionException {
        BeanInfo beanInfo = BEAN_INFO_CACHE.get(clazz);
        if (beanInfo != null) {
            return beanInfo;
        }
        beanInfo = Introspector.getBeanInfo(clazz);
        BEAN_INFO_CACHE.put(clazz, beanInfo);
        return beanInfo;
    }

    /**
     * Retrieve the bridged value (if a FieldBridge is provided by the baseAccessor),
     * otherwise returns the passed value.
     * 
     * @param baseAccessor
     * @param value
     * 
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    protected static Object getBridgedValue(Method baseAccessor, Object value) throws InstantiationException, IllegalAccessException {
        final JSONFieldBridge fieldBridgeAnn = baseAccessor.getAnnotation(JSONFieldBridge.class);
        if (fieldBridgeAnn != null) {
            Class impl = fieldBridgeAnn.impl();
            FieldBridge instance = (FieldBridge) impl.newInstance();

            if (fieldBridgeAnn.params().length > 0 && ParameterizedBridge.class.isAssignableFrom(impl)) {
                Map<String, String> params = new HashMap<>(fieldBridgeAnn.params().length);
                for (JSONParameter param : fieldBridgeAnn.params()) {
                    params.put(param.name(), param.value());
                }
                ((ParameterizedBridge) instance).setParameterValues(params);
            }
            value = instance.objectToString(value);
        }
        return value;
    }

    /**
     * Return the base accessor for the given class and method combination.
     * 
     * @param clazz
     * @param accessor
     * 
     * @return
     */
    protected static Method findBaseAccessor(Class clazz, Method accessor) {
        Method baseAccessor = null;
        if (clazz.getName().contains("$$EnhancerByCGLIB$$")) {
            try {
                baseAccessor = Thread.currentThread().getContextClassLoader().loadClass(
                        clazz.getName().substring(0, clazz.getName().indexOf("$$"))).getMethod(
                        accessor.getName(), accessor.getParameterTypes());
            } catch (Exception ex) {
                LOG.debug(ex.getMessage(), ex);
            }
        } else if (clazz.getName().contains("$$_javassist")) {
            try {
                baseAccessor = Class.forName(
                        clazz.getName().substring(0, clazz.getName().indexOf("_$$")))
                        .getMethod(accessor.getName(), accessor.getParameterTypes());
            } catch (Exception ex) {
                LOG.debug(ex.getMessage(), ex);
            }
            
        //in hibernate4.3.7,because javassist3.18.1's class name generate rule is '_$$_jvst'+...
        } else if(clazz.getName().contains("$$_jvst")){
            try {
                baseAccessor = Class.forName(
                        clazz.getName().substring(0, clazz.getName().indexOf("_$$")))
                        .getMethod(accessor.getName(), accessor.getParameterTypes());
            } catch (Exception ex) {
                LOG.debug(ex.getMessage(), ex);
            }
        }
        else {
            return accessor;
        }
        return baseAccessor;
    }

    /**
     * Determine if a given property should be excluded from JSON processing.
     * 
     * @param prop
     * 
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    protected static boolean shouldExcludeProperty(PropertyDescriptor prop) throws SecurityException, NoSuchFieldException {
        final String name = prop.getName();
        return name.equals("class")
                || name.equals("declaringClass")
                || name.equals("cachedSuperClass")
                || name.equals("metaClass");
    }

    /**
     * Set whether to ignore hierarchy during processing
     * 
     * #param ignoreHierarchy
     */
    @Override
    public void setIgnoreHierarchy(boolean ignoreHierarchy) {
        this.ignoreHierarchy = ignoreHierarchy;
    }

    /**
     * If true, an Enum is serialized as a bean with a special property
     * _name=name() as all as all other properties defined within the enum.<br>
     * If false, an Enum is serialized as a name=value pair (name=name())
     *
     * @param enumAsBean true to serialize an enum as a bean instead of as a name=value
     *                   pair (default=false)
     */
    @Override
    public void setEnumAsBean(boolean enumAsBean) {
        this.enumAsBean = enumAsBean;
    }

    /**
     * Set the desired dateformatter for use in JSON serialization.
     * 
     * @param defaultDateFormat
     */
    @Override
    public void setDateFormatter(String defaultDateFormat) {
        if (defaultDateFormat != null) {
            this.formatter = new SimpleDateFormat(defaultDateFormat);
        }
    }

    /**
     * Set whether to cache bean information or not.
     * 
     * @param cacheBeanInfo
     */
    @Override
    public void setCacheBeanInfo(boolean cacheBeanInfo) {
        this.cacheBeanInfo = cacheBeanInfo;
    }

    /**
     * Set whether to exclude proxy properties or not
     * 
     * @param excludeProxyProperties
     */
    @Override
    public void setExcludeProxyProperties(boolean excludeProxyProperties) {
        this.excludeProxyProperties = excludeProxyProperties;
    }

    /**
     * Helper class for JSON-related annotation processing.
     */
    protected static class JSONAnnotationFinder {
        private boolean serialize = true;
        private Method accessor;
        private String name;

        public JSONAnnotationFinder(Method accessor) {
            this.accessor = accessor;
        }

        public boolean shouldSerialize() {
            return serialize;
        }

        public String getName() {
            return name;
        }

        public JSONAnnotationFinder invoke() {
            JSON json = accessor.getAnnotation(JSON.class);
            serialize = json.serialize();
            if (serialize && json.name().length() > 0) {
                name = json.name();
            }
            return this;
        }
    }

    /**
     * Helper class for JSON processing (serialization).
     * Allows for isolated (thread-safe) processing state (mitigation for WW-5009)
     */
    protected static class JSONWriterProcessingState {
        private final StringBuilder buf = new StringBuilder();
        private final Stack<Object> stack = new Stack<>();
        private final Collection<Pattern> excludeProperties;
        private final Collection<Pattern> includeProperties;
        private final Object root;
        private final boolean ignoreHierarchy;
        private final boolean enumAsBean;
        private final boolean excludeNullProperties;
        private final boolean excludeProxyProperties;
        private boolean buildExpr = true;
        private String exprStack = "";
        private DateFormat formatter;


        /**
         * Create an instance of processing state for JSONWriter that can be used to
         * isolate state in a thread-safe manner.
         * 
         * @param object
         * @param excludeProperties
         * @param includeProperties
         * @param excludeNullProperties
         * @param excludeProxyProperties
         * @param enumAsBean
         * @param ignoreHierarchy
         * @param formatter
         */
        public JSONWriterProcessingState(Object object, Collection<Pattern> excludeProperties,
                        Collection<Pattern> includeProperties, boolean excludeNullProperties,
                        boolean excludeProxyProperties, boolean enumAsBean, boolean ignoreHierarchy,
                        DateFormat formatter) {
            this.root = object;
            this.excludeProperties = excludeProperties;
            this.includeProperties = includeProperties;
            this.excludeNullProperties = excludeNullProperties;
            this.excludeProxyProperties = excludeProxyProperties;
            this.enumAsBean = enumAsBean;
            this.ignoreHierarchy = ignoreHierarchy;
            this.formatter = formatter;
            this.exprStack = "";
            this.buildExpr = ((excludeProperties != null) && !excludeProperties.isEmpty())
                || ((includeProperties != null) && !includeProperties.isEmpty());
        }

        /**
         * Retrieve the current buffer.
         * 
         * @return
         */
        public StringBuilder getBuf() {
            return buf;
        }

        /**
         * Retrieve the current buffer.
         * 
         * @return
         */
        public String getBufAsString() {
            return buf.toString();
        }

        /**
         * Add map to buffer.
         * 
         * @param map
         * @param method
         */
        protected void addMap(Map map, Method method) throws JSONException {
            final Iterator it = map.entrySet().iterator();

            this.add("{");

            boolean warnedNonString = false; // one report per map
            boolean hasData = false;
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (excludeNullProperties && entry.getValue() == null) {
                    continue;
                }

                Object key = entry.getKey();
                if (key == null) {
                    LOG.error("Cannot build expression for null key in {}", exprStack);
                    continue;
                }

                String expr = null;
                if (this.buildExpr) {
                    expr = this.expandExpr(key.toString());
                    if (this.shouldExcludeProperty(expr)) {
                        continue;
                    }
                    expr = this.setExprStack(expr);
                }
                if (hasData) {
                    this.add(',');
                }
                hasData = true;
                if (!warnedNonString && !(key instanceof String)) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("JavaScript doesn't support non-String keys, using toString() on {}", key.getClass().getName());
                    }
                    warnedNonString = true;
                }
                this.addValue(key.toString(), method);
                this.add(":");
                this.addValue(entry.getValue(), method);
                if (this.buildExpr) {
                    this.setExprStack(expr);
                }
            }

            this.add("}");
        }

        /**
         * Add array to buffer
         */
        protected void addArray(Iterator it, Method method) throws JSONException {
            boolean hasData = false;

            this.add("[");

            for (int i = 0; it.hasNext(); i++) {
                String expr = null;
                if (this.buildExpr) {
                    expr = this.expandExpr(i);
                    if (this.shouldExcludeProperty(expr)) {
                        it.next();
                        continue;
                    }
                    expr = this.setExprStack(expr);
                }
                if (hasData) {
                    this.add(',');
                }
                hasData = true;
                this.addValue(it.next(), method);
                if (this.buildExpr) {
                    this.setExprStack(expr);
                }
            }

            this.add("]");
        }

        /**
         * Add array to buffer
         * 
         * @param object
         * @param method
         * 
         * @throws JSONException
         */
        protected void addArray(Object object, Method method) throws JSONException {
            final int length = Array.getLength(object);

            this.add("[");

            boolean hasData = false;
            for (int i = 0; i < length; ++i) {
                String expr = null;
                if (this.buildExpr) {
                    expr = this.expandExpr(i);
                    if (this.shouldExcludeProperty(expr)) {
                        continue;
                    }
                    expr = this.setExprStack(expr);
                }
                if (hasData) {
                    this.add(',');
                }
                hasData = true;
                this.addValue(Array.get(object, i), method);
                if (this.buildExpr) {
                    this.setExprStack(expr);
                }
            }

            this.add("]");
        }

        /**
         * Returns the concatenation of the expression stack and int.
         * Note: The expression stack remains unchanged.
         * 
         * @param i
         * @return
         */
        protected String expandExpr(int i) {
            return this.exprStack + "[" + i + "]";
        }

        /**
         * Returns the concatenation of the expression stack and property.
         * Note: The expression stack remains unchanged.
         * 
         * @param property
         * 
         * @return
         */
        protected String expandExpr(String property) {
            if (this.exprStack.length() == 0) {
                return property;
            }
            return this.exprStack + "." + property;
        }

        /**
         * Set expression stack to a new value, return the old value
         * 
         * @param expr
         * 
         * @return
         */
        protected String setExprStack(String expr) {
            final String s = this.exprStack;
            this.exprStack = expr;
            return s;
        }

        /**
         * Determine if property in expression should be excluded
         * 
         * @param expr
         * 
         * @return
         */
        protected boolean shouldExcludeProperty(String expr) {
            if (this.excludeProperties != null) {
                for (Pattern pattern : this.excludeProperties) {
                    if (pattern.matcher(expr).matches()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Ignoring property because of exclude rule: " + expr);
                        }
                        return true;
                    }
                }
            }

            if (this.includeProperties != null) {
                for (Pattern pattern : this.includeProperties) {
                    if (pattern.matcher(expr).matches()) {
                        return false;
                    }
                }
                if (LOG.isDebugEnabled()){
                    LOG.debug("Ignoring property because of include rule:  " + expr);
                }
                return true;
            }
            return false;
        }

        /**
         * Introspect bean and serialize its properties
         *
         * @param object object
         *
         * @throws JSONException  in case of error during serialize
         */
        protected void addBean(Object object) throws JSONException {
            BeanInfo info;

            this.add("{");

            try {
                Class clazz = excludeProxyProperties ? ProxyUtil.ultimateTargetClass(object) : object.getClass();

                info = ((object == this.root) && this.ignoreHierarchy)
                        ? DefaultJSONWriter.getBeanInfoIgnoreHierarchy(clazz)
                        : DefaultJSONWriter.getBeanInfo(clazz);

                PropertyDescriptor[] props = info.getPropertyDescriptors();

                boolean hasData = false;
                for (PropertyDescriptor prop : props) {
                    String name = prop.getName();
                    Method accessor = prop.getReadMethod();
                    Method baseAccessor = DefaultJSONWriter.findBaseAccessor(clazz, accessor);

                    if (baseAccessor != null) {
                        if (baseAccessor.isAnnotationPresent(JSON.class)) {
                            JSONAnnotationFinder jsonFinder = new JSONAnnotationFinder(baseAccessor).invoke();

                            if (!jsonFinder.shouldSerialize()) continue;
                            if (jsonFinder.getName() != null) {
                                name = jsonFinder.getName();
                            }
                        }
                        // ignore "class" and others
                        if (DefaultJSONWriter.shouldExcludeProperty(prop)) {
                            continue;
                        }
                        String expr = null;
                        if (this.buildExpr) {
                            expr = this.expandExpr(name);
                            if (this.shouldExcludeProperty(expr)) {
                                continue;
                            }
                            expr = this.setExprStack(expr);
                        }

                        Object value = accessor.invoke(object);
                        if (baseAccessor.isAnnotationPresent(JSONFieldBridge.class)) {
                            value = DefaultJSONWriter.getBridgedValue(baseAccessor, value);
                        }

                        boolean propertyPrinted = this.add(name, value, accessor, hasData);
                        hasData = hasData || propertyPrinted;
                        if (this.buildExpr) {
                            this.setExprStack(expr);
                        }
                    }
                }

                // special-case handling for an Enumeration - include the name() as
                // a property */
                if (object instanceof Enum) {
                    Object value = ((Enum) object).name();
                    this.add("_name", value, object.getClass().getMethod("name"), hasData);
                }
            } catch (Exception e) {
                throw new JSONException(e);
            }

            this.add("}");
        }

        /**
         * Introspect an Enum and serialize it as a name/value pair or as a bean
         * including all its own properties
         *
         * @param enumeration the enum
         *
         * @throws JSONException  in case of error during serialize
         */
        protected void enumeration(Enum enumeration) throws JSONException {
            if (enumAsBean) {
                this.addBean(enumeration);
            } else {
                this.addString(enumeration.name());
            }
        }

        /**
         * Serialize custom object into JSON
         *
         * @param object object
         * @param method method
         *
         * @throws JSONException  in case of error during serialize
         */
        protected void processCustom(Object object, Method method) throws JSONException {
            this.addBean(object);
        }

        /**
         * Serialize object into JSON
         *
         * @param object Object to be serialized into JSON
         * @param method method
         *
         * @throws JSONException  in case of error during serialize
         */
        protected void process(Object object, Method method) throws JSONException {
            this.stack.push(object);

            if (object instanceof Class) {
                this.addString(object);
            } else if (object instanceof Boolean) {
                this.addBool((Boolean) object);
            } else if (object instanceof Number) {
                this.add(object);
            } else if (object instanceof String) {
                this.addString(object);
            } else if (object instanceof Character) {
                this.addString(object);
            } else if (object instanceof Map) {
                this.addMap((Map) object, method);
            } else if (object.getClass().isArray()) {
                this.addArray(object, method);
            } else if (object instanceof Iterable) {
                this.addArray(((Iterable) object).iterator(), method);
            } else if (object instanceof Date) {
                this.addDate((Date) object, method);
            } else if (object instanceof Calendar) {
                this.addDate(((Calendar) object).getTime(), method);
            } else if (object instanceof Locale) {
                this.addString(object);
            } else if (object instanceof Enum) {
                this.enumeration((Enum) object);
            } else {
                processCustom(object, method);
            }

            this.stack.pop();
        }

        /**
         * Add value to the buffer.
         * 
         * Note: Detects cyclic references
         *
         * @param object Object to be serialized into JSON
         * @param method method
         *
         * @throws JSONException in case of error during serialize
         */
        protected void addValue(Object object, Method method) throws JSONException {
            if (object == null) {
                this.add("null");
                return;
            }

            if (this.stack.contains(object)) {
                final Class clazz = object.getClass();

                // Cyclic reference
                if (clazz.isPrimitive() || clazz.equals(String.class)) {
                    this.process(object, method);
                } else {
                    LOG.debug("Cyclic reference detected on {}", object);
                    this.add("null");
                }

                return;
            }

            this.process(object, method);
        }

        /**
         * Add object to buffer
         * 
         * @param obj
         */
        protected void add(Object obj) {
            this.buf.append(obj);
        }

        /**
         * Add char to buffer
         * 
         * @param c
         */
        protected void add(char c) {
            this.buf.append(c);
        }

        /**
         * Add name/value pair to buffer
         * 
         * @param name
         * @param value
         * @param method
         * @param hasData
         * 
         * @throws JSONException
         */
        protected boolean add(String name, Object value, Method method, boolean hasData) throws JSONException {
            if (excludeNullProperties && value == null) {
                return false;
            }
            if (hasData) {
                this.add(',');
            }
            this.add('"');
            this.add(name);
            this.add("\":");
            this.addValue(value, method);
            return true;
        }

        /**
         * Add character to buffer, Represented as unicode
         *
         * @param c character to be encoded
         */
        protected void addUnicode(char c) {
            int n = c;

            this.add("\\u");

            for (int i = 0; i < 4; ++i) {
                int digit = (n & 0xf000) >> 12;

                this.add(HEX[digit]);
                n <<= 4;
            }
        }

        /**
         * Add boolean to buffer
         *
         * @param b
         */
        protected void addBool(boolean b) {
            this.add(b ? "true" : "false");
        }

        /**
         * Add string to buffer, escaping characters
         *
         * @param obj the object to escape
         */
        protected void addString(Object obj) {
            final CharacterIterator it = new StringCharacterIterator(obj.toString());

            this.add('"');

            for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
                if (c == '"') {
                    this.add("\\\"");
                } else if (c == '\\') {
                    this.add("\\\\");
                } else if (c == '/') {
                    this.add("\\/");
                } else if (c == '\b') {
                    this.add("\\b");
                } else if (c == '\f') {
                    this.add("\\f");
                } else if (c == '\n') {
                    this.add("\\n");
                } else if (c == '\r') {
                    this.add("\\r");
                } else if (c == '\t') {
                    this.add("\\t");
                } else if (Character.isISOControl(c)) {
                    this.addUnicode(c);
                } else {
                    this.add(c);
                }
            }

            this.add('"');
        }

        /**
         * Add date to buffer
         *
         * @param date
         * @param method
         */
        protected void addDate(Date date, Method method) {
            JSON json = null;

            if (method != null) {
                json = method.getAnnotation(JSON.class);
            }
            if (this.formatter == null) {
                this.formatter = new SimpleDateFormat(JSONUtil.RFC3339_FORMAT);
            }

            final DateFormat localFormatter =
                    ((json != null) && (json.format().length() > 0))
                    ? new SimpleDateFormat(json.format())
                    : this.formatter;
            this.addString(localFormatter.format(date));
        }
    }

}
