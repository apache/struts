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

import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import ognl.ClassResolver;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.TypeConverter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;


/**
 * Utility class that provides common access to the Ognl APIs for
 * setting and getting properties from objects (usually Actions).
 *
 * @author Jason Carreira
 */
public class OgnlUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OgnlUtil.class);
    private ConcurrentMap<String, Object> expressions = new ConcurrentHashMap<String, Object>();
    private final ConcurrentMap<Class, BeanInfo> beanInfoCache = new ConcurrentHashMap<Class, BeanInfo>();
    private TypeConverter defaultConverter;

    private boolean devMode = false;
    private boolean enableExpressionCache = true;
    private boolean enableEvalExpression;

    private Set<Class<?>> excludedClasses;
    private Set<Pattern> excludedPackageNamePatterns;
    private Set<String> excludedPackageNames;

    private Container container;
    private boolean allowStaticMethodAccess;
    private boolean disallowProxyMemberAccess;

    public OgnlUtil() {
        excludedClasses = new HashSet<Class<?>>();
        excludedPackageNamePatterns = new HashSet<Pattern>();
        excludedPackageNames = new HashSet<String>();
    }

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.defaultConverter = new OgnlTypeConverterWrapper(conv);
    }

    @Inject(XWorkConstants.DEV_MODE)
    public void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }

    @Inject(XWorkConstants.ENABLE_OGNL_EXPRESSION_CACHE)
    public void setEnableExpressionCache(String cache) {
       enableExpressionCache = "true".equals(cache);
    }

    @Inject(value = XWorkConstants.ENABLE_OGNL_EVAL_EXPRESSION, required = false)
    public void setEnableEvalExpression(String evalExpression) {
        enableEvalExpression = "true".equals(evalExpression);
        if(enableEvalExpression){
            LOG.warn("Enabling OGNL expression evaluation may introduce security risks " +
                    "(see http://struts.apache.org/release/2.3.x/docs/s2-013.html for further details)");
        }
    }

    @Inject(value = XWorkConstants.OGNL_EXCLUDED_CLASSES, required = false)
    public void setExcludedClasses(String commaDelimitedClasses) {
        Set<Class<?>> excludedClasses = new HashSet<Class<?>>();
        excludedClasses.addAll(this.excludedClasses);
        excludedClasses.addAll(parseExcludedClasses(commaDelimitedClasses));
        this.excludedClasses = Collections.unmodifiableSet(excludedClasses);
    }

    private Set<Class<?>> parseExcludedClasses(String commaDelimitedClasses) {
        Set<String> classNames = TextParseUtil.commaDelimitedStringToSet(commaDelimitedClasses);
        Set<Class<?>> classes = new HashSet<Class<?>>();

        for (String className : classNames) {
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException("Cannot load excluded class: " + className, e);
            }
        }

        return classes;
    }

    @Inject(value = XWorkConstants.OGNL_EXCLUDED_PACKAGE_NAME_PATTERNS, required = false)
    public void setExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        Set<Pattern> excludedPackageNamePatterns = new HashSet<Pattern>();
        excludedPackageNamePatterns.addAll(this.excludedPackageNamePatterns);
        excludedPackageNamePatterns.addAll(parseExcludedPackageNamePatterns(commaDelimitedPackagePatterns));
        this.excludedPackageNamePatterns = Collections.unmodifiableSet(excludedPackageNamePatterns);
    }

    private Set<Pattern> parseExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        Set<String> packagePatterns = TextParseUtil.commaDelimitedStringToSet(commaDelimitedPackagePatterns);
        Set<Pattern> packageNamePatterns = new HashSet<Pattern>();

        for (String pattern : packagePatterns) {
            packageNamePatterns.add(Pattern.compile(pattern));
        }

        return packageNamePatterns;
    }

    @Inject(value = XWorkConstants.OGNL_EXCLUDED_PACKAGE_NAMES, required = false)
    public void setExcludedPackageNames(String commaDelimitedPackageNames) {
        Set<String> excludedPackageNames = new HashSet<String>();
        excludedPackageNames.addAll(this.excludedPackageNames);
        excludedPackageNames.addAll(parseExcludedPackageNames(commaDelimitedPackageNames));
        this.excludedPackageNames = Collections.unmodifiableSet(excludedPackageNames);
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
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(value = XWorkConstants.ALLOW_STATIC_METHOD_ACCESS, required = false)
    public void setAllowStaticMethodAccess(String allowStaticMethodAccess) {
        this.allowStaticMethodAccess = Boolean.parseBoolean(allowStaticMethodAccess);
    }

    @Inject(value = XWorkConstants.XWORK_DISALLOW_PROXY_MEMBER_ACCESS, required = false)
    public void setDisallowProxyMemberAccess(String disallowProxyMemberAccess) {
        this.disallowProxyMemberAccess = Boolean.parseBoolean(disallowProxyMemberAccess);
    }

    public boolean isDisallowProxyMemberAccess() {
        return disallowProxyMemberAccess;
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
        Map context = createDefaultContext(o, null);
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
    public void setValue(final String name, final Map<String, Object> context, final Object root, final Object value) throws OgnlException {
        compileAndExecute(name, context, new OgnlTask<Void>() {
            public Void execute(Object tree) throws OgnlException {
                if (isEvalExpression(tree, context)) {
                    throw new OgnlException("Eval expression/chained expressions cannot be used as parameter name");
                }
                if (isArithmeticExpression(tree, context)) {
                    throw new OgnlException("Arithmetic expressions cannot be used as parameter name");
                }
                Ognl.setValue(tree, context, root, value);
                return null;
            }
        });
    }

    private boolean isEvalExpression(Object tree, Map<String, Object> context) throws OgnlException {
        if (tree instanceof SimpleNode) {
            SimpleNode node = (SimpleNode) tree;
            OgnlContext ognlContext = null;

            if (context!=null && context instanceof OgnlContext) {
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

            if (context!=null && context instanceof OgnlContext) {
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

            if (context!=null && context instanceof OgnlContext) {
                ognlContext = (OgnlContext) context;
            }
            return node.isSimpleMethod(ognlContext) && !node.isChain(ognlContext);
        }
        return false;
    }

    public Object getValue(final String name, final Map<String, Object> context, final Object root) throws OgnlException {
        return compileAndExecute(name, context, new OgnlTask<Object>() {
            public Object execute(Object tree) throws OgnlException {
                return Ognl.getValue(tree, context, root);
            }
        });
    }

    public Object callMethod(final String name, final Map<String, Object> context, final Object root) throws OgnlException {
        return compileAndExecuteMethod(name, context, new OgnlTask<Object>() {
            public Object execute(Object tree) throws OgnlException {
                return Ognl.getValue(tree, context, root);
            }
        });
    }

    public Object getValue(final String name, final Map<String, Object> context, final Object root, final Class resultType) throws OgnlException {
        return compileAndExecute(name, context, new OgnlTask<Object>() {
            public Object execute(Object tree) throws OgnlException {
                return Ognl.getValue(tree, context, root, resultType);
            }
        });
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
            }
        } else {
            tree = Ognl.parseExpression(expression);
            checkEnableEvalExpression(tree, context);
        }

        final T exec = task.execute(tree);
        // if cache is enabled and it's a valid expression, puts it in
        if(enableExpressionCache) {
            expressions.putIfAbsent(expression, tree);
        }
        return exec;
    }

    private <T> Object compileAndExecuteMethod(String expression, Map<String, Object> context, OgnlTask<T> task) throws OgnlException {
        Object tree;
        if (enableExpressionCache) {
            tree = expressions.get(expression);
            if (tree == null) {
                tree = Ognl.parseExpression(expression);
                checkSimpleMethod(tree, context);
            }
        } else {
            tree = Ognl.parseExpression(expression);
            checkSimpleMethod(tree, context);
        }

        final T exec = task.execute(tree);
        // if cache is enabled and it's a valid expression, puts it in
        if(enableExpressionCache) {
            expressions.putIfAbsent(expression, tree);
        }
        return exec;
    }

    public Object compile(String expression, Map<String, Object> context) throws OgnlException {
        return compileAndExecute(expression,context,new OgnlTask<Object>() {
            public Object execute(Object tree) throws OgnlException {
                return tree;
            }
        });
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
        if (from == null || to == null) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Attempting to copy from or to a null source. This is illegal and is bein skipped. This may be due to an error in an OGNL expression, action chaining, or some other event.");
            }

            return;
        }

        TypeConverter conv = getTypeConverterFromContext(context);
        final Map contextFrom = createDefaultContext(from, null);
        Ognl.setTypeConverter(contextFrom, conv);
        final Map contextTo = createDefaultContext(to, null);
        Ognl.setTypeConverter(contextTo, conv);

        PropertyDescriptor[] fromPds;
        PropertyDescriptor[] toPds;

        try {
            fromPds = getPropertyDescriptors(from);
            toPds = getPropertyDescriptors(to);
        } catch (IntrospectionException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("An error occured", e);
            }
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

                if (copy) {
                    PropertyDescriptor toPd = toPdHash.get(fromPd.getName());
                    if ((toPd != null) && (toPd.getWriteMethod() != null)) {
                        try {
                            compileAndExecute(fromPd.getName(), context, new OgnlTask<Object>() {
                                public Void execute(Object expr) throws OgnlException {
                                    Object value = Ognl.getValue(expr, contextFrom, from);
                                    Ognl.setValue(expr, contextTo, to, value);
                                    return null;
                                }
                            });

                        } catch (OgnlException e) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Got OGNL exception", e);
                            }
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
    public Map<String, Object> getBeanMap(final Object source) throws IntrospectionException, OgnlException {
        Map<String, Object> beanMap = new HashMap<String, Object>();
        final Map sourceMap = createDefaultContext(source, null);
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(source);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            final String propertyName = propertyDescriptor.getDisplayName();
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                final Object value = compileAndExecute(propertyName, null, new OgnlTask<Object>() {
                    public Object execute(Object expr) throws OgnlException {
                        return Ognl.getValue(expr, sourceMap, source);
                    }
                });
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
            String msg = "Caught OgnlException while setting property '" + name + "' on type '" + o.getClass().getName() + "'.";
            Throwable exception = (reason == null) ? e : reason;

            if (throwPropertyExceptions) {
                throw new ReflectionException(msg, exception);
            } else if (devMode) {
                LOG.warn(msg, exception);
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

    protected Map createDefaultContext(Object root) {
        return createDefaultContext(root, null);
    }

    protected Map createDefaultContext(Object root, ClassResolver classResolver) {
        ClassResolver resolver = classResolver;
        if (resolver == null) {
            resolver = container.getInstance(CompoundRootAccessor.class);
        }

        SecurityMemberAccess memberAccess = new SecurityMemberAccess(allowStaticMethodAccess);
        memberAccess.setExcludedClasses(excludedClasses);
        memberAccess.setExcludedPackageNamePatterns(excludedPackageNamePatterns);
        memberAccess.setExcludedPackageNames(excludedPackageNames);
        memberAccess.setDisallowProxyMemberAccess(disallowProxyMemberAccess);

        return Ognl.createDefaultContext(root, resolver, defaultConverter, memberAccess);
    }

    private interface OgnlTask<T> {
        T execute(Object tree) throws OgnlException;
    }

}
