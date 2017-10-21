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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.*;

/**
 * Default TextProvider implementation.
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 */
public class TextProviderSupport implements ResourceBundleTextProvider {

    protected Class clazz;
    protected LocaleProvider localeProvider;
    protected ResourceBundle bundle;
    protected LocalizedTextProvider localizedTextProvider;

    /**
     * Constructor.
     *
     * @param clazz    a clazz to use for reading the resource bundle.
     * @param provider a locale provider.
     */
    public TextProviderSupport(Class clazz, LocaleProvider provider, LocalizedTextProvider localizedTextProvider) {
        this.clazz = clazz;
        this.localeProvider = provider;
        this.localizedTextProvider = localizedTextProvider;
    }

    /**
     * Constructor.
     *
     * @param bundle   the resource bundle.
     * @param provider a locale provider.
     */
    public TextProviderSupport(ResourceBundle bundle, LocaleProvider provider, LocalizedTextProvider localizedTextProvider) {
        this.bundle = bundle;
        this.localeProvider = provider;
        this.localizedTextProvider = localizedTextProvider;
    }

    /**
     * @param bundle the resource bundle.
     */
    @Override
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    /**
     * @param clazz a clazz to use for reading the resource bundle.
     */
    @Override
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * @param localeProvider a locale provider.
     */
    @Override
    public void setLocaleProvider(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        this.localeProvider = localeProviderFactory.createLocaleProvider();
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    /**
     * Checks if a key is available in the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class.
     */
    public boolean hasKey(String key) {
    	String message;
    	if (clazz != null) {
            message = localizedTextProvider.findText(clazz, key, getLocale(), null, new Object[0] );
        } else {
            message = localizedTextProvider.findText(bundle, key, getLocale(), null, new Object[0]);
        }
    	return message != null;
    }

    /**
     * Get a text from the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class.
     *
     * @param key name of text to be found
     * @return value of named text or the provided key if no value is found
     */
    public String getText(String key) {
        return getText(key, key, Collections.emptyList());
    }

    /**
     * Get a text from the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class. If no text is found for this text name, the default value is returned.
     *
     * @param key    name of text to be found
     * @param defaultValue the default value which will be returned if no text is found
     * @return value of named text or the provided defaultValue if no value is found
     */
    public String getText(String key, String defaultValue) {
        return getText(key, defaultValue, Collections.emptyList());
    }

    /**
     * Get a text from the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class. If no text is found for this text name, the default value is returned.
     *
     * @param key    name of text to be found
     * @param defaultValue the default value which will be returned if no text is found
     * @return value of named text or the provided defaultValue if no value is found
     */
    public String getText(String key, String defaultValue, String arg) {
        List<Object> args = new ArrayList<>();
        args.add(arg);
        return getText(key, defaultValue, args);
    }

    /**
     * Get a text from the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class. If no text is found for this text name, the default value is returned.
     *
     * @param key name of text to be found
     * @param args      a List of args to be used in a MessageFormat message
     * @return value of named text or the provided key if no value is found
     */
    public String getText(String key, List<?> args) {
        return getText(key, key, args);
    }

    /**
     * Get a text from the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class. If no text is found for this text name, the default value is returned.
     *
     * @param key name of text to be found
     * @param args      an array of args to be used in a MessageFormat message
     * @return value of named text or the provided key if no value is found
     */
    public String getText(String key, String[] args) {
        return getText(key, key, args);
    }

    /**
     * Get a text from the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class. If no text is found for this text name, the default value is returned.
     *
     * @param key    name of text to be found
     * @param defaultValue the default value which will be returned if no text is found
     * @param args         a List of args to be used in a MessageFormat message
     * @return value of named text or the provided defaultValue if no value is found
     */
    public String getText(String key, String defaultValue, List<?> args) {
        Object[] argsArray = ((args != null && !args.equals(Collections.emptyList())) ? args.toArray() : null);
        if (clazz != null) {
            return localizedTextProvider.findText(clazz, key, getLocale(), defaultValue, argsArray);
        } else {
            return localizedTextProvider.findText(bundle, key, getLocale(), defaultValue, argsArray);
        }
    }

    /**
     * Get a text from the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class. If no text is found for this text name, the default value is returned.
     *
     * @param key          name of text to be found
     * @param defaultValue the default value which will be returned if no text is found
     * @param args         an array of args to be used in a MessageFormat message
     * @return value of named text or the provided defaultValue if no value is found
     */
    public String getText(String key, String defaultValue, String[] args) {
        if (clazz != null) {
            return localizedTextProvider.findText(clazz, key, getLocale(), defaultValue, args);
        } else {
            return localizedTextProvider.findText(bundle, key, getLocale(), defaultValue, args);
        }
    }

    /**
     * Gets a message based on a key using the supplied args, as defined in
     * {@link java.text.MessageFormat}, or, if the message is not found, a supplied
     * default value is returned. Instead of using the value stack in the ActionContext
     * this version of the getText() method uses the provided value stack.
     *
     * @param key    the resource bundle key that is to be searched for
     * @param defaultValue the default value which will be returned if no message is found
     * @param args         a list args to be used in a {@link java.text.MessageFormat} message
     * @param stack        the value stack to use for finding the text
     * @return the message as found in the resource bundle, or defaultValue if none is found
     */
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        Object[] argsArray = ((args != null) ? args.toArray() : null);
        Locale locale;
        if (stack == null){
        	locale = getLocale();
        }else{
        	locale = (Locale) stack.getContext().get(ActionContext.LOCALE);
        }
        if (locale == null) {
            locale = getLocale();
        }
        if (clazz != null) {
            return localizedTextProvider.findText(clazz, key, locale, defaultValue, argsArray, stack);
        } else {
            return localizedTextProvider.findText(bundle, key, locale, defaultValue, argsArray, stack);
        }
    }


