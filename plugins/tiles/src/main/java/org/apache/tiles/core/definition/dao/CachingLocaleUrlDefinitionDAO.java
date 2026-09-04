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

package org.apache.tiles.core.definition.dao;

import org.apache.tiles.api.Definition;
import org.apache.tiles.core.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.core.definition.pattern.PatternDefinitionResolverAware;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.LocaleUtil;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * A definitions DAO (loading URLs and using Locale as a customization key) that
 * caches definitions that have been loaded in a raw way (i.e. with inheritance
 * that is not resolved).
 * </p>
 * <p>
 * It can check if the URLs change, but by default this feature is turned off.
 * </p>
 *
 * @since 2.1.0
 */
public class CachingLocaleUrlDefinitionDAO extends BaseLocaleUrlDefinitionDAO implements PatternDefinitionResolverAware<Locale> {

    /**
     * Initialization parameter to set whether we want to refresh URLs when they
     * change.
     *
     * @since 2.1.0
     */
    public static final String CHECK_REFRESH_INIT_PARAMETER = "org.apache.tiles.definition.dao.LocaleUrlDefinitionDAO.CHECK_REFRESH";

    /**
     * Default upper bound on the number of customization keys (locales) whose definitions are cached at once. Since the
     * customization key is derived from the request locale, this bounds the cache so it cannot grow without limit as
     * distinct locales are encountered.
     */
    public static final int DEFAULT_MAX_CACHED_LOCALES = 1000;

    /**
     * The locale-specific set of definitions objects.
     *
     * @since 2.1.0
     */
    protected Map<Locale, Map<String, Definition>> locale2definitionMap;

    /**
     * Maximum number of customization keys (locales) retained in {@link #locale2definitionMap}. When exceeded, the
     * eldest entry is evicted (and reloaded on demand if requested again).
     */
    protected int maxCachedLocales = DEFAULT_MAX_CACHED_LOCALES;

    /**
     * Flag that, when <code>true</code>, enables automatic checking of URLs
     * changing.
     *
     * @since 2.1.0
     */
    protected boolean checkRefresh = false;

