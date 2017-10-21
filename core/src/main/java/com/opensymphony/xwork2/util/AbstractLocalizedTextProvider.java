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
import java.util.Collections;
import java.util.HashSet;
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
    private final String RELOADED = "com.opensymphony.xwork2.util.LocalizedTextProvider.reloaded";

    protected final ConcurrentMap<String, ResourceBundle> bundlesMap = new ConcurrentHashMap<>();
    protected boolean devMode = false;
    protected boolean reloadBundles = false;

    private final ConcurrentMap<MessageFormatKey, MessageFormat> messageFormats = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, List<String>> classLoaderMap = new ConcurrentHashMap<>();
    private final Set<String> missingBundles = Collections.synchronizedSet(new HashSet<String>());
    private final ConcurrentMap<Integer, ClassLoader> delegatedClassLoaderMap = new ConcurrentHashMap<>();

    /**
     * Add's the bundle to the internal list of default bundles.
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
     * @param bundleName Removes the bundle from any cached "misses"
     */
    public void clearBundle(final String bundleName) {
        bundlesMap.remove(getCurrentThreadContextClassLoader().hashCode() + bundleName);
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
                    try {
                        clearMap(ResourceBundle.class, null, "cacheList");
                    } catch (NoSuchFieldException e) {
                        // happens in IBM JVM, that has a different ResourceBundle impl
                        // it has a 'cache' member
                        clearMap(ResourceBundle.class, null, "cache");
                    }

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

    private void clearTomcatCache() {
        ClassLoader loader = getCurrentThreadContextClassLoader();
        // no need for compilation here.
        Class cl = loader.getClass();

        try {
            if ("org.apache.catalina.loader.WebappClassLoader".equals(cl.getName())) {
                clearMap(cl, loader, TOMCAT_RESOURCE_ENTRIES_FIELD);
            } else {
                LOG.debug("Class loader {} is not tomcat loader.", cl.getName());
            }
        } catch (NoSuchFieldException nsfe) {
            if ("org.apache.catalina.loader.WebappClassLoaderBase".equals(cl.getSuperclass().getName())) {
                LOG.debug("Base class {} doesn't contain '{}' field, trying with parent!", cl.getName(), TOMCAT_RESOURCE_ENTRIES_FIELD, nsfe);
                try {
                    clearMap(cl.getSuperclass(), loader, TOMCAT_RESOURCE_ENTRIES_FIELD);
                } catch (Exception e) {
                    LOG.warn("Couldn't clear tomcat cache using {}", cl.getSuperclass().getName(), e);
                }
            }
        } catch (Exception e) {
            LOG.warn("Couldn't clear tomcat cache", cl.getName(), e);
        }
    }

    private void clearMap(Class cl, Object obj, String name)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Field field = cl.getDeclaredField(name);
        field.setAccessible(true);

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
     * @return the message from the named resource bundle.
     */
    protected String getMessage(String bundleName, Locale locale, String key, ValueStack valueStack, Object[] args) {
        ResourceBundle bundle = findResourceBundle(bundleName, locale);
        if (bundle == null) {
            return null;
        }
        if (valueStack != null)
            reloadBundles(valueStack.getContext());
        try {
        	String message = bundle.getString(key);
        	if (valueStack != null)
        		message = TextParseUtil.translateVariables(bundle.getString(key), valueStack);
            MessageFormat mf = buildMessageFormat(message, locale);
            return formatWithNullDetection(mf, args);
        } catch (MissingResourceException e) {
            if (devMode) {
                LOG.warn("Missing key [{}] in bundle [{}]!", key, bundleName);
            } else {
                LOG.debug("Missing key [{}] in bundle [{}]!", key, bundleName);
            }
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
