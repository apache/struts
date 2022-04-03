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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.StrutsConstants;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

abstract class AbstractLocalizedTextProvider implements LocalizedTextProvider {

    private static final Logger LOG = LogManager.getLogger(AbstractLocalizedTextProvider.class);

    public static final String XWORK_MESSAGES_BUNDLE = "com/opensymphony/xwork2/xwork-messages";
    public static final String STRUTS_MESSAGES_BUNDLE = "org/apache/struts2/struts-messages";

    private static final String TOMCAT_RESOURCE_ENTRIES_FIELD = "resourceEntries";
    private static final String TOMCAT_PARALLEL_WEBAPP_CLASSLOADER = "org.apache.catalina.loader.ParallelWebappClassLoader";
    private static final String TOMCAT_WEBAPP_CLASSLOADER = "org.apache.catalina.loader.WebappClassLoader";
    private static final String TOMCAT_WEBAPP_CLASSLOADER_BASE = "org.apache.catalina.loader.WebappClassLoaderBase";
    private static final String RELOADED = "com.opensymphony.xwork2.util.LocalizedTextProvider.reloaded";

    protected final ConcurrentMap<String, ResourceBundle> bundlesMap = new ConcurrentHashMap<>();
    protected boolean devMode = false;
    protected boolean reloadBundles = false;
    protected boolean searchDefaultBundlesFirst = false;  // Search default resource bundles first.  Note: This flag may not be meaningful to all implementations.

    private final ConcurrentMap<MessageFormatKey, MessageFormat> messageFormats = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, List<String>> classLoaderMap = new ConcurrentHashMap<>();
    private final Set<String> missingBundles = ConcurrentHashMap.newKeySet();
    private final ConcurrentMap<Integer, ClassLoader> delegatedClassLoaderMap = new ConcurrentHashMap<>();

    /**
     * Adds the bundle to the internal list of default bundles.
     * If the bundle already exists in the list it will be re-added.
     *
     * @param resourceBundleName the name of the bundle to add.
     */
    @Override
    public void addDefaultResourceBundle(String resourceBundleName) {
        //make sure this doesn't get added more than once
        final ClassLoader ccl = getCurrentThreadContextClassLoader();
        synchronized (XWORK_MESSAGES_BUNDLE) {
            List<String> bundles = classLoaderMap.get(ccl.hashCode());
            if (bundles == null) {
                bundles = new CopyOnWriteArrayList<>();
                classLoaderMap.put(ccl.hashCode(), bundles);
            }
            bundles.remove(resourceBundleName);
            bundles.add(0, resourceBundleName);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Added default resource bundle '{}' to default resource bundles for the following classloader '{}'", resourceBundleName, ccl.toString());
        }
    }

    protected List<String> getCurrentBundleNames() {
        return classLoaderMap.get(getCurrentThreadContextClassLoader().hashCode());
    }

    protected ClassLoader getCurrentThreadContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Inject(value = StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES, required = false)
    public void setCustomI18NResources(String bundles) {
        if (bundles != null && bundles.length() > 0) {
            StringTokenizer customBundles = new StringTokenizer(bundles, ", ");

            while (customBundles.hasMoreTokens()) {
                String name = customBundles.nextToken();
                try {
                    LOG.trace("Loading global messages from [{}]", name);
                    addDefaultResourceBundle(name);
                } catch (Exception e) {
                    LOG.error(new ParameterizedMessage("Could not find messages file {}.properties. Skipping", name), e);
                }
            }
        }
    }

    /**
     * Returns a localized message for the specified key, aTextName.  Neither the key nor the
     * message is evaluated.
     *
     * @param aTextName the message key
     * @param locale    the locale the message should be for
     * @return a localized message based on the specified key, or null if no localized message can be found for it
     */
    @Override
    public String findDefaultText(String aTextName, Locale locale) {
        List<String> localList = getCurrentBundleNames();

        for (String bundleName : localList) {
            ResourceBundle bundle = findResourceBundle(bundleName, locale);
            if (bundle != null) {
                reloadBundles();
                try {
                    return bundle.getString(aTextName);
                } catch (MissingResourceException e) {
                    // will be logged when not found in any bundle
                }
            }
        }

        if (devMode) {
            LOG.warn("Missing key [{}] in bundles [{}]!", aTextName, localList);
        } else {
            LOG.debug("Missing key [{}] in bundles [{}]!", aTextName, localList);
        }

        return null;
    }

