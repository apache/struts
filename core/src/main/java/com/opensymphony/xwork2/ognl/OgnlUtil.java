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
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import ognl.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;


/**
 * Utility class that provides common access to the Ognl APIs for
 * setting and getting properties from objects (usually Actions).
 *
 * @author Jason Carreira
 */
public class OgnlUtil {

    private static final Logger LOG = LogManager.getLogger(OgnlUtil.class);

    // Flag used to reduce flooding logs with WARNs about using DevMode excluded packages
    private final AtomicBoolean warnReported = new AtomicBoolean(false);

    private final ConcurrentMap<String, Object> expressions = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, BeanInfo> beanInfoCache = new ConcurrentHashMap<>();
    private TypeConverter defaultConverter;

    private boolean devMode;
    private boolean enableExpressionCache = true;
    private boolean enableEvalExpression;

    private Set<Class<?>> excludedClasses;
    private Set<Pattern> excludedPackageNamePatterns;
    private Set<String> excludedPackageNames;

    private Set<Class<?>> devModeExcludedClasses;
    private Set<Pattern> devModeExcludedPackageNamePatterns;
    private Set<String> devModeExcludedPackageNames;

    private Container container;
    private boolean allowStaticFieldAccess = true;
    private boolean allowStaticMethodAccess;
    private boolean disallowProxyMemberAccess;

    public OgnlUtil() {
        excludedClasses = Collections.unmodifiableSet(new HashSet<>());
        excludedPackageNamePatterns = Collections.unmodifiableSet(new HashSet<>());
        excludedPackageNames = Collections.unmodifiableSet(new HashSet<>());

        devModeExcludedClasses = Collections.unmodifiableSet(new HashSet<>());
        devModeExcludedPackageNamePatterns = Collections.unmodifiableSet(new HashSet<>());
        devModeExcludedPackageNames = Collections.unmodifiableSet(new HashSet<>());
    }

    @Inject
    protected void setXWorkConverter(XWorkConverter conv) {
        this.defaultConverter = new OgnlTypeConverterWrapper(conv);
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    protected void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean(mode);
    }

    @Inject(StrutsConstants.STRUTS_OGNL_ENABLE_EXPRESSION_CACHE)
    protected void setEnableExpressionCache(String cache) {
        enableExpressionCache = BooleanUtils.toBoolean(cache);
    }

    @Inject(value = StrutsConstants.STRUTS_OGNL_ENABLE_EVAL_EXPRESSION, required = false)
    protected void setEnableEvalExpression(String evalExpression) {
        this.enableEvalExpression = BooleanUtils.toBoolean(evalExpression);
        if (this.enableEvalExpression) {
            LOG.warn("Enabling OGNL expression evaluation may introduce security risks " +
                    "(see http://struts.apache.org/release/2.3.x/docs/s2-013.html for further details)");
        }
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_CLASSES, required = false)
    protected void setExcludedClasses(String commaDelimitedClasses) {
        Set<Class<?>> excludedClasses = new HashSet<>();
        excludedClasses.addAll(this.excludedClasses);
        excludedClasses.addAll(parseExcludedClasses(commaDelimitedClasses));
        this.excludedClasses = Collections.unmodifiableSet(excludedClasses);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_CLASSES, required = false)
    protected void setDevModeExcludedClasses(String commaDelimitedClasses) {
        Set<Class<?>> excludedClasses = new HashSet<>();
        excludedClasses.addAll(this.devModeExcludedClasses);
        excludedClasses.addAll(parseExcludedClasses(commaDelimitedClasses));
        this.devModeExcludedClasses = Collections.unmodifiableSet(excludedClasses);
    }

    private Set<Class<?>> parseExcludedClasses(String commaDelimitedClasses) {
        Set<String> classNames = TextParseUtil.commaDelimitedStringToSet(commaDelimitedClasses);
        Set<Class<?>> classes = new HashSet<>();

        for (String className : classNames) {
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException("Cannot load excluded class: " + className, e);
            }
        }

