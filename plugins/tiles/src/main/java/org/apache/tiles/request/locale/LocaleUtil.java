/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.locale;

import java.util.Locale;

/**
 * Utilities for locale manipulation.
 */
public final class LocaleUtil {

    /**
     * Private constructor to avoid instantiation.
     */
    private LocaleUtil() {
    }

    /**
     * <p>
     * Returns the "parent" locale of a given locale. If the original locale is only language-based,
     * the {@link Locale#ROOT} object is returned.
     * </p>
     *
     * @param locale The original locale.
     * @return The parent locale.
     */
    public static Locale getParentLocale(Locale locale) {
        Locale retValue = null;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (!"".equals(variant)) {
            retValue = new Locale(language, country);
        } else if (!"".equals(country)) {
            retValue = new Locale(language);
        } else if (!"".equals(language)) {
            retValue = Locale.ROOT;
        }

        return retValue;
    }
}
