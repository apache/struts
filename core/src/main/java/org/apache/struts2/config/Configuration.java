/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts2.config;

import com.opensymphony.xwork.ObjectFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;


/**
 * Handles all Struts config properties. Implementation of this class is pluggable (the
 * default implementation is {@link DefaultConfiguration}). This gives developers to ability to customize how
 * Struts properties are set and retrieved. As an example, a developer may wish to check a separate property
 * store before delegating to the Struts Action Framework one. <p>
 * <p/>
 * Key methods: <ul>
 * <p/>
 * <li>{@link #getLocale()}</li>
 * <li>{@link #getString(String)}</li>
 * <li>{@link #set(String, Object)}</li>
 * <li>{@link #list()}</li></ul>
 * <p/>
 * Key methods for subclassers: <ul>
 * <p/>
 * <li>{@link #getImpl(String)}</li>
 * <li>{@link #setImpl(String, Object)}</li>
 * <li>{@link #listImpl()}</li>
 * <li>{@link #isSetImpl(String)}</li></ul>
 */
public class Configuration {

    static Configuration configurationImpl;
    static Configuration defaultImpl;
    static Locale locale; // Cached locale
    private static final Log LOG = LogFactory.getLog(Configuration.class);


    /**
     * Sets the current configuration implementation. Can only be called once.
     *
     * @param config a Configuration implementation
     * @throws IllegalStateException if an error occurs when setting the configuration implementation.
     */
    public static void setConfiguration(Configuration config) throws IllegalStateException {
        configurationImpl = config;
        locale = null; // Reset cached locale
    }

    /**
     * Gets the current configuration implementation.
     *
     * @return the current configuration implementation.
     */
    public static Configuration getConfiguration() {
        return (configurationImpl == null) ? getDefaultConfiguration() : configurationImpl;
    }

    /**
     * Returns the Struts Action Framework locale. Keys off the property <tt>struts.locale</tt> which should be set
     * as the Java {@link java.util.Locale#toString() toString()} representation of a Locale object (i.e.,
     * "en", "de_DE", "_GB", "en_US_WIN", "de__POSIX", "fr_MAC", etc). <p>
     * <p/>
     * If no locale is specified then the default VM locale is used ({@link java.util.Locale#getDefault()}).
     *
     * @return the Struts Action Framework locale if specified or the VM default locale.
     */
    public static Locale getLocale() {
        if (locale == null) {
            try {
                StringTokenizer localeTokens = new StringTokenizer(getString(StrutsConstants.STRUTS_LOCALE), "_");
                String lang = null;
                String country = null;

                if (localeTokens.hasMoreTokens()) {
                    lang = localeTokens.nextToken();
                }

                if (localeTokens.hasMoreTokens()) {
                    country = localeTokens.nextToken();
                }

                locale = new Locale(lang, country);
            } catch (Throwable t) {
                // Default
                LOG.warn("Setting locale to the default locale");
                locale = Locale.getDefault();
            }
        }

        return locale;
    }

    /**
     * Determines whether or not a value has been set. Useful for testing for the existance of parameter without
     * throwing an IllegalArgumentException.
     *
     * @param name the name of the property to test.
     * @return <tt>true</tt> if the property exists and has a value, <tt>false</tt> otherwise.
     */
    public static boolean isSet(String name) {
        return getConfiguration().isSetImpl(name);
    }

    /**
     * Returns a property as a String. This will throw an <tt>IllegalArgumentException</tt> if an error occurs
     * while retrieveing the property or if the property doesn't exist.
     *
     * @param name the name of the property to get.
     * @return the property as a String
     * @throws IllegalArgumentException if an error occurs retrieveing the property or the property does not exist.
     */
    public static String getString(String name) throws IllegalArgumentException {
        String val = get(name).toString();

        return val;
    }

    /**
     * Returns a property as an Object. This will throw an <tt>IllegalArgumentException</tt> if an error occurs
     * while retrieveing the property or if the property doesn't exist.
     *
     * @param name the name of the property to get.
     * @return the property as an Object.
     * @throws IllegalArgumentException if an error occurs retrieveing the property or the property does not exist.
     */
    public static Object get(String name) throws IllegalArgumentException {
        Object val = getConfiguration().getImpl(name);

        return val;
    }

    /**
     * Returns an Iterator of all properties names.
     *
     * @return an Iterator of all properties names.
     */
    public static Iterator list() {
        return getConfiguration().listImpl();
    }

    /**
     * Implementation of the {@link #isSet(String)} method.
     *
     * @see #isSet(String)
     */
    public boolean isSetImpl(String name) {
        // this is dumb.. maybe it should just throw an unsupported op like the rest of the *Impl
        // methods in this class.
        return false;
    }

    /**
     * Sets a property. Throws an exception if an error occurs when setting the property or if the
     * Configuration implementation does not support setting properties.
     *
     * @param name  the name of the property to set.
     * @param value the property to set.
     * @throws IllegalArgumentException      if an error occurs when setting the property.
     * @throws UnsupportedOperationException if the config implementation does not support setting properties.
     */
    public static void set(String name, Object value) throws IllegalArgumentException, UnsupportedOperationException {
        getConfiguration().setImpl(name, value);
    }

    /**
     * Implementation of the {@link #set(String, Object)} method.
     *
     * @see #set(String, Object)
     */
    public void setImpl(String name, Object value) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("This configuration does not support updating a setting");
    }

    /**
     * Implementation of the {@link #get(String)} method.
     *
     * @see #get(String)
     */
    public Object getImpl(String aName) throws IllegalArgumentException {
        return null;
    }

    /**
     * Implementation of the {@link #list()} method.
     *
     * @see #list()
     */
    public Iterator listImpl() {
        throw new UnsupportedOperationException("This configuration does not support listing the settings");
    }

    private static Configuration getDefaultConfiguration() {
        if (defaultImpl == null) {
            // Create bootstrap implementation
            defaultImpl = new DefaultConfiguration();

            // Create default implementation
            try {
                String className = getString(StrutsConstants.STRUTS_CONFIGURATION);

                if (!className.equals(defaultImpl.getClass().getName())) {
                    try {
                        // singleton instances shouldn't be built accessing request or session-specific context data
                        defaultImpl = (Configuration) ObjectFactory.getObjectFactory().buildBean(Thread.currentThread().getContextClassLoader().loadClass(className), null);
                    } catch (Exception e) {
                        LOG.error("Could not instantiate configuration", e);
                    }
                }
            } catch (IllegalArgumentException ex) {
                // ignore
            }
        }

        return defaultImpl;
    }

    public static void reset() {
        defaultImpl = null;
        configurationImpl = null;
    }
}
