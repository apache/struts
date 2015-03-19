/*
 * $Id$
 *
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

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * <p>
 * Serializes an object into JavaScript Object Notation (JSON). If cyclic
 * references are detected they will be nulled out.
 * </p>
 */
public class JSONWriter {

    private static final Logger LOG = LoggerFactory.getLogger(JSONWriter.class);

    /**
     * By default, enums are serialised as name=value pairs
     */
    public static final boolean ENUM_AS_BEAN_DEFAULT = false;

    private static char[] hex = "0123456789ABCDEF".toCharArray();

    private static final ConcurrentMap<Class<?>, BeanInfo> BEAN_INFO_CACHE_IGNORE_HIERARCHY = new ConcurrentHashMap<Class<?>, BeanInfo>();
    private static final ConcurrentMap<Class<?>, BeanInfo> BEAN_INFO_CACHE = new ConcurrentHashMap<Class<?>, BeanInfo>();

    private StringBuilder buf = new StringBuilder();
    private Stack<Object> stack = new Stack<Object>();
    private boolean ignoreHierarchy = true;
    private Object root;
    private boolean buildExpr = true;
    private String exprStack = "";
    private Collection<Pattern> excludeProperties;
    private Collection<Pattern> includeProperties;
    private DateFormat formatter;
    private boolean enumAsBean = ENUM_AS_BEAN_DEFAULT;
    private boolean excludeNullProperties;

    /**
     * @param object Object to be serialized into JSON
     * @return JSON string for object
     * @throws JSONException
     */
    public String write(Object object) throws JSONException {
        return this.write(object, null, null, false);
    }

    /**
     * @param object Object to be serialized into JSON
     * @return JSON string for object
     * @throws JSONException
     */
    public String write(Object object, Collection<Pattern> excludeProperties,
                        Collection<Pattern> includeProperties, boolean excludeNullProperties) throws JSONException {
        this.excludeNullProperties = excludeNullProperties;
        this.buf.setLength(0);
        this.root = object;
        this.exprStack = "";
        this.buildExpr = ((excludeProperties != null) && !excludeProperties.isEmpty())
                || ((includeProperties != null) && !includeProperties.isEmpty());
        this.excludeProperties = excludeProperties;
        this.includeProperties = includeProperties;
        this.value(object, null);

        return this.buf.toString();
    }

    /**
     * Detect cyclic references
     */
    protected void value(Object object, Method method) throws JSONException {
        if (object == null) {
            this.add("null");

            return;
        }

        if (this.stack.contains(object)) {
            Class clazz = object.getClass();

            // cyclic reference
            if (clazz.isPrimitive() || clazz.equals(String.class)) {
                this.process(object, method);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Cyclic reference detected on " + object);
                }

                this.add("null");
            }

            return;
        }

