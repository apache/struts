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
package org.apache.struts2.text;

import org.apache.struts2.util.ValueStack;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

public interface LocalizedTextProvider extends Serializable {

    /**
     * Returns a localized message for the specified key, aTextName.  Neither the key nor the
     * message is evaluated.
     *
     * @param textKey the message key
     * @param locale  the locale the message should be for
     * @return a localized message based on the specified key, or null if no localized message can be found for it
     */
    String findDefaultText(String textKey, Locale locale);

    /**
     * Returns a localized message for the specified key, aTextName, substituting variables from the
     * array of params into the message.  Neither the key nor the message is evaluated.
     *
     * @param textKey the message key
     * @param locale  the locale the message should be for
     * @param params  an array of objects to be substituted into the message text
     * @return A formatted message based on the specified key, or null if no localized message can be found for it
     */
    String findDefaultText(String textKey, Locale locale, Object[] params);

    /**
     * Finds the given resource bundle by it's name.
     * <p>
     * Will use <code>Thread.currentThread().getContextClassLoader()</code> as the classloader.
     * </p>
     *
     * @param bundleName the name of the bundle (usually it's FQN classname).
     * @param locale     the locale.
     * @return the bundle, <tt>null</tt> if not found.
     */
    ResourceBundle findResourceBundle(String bundleName, Locale locale);

    /**
     * Calls {@link #findText(Class startClazz, String textKey, Locale locale, String defaultMessage, Object[] args)}
     * with textKey as the default message.
     *
     * @param startClazz class name
     * @param textKey    text name
     * @param locale     the locale
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     * @see #findText(Class startClazz, String textKey, Locale locale, String defaultMessage, Object[] args)
     */
    String findText(Class<?> startClazz, String textKey, Locale locale);

    /**
     * Finds a localized text message for the given key, textKey. Both the key and the message
     * itself is evaluated as required.  The following algorithm is used to find the requested
     * message:
     *
     * <ol>
     * <li>Look for the message in the default resource bundles.</li>
     * <li>If not found, return defaultMessage</li>
     * </ol>
     * <p>
     * When looking for the message, if the key indexes a collection (e.g. user.phone[0]) and a
     * message for that specific key cannot be found, the general form will also be looked up
     * (i.e. user.phone[*]).
     * <p>
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     *
     * @param startClazz     the class whose name to use as the start point for the search
     * @param textKey        the key to find the text message for
     * @param locale         the locale the message should be for
     * @param defaultMessage the message to be returned if no text message can be found in any
     *                       resource bundle
     * @param args           arguments
     *                       resource bundle
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     */
    String findText(Class<?> startClazz, String textKey, Locale locale, String defaultMessage, Object[] args);

    /**
     * Finds a localized text message for the given key, textKey. Both the key and the message
     * itself is evaluated as required.  The following algorithm is used to find the requested
     * message:
     *
     * <ol>
     * <li>Look for the message in the default resource bundles.</li>
     * <li>If not found, return defaultMessage</li>
     * </ol>
     * <p>
     * When looking for the message, if the key indexes a collection (e.g. user.phone[0]) and a
     * message for that specific key cannot be found, the general form will also be looked up
     * (i.e. user.phone[*]).
     * <p>
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     * <p>
     * If a message is <b>not</b> found a DEBUG level log warning will be logged.
     *
     * @param startClazz     the class whose name to use as the start point for the search
     * @param textKey        the key to find the text message for
     * @param locale         the locale the message should be for
     * @param defaultMessage the message to be returned if no text message can be found in any
     *                       resource bundle
     * @param args           arguments
     * @param valueStack     the value stack to use to evaluate expressions instead of the
     *                       one in the ActionContext ThreadLocal
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     */
    String findText(Class<?> startClazz, String textKey, Locale locale, String defaultMessage, Object[] args, ValueStack valueStack);

    /**
     * Finds a localized text message for the given key, aTextName, in the specified resource bundle
     * with aTextName as the default message.
     * <p>
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     *
     * @param bundle  a resource bundle name
     * @param textKey text name
     * @param locale  the locale
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     * @see #findText(ResourceBundle, String, Locale, String, Object[])
     */
    String findText(ResourceBundle bundle, String textKey, Locale locale);

    /**
     * Finds a localized text message for the given key, aTextName, in the specified resource
     * bundle.
     * <p>
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     * <p>
     * If a message is <b>not</b> found a WARN log will be logged.
     *
     * @param bundle         the bundle
     * @param textKey        the key
     * @param locale         the locale
     * @param defaultMessage the default message to use if no message was found in the bundle
     * @param args           arguments for the message formatter.
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     */
    String findText(ResourceBundle bundle, String textKey, Locale locale, String defaultMessage, Object[] args);

    /**
     * Finds a localized text message for the given key, aTextName, in the specified resource
     * bundle.
     * <p>
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     * <p>
     * If a message is <b>not</b> found a WARN log will be logged.
     *
     * @param bundle         the bundle
     * @param textKey        the key
     * @param locale         the locale
     * @param defaultMessage the default message to use if no message was found in the bundle
     * @param args           arguments for the message formatter.
     * @param valueStack     the OGNL value stack.
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     */
    String findText(ResourceBundle bundle, String textKey, Locale locale, String defaultMessage, Object[] args, ValueStack valueStack);

    /**
     * Adds the bundle to the internal list of default bundles.
     * If the bundle already exists in the list it will be re-added.
     *
     * @param bundleName the name of the bundle to add.
     */
    void addDefaultResourceBundle(String bundleName);

}
