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
package org.apache.struts2.text;

import org.apache.struts2.ActionContext;
import org.apache.struts2.XWorkTestCase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class CompositeTextProviderTest extends XWorkTestCase {

    private CompositeTextProvider textProvider = null;

    public void testGetText() {
        // we should get the text from the 1st text provider
        assertEquals(textProvider.getText("name"), "1 name");
        assertEquals(textProvider.getText("age"), "1 age");
        assertEquals(textProvider.getText("dog"), "This is a dog");
        assertEquals(textProvider.getText("cat"), "This is a cat");
        assertEquals(textProvider.getText("car"), "This is a car");
        assertEquals(textProvider.getText("bike"), "This is a bike");
        assertEquals(textProvider.getText("someNonExistingKey"), "someNonExistingKey");
    }

    public void testGetTextWithDefaultValues() {
        assertEquals(textProvider.getText("name", "some default name"), "1 name");
        assertEquals(textProvider.getText("age", "some default age"), "1 age");
        assertEquals(textProvider.getText("no_such_key", "default value"), "default value");
        assertEquals(textProvider.getText("dog", "some default dog"), "This is a dog");
        assertEquals(textProvider.getText("cat", "some default cat"), "This is a cat");
        assertEquals(textProvider.getText("car", "some default car"), "This is a car");
        assertEquals(textProvider.getText("bike", "some default bike"), "This is a bike");
    }

    public void testGetTextWithDefaultValuesAndArgs() {
        assertEquals(textProvider.getText("goodnight", "say good night", "Adam"), "1 good night Adam");
        assertEquals(textProvider.getText("goodnight", "say good night", new String[]{"Adam"}), "1 good night Adam");
        assertEquals(textProvider.getText("goodnight", "say good night", new ArrayList<>() {
            {
                add("Adam");
            }
        }), "1 good night Adam");
        assertEquals(textProvider.getText("goodmorning", "say good morning", new String[]{"Jack", "Jim"}), "1 good morning Jack and Jim");
        assertEquals(textProvider.getText("goodmorning", "say good morning", new ArrayList<>() {
            {
                add("Jack");
                add("Jim");
            }
        }), "1 good morning Jack and Jim");
    }

    public void testHasKey() {
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

    public void testGetResourceBundleByName() {
        assertNotNull(textProvider.getTexts("org.apache.struts2.validator.CompositeTextProviderTestResourceBundle1"));
        assertNotNull(textProvider.getTexts("org.apache.struts2.validator.CompositeTextProviderTestResourceBundle2"));
        assertNull(textProvider.getTexts("org.apache.struts2.validator.CompositeTextProviderTestResourceBundle3"));
    }

    public void testGetResourceBundle() {
        assertNotNull(textProvider.getTexts());
        // we should get the first resource bundle where 'car' and 'bike' has an i18n msg
        assertNotNull(textProvider.getTexts().getString("car"));
        assertNotNull(textProvider.getTexts().getString("bike"));
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        TextProviderFactory tpf = container.getInstance(TextProviderFactory.class);

        ActionContext.getContext().withLocale(Locale.ENGLISH);

        textProvider = new CompositeTextProvider(new TextProvider[]{
                tpf.createInstance(ResourceBundle.getBundle("org.apache.struts2.validator.CompositeTextProviderTestResourceBundle1")),
                tpf.createInstance(ResourceBundle.getBundle("org.apache.struts2.validator.CompositeTextProviderTestResourceBundle2"))
        });
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        textProvider = null;
    }
}
