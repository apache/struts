package com.opensymphony.xwork2.config.entities;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class AllowedMethodsTest extends TestCase {

    public void testLiteralMethods() throws Exception {
        // given
        String method = "myMethod";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(false, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed(method));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

    public void testWildcardMethodsWithNoSMI() throws Exception {
        // given
        String method = "my{1}";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(false, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("myMethod"));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

    public void testWildcardMethodsWithSMI() throws Exception {
        // given
        Set<String> literals = new HashSet<>();
        literals.add("my{1}");
        literals.add("myMethod");

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(true, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertFalse(allowedMethods.isAllowed("my{1}"));
        assertTrue(allowedMethods.isAllowed("myMethod"));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

    public void testWildcardWithStarMethodsWithNoSMI() throws Exception {
        // given
        String method = "cancel*Action*";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(false, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("cancelAction"));
        assertFalse(allowedMethods.isAllowed("startEvent"));
    }

    public void testWildcardWithStarMethodsWithSMI() throws Exception {
        // given
        String method = "cancel*";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(true, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("cancel*"));
        assertFalse(allowedMethods.isAllowed("cancelAction"));
        assertFalse(allowedMethods.isAllowed("startEvent"));
    }

    public void testRegexMethods() throws Exception {
        // given
        String method = "regex:my([a-zA-Z].*)";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        // when
        AllowedMethods allowedMethods = AllowedMethods.build(true, literals, ActionConfig.DEFAULT_METHOD_REGEX);

        // then
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("myMethod"));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

}
