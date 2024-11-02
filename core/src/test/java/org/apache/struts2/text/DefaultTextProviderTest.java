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

import java.util.*;

public class DefaultTextProviderTest extends XWorkTestCase {

    private DefaultTextProvider tp;

    public void testSimpleGetTexts() {
        assertEquals("Hello World", tp.getText("hello"));
        assertNull(tp.getText("not.in.bundle"));

        assertEquals("Hello World", tp.getText("hello", "this is default"));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default"));

        List<Object> nullList = null;
        assertEquals("Hello World", tp.getText("hello", nullList));

        String[] nullStrings = null;
        assertEquals("Hello World", tp.getText("hello", nullStrings));
    }

   public void testGetTextsWithArgs() {
        assertEquals("Hello World", tp.getText("hello", "this is default", "from me")); // no args in bundle
        assertEquals("Hello World from me", tp.getText("hello.0", "this is default", "from me"));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", "from me"));
        assertEquals("this is default from me", tp.getText("not.in.bundle", "this is default {0}", "from me"));

       assertNull(tp.getText("not.in.bundle"));
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

        assertNull(tp.getText("not.in.bundle", args));

        assertEquals("Hello World", tp.getText("hello", "this is default", (List) null));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", (List) null));
    }

    public void testGetTextsWithArrayArgs() {
        String[] args = { "Santa", "loud" };
        assertEquals("Hello World", tp.getText("hello", "this is default", args)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", "this is default", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", "this is default", args));

        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", args));
        assertEquals("this is default Santa", tp.getText("not.in.bundle", "this is default {0}", args));
        assertEquals("this is default Santa speaking loud", tp.getText("not.in.bundle", "this is default {0} speaking {1}", args));

        assertEquals("Hello World", tp.getText("hello", args)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", args));

        assertNull(tp.getText("not.in.bundle", args));

        assertEquals("Hello World", tp.getText("hello", "this is default", (String[]) null));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", (String[]) null));
    }

    public void testGetTextsWithListAndStack() {
        List<Object> args = new ArrayList<>();
        args.add("Santa");
        args.add("loud");
        assertEquals("Hello World", tp.getText("hello", "this is default", args, null)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", "this is default", args, null)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", "this is default", args, null));

        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", args, null));
        assertEquals("this is default Santa", tp.getText("not.in.bundle", "this is default {0}", args, null));
        assertEquals("this is default Santa speaking loud", tp.getText("not.in.bundle", "this is default {0} speaking {1}", args, null));
    }

    public void testGetTextsWithArrayAndStack() {
        String[] args = { "Santa", "loud" };
        assertEquals("Hello World", tp.getText("hello", "this is default", args, null)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", "this is default", args, null)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", "this is default", args, null));

        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", args, null));
        assertEquals("this is default Santa", tp.getText("not.in.bundle", "this is default {0}", args, null));
        assertEquals("this is default Santa speaking loud", tp.getText("not.in.bundle", "this is default {0} speaking {1}", args, null));
    }

    public void testGetBundle() {
        assertNull(tp.getTexts()); // always returns null

        ResourceBundle rb = ResourceBundle.getBundle(TextProviderSupportTest.class.getName(), Locale.CANADA);
        assertEquals(rb, tp.getTexts(TextProviderSupportTest.class.getName()));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ActionContext.of().withLocale(Locale.CANADA).bind();

        container.getInstance(LocalizedTextProvider.class).addDefaultResourceBundle(DefaultTextProviderTest.class.getName());

        tp = container.inject(DefaultTextProvider.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.clear();
        tp = null;
    }


}
