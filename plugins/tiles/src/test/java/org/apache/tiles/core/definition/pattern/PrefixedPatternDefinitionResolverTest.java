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

import org.apache.tiles.api.Attribute;
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
 * Tests {@link PrefixedPatternDefinitionResolver}.
 */
public class PrefixedPatternDefinitionResolverTest {

    /**
     * Test method for {@link PrefixedPatternDefinitionResolver#addDefinitionsAsPatternMatchers(List, Map)}.
     */
    @Test
    public void testAddDefinitionsAsPatternMatchers() {
        DefinitionPatternMatcherFactory factory1 = createMock(DefinitionPatternMatcherFactory.class);
        DefinitionPatternMatcherFactory factory2 = createMock(DefinitionPatternMatcherFactory.class);
        DefinitionPatternMatcher matcher1 = createMock(DefinitionPatternMatcher.class);
        DefinitionPatternMatcher matcher2 = createMock(DefinitionPatternMatcher.class);
        Definition definition1 = new Definition("DF1:definition1", null, null);
        Definition definition2 = new Definition("DF2:definition2", null, null);
        Definition definition3 = new Definition("noLanguageHere", null, null);

        expect(factory1.createDefinitionPatternMatcher("definition1", definition1)).andReturn(matcher1);
        expect(factory2.createDefinitionPatternMatcher("definition2", definition2)).andReturn(matcher2);

        replay(factory1, factory2, matcher1, matcher2);

        PrefixedPatternDefinitionResolver<Integer> resolver = new PrefixedPatternDefinitionResolver<>();
        resolver.registerDefinitionPatternMatcherFactory("DF1", factory1);
        resolver.registerDefinitionPatternMatcherFactory("DF2", factory2);
        List<DefinitionPatternMatcher> matchers = new ArrayList<>();
        Map<String, Definition> definitions = new LinkedHashMap<>();
        definitions.put("DF1:definition1", definition1);
        definitions.put("DF2:definition2", definition2);
        definitions.put("noLanguageHere", definition3);

        resolver.addDefinitionsAsPatternMatchers(matchers, definitions);

        assertEquals(2, matchers.size());
        assertEquals(matcher1, matchers.get(0));
        assertEquals(matcher2, matchers.get(1));

        verify(factory1, factory2, matcher1, matcher2);
    }
}
