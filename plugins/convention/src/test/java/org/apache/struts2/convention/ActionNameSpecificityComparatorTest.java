/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.convention;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActionNameSpecificityComparatorTest {

    private final ActionNameSpecificityComparator comparator = new ActionNameSpecificityComparator();

    @Test
    public void moreLiteralPrefixIsMoreSpecific_ticketCase() {
        // equal wildcard count (1 each); "some/usefull/*" has more literal chars -> more specific
        assertTrue(comparator.compare("some/usefull/*", "some/*") < 0);
    }

    @Test
    public void fewerWildcardsIsMoreSpecific() {
        assertTrue(comparator.compare("a/*", "a/*/*") < 0);
    }

    @Test
    public void singleStarBeatsPathStarAtEqualLiterals() {
        // both "a/" literal (2 chars), one wildcard each; "a/*" (file) beats "a/**" (path)
        assertTrue(comparator.compare("a/*", "a/**") < 0);
    }

    @Test
    public void namedVariablesCountAsWildcards() {
        assertTrue(comparator.compare("some/usefull/{id}", "some/{id}") < 0);
    }

    @Test
    public void literalRanksBeforeAnyWildcard() {
        assertTrue(comparator.compare("some/list", "some/*") < 0);
    }

    @Test
    public void sortIsDeterministicRegardlessOfInputOrder() {
        List<String> expected = Arrays.asList("some/usefull/*", "some/*", "*");
        List<String> shuffled = new ArrayList<>(expected);
        Collections.shuffle(shuffled, new Random(42));
        shuffled.sort(comparator);
        assertEquals(expected, shuffled);
    }

    @Test
    public void naturalOrderBreaksTiesForEquallySpecificPatterns() {
        // equal on wildcard count, literal chars, and ** count -> alphabetical tiebreak
        assertTrue(comparator.compare("a/*", "b/*") < 0);
    }
}
