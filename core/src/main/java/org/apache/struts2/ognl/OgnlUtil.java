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

import ognl.ClassResolver;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.TypeConverter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.conversion.impl.XWorkConverter;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.accessor.RootAccessor;
import org.apache.struts2.util.CompoundRoot;
import org.apache.struts2.util.reflection.ReflectionException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.apache.struts2.ognl.OgnlGuard.EXPR_BLOCKED;


/**
 * Utility class that provides common access to the Ognl APIs for
 * setting and getting properties from objects (usually Actions).
 *
 * @author Jason Carreira
 */
public class OgnlUtil {

    private static final Logger LOG = LogManager.getLogger(OgnlUtil.class);

    private final OgnlCache<String, Object> expressionCache;
    private final OgnlCache<Class<?>, BeanInfo> beanInfoCache;
    private TypeConverter defaultConverter;
    private final OgnlGuard ognlGuard;

    private boolean devMode;
    private boolean enableExpressionCache = true;
    private boolean enableEvalExpression;

    private Container container;

    /**
     * Construct a new OgnlUtil instance for use with the framework, with optional cache factories for OGNL Expression
     * and BeanInfo caches.
     *
     * @param ognlExpressionCacheFactory factory for Expression cache instance
     * @param ognlBeanInfoCacheFactory   factory for BeanInfo cache instance
     * @param ognlGuard                  OGNL Guard instance
     */
    @Inject
    public OgnlUtil(@Inject ExpressionCacheFactory<String, Object> ognlExpressionCacheFactory,
                    @Inject BeanInfoCacheFactory<Class<?>, BeanInfo> ognlBeanInfoCacheFactory,
                    @Inject OgnlGuard ognlGuard) {
        this.expressionCache =  requireNonNull(ognlExpressionCacheFactory).buildOgnlCache();
        this.beanInfoCache =  requireNonNull(ognlBeanInfoCacheFactory).buildOgnlCache();
        this.ognlGuard = requireNonNull(ognlGuard);
    }

    @Inject
    protected void setXWorkConverter(XWorkConverter conv) {
        this.defaultConverter = new OgnlTypeConverterWrapper(conv);
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    protected void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean(mode);
    }

    @Inject(value = StrutsConstants.STRUTS_OGNL_ENABLE_EXPRESSION_CACHE, required = false)
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

    @Inject
    protected void setContainer(Container container) {
        this.container = container;
    }

    /**
     * @param maxLength Injects the Struts OGNL expression maximum length.
     */
    @Inject(value = StrutsConstants.STRUTS_OGNL_EXPRESSION_MAX_LENGTH, required = false)
    protected void applyExpressionMaxLength(String maxLength) {
        try {
            if (maxLength == null || maxLength.isEmpty()) {
                Ognl.applyExpressionMaxLength(null);
                LOG.warn("OGNL Expression Max Length disabled.");
            } else {
                Ognl.applyExpressionMaxLength(Integer.parseInt(maxLength));
                LOG.debug("OGNL Expression Max Length enabled with {}.", maxLength);
            }
        } catch (Exception ex) {
            LOG.error("Unable to set OGNL Expression Max Length {}.", maxLength);  // Help configuration debugging.
            throw ex;
        }
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
        expressionCache.clear();
    }

