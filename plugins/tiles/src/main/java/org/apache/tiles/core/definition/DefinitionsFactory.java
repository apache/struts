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

package org.apache.tiles.core.definition;

import org.apache.tiles.api.Definition;
import org.apache.tiles.request.Request;

/**
 * Interface for creating a {@link Definition}s and managing their contents.
 * <p/>
 * <p>
 * DefinitionsFactory implementations are responsible for maintaining the data
 * sources of Tiles configuration data and using the data to create Definitions
 * sets. Implementations also know how to append locale-specific configuration
 * data to an existing Definitions set.
 * </p>
 */
public interface DefinitionsFactory {

    /**
     * Property name that specifies the implementation of the DefinitionsReader.
     */
    String READER_IMPL_PROPERTY =
        "org.apache.tiles.definition.DefinitionsReader";

    /**
     * Property name that specifies the implementation of
     * {@link org.apache.tiles.core.locale.LocaleResolver}.
     */
    String LOCALE_RESOLVER_IMPL_PROPERTY =
        "org.apache.tiles.locale.LocaleResolver";

    /**
     * Constant representing the configuration parameter
     * used to define the tiles definition resources.
     *
     * @since 2.1.0
     */
    String DEFINITIONS_CONFIG = "org.apache.tiles.definition.DefinitionsFactory.DEFINITIONS_CONFIG";

    /**
     * Constant representing the configuration parameter used to define the
     * definition DAO to use.
     */
    String DEFINITION_DAO_INIT_PARAM =
        "org.apache.tiles.definition.DefinitionsFactory.DefinitionDAO";

    /**
     * Returns a Definition object that matches the given name and
     * Tiles context.
     *
     * @param name         The name of the Definition to return.
     * @param tilesContext The Tiles context to use to resolve the definition.
     * @return the Definition matching the given name or null if none
     *         is found.
     */
    Definition getDefinition(String name, Request tilesContext);
}