    /**
     * Gets a message based on a key using the supplied args, as defined in
     * {@link java.text.MessageFormat}, or, if the message is not found, a supplied
     * default value is returned. Instead of using the value stack in the ActionContext
     * this version of the getText() method uses the provided value stack.
     *
     * @param key          the resource bundle key that is to be searched for
     * @param defaultValue the default value which will be returned if no message is found
     * @param args         an array args to be used in a {@link java.text.MessageFormat} message
     * @param stack        the value stack to use for finding the text
     * @return the message as found in the resource bundle, or defaultValue if none is found
     */
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        Locale locale;
        if (stack == null){
        	locale = getLocale();
        }else{
        	locale = (Locale) stack.getContext().get(ActionContext.LOCALE);
        }
        if (locale == null) {
            locale = getLocale();
        }
        if (clazz != null) {
            return localizedTextProvider.findText(clazz, key, locale, defaultValue, args, stack);
        } else {
            return localizedTextProvider.findText(bundle, key, locale, defaultValue, args, stack);
        }

    }

    /**
     * Get the named bundle.
     *
     * <p>
     * You can override the getLocale() methodName to change the behaviour of how
     * to choose locale for the bundles that are returned. Typically you would
     * use the TextProvider interface to get the users configured locale, or use
     * your own methodName to allow the user to select the locale and store it in
     * the session (by using the SessionAware interface).
     * </p>
     *
     * @param aBundleName bundle name
     * @return a resource bundle
     */
    public ResourceBundle getTexts(String aBundleName) {
        return localizedTextProvider.findResourceBundle(aBundleName, getLocale());
    }

    /**
     * Get the resource bundle associated with this action.
     * This will be based on the actual subclass that is used.
     *
     * @return resouce bundle
     */
    public ResourceBundle getTexts() {
        if (clazz != null) {
            return getTexts(clazz.getName());
        }
        return bundle;
    }

    /**
     * Get's the locale from the localeProvider.
     *
     * @return the locale from the localeProvider.
     */
    private Locale getLocale() {
        return localeProvider.getLocale();
    }
}
