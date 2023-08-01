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
package org.apache.tiles.core.definition.pattern;

import org.apache.tiles.api.Definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A pattern definition resolver that stores {@link DefinitionPatternMatcher}
 * separated by customization key. <br>
 * Implementations should provide a way to translate a definition to a
 * {@link DefinitionPatternMatcher}.
 *
 * @param <T> The type of the customization key.
 * @since 2.2.0
 */
public abstract class AbstractPatternDefinitionResolver<T> implements PatternDefinitionResolver<T> {

    /**
     * Stores patterns depending on the locale they refer to.
     */
    private final Map<T, List<DefinitionPatternMatcher>> localePatternPaths = new HashMap<>();

    /** {@inheritDoc} */
    public Definition resolveDefinition(String name, T customizationKey) {
        Definition retValue = null;
        if (localePatternPaths.containsKey(customizationKey)) {
            retValue = searchAndResolveDefinition(localePatternPaths
                    .get(customizationKey), name);
        }
        return retValue;
    }

    /** {@inheritDoc} */
    public Map<String, Definition> storeDefinitionPatterns(Map<String, Definition> localeDefsMap, T customizationKey) {
        List<DefinitionPatternMatcher> lpaths = localePatternPaths.computeIfAbsent(customizationKey, k -> new ArrayList<>());
        return addDefinitionsAsPatternMatchers(lpaths, localeDefsMap);
    }

    /**
     * Adds definitions, filtering and adding them to the list of definition
     * pattern matchers. Only a subset of definitions will be transformed into
     * definition pattern matchers.
     *
     * @param matchers The list containing the currently stored definition pattern
     * matchers.
     * @param defsMap The definition map to parse.
     * @return The map of the definitions not recognized as containing
     * definition patterns.
     * @since 2.2.1
     */
    protected abstract Map<String, Definition> addDefinitionsAsPatternMatchers(List<DefinitionPatternMatcher> matchers, Map<String, Definition> defsMap);

    /**
     * Try to resolve a definition by iterating all pattern matchers.
     *
     * @param paths The list containing the currently stored paths.
     * @param name The name of the definition to resolve.
     * @return A definition, if found, or <code>null</code> if not.
     */
    private Definition searchAndResolveDefinition(List<DefinitionPatternMatcher> paths, String name) {
        Definition d = null;

        for (DefinitionPatternMatcher wm : paths) {
            d = wm.createDefinition(name);
            if (d != null) {
                break;
            }
        }

        return d;
    }


    /**
     * Used to clear all entries in the localePatternPaths for a specific locale. Necessary when reloading definition
     * files to ensure that the list is cleared first
     *
     * @param customizationKey customization key
     */
    @Override
    public void clearPatternPaths(T customizationKey) {
        if (localePatternPaths.get(customizationKey) != null)
            localePatternPaths.get(customizationKey).clear();
    }
}
