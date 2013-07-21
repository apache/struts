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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;

import java.util.List;
import java.util.ResourceBundle;


/**
 * Provides access to {@link ResourceBundle}s and their underlying text messages.
 * Implementing classes can delegate {@link TextProviderSupport}. Messages will be
 * searched in multiple resource bundles, starting with the one associated with
 * this particular class (action in most cases), continuing to try the message
 * bundle associated with each superclass as well. It will stop once a bundle is
 * found that contains the given text. This gives a cascading style that allow
 * global texts to be defined for an application base class.
 * <p/>
 * You can override {@link LocaleProvider#getLocale()} to change the behaviour of how
 * to choose locale for the bundles that are returned. Typically you would
 * use the {@link LocaleProvider} interface to get the users configured locale.
 * <p/>
 * When you want to use your own implementation for Struts 2 project you have to define following
 * bean and constant in struts.xml:
 * &lt;bean class=&quot;org.demo.MyTextProvider&quot; name=&quot;myTextProvider&quot; type=&quot;com.opensymphony.xwork2.TextProvider&quot; /&gt;
 * &lt;constant name=&quot;struts.xworkTextProvider&quot; value=&quot;myTextProvider&quot; /&gt;
 * <p/>
 * if you want to also use your implementation for framework's messages define another constant (remember to put
 * into it all framework messages)
 * &lt;constant name=&quot;system&quot; value=&quot;myTextProvider&quot; /&gt;
 * <p/>
 * Take a look on {@link com.opensymphony.xwork2.ActionSupport} for example TextProvider implemntation.
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 * @see LocaleProvider
 * @see TextProviderSupport
 */
public interface TextProvider {

    /**
     * Checks if a message key exists.
     *
     * @param key message key to check for
     * @return boolean true if key exists, false otherwise.
     */
    boolean hasKey(String key);

    /**
     * Gets a message based on a message key or if no message is found the provided key
     * is returned.
     *
     * @param key the resource bundle key that is to be searched for
     * @return the message as found in the resource bundle, or the provided key if none is found.
     */
    String getText(String key);

    /**
     * Gets a message based on a key, or, if the message is not found, a supplied
     * default value is returned.
     *
     * @param key          the resource bundle key that is to be searched for
     * @param defaultValue the default value which will be returned if no message is found
     * @return the message as found in the resource bundle, or defaultValue if none is found
     */
    String getText(String key, String defaultValue);

    /**
     * Gets a message based on a key using the supplied obj, as defined in
     * {@link java.text.MessageFormat}, or, if the message is not found, a supplied
     * default value is returned.
     *
     * @param key          the resource bundle key that is to be searched for
     * @param defaultValue the default value which will be returned if no message is found
     * @param obj          obj to be used in a {@link java.text.MessageFormat} message
     * @return the message as found in the resource bundle, or defaultValue if none is found
     */
    String getText(String key, String defaultValue, String obj);

    /**
     * Gets a message based on a key using the supplied args, as defined in
     * {@link java.text.MessageFormat} or the provided key if no message is found.
     *
     * @param key  the resource bundle key that is to be searched for
     * @param args a list args to be used in a {@link java.text.MessageFormat} message
     * @return the message as found in the resource bundle, or the provided key if none is found.
     */
    String getText(String key, List<?> args);

    /**
     * Gets a message based on a key using the supplied args, as defined in
     * {@link java.text.MessageFormat}, or the provided key if no message is found.
     *
     * @param key  the resource bundle key that is to be searched for
     * @param args an array args to be used in a {@link java.text.MessageFormat} message
     * @return the message as found in the resource bundle, or the provided key if none is found.
     */
    String getText(String key, String[] args);

    /**
     * Gets a message based on a key using the supplied args, as defined in
     * {@link java.text.MessageFormat}, or, if the message is not found, a supplied
     * default value is returned.
     *
     * @param key          the resource bundle key that is to be searched for
     * @param defaultValue the default value which will be returned if no message is found
     * @param args         a list args to be used in a {@link java.text.MessageFormat} message
     * @return the message as found in the resource bundle, or defaultValue if none is found
     */
    String getText(String key, String defaultValue, List<?> args);

    /**
     * Gets a message based on a key using the supplied args, as defined in
     * {@link java.text.MessageFormat}, or, if the message is not found, a supplied
     * default value is returned.
     *
     * @param key          the resource bundle key that is to be searched for
     * @param defaultValue the default value which will be returned if no message is found
     * @param args         an array args to be used in a {@link java.text.MessageFormat} message
     * @return the message as found in the resource bundle, or defaultValue if none is found
     */
    String getText(String key, String defaultValue, String[] args);

    /**
     * Gets a message based on a key using the supplied args, as defined in
     * {@link java.text.MessageFormat}, or, if the message is not found, a supplied
     * default value is returned. Instead of using the value stack in the ActionContext
     * this version of the getText() method uses the provided value stack.
     *
     * @param key          the resource bundle key that is to be searched for
     * @param defaultValue the default value which will be returned if no message is found
     * @param args         a list args to be used in a {@link java.text.MessageFormat} message
     * @param stack        the value stack to use for finding the text
     * @return the message as found in the resource bundle, or defaultValue if none is found
     */
    String getText(String key, String defaultValue, List<?> args, ValueStack stack);

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
    String getText(String key, String defaultValue, String[] args, ValueStack stack);

    /**
     * Get the named bundle, such as "com/acme/Foo".
     *
     * @param bundleName the name of the resource bundle, such as <code>"com/acme/Foo"</code>.
     * @return the bundle
     */
    ResourceBundle getTexts(String bundleName);

    /**
     * Get the resource bundle associated with the implementing class (usually an action).
     *
     * @return the bundle
     */
    ResourceBundle getTexts();
}
