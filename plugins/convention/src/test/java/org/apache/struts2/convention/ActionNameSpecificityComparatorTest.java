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
}
