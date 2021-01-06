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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides support for localization in the framework, it can be used to read only default bundles.
 * 
 * Note that unlike {@link StrutsLocalizedTextProvider}, this class {@link GlobalLocalizedTextProvider} will
 * <em>only</em> search the default bundles for localized text.
 */
public class GlobalLocalizedTextProvider extends AbstractLocalizedTextProvider {

    private static final Logger LOG = LogManager.getLogger(GlobalLocalizedTextProvider.class);

    public GlobalLocalizedTextProvider() {
        addDefaultResourceBundle(XWORK_MESSAGES_BUNDLE);
        addDefaultResourceBundle(STRUTS_MESSAGES_BUNDLE);
    }

    /**
     * Calls {@link #findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args)}
     * with aTextName as the default message.
     *
     * @param aClass    class name
     * @param aTextName text name
     * @param locale    the locale
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     * @see #findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args)
     */
    @Override
    public String findText(Class aClass, String aTextName, Locale locale) {
        return findText(aClass, aTextName, locale, aTextName, new Object[0]);
    }

    /**
     * <p>
     * Finds a localized text message for the given key, aTextName. Both the key and the message
     * itself is evaluated as required.  The following algorithm is used to find the requested
     * message:
     * </p>
     *
     * <ol>
     * <li>Look for the message in the default resource bundles.</li>
     * <li>If not found, return defaultMessage</li>
     * </ol>
     *
     * <p>
     * When looking for the message, if the key indexes a collection (e.g. user.phone[0]) and a
     * message for that specific key cannot be found, the general form will also be looked up
     * (i.e. user.phone[*]).
     * </p>
     *
     * <p>
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     * </p>
     *
     * @param aClass         the class whose name to use as the start point for the search
     * @param aTextName      the key to find the text message for
     * @param locale         the locale the message should be for
     * @param defaultMessage the message to be returned if no text message can be found in any
     *                       resource bundle
     * @param args           arguments
     *                       resource bundle
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     */
    @Override
    public String findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args) {
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        return findText(aClass, aTextName, locale, defaultMessage, args, valueStack);

    }

    /**
     * <p>
     * Finds a localized text message for the given key, aTextName. Both the key and the message
     * itself is evaluated as required.  The following algorithm is used to find the requested
     * message:
     * </p>
     *
     * <ol>
     * <li>Look for the message in the default resource bundles.</li>
     * <li>If not found, return defaultMessage</li>
     * </ol>
     *
     * <p>
     * When looking for the message, if the key indexes a collection (e.g. user.phone[0]) and a
     * message for that specific key cannot be found, the general form will also be looked up
     * (i.e. user.phone[*]).
     * </p>
     *
     * <p>
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     * </p>
     *
     * <p>
     * If a message is <b>not</b> found a DEBUG level log warning will be logged.
     * </p>
     *
     * @param aClass         the class whose name to use as the start point for the search
     * @param aTextName      the key to find the text message for
     * @param locale         the locale the message should be for
     * @param defaultMessage the message to be returned if no text message can be found in any
     *                       resource bundle
     * @param args           arguments
     * @param valueStack     the value stack to use to evaluate expressions instead of the
     *                       one in the ActionContext ThreadLocal
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     */
    @Override
    public String findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args, ValueStack valueStack) {
        String indexedTextName = null;
        if (aTextName == null) {
            LOG.warn("Trying to find text with null key!");
            aTextName = "";
        }
        // calculate indexedTextName (collection[*]) if applicable
        if (aTextName.contains("[")) {
            int i = -1;

            indexedTextName = aTextName;

            while ((i = indexedTextName.indexOf('[', i + 1)) != -1) {
                int j = indexedTextName.indexOf(']', i);
                String a = indexedTextName.substring(0, i);
                String b = indexedTextName.substring(j);
                indexedTextName = a + "[*" + b;
            }
        }

        // get default
        GetDefaultMessageReturnArg result = getDefaultMessageWithAlternateKey(aTextName, indexedTextName, locale, valueStack, args, defaultMessage);

        // could we find the text, if not log a warn
        if (unableToFindTextForKey(result) && LOG.isDebugEnabled()) {
            String warn = "Unable to find text for key '" + aTextName + "' ";
            if (indexedTextName != null) {
                warn += " or indexed key '" + indexedTextName + "' ";
            }
            warn += "in class '" + aClass.getName() + "' and locale '" + locale + "'";
            LOG.debug(warn);
        }

        return result != null ? result.message : null;
    }

    /**
     * <p>
     * Finds a localized text message for the given key, aTextName, in the specified resource bundle
     * with aTextName as the default message.
     * </p>
     *
     * <p>
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     * </p>
     *
     * @param bundle    a resource bundle name
     * @param aTextName text name
     * @param locale    the locale
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     * @see #findText(ResourceBundle, String, Locale, String, Object[])
     */
    @Override
    public String findText(ResourceBundle bundle, String aTextName, Locale locale) {
        return findText(bundle, aTextName, locale, aTextName, new Object[0]);
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
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     */
    @Override
    public String findText(ResourceBundle bundle, String aTextName, Locale locale, String defaultMessage, Object[] args) {
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        return findText(bundle, aTextName, locale, defaultMessage, args, valueStack);
    }

}