    /**
     * Check the size of the expression cache (current number of elements).
     *
     * @return current number of elements in the expression cache.
     *
     * @since 2.5.21
     */
    public int expressionCacheSize() {
        return expressionCache.size();
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
     * Wrapper around Ognl#setValue
     *
     * @param name  the name
     * @param context context map
     * @param root root
     * @param value value
     *
     * @throws OgnlException in case of ognl errors
     */
    public void setValue(final String name, final Map<String, Object> context, final Object root, final Object value) throws OgnlException {
        ognlSet(name, context, root, value, context, this::checkEvalExpression, this::checkArithmeticExpression);
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
        return getValue(name, context, root, null);
    }

    public Object callMethod(final String name, final Map<String, Object> context, final Object root) throws OgnlException {
        return ognlGet(name, context, root, null, context, this::checkSimpleMethod);
    }

    public Object getValue(final String name, final Map<String, Object> context, final Object root, final Class<?> resultType) throws OgnlException {
        return ognlGet(name, context, root, resultType, context, this::checkEnableEvalExpression);
    }

    public Object compile(String expression) throws OgnlException {
        return compile(expression, null);
    }

    private void ognlSet(String expr, Map<String, Object> context, Object root, Object value, Map<String, Object> checkContext, TreeValidator... treeValidators) throws OgnlException {
        Object tree = toTree(expr);
        for (TreeValidator validator : treeValidators) {
            validator.validate(tree, checkContext);
        }
        Ognl.setValue(tree, context, root, value);
    }

    private <T> T ognlGet(String expr, Map<String, Object> context, Object root, Class<T> resultType, Map<String, Object> checkContext, TreeValidator... treeValidators) throws OgnlException {
        Object tree = toTree(expr);
        for (TreeValidator validator : treeValidators) {
            validator.validate(tree, checkContext);
        }
        return (T) Ognl.getValue(tree, context, root, resultType);
    }

    private Object toTree(String expr) throws OgnlException {
        Object tree = null;
        if (enableExpressionCache) {
            tree = expressionCache.get(expr);
        }
        if (tree instanceof OgnlException) {
            // OgnlException was cached, rethrow it with empty stack trace (refilling the stack trace is expensive)
            clearStackTraceAndRethrow(tree);
        }
        if (tree == null) {
            try {
                tree = ognlGuard.parseExpression(expr);
            } catch (OgnlException e) {
                tree = e;
            }
            if (enableExpressionCache) {
                expressionCache.put(expr, tree);
            }
            if (tree instanceof OgnlException) {
                // Rethrow OgnlException after caching
                throw (OgnlException) tree;
            }
        }
        if (EXPR_BLOCKED.equals(tree)) {
            throw new OgnlException("Expression blocked by OgnlGuard: " + expr);
        }
        return tree;
    }

    private void clearStackTraceAndRethrow(Object ognlException) throws OgnlException {
        OgnlException e = (OgnlException) ognlException;
        e.setStackTrace(new StackTraceElement[0]);
        if (e.getCause() != null) {
            e.getCause().setStackTrace(new StackTraceElement[0]);
        }
        throw e;
    }

    public Object compile(String expression, Map<String, Object> context) throws OgnlException {
        Object tree = toTree(expression);
        checkEnableEvalExpression(tree, context);
        return tree;
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

    private void checkEvalExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (isEvalExpression(tree, context)) {
            throw new OgnlException("Eval expression/chained expressions cannot be used as parameter name");
        }
    }

    private void checkArithmeticExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (isArithmeticExpression(tree, context)) {
            throw new OgnlException("Arithmetic expressions cannot be used as parameter name");
        }
    }

    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * using specified type converter, or {@link XWorkConverter} if none
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
     * using specified type converter, or {@link XWorkConverter} if none
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
    public void copy(final Object from,
                     final Object to,
                     final Map<String, Object> context,
                     Collection<String> exclusions,
                     Collection<String> inclusions,
                     Class<?> editable) {
        if (from == null || to == null) {
            LOG.warn(
                    "Skipping attempt to copy from, or to, a null source.", new RuntimeException());
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
            } else {
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
            if (fromPd.getReadMethod() == null) {
                continue;
            }

            if (exclusions != null && exclusions.contains(fromPd.getName()) ||
                    inclusions != null && !inclusions.contains(fromPd.getName())) {
                continue;
            }

            PropertyDescriptor toPd = toPdHash.get(fromPd.getName());
            if (toPd == null || toPd.getWriteMethod() == null) {
                continue;
            }

            try {
                Object value = ognlGet(fromPd.getName(),
                        contextFrom,
                        from,
                        null,
                        context,
                        this::checkEnableEvalExpression);
                ognlSet(fromPd.getName(), contextTo, to, value, context);
            } catch (OgnlException e) {
                LOG.debug("Got OGNL exception", e);
            }
        }
    }


    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * using specified type converter, or {@link XWorkConverter} if none
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
     * Gets the java beans property descriptors for the given source.
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
                final Object value = ognlGet(propertyName, sourceMap, source, null, null, this::checkEnableEvalExpression);
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
        try {
            return beanInfoCache.computeIfAbsent(clazz, k -> {
                try {
                    return Introspector.getBeanInfo(k, Object.class);
                } catch (IntrospectionException e) {
                    throw new IllegalArgumentException(e);
                }
            });
        } catch (IllegalArgumentException e) {
            if (e.getCause() instanceof IntrospectionException innerEx) {
                throw innerEx;
            }
            throw e;
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

    protected Map<String, Object> createDefaultContext(Object root, ClassResolver resolver) {
        if (resolver == null) {
            resolver = container.getInstance(RootAccessor.class);
            if (resolver == null) {
                throw new IllegalStateException("Cannot find ClassResolver");
            }
        }
        return Ognl.createDefaultContext(root, container.getInstance(SecurityMemberAccess.class), resolver, defaultConverter);
    }

    @FunctionalInterface
    private interface TreeValidator {
        void validate(Object tree, Map<String, Object> context) throws OgnlException;
    }
}
