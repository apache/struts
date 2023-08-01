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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link AbstractPatternDefinitionResolver}.
 */
public class AbstractPatternDefinitionResolverTest {

    private DefinitionPatternMatcher firstMatcher;
    private DefinitionPatternMatcher thirdMatcher;

    private final PatternDefinitionResolver<Integer> resolver = new AbstractPatternDefinitionResolver<Integer>() {
        @Override
        protected Map<String, Definition> addDefinitionsAsPatternMatchers(List<DefinitionPatternMatcher> matchers, Map<String, Definition> defsMap) {
            if (defsMap.containsKey("first")) {
                matchers.add(firstMatcher);
            }
            if (defsMap.containsKey("third")) {
                matchers.add(thirdMatcher);
            }
            Map<String, Definition> retValue = new HashMap<>(defsMap);
            retValue.remove("first");
            retValue.remove("third");
            return retValue;
        }
    };

    /**
     * Test method for
     * {@link BasicPatternDefinitionResolver#resolveDefinition(String, Object)}.
     */
    @Test
    public void testResolveDefinition() {
        testResolveDefinitionImpl();
    }

    /**
     * Test method for
     * {@link BasicPatternDefinitionResolver#clearPatternPaths(Object)}.
     */
    @Test
    public void testClearPatternPaths() {
        testResolveDefinitionImpl();
        resolver.clearPatternPaths(1);
        resolver.clearPatternPaths(2);
        testResolveDefinitionImpl();
    }

    private void testResolveDefinitionImpl() {

        firstMatcher = createMock(DefinitionPatternMatcher.class);
        thirdMatcher = createMock(DefinitionPatternMatcher.class);

        Definition firstDefinition = new Definition("first", null, null);
        Definition secondDefinition = new Definition("second", null, null);
        Definition thirdDefinition = new Definition("third", null, null);

        Definition firstTransformedDefinition = new Definition("firstTransformed", null, null);
        Definition thirdTransformedDefinition = new Definition("thirdTransformed", null, null);

        expect(firstMatcher.createDefinition("firstTransformed")).andReturn(firstTransformedDefinition);
        expect(firstMatcher.createDefinition("secondTransformed")).andReturn(null);
        expect(firstMatcher.createDefinition("thirdTransformed")).andReturn(null);
        expect(thirdMatcher.createDefinition("thirdTransformed")).andReturn(thirdTransformedDefinition).times(2);
        expect(thirdMatcher.createDefinition("firstTransformed")).andReturn(null);
        expect(thirdMatcher.createDefinition("secondTransformed")).andReturn(null).times(2);

        replay(firstMatcher, thirdMatcher);

        Map<String, Definition> localeDefsMap = new LinkedHashMap<>();
        localeDefsMap.put("first", firstDefinition);
        localeDefsMap.put("second", secondDefinition);
        localeDefsMap.put("third", thirdDefinition);
        resolver.storeDefinitionPatterns(localeDefsMap, 1);
        localeDefsMap = new LinkedHashMap<>();
        localeDefsMap.put("third", thirdDefinition);
        resolver.storeDefinitionPatterns(localeDefsMap, 2);
        assertEquals(firstTransformedDefinition, resolver.resolveDefinition("firstTransformed", 1));
        assertNull(resolver.resolveDefinition("secondTransformed", 1));
        assertEquals(thirdTransformedDefinition, resolver.resolveDefinition("thirdTransformed", 1));
        assertNull(resolver.resolveDefinition("firstTransformed", 2));
        assertNull(resolver.resolveDefinition("secondTransformed", 2));
        assertEquals(thirdTransformedDefinition, resolver.resolveDefinition("thirdTransformed", 2));
        verify(firstMatcher, thirdMatcher);
    }
}