        return classes;
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAME_PATTERNS, required = false)
    protected void setExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        Set<Pattern> excludedPackageNamePatterns = new HashSet<>();
        excludedPackageNamePatterns.addAll(this.excludedPackageNamePatterns);
        excludedPackageNamePatterns.addAll(parseExcludedPackageNamePatterns(commaDelimitedPackagePatterns));
        this.excludedPackageNamePatterns = Collections.unmodifiableSet(excludedPackageNamePatterns);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_PACKAGE_NAME_PATTERNS, required = false)
    protected void setDevModeExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        Set<Pattern> excludedPackageNamePatterns = new HashSet<>();
        excludedPackageNamePatterns.addAll(this.devModeExcludedPackageNamePatterns);
        excludedPackageNamePatterns.addAll(parseExcludedPackageNamePatterns(commaDelimitedPackagePatterns));
        this.devModeExcludedPackageNamePatterns = Collections.unmodifiableSet(excludedPackageNamePatterns);
    }

    private Set<Pattern> parseExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        Set<String> packagePatterns = TextParseUtil.commaDelimitedStringToSet(commaDelimitedPackagePatterns);
        Set<Pattern> packageNamePatterns = new HashSet<>();

        for (String pattern : packagePatterns) {
            packageNamePatterns.add(Pattern.compile(pattern));
        }

        return packageNamePatterns;
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAMES, required = false)
    protected void setExcludedPackageNames(String commaDelimitedPackageNames) {
        Set<String> excludedPackageNames = new HashSet<>();
        excludedPackageNames.addAll(this.excludedPackageNames);
        excludedPackageNames.addAll(parseExcludedPackageNames(commaDelimitedPackageNames));
        this.excludedPackageNames = Collections.unmodifiableSet(excludedPackageNames);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_PACKAGE_NAMES, required = false)
    protected void setDevModeExcludedPackageNames(String commaDelimitedPackageNames) {
        Set<String> excludedPackageNames = new HashSet<>();
        excludedPackageNames.addAll(this.devModeExcludedPackageNames);
        excludedPackageNames.addAll(parseExcludedPackageNames(commaDelimitedPackageNames));
        this.devModeExcludedPackageNames = Collections.unmodifiableSet(excludedPackageNames);
    }

    private Set<String> parseExcludedPackageNames(String commaDelimitedPackageNames) {
        return TextParseUtil.commaDelimitedStringToSet(commaDelimitedPackageNames);
    }

    public Set<Class<?>> getExcludedClasses() {
        return excludedClasses;
    }

    public Set<Pattern> getExcludedPackageNamePatterns() {
        return excludedPackageNamePatterns;
    }

    public Set<String> getExcludedPackageNames() {
        return excludedPackageNames;
    }

    @Inject
    protected void setContainer(Container container) {
        this.container = container;
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOW_STATIC_FIELD_ACCESS, required = false)
    protected void setAllowStaticFieldAccess(String allowStaticFieldAccess) {
        this.allowStaticFieldAccess = BooleanUtils.toBoolean(allowStaticFieldAccess);
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS, required = false)
    protected void setAllowStaticMethodAccess(String allowStaticMethodAccess) {
        this.allowStaticMethodAccess = BooleanUtils.toBoolean(allowStaticMethodAccess);
    }

    @Inject(value = StrutsConstants.STRUTS_DISALLOW_PROXY_MEMBER_ACCESS, required = false)
    protected void setDisallowProxyMemberAccess(String disallowProxyMemberAccess) {
        this.disallowProxyMemberAccess = BooleanUtils.toBoolean(disallowProxyMemberAccess);
    }

    /**
     * @param maxLength Injects the Struts OGNL expression maximum length.
     */
    @Inject(value = StrutsConstants.STRUTS_OGNL_EXPRESSION_MAX_LENGTH, required = false)
    protected void applyExpressionMaxLength(String maxLength) {
        try {
            if (maxLength == null || maxLength.isEmpty()) {
                Ognl.applyExpressionMaxLength(null);
                LOG.info("OGNL Expression Max Length disabled.");
            } else {
                Ognl.applyExpressionMaxLength(Integer.parseInt(maxLength));
                LOG.info("OGNL Expression Max Length enabled with {}.", maxLength);
            }
        } catch (Exception ex) {
            LOG.error("Unable to set OGNL Expression Max Length {}.", maxLength);  // Help configuration debugging.
            throw ex;
        }
    }

    public boolean isDisallowProxyMemberAccess() {
        return disallowProxyMemberAccess;
    }

    /**
     * Convenience mechanism to clear the OGNL Runtime Cache via OgnlUtil.  May be utilized
     * by applications that generate many unique OGNL expressions over time.
     *
     * Note: This call affects the global OGNL cache, see ({@link ognl.OgnlRuntime#clearCache()} for details.
     *
     * Warning: Frequent calling if this method may negatively impact performance, but may be required
     *          to avoid memory exhaustion (resource leak) with too many OGNL expressions being cached.
     *
     * @since 2.5.21
     */
    public static void clearRuntimeCache() {
        OgnlRuntime.clearCache();
    }

    /**
     * Provide a mechanism to clear the OGNL expression cache.  May be utilized by applications
     * that generate many unique OGNL expressions over time.
     *
     * Note: This call affects the current OgnlUtil instance.  For Struts this is often a Singleton
     *       instance so it can be "effectively global".
     *
     * Warning: Frequent calling if this method may negatively impact performance, but may be required
     *          to avoid memory exhaustion (resource leak) with too many OGNL expressions being cached.
     *
     * @since 2.5.21
     */
    public void clearExpressionCache() {
        expressions.clear();
    }

    /**
     * Check the size of the expression cache (current number of elements).
     *
     * @return current number of elements in the expression cache.
     *
     * @since 2.5.21
     */
    public int expressionCacheSize() {
        return expressions.size();
    }

    /**
     * Provide a mechanism to clear the BeanInfo cache.  May be utilized by applications
     * that request BeanInfo and/or PropertyDescriptors for many unique classes or objects over time
     * (especially dynamic objects).
     *
     * Note: This call affects the current OgnlUtil instance.  For Struts this is often a Singleton
     *       instance so it can be "effectively global".
     *
     * Warning: Frequent calling if this method may negatively impact performance, but may be required
     *          to avoid memory exhaustion (resource leak) with too many BeanInfo elements being cached.
     *
     * @since 2.5.21
     */
    public void clearBeanInfoCache() {
        beanInfoCache.clear();
    }

    /**
     * Check the size of the BeanInfo cache (current number of elements).
     *
     * @return current number of elements in the BeanInfo cache.
     *
     * @since 2.5.21
     */
    public int beanInfoCacheSize() {
        return beanInfoCache.size();
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
     * @param properties map of properties
     * @param o object
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
        Map<String, Object> context = createDefaultContext(o);
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

        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, o);

        internalSetProperty(name, value, o, context, throwPropertyExceptions);

        Ognl.setRoot(context, oldRoot);
    }

    /**
     * Looks for the real target with the specified property given a root Object which may be a
     * CompoundRoot.
     *
     * @param property  the property
     * @param context context map
     * @param root compound root
     *
     * @return the real target or null if no object can be found with the specified property
     * @throws OgnlException in case of ognl errors
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
                    if (OgnlRuntime.hasSetProperty((OgnlContext) context, target, property)
                            || OgnlRuntime.hasGetProperty((OgnlContext) context, target, property)
                            || OgnlRuntime.getIndexedPropertyType((OgnlContext) context, target.getClass(), property) != OgnlRuntime.INDEXED_PROPERTY_NONE
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
     *
     * @param name  the name
     * @param context context map
     * @param root root
     * @param value value
     *
     * @throws OgnlException in case of ognl errors
     */
    public void setValue(final String name, final Map<String, Object> context, final Object root, final Object value) throws OgnlException {
        compileAndExecute(name, context, (OgnlTask<Void>) tree -> {
            if (isEvalExpression(tree, context)) {
                throw new OgnlException("Eval expression/chained expressions cannot be used as parameter name");
            }
            if (isArithmeticExpression(tree, context)) {
                throw new OgnlException("Arithmetic expressions cannot be used as parameter name");
            }
            Ognl.setValue(tree, context, root, value);
            return null;
        });
    }

    private boolean isEvalExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (tree instanceof SimpleNode) {
            SimpleNode node = (SimpleNode) tree;
            OgnlContext ognlContext = null;

            if (context instanceof OgnlContext) {
                ognlContext = (OgnlContext) context;
            }
            return node.isEvalChain(ognlContext) || node.isSequence(ognlContext);
        }
        return false;
    }

    private boolean isArithmeticExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (tree instanceof SimpleNode) {
            SimpleNode node = (SimpleNode) tree;
            OgnlContext ognlContext = null;

            if (context instanceof OgnlContext) {
                ognlContext = (OgnlContext) context;
            }
            return node.isOperation(ognlContext);
        }
        return false;
    }

    private boolean isSimpleMethod(Object tree, Map<String, Object> context) throws OgnlException {
        if (tree instanceof SimpleNode) {
            SimpleNode node = (SimpleNode) tree;
            OgnlContext ognlContext = null;

            if (context instanceof OgnlContext) {
                ognlContext = (OgnlContext) context;
            }
            return node.isSimpleMethod(ognlContext) && !node.isChain(ognlContext);
        }
        return false;
    }

    public Object getValue(final String name, final Map<String, Object> context, final Object root) throws OgnlException {
        return compileAndExecute(name, context, tree -> Ognl.getValue(tree, context, root));
    }

    public Object callMethod(final String name, final Map<String, Object> context, final Object root) throws OgnlException {
        return compileAndExecuteMethod(name, context, tree -> Ognl.getValue(tree, context, root));
    }

    public Object getValue(final String name, final Map<String, Object> context, final Object root, final Class<?> resultType) throws OgnlException {
        return compileAndExecute(name, context, tree -> Ognl.getValue(tree, context, root, resultType));
    }


    public Object compile(String expression) throws OgnlException {
        return compile(expression, null);
    }

    private <T> Object compileAndExecute(String expression, Map<String, Object> context, OgnlTask<T> task) throws OgnlException {
        Object tree;
        if (enableExpressionCache) {
            tree = expressions.get(expression);
            if (tree == null) {
                tree = Ognl.parseExpression(expression);
                checkEnableEvalExpression(tree, context);
                expressions.putIfAbsent(expression, tree);
            }
        } else {
            tree = Ognl.parseExpression(expression);
            checkEnableEvalExpression(tree, context);
        }

        return task.execute(tree);
    }

    private <T> Object compileAndExecuteMethod(String expression, Map<String, Object> context, OgnlTask<T> task) throws OgnlException {
        Object tree;
        if (enableExpressionCache) {
            tree = expressions.get(expression);
            if (tree == null) {
                tree = Ognl.parseExpression(expression);
                checkSimpleMethod(tree, context);
                expressions.putIfAbsent(expression, tree);
            }
        } else {
            tree = Ognl.parseExpression(expression);
            checkSimpleMethod(tree, context);
        }

        return task.execute(tree);
    }

    public Object compile(String expression, Map<String, Object> context) throws OgnlException {
        return compileAndExecute(expression, context, tree -> tree);
    }

    private void checkEnableEvalExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (!enableEvalExpression && isEvalExpression(tree, context)) {
            throw new OgnlException("Eval expressions/chained expressions have been disabled!");
        }
    }

    private void checkSimpleMethod(Object tree, Map<String, Object> context) throws OgnlException {
        if (!isSimpleMethod(tree, context)) {
            throw new OgnlException("It isn't a simple method which can be called!");
        }
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
    public void copy(final Object from, final Object to, final Map<String, Object> context, Collection<String> exclusions, Collection<String> inclusions) {
        copy(from, to, context, exclusions, inclusions, null);
    }

    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * only setting properties defined in the given "editable" class (or interface)
     * using specified type converter, or {@link com.opensymphony.xwork2.conversion.impl.XWorkConverter} if none
     * is specified.
     *
     * @param from       the source object
     * @param to         the target object
     * @param context    the action context we're running under
     * @param exclusions collection of method names to excluded from copying ( can be null)
     * @param inclusions collection of method names to included copying  (can be null)
     *                   note if exclusions AND inclusions are supplied and not null nothing will get copied.
     * @param editable the class (or interface) to restrict property setting to
     */
    public void copy(final Object from, final Object to, final Map<String, Object> context, Collection<String> exclusions, Collection<String> inclusions, Class<?> editable) {
        if (from == null || to == null) {
            LOG.warn("Attempting to copy from or to a null source. This is illegal and is bein skipped. This may be due to an error in an OGNL expression, action chaining, or some other event.");
            return;
        }

        final Map<String, Object> contextFrom = createDefaultContext(from);
        final Map<String, Object> contextTo = createDefaultContext(to);

        PropertyDescriptor[] fromPds;
        PropertyDescriptor[] toPds;

        try {
            fromPds = getPropertyDescriptors(from);
            if (editable != null) {
                toPds = getPropertyDescriptors(editable);
            }
            else {
                toPds = getPropertyDescriptors(to);
            }
        } catch (IntrospectionException e) {
            LOG.error("An error occurred", e);
            return;
        }

        Map<String, PropertyDescriptor> toPdHash = new HashMap<>();

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

                if (copy) {
                    PropertyDescriptor toPd = toPdHash.get(fromPd.getName());
                    if ((toPd != null) && (toPd.getWriteMethod() != null)) {
                        try {
                            compileAndExecute(fromPd.getName(), context, expr -> {
                                Object value = Ognl.getValue(expr, contextFrom, from);
                                Ognl.setValue(expr, contextTo, to, value);
                                return null;
                            });

                        } catch (OgnlException e) {
                            LOG.debug("Got OGNL exception", e);
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
    public PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws IntrospectionException {
        BeanInfo beanInfo = getBeanInfo(clazz);
        return beanInfo.getPropertyDescriptors();
    }

    /**
     * Creates a Map with read properties for the given source object.
     * <p>
     * If the source object does not have a read property (i.e. write-only) then
     * the property is added to the map with the value <code>here is no read method for property-name</code>.
     * </p>
     *
     * @param source the source object.
     * @return a Map with (key = read property name, value = value of read property).
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     * @throws OgnlException          is thrown by OGNL if the property value could not be retrieved
     */
    public Map<String, Object> getBeanMap(final Object source) throws IntrospectionException, OgnlException {
        Map<String, Object> beanMap = new HashMap<>();
        final Map<String, Object> sourceMap = createDefaultContext(source);
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(source);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            final String propertyName = propertyDescriptor.getDisplayName();
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                final Object value = compileAndExecute(propertyName, null, expr -> Ognl.getValue(expr, sourceMap, source));
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
    public BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
        synchronized (beanInfoCache) {
            BeanInfo beanInfo = beanInfoCache.get(clazz);
            if (beanInfo == null) {
                beanInfo = Introspector.getBeanInfo(clazz, Object.class);
                beanInfoCache.putIfAbsent(clazz, beanInfo);
            }
            return beanInfo;
        }
    }

    void internalSetProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws ReflectionException{
        try {
            setValue(name, context, o, value);
        } catch (OgnlException e) {
            Throwable reason = e.getReason();
            if (reason instanceof SecurityException) {
                LOG.error("Could not evaluate this expression due to security constraints: [{}]", name, e);
            }
            String msg = "Caught OgnlException while setting property '" + name + "' on type '" + o.getClass().getName() + "'.";
            Throwable exception = (reason == null) ? e : reason;

            if (throwPropertyExceptions) {
                throw new ReflectionException(msg, exception);
            } else if (devMode) {
                LOG.warn(msg, exception);
            }
        }
    }

    protected Map<String, Object> createDefaultContext(Object root) {
        return createDefaultContext(root, null);
    }

    protected Map<String, Object> createDefaultContext(Object root, ClassResolver classResolver) {
        ClassResolver resolver = classResolver;
        if (resolver == null) {
            resolver = container.getInstance(CompoundRootAccessor.class);
        }

        SecurityMemberAccess memberAccess = new SecurityMemberAccess(allowStaticMethodAccess, allowStaticFieldAccess);
        memberAccess.setDisallowProxyMemberAccess(disallowProxyMemberAccess);

        if (devMode) {
            if (!warnReported.get()) {
                warnReported.set(true);
                LOG.warn("Working in devMode, using devMode excluded classes and packages!");
            }
            memberAccess.setExcludedClasses(devModeExcludedClasses);
            memberAccess.setExcludedPackageNamePatterns(devModeExcludedPackageNamePatterns);
            memberAccess.setExcludedPackageNames(devModeExcludedPackageNames);
        } else {
            memberAccess.setExcludedClasses(excludedClasses);
            memberAccess.setExcludedPackageNamePatterns(excludedPackageNamePatterns);
            memberAccess.setExcludedPackageNames(excludedPackageNames);
        }

        return Ognl.createDefaultContext(root, memberAccess, resolver, defaultConverter);
    }

    private interface OgnlTask<T> {
        T execute(Object tree) throws OgnlException;
    }

}
