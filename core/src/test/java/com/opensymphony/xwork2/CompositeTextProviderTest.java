package com.opensymphony.xwork2;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <code>CompositeTextProviderTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class CompositeTextProviderTest extends XWorkTestCase {


    private CompositeTextProvider textProvider = null;


    public void testGetText() throws Exception {
        // we should get the text from the 1st text provider
        assertEquals(textProvider.getText("name"), "1 name");
        assertEquals(textProvider.getText("age"), "1 age");
        assertEquals(textProvider.getText("dog"), "This is a dog");
        assertEquals(textProvider.getText("cat"), "This is a cat");
        assertEquals(textProvider.getText("car"), "This is a car");
        assertEquals(textProvider.getText("bike"), "This is a bike");
        assertEquals(textProvider.getText("someNonExistingKey"), "someNonExistingKey");
    }


    public void testGetTextWithDefaultValues() throws Exception {
        assertEquals(textProvider.getText("name", "some default name"), "1 name");
        assertEquals(textProvider.getText("age", "some default age"), "1 age");
        assertEquals(textProvider.getText("no_such_key", "default value"), "default value");
        assertEquals(textProvider.getText("dog", "some default dog"), "This is a dog");
        assertEquals(textProvider.getText("cat", "some default cat"), "This is a cat");
        assertEquals(textProvider.getText("car", "some default car"), "This is a car");
        assertEquals(textProvider.getText("bike", "some default bike"), "This is a bike");
    }


    public void testGetTextWithDefaultValuesAndArgs() throws Exception {
        assertEquals(textProvider.getText("goodnight", "say good night", "Adam"), "1 good night Adam");
        assertEquals(textProvider.getText("goodnight", "say good night", new String[] { "Adam" }), "1 good night Adam");
        assertEquals(textProvider.getText("goodnight", "say good night", new ArrayList<Object>() { {add("Adam");} }), "1 good night Adam");
        assertEquals(textProvider.getText("goodmorning", "say good morning", new String[] { "Jack", "Jim" }), "1 good morning Jack and Jim");
        assertEquals(textProvider.getText("goodmorning", "say good morning", new ArrayList<Object>() { { add("Jack"); add("Jim"); }}), "1 good morning Jack and Jim");
    }

    public void testHasKey() throws Exception {
        assertTrue(textProvider.hasKey("name"));
        assertTrue(textProvider.hasKey("age"));
        assertTrue(textProvider.hasKey("cat"));
        assertTrue(textProvider.hasKey("dog"));
        assertTrue(textProvider.hasKey("car"));
        assertTrue(textProvider.hasKey("bike"));
        assertTrue(textProvider.hasKey("goodnight"));
        assertTrue(textProvider.hasKey("goodmorning"));
        assertFalse(textProvider.hasKey("nosuchkey"));
    }

    public void testGetResourceBundleByName() throws Exception {
        assertNotNull(textProvider.getTexts("com.opensymphony.xwork2.validator.CompositeTextProviderTestResourceBundle1"));
        assertNotNull(textProvider.getTexts("com.opensymphony.xwork2.validator.CompositeTextProviderTestResourceBundle2"));
        assertNull(textProvider.getTexts("com.opensymphony.xwork2.validator.CompositeTextProviderTestResourceBundle3"));
    }

    public void testGetResourceBundle() throws Exception {
        assertNotNull(textProvider.getTexts());
        // we should get the first resource bundle where 'car' and 'bike' has a i18n msg
        assertNotNull(textProvider.getTexts().getString("car"));
        assertNotNull(textProvider.getTexts().getString("bike"));
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        textProvider = new CompositeTextProvider(new TextProvider[] {
                new TextProviderSupport(ResourceBundle.getBundle("com.opensymphony.xwork2.validator.CompositeTextProviderTestResourceBundle1"),
                        new LocaleProvider() {
                            public Locale getLocale() {
                                return Locale.ENGLISH;
                            }
                        }),
                new TextProviderSupport(ResourceBundle.getBundle("com.opensymphony.xwork2.validator.CompositeTextProviderTestResourceBundle2"),
                        new LocaleProvider() {
                            public Locale getLocale() {
                                return Locale.ENGLISH;
                            }
                        })

        });
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        textProvider = null;
    }
}
