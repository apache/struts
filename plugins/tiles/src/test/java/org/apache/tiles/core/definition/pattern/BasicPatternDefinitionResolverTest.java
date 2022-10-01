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
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link BasicPatternDefinitionResolver}.
 */
public class BasicPatternDefinitionResolverTest {

    /**
     * Test method for
     * {@link BasicPatternDefinitionResolver#addDefinitionsAsPatternMatchers(List, Map)}.
     */
    @Test
    public void testAddDefinitionsAsPatternMatchers() {
        DefinitionPatternMatcherFactory factory = createMock(DefinitionPatternMatcherFactory.class);
        PatternRecognizer recognizer = createMock(PatternRecognizer.class);
        DefinitionPatternMatcher firstMatcher = createMock(DefinitionPatternMatcher.class);
        DefinitionPatternMatcher thirdMatcher = createMock(DefinitionPatternMatcher.class);

        expect(recognizer.isPatternRecognized("first")).andReturn(true);
        expect(recognizer.isPatternRecognized("second")).andReturn(false);
        expect(recognizer.isPatternRecognized("third")).andReturn(true);

        Definition firstDefinition = new Definition("first", null, null);
        Definition secondDefinition = new Definition("second", null, null);
        Definition thirdDefinition = new Definition("third", null, null);

        expect(factory.createDefinitionPatternMatcher("first", firstDefinition))
            .andReturn(firstMatcher);
        expect(factory.createDefinitionPatternMatcher("third", thirdDefinition))
            .andReturn(thirdMatcher);

        replay(factory, recognizer, firstMatcher, thirdMatcher);
        BasicPatternDefinitionResolver<Integer> resolver = new BasicPatternDefinitionResolver<>(factory, recognizer);
        Map<String, Definition> localeDefsMap = new LinkedHashMap<>();
        localeDefsMap.put("first", firstDefinition);
        localeDefsMap.put("second", secondDefinition);
        localeDefsMap.put("third", thirdDefinition);
        List<DefinitionPatternMatcher> matchers = new ArrayList<>();
        resolver.addDefinitionsAsPatternMatchers(matchers, localeDefsMap);
        assertEquals(2, matchers.size());
        assertEquals(firstMatcher, matchers.get(0));
        assertEquals(thirdMatcher, matchers.get(1));
        verify(factory, recognizer, firstMatcher, thirdMatcher);
    }
}
