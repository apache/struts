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
package org.apache.struts2.locale;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Indicates that the implementing class can provide its own {@link Locale}.
 * <p>
 * This is useful for when an action may wish override the default locale. All that is
 * needed is to implement this interface and return your own custom locale.
 * The {@link org.apache.struts2.text.TextProvider} interface uses this interface
 * heavily for retrieving internationalized messages from resource bundles.
 */
public interface LocaleProvider {

    /**
     * Gets the provided locale.
     *
     * @return the locale.
     */
    Locale getLocale();

    /**
     * Validates if provided string is a valid {@link Locale}
     *
     * @param localeStr a String representing locale, e.g. en_EN
     * @return true if valid
     */
    boolean isValidLocaleString(String localeStr);

    /**
     * Validates if provided {@link Locale} is value
     *
     * @param locale instance of {@link Locale} to validate
     * @return true if valid
     */
    boolean isValidLocale(Locale locale);

    /**
     * Tries to convert provided locale string into {@link Locale} or returns null
     *
     * @param localeStr a String representing locale, e.g.: en_EN
     * @return instance of {@link Locale} or null
     * @since Struts 6.5.0
     */
    default Locale toLocale(String localeStr) {
        try {
            return LocaleUtils.toLocale(StringUtils.trimToNull(localeStr));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
