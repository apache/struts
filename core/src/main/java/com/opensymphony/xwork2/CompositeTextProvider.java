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

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * This is a composite {@link TextProvider} that takes in an array or {@link java.util.List} of {@link TextProvider}s, it will
 * consult each of them in order to get a composite result. To know how each method behaves, please refer to the
 * javadoc for each methods.
 *
 * @author tmjee
 */
public class CompositeTextProvider implements TextProvider {

    private static final Logger LOG = LogManager.getLogger(CompositeTextProvider.class);

    private List<TextProvider> textProviders = new ArrayList<>();

    /**
     * Instantiates a {@link CompositeTextProvider} with some predefined <code>textProviders</code>.
     *
     * @param textProviders list of text providers
     */
    public CompositeTextProvider(List<TextProvider> textProviders) {
        this.textProviders.addAll(textProviders);
    }

    /**
     * Instantiates a {@link CompositeTextProvider} with some predefined <code>textProviders</code>.
     *
     * @param textProviders array of text providers
     */
    public CompositeTextProvider(TextProvider[] textProviders) {
        this(Arrays.asList(textProviders));
    }

    /**
     *  It will consult each individual {@link TextProvider}s and return true if either one of the {@link TextProvider}" has such a <code>key</code> else false.
     *
     * @param key The key to lookup in resource bundles.
     * @return <tt>true</tt>, if the requested key is found in one of the resource bundles.
     *
     * @see com.opensymphony.xwork2.TextProvider#hasKey(String)
     *
     */
    public boolean hasKey(String key) {
        // if there's a key in either text providers we are ok, else try the next text provider
        for (TextProvider tp : textProviders) {
            if (tp.hasKey(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>
     *
     * @param key The key to lookup in resource bundles.
     * @return The i18n text for the requested key.
     * @see com.opensymphony.xwork2.TextProvider#getText(String)
     */
    public String getText(String key) {
        return getText(key, key, Collections.emptyList());
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code> before returning <code>defaultValue</code> if every else fails.
     *
     * @param key the message key
     * @param defaultValue the default value
     * @return the first valid message for the key or default value
     * @see com.opensymphony.xwork2.TextProvider#getText(String, String)
     */
    public String getText(String key, String defaultValue) {
        return getText(key, defaultValue, Collections.emptyList());
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returning <code>defaultValue</code>
     * if every else fails.
     *
     * @param key the message key
     * @param defaultValue the default value
     * @param obj object
     * @return the first valid message for the key or default value
     * @see com.opensymphony.xwork2.TextProvider#getText(String, String, String)
     */
    public String getText(String key, String defaultValue, final String obj) {
        return getText(key, defaultValue, new ArrayList<Object>() {
            {
                add(obj);
            }
        });
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>.
     *
     * @param key the message key
     * @param args additional arguments
     * @return the first valid message for the key
     * @see com.opensymphony.xwork2.TextProvider#getText(String, java.util.List)
     */
    public String getText(String key, List<?> args) {
        return getText(key, key, args);
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>.
     *
     * @param key the message key
     * @param args additional arguments
     * @return the first valid message for the key or default value
     * @see com.opensymphony.xwork2.TextProvider#getText(String, String[])
     */
    public String getText(String key, String[] args) {
        return getText(key, key, args);
    }


    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returning <code>defaultValue</code>
     *
     * @param key the message key
     * @param defaultValue the default value
     * @param args additional arguments
     * @return the first valid message for the key or default value
     * @see com.opensymphony.xwork2.TextProvider#getText(String, String, java.util.List)
     */
    public String getText(String key, String defaultValue, List<?> args) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returning <code>defaultValue</code>.
     *
     * @param key the message key
     * @param defaultValue the default value
     * @param args additional arguments
     * @return the first valid message for the key or default value
     * @see com.opensymphony.xwork2.TextProvider#getText(String, String, String[])
     */
    public String getText(String key, String defaultValue, String[] args) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returning <code>defaultValue</code>
     *
     * @param key the message key
     * @param defaultValue the default value
     * @param args additional arguments
     * @param stack the value stack
     * @return the first valid message for the key or default value
     * @see com.opensymphony.xwork2.TextProvider#getText(String, String, java.util.List, com.opensymphony.xwork2.util.ValueStack)
     */
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args, stack);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returning <code>defaultValue</code>
     *
     * @param key the message key
     * @param defaultValue the default value
     * @param args additional arguments
     * @param stack the value stack
     * @return the first valid message for the key or default value
     * @see com.opensymphony.xwork2.TextProvider#getText(String, String, String[], com.opensymphony.xwork2.util.ValueStack)
     */
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args, stack);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    /**
     * It will consult each {@link TextProvider}s and return the first non-null {@link ResourceBundle}.
     *
     * @param bundleName the bundle name
     * @return the resource bundle found for bundle name
     * @see TextProvider#getTexts(String)
     */
    public ResourceBundle getTexts(String bundleName) {
        // if there's one text provider that gives us a non-null resource bundle for this bundleName, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            ResourceBundle bundle = textProvider.getTexts(bundleName);
            if (bundle != null) {
                return bundle;
            }
        }
        return null;
    }

    /**
     * It will consult each {@link com.opensymphony.xwork2.TextProvider}s and return the first non-null {@link ResourceBundle}.
     *
     * @return the resource bundle
     * @see TextProvider#getTexts()
     */
    public ResourceBundle getTexts() {
        // if there's one text provider that gives us a non-null resource bundle, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            ResourceBundle bundle = textProvider.getTexts();
            if (bundle != null) {
                return bundle;
            }
        }
        return null;
    }
}


