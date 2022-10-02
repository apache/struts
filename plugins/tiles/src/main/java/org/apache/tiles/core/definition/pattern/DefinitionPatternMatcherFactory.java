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
 * Creates a new definition pattern matcher for the given pattern and the given
 * base definition with pattern expressions.
 * @since 2.2.0
 */
public interface DefinitionPatternMatcherFactory {

    /**
     * Creates a new definition pattern matcher.
     *
     * @param pattern The pattern to be matched.
     * @param definition The base definition. Created definitions by
     * {@link DefinitionPatternMatcher#createDefinition(String)} will created
     * with this one as a basis.
     * @return The definition pattern matcher.
     * @since 2.2.0
     */
    DefinitionPatternMatcher createDefinitionPatternMatcher(String pattern, Definition definition);
}