    /**
     * Resolves definitions using patterns.
     *
     * @since 2.2.0
     */
    protected PatternDefinitionResolver<Locale> definitionResolver;

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    public CachingLocaleUrlDefinitionDAO(ApplicationContext applicationContext) {
        super(applicationContext);
        locale2definitionMap = new LinkedHashMap<Locale, Map<String, Definition>>(16, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Locale, Map<String, Definition>> eldest) {
                if (size() <= maxCachedLocales) {
                    return false;
                }
                if (definitionResolver != null) {
                    definitionResolver.removePatternPaths(eldest.getKey());
                }
                return true;
            }
        };
    }

    /**
     * Sets the maximum number of customization keys (locales) whose definitions are cached. When more distinct keys are
     * requested, the eldest cached entry is evicted so the cache cannot grow without bound. Evicted entries are reloaded
     * on demand if requested again, so eviction never changes rendering, only re-incurs a load.
     *
     * @param maxCachedLocales the maximum number of cached customization keys; values below 1 are treated as 1
     */
    public void setMaxCachedLocales(int maxCachedLocales) {
        this.maxCachedLocales = Math.max(1, maxCachedLocales);
    }

    /**
     * {@inheritDoc}
     */
    public void setPatternDefinitionResolver(
        PatternDefinitionResolver<Locale> definitionResolver) {
        this.definitionResolver = definitionResolver;
    }

    /**
     * {@inheritDoc}
     */
    public Definition getDefinition(String name, Locale customizationKey) {
        Definition retValue = null;
        if (customizationKey == null) {
            customizationKey = Locale.ROOT;
        }
        Map<String, Definition> definitions = getDefinitions(customizationKey);
        if (definitions != null) {
            retValue = definitions.get(name);

            if (retValue == null) {
                retValue = getDefinitionFromResolver(name, customizationKey);

                if (retValue != null) {
                    synchronized (definitions) {
                        definitions.put(name, retValue);
                    }
                }
            }
        }

        return retValue;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Definition> getDefinitions(Locale customizationKey) {
        if (customizationKey == null) {
            customizationKey = Locale.ROOT;
        }
        Map<String, Definition> retValue = locale2definitionMap
            .get(customizationKey);
        if (retValue == null || (checkRefresh && refreshRequired())) {
            retValue = checkAndloadDefinitions(customizationKey);
        }
        return retValue;
    }

    /**
     * Sets the flag to check source refresh. If not called, the default is
     * <code>false</code>.
     *
     * @param checkRefresh When <code>true</code>, enables automatic checking
     *                     of sources changing.
     * @since 2.1.0
     */
    public void setCheckRefresh(boolean checkRefresh) {
        this.checkRefresh = checkRefresh;
    }

    /**
     * Returns a definition from the definition resolver.
     *
     * @param name             The name of the definition.
     * @param customizationKey The customization key to use.
     * @return The resolved definition.
     */
    protected Definition getDefinitionFromResolver(String name,
                                                   Locale customizationKey) {
        return definitionResolver.resolveDefinition(name,
            customizationKey);
    }

    /**
     * Checks if sources have changed. If yes, it clears the cache. Then continues
     * loading definitions.
     *
     * @param customizationKey The locale to use when loading sources.
     * @return The loaded definitions.
     * @since 2.1.0
     */
    protected synchronized Map<String, Definition> checkAndloadDefinitions(Locale customizationKey) {
        Map<String, Definition> existingDefinitions = locale2definitionMap.get(customizationKey);
        boolean definitionsAlreadyLoaded = existingDefinitions != null;
        if (definitionsAlreadyLoaded) {
            return existingDefinitions;
        }
        if (checkRefresh && refreshRequired()) {
            locale2definitionMap.clear();
            definitionResolver.clearPatternPaths(customizationKey);
        }
        loadDefinitions(customizationKey);
        return locale2definitionMap.get(customizationKey);
    }

    /**
     * Tries to load definitions if necessary.
     *
     * @param customizationKey The locale to use when loading sources.
     * @return The loaded definitions.
     * @since 2.1.0
     */
    protected Map<String, Definition> loadDefinitions(Locale customizationKey) {
        Map<String, Definition> localeDefsMap = locale2definitionMap
            .get(customizationKey);
        if (localeDefsMap != null) {
            return localeDefsMap;
        }

        return loadDefinitionsFromResources(customizationKey);
    }

    /**
     * Loads definitions from the sources.
     *
     * @param customizationKey The locale to use when loading Resources.
     * @return The loaded definitions.
     * @since 2.1.0
     */
    protected Map<String, Definition> loadDefinitionsFromResources(Locale customizationKey) {
        Map<String, Definition> localeDefsMap = loadRawDefinitionsFromResources(customizationKey);
        Map<String, Definition> defsMap = definitionResolver
            .storeDefinitionPatterns(copyDefinitionMap(localeDefsMap),
                customizationKey);
        locale2definitionMap.put(customizationKey, defsMap);
        return localeDefsMap;
    }

    /**
     * Loads the raw definitions from the sources associated with a locale.
     *
     * @param customizationKey The locale to use when loading Resources.
     * @return The loaded definitions.
     * @since 2.1.3
     */
    protected Map<String, Definition> loadRawDefinitionsFromResources(Locale customizationKey) {
        Map<String, Definition> localeDefsMap;

        Locale parentLocale = LocaleUtil.getParentLocale(customizationKey);
        localeDefsMap = new LinkedHashMap<>();
        if (parentLocale != null) {
            Map<String, Definition> parentDefs = loadRawDefinitionsFromResources(parentLocale);
            if (parentDefs != null) {
                localeDefsMap.putAll(parentDefs);
            }
        }
        // For each source, the resource must be loaded.
        for (ApplicationResource resource : sources) {
            ApplicationResource newResource = applicationContext.getResource(resource, customizationKey);
            if (newResource != null) {
                Map<String, Definition> defsMap = loadDefinitionsFromResource(newResource);
                if (defsMap != null) {
                    localeDefsMap.putAll(defsMap);
                }
            }
        }
        return localeDefsMap;
    }

    /**
     * Loads parent definitions, i.e. definitions mapped to a parent locale.
     *
     * @param parentLocale The locale to use when loading URLs.
     * @return The loaded parent definitions.
     * @since 2.1.0
     */
    protected Map<String, Definition> loadParentDefinitions(Locale parentLocale) {
        return loadDefinitions(parentLocale);
    }

    /**
     * Copies the definition map to be passed to a higher level of customization
     * key.
     *
     * @param localeDefsMap The map of definition to be copied.
     * @return The copy of the definition map. This particular implementation
     * return the <code>localeDefsMap</code> itself.
     * @since 2.1.4
     */
    protected Map<String, Definition> copyDefinitionMap(
        Map<String, Definition> localeDefsMap) {
        return localeDefsMap;
    }
}
