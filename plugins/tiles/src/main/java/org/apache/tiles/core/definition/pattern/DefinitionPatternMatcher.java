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

/**
 * Matches a definition name to a definition, through pattern-matching. The
 * matched pattern should be a single one.
 * @since 2.2.0
 */
public interface DefinitionPatternMatcher {

    /**
     * Creates a definition, given the definition name, through the use of
     * pattern matching.
     *
     * @param definitionName The definition name to match.
     * @return The created definition, if matched, or <code>null</code> if not
     * matched.
     * @since 2.2.0
     */
    Definition createDefinition(String definitionName);
}
