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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.request.ApplicationResource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * An ApplicationResource whose localization is managed by postfixing the file name.
 * The various localizations are file sitting next to each other, with the locale identified in the postfix.
 * <p>
 * For instance:
 * <pre>
 * /WEB-INF/tiles.xml
 * /WEB-INF/tiles_fr.xml
 * /WEB-INF/tiles_it.xml
 * /WEB-INF/tiles_it_IT.xml
 * </pre>
 *
 * Two PostfixedApplicationResources are equals if they share the same localized path and the same class.
 */
public abstract class PostfixedApplicationResource implements ApplicationResource {

    private static final Logger LOG = LogManager.getLogger(PostfixedApplicationResource.class);

    /** The path without its suffix and its locale postfix. */
    private String pathPrefix;
    /** The suffix. */
    private final String suffix;
    /** The Locale. */
    private final Locale locale;

    /**
     * Create a new PostfixedApplicationResource for the specified path.
     * @param localePath the path including localization.
     */
    protected PostfixedApplicationResource(String localePath) {
        int prefixIndex = localePath.indexOf('_', localePath.lastIndexOf("/"));
        int suffixIndex = localePath.lastIndexOf('.');
        if (suffixIndex < 0) {
            suffix = "";
            suffixIndex = localePath.length();
        } else {
            suffix = localePath.substring(suffixIndex);
        }
        if (prefixIndex < 0) {
            pathPrefix = localePath.substring(0, suffixIndex);
            locale = Locale.ROOT;
        } else {
            pathPrefix = localePath.substring(0, prefixIndex);
            String localeString = localePath.substring(prefixIndex + 1, suffixIndex);
            Locale found = localeFrom(localeString);
            locale = validateLocale(found);
            if (Locale.ROOT.equals(locale)) {
                pathPrefix = localePath.substring(0, suffixIndex);

                LOG.warn("No supported matching language for locale \"" + localeString + "\". Using "
                        + getPath() + " as a non-localized resource path. see TILES-571");

            } else if (!localeString.equalsIgnoreCase(getPostfix(locale).substring(1))) {
                LOG.warn("For resource " + localePath
                        + " the closest supported matching locale to \"" + localeString + "\" is \"" + locale
                        + "\". Using " + getPath() + " as resource path. see TILES-571");
            }
        }
    }

    /**
     * Create a new PostfixedApplicationResource for the specified path.
     * @param path the path excluding localization.
     * @param locale the Locale.
     */
    protected PostfixedApplicationResource(String path, Locale locale) {
        int suffixIndex = path.lastIndexOf('.');
        if (suffixIndex < 0) {
            suffix = "";
            pathPrefix = path;
        } else {
            pathPrefix = path.substring(0, suffixIndex);
            suffix = path.substring(suffixIndex);
        }
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public final String getLocalePath() {
        return getLocalePath(locale);
    }

    /** {@inheritDoc} */
    @Override
    public final String getPath() {
        return pathPrefix + suffix;
    }

    /** {@inheritDoc} */
    @Override
    public final String getLocalePath(Locale newLocale) {
        return pathPrefix + getPostfix(newLocale) + suffix;
    }

    /**
     * Get the postfix for that Locale.
     * @param locale a locale.
     * @return the matching postfix.
     */
    private static String getPostfix(Locale locale) {
        if (locale == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (!"".equals(language)) {
            builder.append("_");
            builder.append(language);
            if (!"".equals(country)) {
                builder.append("_");
                builder.append(country);
                if (!"".equals(variant)) {
                    builder.append("_");
                    builder.append(variant);
                }
            }
        }
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public final Locale getLocale() {
        return locale;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        result = prime * result + ((pathPrefix == null) ? 0 : pathPrefix.hashCode());
        result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PostfixedApplicationResource other = (PostfixedApplicationResource) obj;
        if (locale == null) {
            if (other.locale != null)
                return false;
        } else if (!locale.equals(other.locale))
            return false;
        if (pathPrefix == null) {
            if (other.pathPrefix != null)
                return false;
        } else if (!pathPrefix.equals(other.pathPrefix))
            return false;
        if (suffix == null) {
            return other.suffix == null;
        } else return suffix.equals(other.suffix);
    }

    private static Locale localeFrom(String localeString) {
        Locale result;
        int countryIndex = localeString.indexOf('_');
        if (countryIndex < 0) {
            result = new Locale(localeString);
        } else {
            int variantIndex = localeString.indexOf('_', countryIndex + 1);
            if (variantIndex < 0) {
                result = new Locale(
                        localeString.substring(0, countryIndex),
                        localeString.substring(countryIndex + 1));
            } else {
                result = new Locale(
                        localeString.substring(0, countryIndex),
                        localeString.substring(countryIndex + 1, variantIndex),
                        localeString.substring(variantIndex + 1));
            }
        }
        return result;
    }

    private static final Set<Locale> availableLocales = new HashSet<>(Arrays.asList(Locale.getAvailableLocales()));

    private static Locale validateLocale(Locale locale) {
        Locale withoutVariant = locale.getVariant().isEmpty()
                ? locale
                : new Locale(locale.getLanguage(), locale.getCountry());

        Locale result = locale;
        if (!availableLocales.contains(withoutVariant)) {
            if (!result.getCountry().isEmpty()) {
                result = new Locale(result.getLanguage());
            }
            if (!availableLocales.contains(result)) {
                result = Locale.ROOT;
            }
        }
        return result;
    }
}
