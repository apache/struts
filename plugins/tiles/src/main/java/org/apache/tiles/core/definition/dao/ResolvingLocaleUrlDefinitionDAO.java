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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.api.Definition;
import org.apache.tiles.core.definition.NoSuchDefinitionException;
import org.apache.tiles.request.ApplicationContext;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * A definitions DAO (loading URLs and using Locale as a customization key) that
 * caches definitions that have been loaded and resolves inheritances.
 * </p>
 * <p>
 * It can check if the URLs change, but by default this feature is turned off.
 * </p>
 *
 * @since 2.1.0
 */
public class ResolvingLocaleUrlDefinitionDAO extends CachingLocaleUrlDefinitionDAO {

    /**
     * The logging object.
     */
    private static final Logger LOG = LogManager.getLogger(ResolvingLocaleUrlDefinitionDAO.class);

    public ResolvingLocaleUrlDefinitionDAO(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    /** {@inheritDoc} */
    @Override
    protected Map<String, Definition> loadParentDefinitions(Locale parentLocale) {
        return loadRawDefinitionsFromResources(parentLocale);
    }

    @Override
    protected Map<String, Definition> loadDefinitions(Locale customizationKey) {
        Map<String, Definition> localeDefsMap = super.loadDefinitions(customizationKey);
        Map<String, Definition> defsMap = definitionResolver
                .storeDefinitionPatterns(copyDefinitionMap(localeDefsMap),
                        customizationKey);
        resolveInheritances(defsMap, customizationKey);
        locale2definitionMap.put(customizationKey, defsMap);
        return defsMap;
    }

    /** {@inheritDoc} */
    @Override
    protected Definition getDefinitionFromResolver(String name,
            Locale customizationKey) {
        Definition retValue = super.getDefinitionFromResolver(name, customizationKey);
        if (retValue != null && retValue.getExtends() != null) {
            Definition parent = getDefinition(retValue.getExtends(), customizationKey);
            retValue.inherit(parent);
        }

        return retValue;
    }

    /**
     * Resolve locale-specific extended instances.
     *
     * @param map The definition map containing the definitions to resolve.
     * @param locale The locale to use.
     * @throws NoSuchDefinitionException If a parent definition is not found.
     * @since 2.1.0
     */
    protected void resolveInheritances(Map<String, Definition> map, Locale locale) {
        if (map != null) {
            Set<String> alreadyResolvedDefinitions = new HashSet<>();
            for (Definition definition : map.values()) {
                resolveInheritance(definition, map, locale,
                        alreadyResolvedDefinitions);
            }  // end loop
        }
    }

    /**
     * Resolve locale-specific inheritance. First, resolve parent's inheritance,
     * then set template to the parent's template. Also copy attributes setted
     * in parent, and not set in child If instance doesn't extend anything, do
     * nothing.
     *
     * @param definition The definition to resolve
     * @param definitions The definitions to take when obtaining a parent
     * definition.
     * @param locale The locale to use.
     * @param alreadyResolvedDefinitions The set of the definitions that have
     * been already resolved.
     * @throws NoSuchDefinitionException If an inheritance can not be solved.
     * @since 2.1.0
     */
    protected void resolveInheritance(Definition definition,
            Map<String, Definition> definitions, Locale locale,
            Set<String> alreadyResolvedDefinitions) {
        // Already done, or not needed ?
        if (!definition.isExtending() || alreadyResolvedDefinitions.contains(definition.getName())) {
            return;
        }

        LOG.debug("Resolve definition for child name='{}' extends='{}.", definition.getName(), definition.getExtends());

        // Set as visited to avoid endless recursivity.
        alreadyResolvedDefinitions.add(definition.getName());

        // Resolve parent before itself.
        Definition parent = definitions.get(definition.getExtends());
        if (parent == null) { // error
            String msg = "Error while resolving definition inheritance: child '"
                + definition.getName()
                + "' can't find its ancestor '"
                + definition.getExtends()
                + "'. Please check your description file.";
            // to do : find better exception
            throw new NoSuchDefinitionException(msg);
        }

        resolveInheritance(parent, definitions, locale,
                alreadyResolvedDefinitions);

        definition.inherit(parent);
    }

    /**
     * Copies the definition map to be passed to a higher level of customization
     * key.
     *
     * @param localeDefsMap The map of definition to be copied.
     * @return The copy of the definition map. This particular implementation
     * deep-copies the <code>localeDefsMap</code> into a {@link LinkedHashMap}.
     * @since 2.1.4
     */
    @Override
    protected Map<String, Definition> copyDefinitionMap(
            Map<String, Definition> localeDefsMap) {
        Map<String, Definition> retValue = new LinkedHashMap<>(
            localeDefsMap.size());

        for (Map.Entry<String, Definition> entry : localeDefsMap.entrySet()) {
            Definition definition = new Definition(entry.getValue());
            retValue.put(entry.getKey(), definition);
        }

        return retValue;
    }
}
