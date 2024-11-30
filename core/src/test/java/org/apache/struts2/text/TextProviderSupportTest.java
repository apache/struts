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

import org.apache.struts2.locale.LocaleProvider;
import org.apache.struts2.XWorkTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TextProviderSupportTest extends XWorkTestCase {

    private TextProviderSupport tp;
    private java.util.ResourceBundle rb;

    public void testHasKey() {
        assertTrue(tp.hasKey("hello"));
        assertFalse(tp.hasKey("not.in.bundle"));
    }

    public void testSimpleGetTexts() {
        assertEquals("Hello World", tp.getText("hello"));
        assertEquals("not.in.bundle", tp.getText("not.in.bundle"));

        assertEquals("Hello World", tp.getText("hello", "this is default"));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default"));
    }

    public void testGetTextsWithArgs() {
        assertEquals("Hello World", tp.getText("hello", "this is default", "from me")); // no args in bundle
        assertEquals("Hello World from me", tp.getText("hello.0", "this is default", "from me"));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", "from me"));
        assertEquals("this is default from me", tp.getText("not.in.bundle", "this is default {0}", "from me"));

        assertEquals("not.in.bundle", tp.getText("not.in.bundle"));
    }

    public void testGetTextsWithListArgs() {
        List<Object> args = new ArrayList<>();
        args.add("Santa");
        args.add("loud");
        assertEquals("Hello World", tp.getText("hello", "this is default", args)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", "this is default", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", "this is default", args));

        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", args));
        assertEquals("this is default Santa", tp.getText("not.in.bundle", "this is default {0}", args));
        assertEquals("this is default Santa speaking loud", tp.getText("not.in.bundle", "this is default {0} speaking {1}", args));

        assertEquals("Hello World", tp.getText("hello", args)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", args));

        assertEquals("not.in.bundle", tp.getText("not.in.bundle", args));
    }

    public void testGetTextsWithArrayArgs() {
        String[] args = {"Santa", "loud"};
        assertEquals("Hello World", tp.getText("hello", "this is default", args)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", "this is default", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", "this is default", args));

        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", args));
        assertEquals("this is default Santa", tp.getText("not.in.bundle", "this is default {0}", args));
        assertEquals("this is default Santa speaking loud", tp.getText("not.in.bundle", "this is default {0} speaking {1}", args));

        assertEquals("Hello World", tp.getText("hello", args)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", args));

        assertEquals("not.in.bundle", tp.getText("not.in.bundle", args));
    }

    public void testGetBundle() {
        assertEquals(rb, tp.getTexts());
        assertEquals(rb, tp.getTexts(TextProviderSupportTest.class.getName()));
    }

    public void testDifficultSymbols1() {
        String val = tp.getText("symbols1");
        assertEquals("\"=!@#$%^&*(){qwe}<>?:|}{[]\\';/.,<>`~'", val);
    }

    public void testDifficultSymbols2() {
        String val = tp.getText("symbols2");
        assertEquals("\"=!@#$%^&*()<>?:|[]\\';/.,<>`~'", val);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rb = ResourceBundle.getBundle(TextProviderSupportTest.class.getName(), Locale.ENGLISH);

        LocalizedTextProvider ltu = container.getInstance(LocalizedTextProvider.class);

        tp = new TextProviderSupport(rb, new LocaleProvider() {
            public Locale getLocale() {
                return Locale.ENGLISH;
            }

            @Override
            public boolean isValidLocaleString(String localeStr) {
                return true;
            }

            @Override
            public boolean isValidLocale(Locale locale) {
                return true;
            }
        }, ltu);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        rb = null;
        tp = null;
    }


}

