/*
 * $Id$
 *
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

package org.apache.struts2.config;

import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.struts2.StrutsConstants;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * Settings retrieves and exposes default values used by the framework.
 * An application can override a factory default and provide its own value for a setting.
 * <p>
 * Implementation of the class is pluggable (the default implementation is {@link DefaultSettings}).
 * Pluggability gives applications to ability to customize how settings are retrieved.
 * As an example, an application may wish to check some custom property store
 * before delegating to the usual configuration and property files.
 * <p>
 * Key methods:
 * <ul>
 * <li>{@link #getLocale()}</li>
 * <li>{@link #get(String)}</li>
 * <li>{@link #set(String, String)}</li>
 * <li>{@link #list()}</li>
 * </ul>
 * <p>
 * Key methods for subclasses (plugins):
 * <ul>
 * <li>{@link #getImpl(String)}</li>
 * <li>{@link #setImpl(String, String)}</li>
 * <li>{@link #listImpl()}</li>
 * <li>{@link #isSetImpl(String)}</li>
 * </ul>
 * @deprecated Since Struts 2.1.2
 */
class Settings {


    /**
     * A pluggable implementation of Settings,
     * provided through the {@link #setInstance} method.
     */
    static Settings settingsImpl;

    /**
     * An instance of {@link DefaultSettings}
     * to use when another implementation is not provided (plugged in).
     */
    static Settings defaultImpl;

    /**
     * An instance of the default locale as specified by the <code>struts.locale</code>  setting.
     *
     * @see #getLocale
     */
    static Locale locale;

    /**
     * The Logging instance for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);

    /**
     * Registers a custom Settings implementation (plugin),
     * and resets the cached locale.
     * <p>
     * This method can only be called once.
     *
     * @param config a Settings implementation
     * @throws IllegalStateException if an error occurs when setting the settings implementation.
     */
    public static void setInstance(Settings config) throws IllegalStateException {
        settingsImpl = config;
        locale = null;
    }

    /**
     * Provides the Settings object.
     * <p>
     * This method will substitute the default instance if another instance is not registered.
     *
     * @return the Settings object.
     */
    public static Settings getInstance() {
        return (settingsImpl == null) ? getDefaultInstance() : settingsImpl;
    }

    /**
     * Provides the Struts default locale.
     * <p>
     * This method utilizes the <code>struts.locale</code> setting, which should be given
     * as the Java {@link java.util.Locale#toString() toString()} representation of a Locale object
     * ("en", "de_DE", "_GB", "en_US_WIN", "de__POSIX", "fr_MAC", and so forth).
     * <p>
     * If a <code>struts.locale</code> setting is not registered,
     * then the default virtual machine locale is substituted and cached.
     *
     * @return the Struts default locale if specified or the VM default locale.
     * @see java.util.Locale#getDefault()
     */
    public static Locale getLocale() {
        // Locale processing has been moved to the LegacyPropertiesConfigurationProvider

        return locale;
    }

    /**
     * Determines whether or not a setting has a registered value.
     * <p>
     * This method is useful for testing for the existance of setting without
     * throwing an IllegalArgumentException.
     *
     * @param name the name of a setting to test.
     * @return <code>true</code> if the setting exists and has a value, <code>false</code> otherwise.
     */
    public static boolean isSet(String name) {
        return getInstance().isSetImpl(name);
    }

    /**
     * Provides a setting value as a String.
     * <p>
     * The method will throw an <code>IllegalArgumentException</code> if an error occurs
     * while retrieveing the property or if the property doesn't exist.
     *
     * @param name the name of the setting to retrieve.
     * @return the setting value as a String.
     * @throws IllegalArgumentException if an error occurs retrieving the property or the property does not exist.
     */
    public static String get(String name) throws IllegalArgumentException {
        return getInstance().getImpl(name);
    }