        this.process(object, method);
    }

    /**
     * Serialize object into json
     */
    protected void process(Object object, Method method) throws JSONException {
        this.stack.push(object);

        if (object instanceof Class) {
            this.string(object);
        } else if (object instanceof Boolean) {
            this.bool((Boolean) object);
        } else if (object instanceof Number) {
            this.add(object);
        } else if (object instanceof String) {
            this.string(object);
        } else if (object instanceof Character) {
            this.string(object);
        } else if (object instanceof Map) {
            this.map((Map) object, method);
        } else if (object.getClass().isArray()) {
            this.array(object, method);
        } else if (object instanceof Iterable) {
            this.array(((Iterable) object).iterator(), method);
        } else if (object instanceof Date) {
            this.date((Date) object, method);
        } else if (object instanceof Calendar) {
            this.date(((Calendar) object).getTime(), method);
        } else if (object instanceof Locale) {
            this.string(object);
        } else if (object instanceof Enum) {
            this.enumeration((Enum) object);
        } else {
            processCustom(object, method);
        }

        this.stack.pop();
    }

    /**
     * Serialize custom object into json
     */
    protected void processCustom(Object object, Method method) throws JSONException {
        this.bean(object);
    }

    /**
     * Instrospect bean and serialize its properties
     */
    protected void bean(Object object) throws JSONException {
        this.add("{");

        BeanInfo info;

        try {
            Class clazz = object.getClass();

            info = ((object == this.root) && this.ignoreHierarchy)
                    ? getBeanInfoIgnoreHierarchy(clazz)
                    : getBeanInfo(clazz);

            PropertyDescriptor[] props = info.getPropertyDescriptors();

            boolean hasData = false;
            for (PropertyDescriptor prop : props) {
                String name = prop.getName();
                Method accessor = prop.getReadMethod();
                Method baseAccessor = findBaseAccessor(clazz, accessor);

                if (baseAccessor != null) {
                    if (baseAccessor.isAnnotationPresent(JSON.class)) {
                        JSONAnnotationFinder jsonFinder = new JSONAnnotationFinder(baseAccessor).invoke();

                        if (!jsonFinder.shouldSerialize()) continue;
                        if (jsonFinder.getName() != null) {
                            name = jsonFinder.getName();
                        }
                    }
                    // ignore "class" and others
                    if (this.shouldExcludeProperty(prop)) {
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
                        value = getBridgedValue(baseAccessor, value);
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

    protected BeanInfo getBeanInfoIgnoreHierarchy(final Class<?> clazz) throws IntrospectionException {
        BeanInfo beanInfo = BEAN_INFO_CACHE_IGNORE_HIERARCHY.get(clazz);
        if (beanInfo != null) {
            return beanInfo;
        }
        beanInfo = Introspector.getBeanInfo(clazz, clazz.getSuperclass());
        BEAN_INFO_CACHE_IGNORE_HIERARCHY.put(clazz, beanInfo);
        return beanInfo;
    }

    protected BeanInfo getBeanInfo(final Class<?> clazz) throws IntrospectionException {
        BeanInfo beanInfo = BEAN_INFO_CACHE.get(clazz);
        if (beanInfo != null) {
            return beanInfo;
        }
        beanInfo = Introspector.getBeanInfo(clazz);
        BEAN_INFO_CACHE.put(clazz, beanInfo);
        return beanInfo;
    }

    protected Object getBridgedValue(Method baseAccessor, Object value) throws InstantiationException, IllegalAccessException {
        JSONFieldBridge fieldBridgeAnn = baseAccessor.getAnnotation(JSONFieldBridge.class);
        if (fieldBridgeAnn != null) {
            Class impl = fieldBridgeAnn.impl();
            FieldBridge instance = (FieldBridge) impl.newInstance();

            if (fieldBridgeAnn.params().length > 0 && ParameterizedBridge.class.isAssignableFrom(impl)) {
                Map<String, String> params = new HashMap<String, String>(fieldBridgeAnn.params().length);
                for (JSONParameter param : fieldBridgeAnn.params()) {
                    params.put(param.name(), param.value());
                }
                ((ParameterizedBridge) instance).setParameterValues(params);
            }
            value = instance.objectToString(value);
        }
        return value;
    }

    protected Method findBaseAccessor(Class clazz, Method accessor) {
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
     * Instrospect an Enum and serialize it as a name/value pair or as a bean
     * including all its own properties
     */
    protected void enumeration(Enum enumeration) throws JSONException {
        if (enumAsBean) {
            this.bean(enumeration);
        } else {
            this.string(enumeration.name());
        }
    }

    protected boolean shouldExcludeProperty(PropertyDescriptor prop) throws SecurityException, NoSuchFieldException {
        String name = prop.getName();
        return name.equals("class")
                || name.equals("declaringClass")
                || name.equals("cachedSuperClass")
                || name.equals("metaClass");
    }

    protected String expandExpr(int i) {
        return this.exprStack + "[" + i + "]";
    }

    protected String expandExpr(String property) {
        if (this.exprStack.length() == 0) {
            return property;
        }
        return this.exprStack + "." + property;
    }

    protected String setExprStack(String expr) {
        String s = this.exprStack;
        this.exprStack = expr;
        return s;
    }

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
     * Add name/value pair to buffer
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
        this.value(value, method);
        return true;
    }

    /**
     * Add map to buffer
     */
    protected void map(Map map, Method method) throws JSONException {
        this.add("{");

        Iterator it = map.entrySet().iterator();

        boolean warnedNonString = false; // one report per map
        boolean hasData = false;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (excludeNullProperties && entry.getValue() == null) {
                continue;
            }

            Object key = entry.getKey();
            if (key == null) {
                LOG.error("Cannot build expression for null key in #0", exprStack);
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
                    LOG.warn("JavaScript doesn't support non-String keys, using toString() on #0", key.getClass().getName());
                }
                warnedNonString = true;
            }
            this.value(key.toString(), method);
            this.add(":");
            this.value(entry.getValue(), method);
            if (this.buildExpr) {
                this.setExprStack(expr);
            }
        }

        this.add("}");
    }

    /**
     * Add date to buffer
     */
    protected void date(Date date, Method method) {
        JSON json = null;
        if (method != null)
            json = method.getAnnotation(JSON.class);
        if (this.formatter == null)
            this.formatter = new SimpleDateFormat(JSONUtil.RFC3339_FORMAT);

        DateFormat formatter = (json != null) && (json.format().length() > 0) ? new SimpleDateFormat(json
                .format()) : this.formatter;
        this.string(formatter.format(date));
    }

    /**
     * Add array to buffer
     */
    protected void array(Iterator it, Method method) throws JSONException {
        this.add("[");

        boolean hasData = false;
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
            this.value(it.next(), method);
            if (this.buildExpr) {
                this.setExprStack(expr);
            }
        }

        this.add("]");
    }

    /**
     * Add array to buffer
     */
    protected void array(Object object, Method method) throws JSONException {
        this.add("[");

        int length = Array.getLength(object);

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
            this.value(Array.get(object, i), method);
            if (this.buildExpr) {
                this.setExprStack(expr);
            }
        }

        this.add("]");
    }

    /**
     * Add boolean to buffer
     */
    protected void bool(boolean b) {
        this.add(b ? "true" : "false");
    }

    /**
     * escape characters
     */
    protected void string(Object obj) {
        this.add('"');

        CharacterIterator it = new StringCharacterIterator(obj.toString());

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
                this.unicode(c);
            } else {
                this.add(c);
            }
        }

        this.add('"');
    }

    /**
     * Add object to buffer
     */
    protected void add(Object obj) {
        this.buf.append(obj);
    }

    /**
     * Add char to buffer
     */
    protected void add(char c) {
        this.buf.append(c);
    }

    /**
     * Represent as unicode
     *
     * @param c character to be encoded
     */
    protected void unicode(char c) {
        this.add("\\u");

        int n = c;

        for (int i = 0; i < 4; ++i) {
            int digit = (n & 0xf000) >> 12;

            this.add(hex[digit]);
            n <<= 4;
        }
    }

    public void setIgnoreHierarchy(boolean ignoreHierarchy) {
        this.ignoreHierarchy = ignoreHierarchy;
    }

    /**
     * If true, an Enum is serialized as a bean with a special property
     * _name=name() as all as all other properties defined within the enum.<br/>
     * If false, an Enum is serialized as a name=value pair (name=name())
     *
     * @param enumAsBean true to serialize an enum as a bean instead of as a name=value
     *                   pair (default=false)
     */
    public void setEnumAsBean(boolean enumAsBean) {
        this.enumAsBean = enumAsBean;
    }

    public void setDateFormatter(String defaultDateFormat) {
        if (defaultDateFormat != null) {
            this.formatter = new SimpleDateFormat(defaultDateFormat);
        }
    }

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

}
