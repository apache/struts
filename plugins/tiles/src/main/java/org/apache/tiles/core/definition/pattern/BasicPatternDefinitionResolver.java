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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A pattern definition resolver that stores {@link org.apache.tiles.core.definition.pattern.DefinitionPatternMatcher}
 * separated by customization key. <br>
 * It delegates creation of definition pattern matchers to a
 * {@link DefinitionPatternMatcherFactory} and recgnizes patterns through the
 * use of a {@link PatternRecognizer}.
 *
 * @param <T> The type of the customization key.
 * @since 2.2.0
 */
public class BasicPatternDefinitionResolver<T> extends AbstractPatternDefinitionResolver<T> {

    /**
     * The factory of pattern matchers.
     */
    private final DefinitionPatternMatcherFactory definitionPatternMatcherFactory;

    /**
     * The pattern recognizer.
     */
    private final PatternRecognizer patternRecognizer;

    /**
     * Constructor.
     *
     * @param definitionPatternMatcherFactory The definition pattern matcher factory.
     * @param patternRecognizer The pattern recognizer.
     */
    public BasicPatternDefinitionResolver(DefinitionPatternMatcherFactory definitionPatternMatcherFactory, PatternRecognizer patternRecognizer) {
        this.definitionPatternMatcherFactory = definitionPatternMatcherFactory;
        this.patternRecognizer = patternRecognizer;
    }

    /** {@inheritDoc} */
    @Override
    protected Map<String, Definition> addDefinitionsAsPatternMatchers(List<DefinitionPatternMatcher> matchers, Map<String, Definition> defsMap) {
        Set<String> excludedKeys = new LinkedHashSet<>();
        for (Map.Entry<String, Definition> de : defsMap.entrySet()) {
            String key = de.getKey();
            if (patternRecognizer.isPatternRecognized(key)) {
                matchers.add(definitionPatternMatcherFactory.createDefinitionPatternMatcher(key, de.getValue()));
            } else {
                excludedKeys.add(key);
            }
        }
        return PatternUtil.createExtractedMap(defsMap, excludedKeys);
    }
}