    /**
     * Provides the Location of a setting.
     * <p>
     * The Location is utilized as part of precise error reporting.
     * <p>
      * This method will throw an <code>IllegalArgumentException</code> if an error occurs
     * while retrieving the value or if the setting doesn't exist.
     *
     * @param name the name of the property to get.
     * @return the Location of a property.
     * @throws IllegalArgumentException if an error occurs retrieving the property or the property does not exist.
     */
    public static Location getLocation(String name) throws IllegalArgumentException {
        return getInstance().getLocationImpl(name);
    }

    /**
     * Provides an Iterator of all properties names.
     *
     * @return an Iterator of all properties names.
     */
    public static Iterator list() {
        return getInstance().listImpl();
    }

    /**
     * Implements the {@link #isSet(String)} method.
     *
     * @param name Identifier for the setting value to change
     * @return True if the setting exists and has a value, false otherwise.
     * @see #isSet(String)
     */
    public boolean isSetImpl(String name) {
        // this is dumb.. maybe it should just throw an unsupported op like the rest of the *Impl
        // methods in this class.
        return false;
    }

    /**
     * Registers a value for a setting.
     * <p>
     * This method raises an exception if an error occurs when setting the value or if the
     * settings implementation does not support setting values.
     *
     * @param name  the name of the setting.
     * @param value the value to register for the setting.
     * @throws IllegalArgumentException      if an error occurs when setting the value.
     * @throws UnsupportedOperationException if the config implementation does not support setting values.
     */
    public static void set(String name, String value) throws IllegalArgumentException, UnsupportedOperationException {
        getInstance().setImpl(name, value);
    }

    /**
     * Implements the {@link #set(String, String)} method.
     *
     * @param name Identifer for the setting to change.
     * @param value The new value for the setting.
     * @throws IllegalArgumentException      if an error occurs when setting the value.
     * @throws UnsupportedOperationException if the config implementation does not support setting values.
     * @see #set(String, String)
     */
    public void setImpl(String name, String value) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Settings: This implementation does not support setting a value.");
    }

    /**
     * Implements the {@link #get(String)} method.
     *
     * @param name The name of the setting value to retreive
     * @return The setting value as a String
     * @throws IllegalArgumentException if an error occurs when retrieving the value
     * @see #get(String)
     */
    public String getImpl(String name) throws IllegalArgumentException {
        return null;
    }

    /**
     * Implements the {@link #getLocation(String)} method.
     *
     * @param name Name of the setting to locate
     * @return The location  of the setting
     * @throws IllegalArgumentException if an error occurs when retrieving the value
     * @see #getLocation(String)
     */
    public Location getLocationImpl(String name) throws IllegalArgumentException {
        return null;
    }

    /**
     * Implements the {@link #list()} method.
     *
     * @see #list()
     * @return A list of the settings as an iterator
     */
    public Iterator listImpl() {
        throw new UnsupportedOperationException("Settings: This implementation does not support listing the registered settings");
    }

    /**
     * Creates a default Settings object.
     * <p>
     * A default implementation may be specified by the <code>struts.configuration</code> setting;
     * otherwise, this method instantiates {@link DefaultSettings} as the default implementation.
     *
     * @return A default Settings object.
     */
    private static Settings getDefaultInstance() {
        if (defaultImpl == null) {
            // Create bootstrap implementation
            defaultImpl = new DefaultSettings();

            // Create default implementation
            try {
                String className = get(StrutsConstants.STRUTS_CONFIGURATION);

                if (!className.equals(defaultImpl.getClass().getName())) {
                    try {
                        // singleton instances shouldn't be built accessing request or session-specific context data
                        defaultImpl = (Settings) ObjectFactory.getObjectFactory().buildBean(Thread.currentThread().getContextClassLoader().loadClass(className), null);
                    } catch (Exception e) {
                        LOG.error("Settings: Could not instantiate the struts.configuration object, substituting the default implementation.", e);
                    }
                }
            } catch (IllegalArgumentException ex) {
                // ignore
            }
        }

        return defaultImpl;
    }

    /**
     * Resets the default and any plugin Setting instance to null.
     */
    public static void reset() {
        defaultImpl = null;
        settingsImpl = null;
    }
}
