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
package org.apache.tiles.core.impl.mgmt;

import org.apache.tiles.api.Definition;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.TilesContainerWrapper;
import org.apache.tiles.api.mgmt.MutableTilesContainer;
import org.apache.tiles.core.definition.NoSuchDefinitionException;
import org.apache.tiles.request.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages custom and configured definitions, so they can be used by the
 * container, instead of using a simple {@link org.apache.tiles.core.definition.DefinitionsFactory}.
 */
public class CachingTilesContainer extends TilesContainerWrapper implements MutableTilesContainer {

    /**
     * The default name of the attribute in which storing custom definitions.
     */
    private static final String DEFAULT_DEFINITIONS_ATTRIBUTE_NAME = "org.apache.tiles.impl.mgmt.DefinitionManager.DEFINITIONS";

    /**
     * The name of the attribute in which storing custom definitions.
     */
    private final String definitionsAttributeName;

    /**
     * Constructor.
     *
     * @param originalContainer The original container to wrap.
     */
    public CachingTilesContainer(TilesContainer originalContainer) {
        super(originalContainer);
        definitionsAttributeName = DEFAULT_DEFINITIONS_ATTRIBUTE_NAME;
    }

    /**
     * Returns a definition by name.
     *
     * @param definition The name of the definition.
     * @param request    The current request.
     * @return The requested definition, either main or custom.
     */
    public Definition getDefinition(String definition, Request request) {
        Definition retValue;
        retValue = getCustomDefinition(definition, request);
        if (retValue == null) {
            retValue = super.getDefinition(definition, request);
        }
        return retValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidDefinition(String definition, Request request) {
        if (getCustomDefinition(definition, request) != null) {
            return true;
        }
        return super.isValidDefinition(definition, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(Definition definition, Request request) {
        Map<String, Definition> definitions = getOrCreateDefinitions(request);
        if (definition.getName() == null) {
            definition.setName(getNextUniqueDefinitionName(definitions));
        }

        if (definition.isExtending()) {
            this.resolveInheritance(definition, request);
        }

        definitions.put(definition.getName(), definition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(String definition, Request request) {
        Definition toRender = getDefinition(definition, request);
        if (toRender == null) {
            throw new NoSuchDefinitionException("Cannot find definition named '" + definition + "'");
        }
        super.render(toRender, request);
    }

    /**
     * Resolve inheritance.
     * First, resolve parent's inheritance, then set template to the parent's
     * template.
     * Also copy attributes set in parent, and not set in child
     * If instance doesn't extend anything, do nothing.
     *
     * @param definition The definition that needs to have its inheritances
     *                   resolved.
     * @param request    The current request.
     * @throws org.apache.tiles.core.definition.DefinitionsFactoryException If an
     *                                                                      inheritance can not be solved.
     */
    private void resolveInheritance(Definition definition,
                                    Request request) {
        // Already done, or not needed ?
        if (!definition.isExtending()) {
            return;
        }

        String parentDefinitionName = definition.getExtends();

        boolean recurse = true;
        Definition parent = getCustomDefinition(parentDefinitionName, request);
        if (parent == null) {
            parent = container.getDefinition(parentDefinitionName, request);
            recurse = false;
        }

        if (parent == null) {
            throw new NoSuchDefinitionException(
                "Error while resolving definition inheritance: child '"
                    + definition.getName()
                    + "' can't find its ancestor '"
                    + parentDefinitionName
                    + "'. Please check your description file.");
        }

        // Resolve parent before itself.
        if (recurse) {
            resolveInheritance(parent, request);
        }
        definition.inherit(parent);
    }

    /**
     * Returns the map with custom definitions for the current request.
     *
     * @param request The current request.
     * @return A map that connects a definition name to a definition.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Definition> getDefinitions(
        Request request) {
        return (Map<String, Definition>) request.getContext("request")
            .get(definitionsAttributeName);
    }

    /**
     * Returns a map of type "definition name -> definition" and, if it has not
     * been defined before, creates one.
     *
     * @param request The current request.
     * @return A map that connects a definition name to a definition.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Definition> getOrCreateDefinitions(Request request) {
        Map<String, Definition> definitions = (Map<String, Definition>) request.getContext("request").get(definitionsAttributeName);
        if (definitions == null) {
            definitions = new HashMap<>();
            request.getContext("request").put(definitionsAttributeName, definitions);
        }

        return definitions;
    }

    /**
     * Create a unique definition name usable to store anonymous definitions.
     *
     * @param definitions The already created definitions.
     * @return The unique definition name to be used to store the definition.
     * @since 2.1.0
     */
    private String getNextUniqueDefinitionName(Map<String, Definition> definitions) {
        String candidate;
        int anonymousDefinitionIndex = 1;

        do {
            candidate = "$anonymousMutableDefinition" + anonymousDefinitionIndex;
            anonymousDefinitionIndex++;
        } while (definitions.containsKey(candidate));

        return candidate;
    }

    /**
     * Returns a custom definition from the cache.
     *
     * @param definition The definition to search.
     * @param request    The request.
     * @return The requested definition.
     */
    private Definition getCustomDefinition(String definition, Request request) {
        Map<String, Definition> definitions = getDefinitions(request);
        if (definitions != null) {
            return definitions.get(definition);
        }
        return null;
    }
}