    /**
     * Returns a localized message for the specified key, aTextName, substituting variables from the
     * array of params into the message.  Neither the key nor the message is evaluated.
     *
     * @param aTextName the message key
     * @param locale    the locale the message should be for
     * @param params    an array of objects to be substituted into the message text
     * @return A formatted message based on the specified key, or null if no localized message can be found for it
     */
    @Override
    public String findDefaultText(String aTextName, Locale locale, Object[] params) {
        String defaultText = findDefaultText(aTextName, locale);
        if (defaultText != null) {
            MessageFormat mf = buildMessageFormat(defaultText, locale);
            return formatWithNullDetection(mf, params);
        }
        return null;
    }


    /**
     * <p>
     * Finds a localized text message for the given key, aTextName, in the specified resource
     * bundle.
     * </p>
     *
     * <p>
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     * </p>
     *
     * <p>
     * If a message is <b>not</b> found a WARN log will be logged.
     * </p>
     *
     * @param bundle         the bundle
     * @param aTextName      the key
     * @param locale         the locale
     * @param defaultMessage the default message to use if no message was found in the bundle
     * @param args           arguments for the message formatter.
     * @param valueStack     the OGNL value stack.
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     */
    @Override
    public String findText(ResourceBundle bundle, String aTextName, Locale locale, String defaultMessage, Object[] args,
                           ValueStack valueStack) {
        try {
            reloadBundles(valueStack.getContext());

            String message = TextParseUtil.translateVariables(bundle.getString(aTextName), valueStack);
            MessageFormat mf = buildMessageFormat(message, locale);

            return formatWithNullDetection(mf, args);
        } catch (MissingResourceException ex) {
            if (devMode) {
                LOG.warn("Missing key [{}] in bundle [{}]!", aTextName, bundle);
            } else {
                LOG.debug("Missing key [{}] in bundle [{}]!", aTextName, bundle);
            }
        }

        GetDefaultMessageReturnArg result = getDefaultMessage(aTextName, locale, valueStack, args, defaultMessage);
        if (unableToFindTextForKey(result)) {
            LOG.warn("Unable to find text for key '{}' in ResourceBundles for locale '{}'", aTextName, locale);
        }
        return result != null ? result.message : null;
    }

    /**
     * @param classLoader a {@link ClassLoader} to look up the bundle from if none can be found on the current thread's classloader
     */
    public void setDelegatedClassLoader(final ClassLoader classLoader) {
        synchronized (bundlesMap) {
            delegatedClassLoaderMap.put(getCurrentThreadContextClassLoader().hashCode(), classLoader);
        }
    }

    /**
     * Clear a specific bundle from the <code>bundlesMap</code>.
     *
     * Warning: This method is <b>now a "no-op"</b>.  It <b>was ineffective</b> due
     *   to the way the <code>bundlesMap</code> is used in combination with locale.
     *   Descendants should use the method {@link #clearBundle(java.lang.String, java.util.Locale)} instead.
     *
     * @param bundleName The bundle to remove from the bundle map
     *
     * @deprecated A "no-op" since 2.6.  Use {@link #clearBundle(java.lang.String, java.util.Locale)} instead.
     */
    public void clearBundle(final String bundleName) {
        LOG.debug("No-op.  Did NOT clear resource bundle [{}], result: false.", bundleName);
    }

    /**
     * Clear a specific bundle + locale combination from the <code>bundlesMap</code>.
     *   Intended for descendants to use clear a bundle + locale combination.
     *
     * @param bundleName The bundle (combined with locale) to remove from the bundle map
     * @param locale     Provides the locale to combine with the bundle to get the key
     *
     * @since 2.6
     */
    protected void clearBundle(final String bundleName, Locale locale) {
        final String key = createMissesKey(String.valueOf(getCurrentThreadContextClassLoader().hashCode()), bundleName, locale);
        final ResourceBundle removedBundle = bundlesMap.remove(key);
        LOG.debug("Clearing resource bundle [{}], locale [{}], result: [{}].", bundleName, locale, Boolean.valueOf(removedBundle != null));
    }

