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

import java.util.Map;

/**
 * It represents an object that provides definitions, depending on a
 * customization key.
 *
 * @param <K> The customization key class.
 * @since 2.1.0
 */
public interface DefinitionDAO<K> {

    /**
     * Returns a definition, given its name and the customization key.
     *
     * @param name The name of the definition.
     * @param customizationKey The customization key.
     * @return The requested definition, if found, otherwise <code>null</code>.
     * The inheritance of the definition must not be resolved.
     * @since 2.1.0
     */
    Definition getDefinition(String name, K customizationKey);

    /**
     * Returns all the definitions used of a customization key.
     *
     * @param customizationKey The customization key.
     * @return All the definitions that are connected to the customization key.
     * The inheritance of the definitions must not be resolved.
     * @since 2.1.0
     */
    Map<String, Definition> getDefinitions(K customizationKey);
}
