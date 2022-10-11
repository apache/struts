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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.api.Definition;
import org.apache.tiles.api.Expression;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This resolver allows the use of multiple pattern matching languages. The
 * syntax of definition names must be <code>LANGUAGENAME:expression</code>.<br>
 * The different languages must be registered through the use of
 * {@link #registerDefinitionPatternMatcherFactory(String, DefinitionPatternMatcherFactory)}
 * method before using this resolver.
 *
 * @param <T> The type of the customization key.
 * @since 2.2.0
 */
public class PrefixedPatternDefinitionResolver<T> extends AbstractPatternDefinitionResolver<T> {

    /**
     * The logging object.
     */
    private static final Logger LOG = LogManager.getLogger(PrefixedPatternDefinitionResolver.class);

    /**
     * Matches languages names to the corresponding
     * {@link DefinitionPatternMatcherFactory}.
     */
    private final Map<String, DefinitionPatternMatcherFactory> language2matcherFactory;

    /**
     * Constructor.
     *
     * @since 2.2.0
     */
    public PrefixedPatternDefinitionResolver() {
        language2matcherFactory = new HashMap<>();
    }

    /**
     * Registers a {@link DefinitionPatternMatcherFactory} connected to a
     * particular language.
     *
     * @param language The name of the language.
     * @param factory The pattern matcher factory to register.
     * @since 2.2.0
     */
    public void registerDefinitionPatternMatcherFactory(String language,
            DefinitionPatternMatcherFactory factory) {
        language2matcherFactory.put(language, factory);
    }

    /** {@inheritDoc} */
    @Override
    protected Map<String, Definition> addDefinitionsAsPatternMatchers(List<DefinitionPatternMatcher> matchers, Map<String, Definition> defsMap) {
        Set<String> excludedKeys = new LinkedHashSet<String>();
        for (Map.Entry<String, Definition> entry : defsMap.entrySet()) {
            String key = entry.getKey();
            Expression expression = Expression
                    .createExpressionFromDescribedExpression(key);
            if (expression.getLanguage() != null) {
                DefinitionPatternMatcherFactory factory = language2matcherFactory
                        .get(expression.getLanguage());
                if (factory != null) {
                    DefinitionPatternMatcher matcher = factory
                            .createDefinitionPatternMatcher(expression
                                    .getExpression(), new Definition(entry
                                    .getValue()));
                    matchers.add(matcher);
                } else {
                    LOG.warn("Cannot find a DefinitionPatternMatcherFactory for expression '{}'", key);
                }
            } else {
                excludedKeys.add(key);
            }
        }
        return PatternUtil.createExtractedMap(defsMap, excludedKeys);
    }
}