    /**
     * Clears the <code>missingBundles</code> contents.  This allows descendants to
     *   clear the <b>"missing bundles cache"</b> when desired (or needed).
     *
     * Note: This method may be used when the <code>bundlesMap</code> state has changed
     *   in such a way that bundles that were previously "missing" may now be available
     *   (e.g. after calling {@link #addDefaultResourceBundle(java.lang.String)} when the
     *   {@link AbstractLocalizedTextProvider} has already been used for failed bundle
     *   lookups of a given key, or some transitory state made a bundle lookup fail.
     *
     * @since 2.6
     */
    protected void clearMissingBundlesCache() {
        missingBundles.clear();
        LOG.debug("Cleared the missing bundles cache.");
    }

    protected void reloadBundles() {
        reloadBundles(ActionContext.getContext() != null ? ActionContext.getContext().getContextMap() : null);
    }

    protected void reloadBundles(Map<String, Object> context) {
        if (reloadBundles) {
            try {
                Boolean reloaded;
                if (context != null) {
                    reloaded = (Boolean) ObjectUtils.defaultIfNull(context.get(RELOADED), Boolean.FALSE);
                } else {
                    reloaded = Boolean.FALSE;
                }
                if (!reloaded) {
                    bundlesMap.clear();
                    clearResourceBundleClassloaderCaches();

                    // now, for the true and utter hack, if we're running in tomcat, clear
                    // it's class loader resource cache as well.
                    clearTomcatCache();
                    if (context != null) {
                        context.put(RELOADED, true);
                    }
                    LOG.debug("Resource bundles reloaded");
                }
            } catch (Exception e) {
                LOG.error("Could not reload resource bundles", e);
            }
        }
    }

    /**
     * A helper method for {@link ResourceBundle} bundle reload logic.
     *
     * Uses standard {@link ResourceBundle} methods to clear the bundle caches for the
     * {@link ClassLoader} instances that this class is aware of at the time of the call.
     *
     * The <code>clearCache()</code> methods have been available since Java 1.6, so
     * it is anticipated the logic will work on any subsequent JVM versions.
     *
     * @since 2.6
     */
    private void clearResourceBundleClassloaderCaches() {
        final ClassLoader ccl = getCurrentThreadContextClassLoader();
        ResourceBundle.clearCache();     // Bundles loaded by the caller's classloader.
        ResourceBundle.clearCache(ccl);  // Bundles loaded by the context classloader (may be the same).
        // Clear the bundle cache for any non-null delegated classloaders.
        delegatedClassLoaderMap.forEach( (key, value) -> { if (value != null) ResourceBundle.clearCache(value) ;} );
    }

    /**
     * "Hacky" helper method that attempts to clear the Tomcat <code>ResourceEntry</code>
     * {@link Map} using knowledge of the Tomcat source code.
     *
     * It relies on the {@link #TOMCAT_RESOURCE_ENTRIES_FIELD} field name, base class name
     * {@link #TOMCAT_WEBAPP_CLASSLOADER_BASE}. and descendant class names {@link #TOMCAT_WEBAPP_CLASSLOADER},
     * {@link #TOMCAT_PARALLEL_WEBAPP_CLASSLOADER}, to keep the values identified in the constants.
     * It appears to be valid for Tomcat versions 7-10 so far, but could become invalid at any time in the future
     * when the resource handling logic in Tomcat changes.
     *
     * Note: With Java 9+, calling this method may result in "Illegal reflective access" warnings.  Be aware
     *       its logic may fail in a future version of Java that blocks the reflection calls needed for this method.
     */
    private void clearTomcatCache() {
        ClassLoader loader = getCurrentThreadContextClassLoader();
        // no need for compilation here.
        Class cl = loader.getClass();
        Class superCl = cl.getSuperclass();

        try {
            if ((TOMCAT_WEBAPP_CLASSLOADER.equals(cl.getName()) || TOMCAT_PARALLEL_WEBAPP_CLASSLOADER.equals(cl.getName())) &&
                    (superCl != null && TOMCAT_WEBAPP_CLASSLOADER_BASE.equals(superCl.getName()))) {
                // The classloader name and superclass name match the expecations for a Tomcat classloader.
                // Expect the classloader superclass to have the field, otherwise fallback to the classloader class if the field is not found.
                clearMap(superCl, loader, TOMCAT_RESOURCE_ENTRIES_FIELD);
                LOG.debug("Cleared tomcat cache via classloader's parent class.");
            } else {
                LOG.debug("Class loader {} is not tomcat loader.", cl.getName());
            }
        } catch (NoSuchFieldException nsfe) {
            LOG.debug("Parent class {} doesn't contain '{}' field, trying with base!", superCl.getName(), TOMCAT_RESOURCE_ENTRIES_FIELD, nsfe);
            try {
                clearMap(cl, loader, TOMCAT_RESOURCE_ENTRIES_FIELD);
                LOG.debug("Cleared tomcat cache via classloader's class.");
            } catch (Exception e) {
                LOG.warn("Couldn't clear tomcat cache using {}", cl.getName(), e);
            }
        } catch (Exception e) {
            LOG.warn("Couldn't clear tomcat cache using {}", (superCl != null ? superCl.getName() : null), e);
        }
    }

