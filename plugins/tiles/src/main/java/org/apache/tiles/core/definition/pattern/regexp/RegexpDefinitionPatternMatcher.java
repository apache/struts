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

package org.apache.tiles.core.definition.pattern.regexp;

import org.apache.tiles.api.Definition;
import org.apache.tiles.core.definition.pattern.DefinitionPatternMatcher;
import org.apache.tiles.core.definition.pattern.PatternUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches regular expression patterns in definitions.
 *
 * @since 2.2.0
 */
public class RegexpDefinitionPatternMatcher implements DefinitionPatternMatcher {

    /**
     * The pattern to match.
     */
    private final Pattern pattern;

    /**
     * The definition to use as a basis.
     */
    private final Definition definition;

    /**
     * Constructor.
     *
     * @param pattern The pattern to use, in string form.
     * @param definition The definition to use as a basis.
     * @since 2.2.0
     */
    public RegexpDefinitionPatternMatcher(String pattern, Definition definition) {
        this.pattern = Pattern.compile(pattern);
        this.definition = definition;
    }

    /** {@inheritDoc} */
    public Definition createDefinition(String definitionName) {
        Definition retValue = null;
        Matcher matcher = pattern.matcher(definitionName);
        if (matcher.matches()) {
            int groupCount = matcher.groupCount() + 1;
            Object[] vars = new Object[groupCount];
            for (int i = 0; i < groupCount; i++) {
                vars[i] = matcher.group(i);
            }
            retValue = PatternUtil.replacePlaceholders(definition, definitionName, vars);
        }
        return retValue;
    }
}
