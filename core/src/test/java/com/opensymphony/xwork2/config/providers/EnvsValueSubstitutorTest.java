package com.opensymphony.xwork2.config.providers;

import org.apache.struts2.StrutsInternalTestCase;


public class EnvsValueSubstitutorTest extends StrutsInternalTestCase {

    public void testSimpleValue() throws Exception {
        // given
        String expected = System.getenv("USER");
        ValueSubstitutor substitutor = new EnvsValueSubstitutor();

        // when
        String actual = substitutor.substitute("${env.USER}");

        // then
        assertEquals(expected, actual);
    }

    public void testNoSubstitution() throws Exception {
        // given
        ValueSubstitutor substitutor = new EnvsValueSubstitutor();

        // when
        String actual = substitutor.substitute("val1");

        // then
        assertEquals("val1", actual);
    }
}