    /**
     * Helper method that is intended to clear a {@link Map} instance by name.
     *
     * This method relies on reflection to perform its operations, and may be blocked in Java 9 and later,
     * depending on the accessibility of the field.
     *
     * @param cl The {@link Class} of the obj parameter.
     * @param obj The {@link Object} from which the named field is to be extracted (may be <code>null</code> for a static field).
     * @param name The name of the field containing a {@link Map} reference.
     * @throws NoSuchFieldException if a field accessed by this call does not exist.
     * @throws IllegalAccessException if a field, method or or class accessed by this call cannot be accessed.
     * @throws NoSuchMethodException if a method accessed by this call does not exist.
     * @throws InvocationTargetException if a method accessed by this call fails invocation.
     */
    private void clearMap(Class cl, Object obj, String name)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Field field = cl.getDeclaredField(name);

        if (!field.isAccessible()) {
            field.setAccessible(true);  // Change state only if necessary.
        }

        Object cache = field.get(obj);

        synchronized (cache) {
            Class ccl = cache.getClass();
            Method clearMethod = ccl.getMethod("clear");
            clearMethod.invoke(cache);
        }
    }

    protected MessageFormat buildMessageFormat(String pattern, Locale locale) {
        MessageFormatKey key = new MessageFormatKey(pattern, locale);
        MessageFormat format = messageFormats.get(key);
        if (format == null) {
            format = new MessageFormat(pattern);
            format.setLocale(locale);
            format.applyPattern(pattern);
            messageFormats.put(key, format);
        }

        return format;
    }

    protected String formatWithNullDetection(MessageFormat mf, Object[] args) {
        String message = mf.format(args);
        if ("null".equals(message)) {
            return null;
        } else {
            return message;
        }
    }

    @Inject(value = StrutsConstants.STRUTS_I18N_RELOAD, required = false)
    public void setReloadBundles(String reloadBundles) {
        this.reloadBundles = Boolean.parseBoolean(reloadBundles);
    }

    @Inject(value = StrutsConstants.STRUTS_DEVMODE, required = false)
    public void setDevMode(String devMode) {
        this.devMode = Boolean.parseBoolean(devMode);
    }

    /**
     * Set the {@link #searchDefaultBundlesFirst} flag state.  This flag may be used by descendant TextProvider
     * implementations to determine if default bundles should be searched for messages first (before the standard
     * flow of the {@link LocalizedTextProvider} implementation the descendant provides).
     *
     * @param searchDefaultBundlesFirst provide {@link String} "true" or "false" to set the flag state accordingly.
     *
     * @since 2.6
     */
    @Inject(value = StrutsConstants.STRUTS_I18N_SEARCH_DEFAULTBUNDLES_FIRST, required = false)
    public void setSearchDefaultBundlesFirst(String searchDefaultBundlesFirst) {
        this.searchDefaultBundlesFirst = Boolean.parseBoolean(searchDefaultBundlesFirst);
    }

    /**
     * Finds the given resource bundle by it's name.
     * <p>
     * Will use <code>Thread.currentThread().getContextClassLoader()</code> as the classloader.
     * </p>
     *
     * @param aBundleName the name of the bundle (usually it's FQN classname).
     * @param locale      the locale.
     * @return the bundle, <tt>null</tt> if not found.
     */
    @Override
    public ResourceBundle findResourceBundle(String aBundleName, Locale locale) {
        ClassLoader classLoader = getCurrentThreadContextClassLoader();
        String key = createMissesKey(String.valueOf(classLoader.hashCode()), aBundleName, locale);

        if (missingBundles.contains(key)) {
            return null;
        }

        ResourceBundle bundle = null;
        try {
            if (bundlesMap.containsKey(key)) {
                bundle = bundlesMap.get(key);
            } else {
                bundle = ResourceBundle.getBundle(aBundleName, locale, classLoader);
                bundlesMap.putIfAbsent(key, bundle);
            }
        } catch (MissingResourceException ex) {
            if (delegatedClassLoaderMap.containsKey(classLoader.hashCode())) {
                try {
                    if (bundlesMap.containsKey(key)) {
                        bundle = bundlesMap.get(key);
                    } else {
                        bundle = ResourceBundle.getBundle(aBundleName, locale, delegatedClassLoaderMap.get(classLoader.hashCode()));
                        bundlesMap.putIfAbsent(key, bundle);
                    }
                } catch (MissingResourceException e) {
                    LOG.debug("Missing resource bundle [{}]!", aBundleName, e);
                    missingBundles.add(key);
                }
            } else {
                LOG.debug("Missing resource bundle [{}]!", aBundleName);
                missingBundles.add(key);
            }
        }
        return bundle;
    }

    /**
     * Clears all the internal lists.
     *
     * @deprecated used only in tests
     */
    @Deprecated
    public void reset() {
        // no-op
    }

    /**
     * Determines if we found the text in the bundles.
     *
     * @param result the result so far
     * @return <tt>true</tt> if we could <b>not</b> find the text, <tt>false</tt> if the text was found (=success).
     */
    protected boolean unableToFindTextForKey(GetDefaultMessageReturnArg result) {
        if (result == null || result.message == null) {
            return true;
        }

        // did we find it in the bundle, then no problem?
        if (result.foundInBundle) {
            return false;
        }

        // not found in bundle
        return true;
    }

    /**
     * Creates a key to used for lookup/storing in the bundle misses cache.
     *
     * @param prefix      the prefix for the returning String - it is supposed to be the ClassLoader hash code.
     * @param aBundleName the name of the bundle (usually it's FQN classname).
     * @param locale      the locale.
     * @return the key to use for lookup/storing in the bundle misses cache.
     */
    private String createMissesKey(String prefix, String aBundleName, Locale locale) {
        return prefix + aBundleName + "_" + locale.toString();
    }

    /**
     * @return the default message.
     */
    protected GetDefaultMessageReturnArg getDefaultMessage(String key, Locale locale, ValueStack valueStack, Object[] args,
                                                                String defaultMessage) {
        GetDefaultMessageReturnArg result = null;
        boolean found = true;

        if (key != null) {
            String message = findDefaultText(key, locale);

            if (message == null) {
                message = defaultMessage;
                found = false; // not found in bundles
            }

            // defaultMessage may be null
            if (message != null) {
                MessageFormat mf = buildMessageFormat(TextParseUtil.translateVariables(message, valueStack), locale);

                String msg = formatWithNullDetection(mf, args);
                result = new GetDefaultMessageReturnArg(msg, found);
            }
        }

        return result;
    }

    /**
     * A helper method that can be used by descendant classes to perform some common two-stage message lookup operations
     * against the default resource bundles.  The default resource bundles are searched for a value using key first, then
     * alternateKey when the first search fails, then utilizing defaultMessage (which may be <code>null</code>) if <em>both</em>
     * key lookup operations fail.
     *
     * <p>
     * A known use case is when a key indexes a collection (e.g. user.phone[0]) for which some specific keys may exist, but not all,
     * along with a general key (e.g. user.phone[*]).  In such cases the specific key would be passed in the key parameter and the
     * general key would be passed in the alternateKey parameter.
     * </p>
     *
     * @param key             the initial key to search for a value within the default resource bundles.
     * @param alternateKey    the alternate (fall-back) key to search for a value within the default resource bundles, if the initial key lookup fails.
     * @param locale          the {@link Locale} to be used for the default resource bundle lookup.
     * @param valueStack      the {@link ValueStack} associated with the operation.
     * @param args            the argument array for parameterized messages (may be <code>null</code>).
     * @param defaultMessage  the default message {@link String} to use if both key lookup operations fail.
     * @return the {@link GetDefaultMessageReturnArg} result containing the processed message lookup (by key first, then alternateKey if key's lookup fails).
     *         If both key lookup operations fail, defaultMessage is used for processing.
     *         If defaultMessage is <code>null</code> then the return result may be <code>null</code>.
     */
    protected GetDefaultMessageReturnArg getDefaultMessageWithAlternateKey(String key, String alternateKey, Locale locale, ValueStack valueStack,
            Object[] args, String defaultMessage) {
        GetDefaultMessageReturnArg result;
        if (alternateKey == null || alternateKey.isEmpty()) {
            result = getDefaultMessage(key, locale, valueStack, args, defaultMessage);
        } else {
            result = getDefaultMessage(key, locale, valueStack, args, null);
            if (result == null || result.message == null) {
                result = getDefaultMessage(alternateKey, locale, valueStack, args, defaultMessage);
            }
        }
        return result;
    }

    /**
     * @return the message from the named resource bundle.
     */
    protected String getMessage(String bundleName, Locale locale, String key, ValueStack valueStack, Object[] args) {
        ResourceBundle bundle = findResourceBundle(bundleName, locale);
        if (bundle == null) {
            return null;
        }
        if (valueStack != null) {
            reloadBundles(valueStack.getContext());
        }
        try {
            String message = bundle.getString(key);
            if (valueStack != null) {
                message = TextParseUtil.translateVariables(bundle.getString(key), valueStack);
            }
            MessageFormat mf = buildMessageFormat(message, locale);
            return formatWithNullDetection(mf, args);
        } catch (MissingResourceException e) {
            LOG.debug("Missing key [{}] in bundle [{}]!", key, bundleName);
            return null;
        }
    }

    /**
     * Traverse up class hierarchy looking for message.  Looks at class, then implemented interface,
     * before going up hierarchy.
     *
     * @return the message
     */
    protected String findMessage(Class clazz, String key, String indexedKey, Locale locale, Object[] args, Set<String> checked,
                                      ValueStack valueStack) {
        if (checked == null) {
            checked = new TreeSet<>();
        } else if (checked.contains(clazz.getName())) {
            return null;
        }

        // look in properties of this class
        String msg = getMessage(clazz.getName(), locale, key, valueStack, args);

        if (msg != null) {
            return msg;
        }

        if (indexedKey != null) {
            msg = getMessage(clazz.getName(), locale, indexedKey, valueStack, args);

            if (msg != null) {
                return msg;
            }
        }

        // look in properties of implemented interfaces
        Class[] interfaces = clazz.getInterfaces();

        for (Class anInterface : interfaces) {
            msg = getMessage(anInterface.getName(), locale, key, valueStack, args);

            if (msg != null) {
                return msg;
            }

            if (indexedKey != null) {
                msg = getMessage(anInterface.getName(), locale, indexedKey, valueStack, args);

                if (msg != null) {
                    return msg;
                }
            }
        }

        // traverse up hierarchy
        if (clazz.isInterface()) {
            interfaces = clazz.getInterfaces();

            for (Class anInterface : interfaces) {
                msg = findMessage(anInterface, key, indexedKey, locale, args, checked, valueStack);

                if (msg != null) {
                    return msg;
                }
            }
        } else {
            if (!clazz.equals(Object.class) && !clazz.isPrimitive()) {
                return findMessage(clazz.getSuperclass(), key, indexedKey, locale, args, checked, valueStack);
            }
        }

        return null;
    }

    static class MessageFormatKey {
        String pattern;
        Locale locale;

        MessageFormatKey(String pattern, Locale locale) {
            this.pattern = pattern;
            this.locale = locale;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MessageFormatKey that = (MessageFormatKey) o;

            if (pattern != null ? !pattern.equals(that.pattern) : that.pattern != null) return false;
            return locale != null ? locale.equals(that.locale) : that.locale == null;
        }

        @Override
        public int hashCode() {
            int result = pattern != null ? pattern.hashCode() : 0;
            result = 31 * result + (locale != null ? locale.hashCode() : 0);
            return result;
        }
    }

    static class GetDefaultMessageReturnArg {
        String message;
        boolean foundInBundle;

        public GetDefaultMessageReturnArg(String message, boolean foundInBundle) {
            this.message = message;
            this.foundInBundle = foundInBundle;
        }
    }

}
