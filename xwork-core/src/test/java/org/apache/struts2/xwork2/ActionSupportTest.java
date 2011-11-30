/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Unit test for {@link ActionSupport}.
 *
 * @author Claus Ibsen
 */
public class ActionSupportTest extends XWorkTestCase {

    private ActionSupport as;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        as = new ActionSupport();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        as = null;
    }

    public void testNothingDoneOnActionSupport() throws Exception {
        assertEquals(false, as.hasErrors());

        assertNotNull(as.getActionErrors());
        assertEquals(0, as.getActionErrors().size());
        assertEquals(false, as.hasActionErrors());

        assertNotNull(as.getActionMessages());
        assertEquals(0, as.getActionMessages().size());
        assertEquals(false, as.hasActionMessages());

        assertNotNull(as.getFieldErrors());
        assertEquals(0, as.getFieldErrors().size());
        assertEquals(false, as.hasFieldErrors());

        assertNull(as.getText(null));

        try {
            as.pause(null);
        } catch (Exception e) {
            fail("Should not fail");
        }

        assertEquals(Action.INPUT, as.input());
        assertEquals(Action.SUCCESS, as.doDefault());
        assertEquals(Action.SUCCESS, as.execute());
        try {
            as.clone();
            fail("Failure expected for clone()");
        } catch (CloneNotSupportedException e) {
            // success!
        }


        assertNull(as.getText(null, (List) null));
        assertNull(as.getText(null, (String) null));
        assertNull(as.getText(null, (String[]) null));

        assertNull(as.getText(null, (String) null, (List) null));
        assertNull(as.getText(null, (String) null, (String) null));
        assertNull(as.getText(null, (String) null, (String[]) null));

        assertNull(as.getText(null, (String) null, (List) null, (ValueStack) null));
        assertNull(as.getText(null, (String) null, (String[]) null, (ValueStack) null));

        assertNotNull(as.getLocale());
        assertEquals(ActionContext.getContext().getLocale(), as.getLocale());

        assertNull(as.getTexts()); // can not find a bundle
        assertEquals("not.in.bundle", as.getText("not.in.bundle"));
    }

    public void testActionErrors() {
        assertEquals(false, as.hasActionErrors());
        assertEquals(0, as.getActionErrors().size());
        as.addActionError("Damm");
        assertEquals(1, as.getActionErrors().size());
        assertEquals("Damm", as.getActionErrors().iterator().next());
        assertEquals(true, as.hasActionErrors());
        assertEquals(true, as.hasErrors());

        as.clearErrorsAndMessages();
        assertEquals(false, as.hasActionErrors());
        assertEquals(false, as.hasErrors());
    }

    public void testActionMessages() {
        assertEquals(false, as.hasActionMessages());
        assertEquals(0, as.getActionMessages().size());
        as.addActionMessage("Killroy was here");
        assertEquals(1, as.getActionMessages().size());
        assertEquals("Killroy was here", as.getActionMessages().iterator().next());
        assertEquals(true, as.hasActionMessages());

        assertEquals(false, as.hasActionErrors()); // does not count as a error
        assertEquals(false, as.hasErrors()); // does not count as a error

        as.clearErrorsAndMessages();
        assertEquals(false, as.hasActionMessages());
        assertEquals(false, as.hasErrors());
    }

    public void testFieldErrors() {
        assertEquals(false, as.hasFieldErrors());
        assertEquals(0, as.getFieldErrors().size());
        as.addFieldError("username", "Admin is not allowed as username");
        List<String> errors = as.getFieldErrors().get("username");
        assertEquals(1, errors.size());
        assertEquals("Admin is not allowed as username", errors.get(0));

        assertEquals(true, as.hasFieldErrors());
        assertEquals(true, as.hasErrors());

        as.clearErrorsAndMessages();
        assertEquals(false, as.hasFieldErrors());
        assertEquals(false, as.hasErrors());
    }

    public void testDeprecated() throws Exception {
        assertNotNull(as.getErrorMessages());
        assertEquals(0, as.getErrorMessages().size());

        assertNotNull(as.getErrors());
        assertEquals(0, as.getErrors().size());
    }

    public void testLocale() {
        Locale defLocale = Locale.getDefault();
        ActionContext.getContext().setLocale(null);

        // will never return null, if no locale is set then default is returned
        assertNotNull(as.getLocale());
        assertEquals(defLocale, as.getLocale());

        ActionContext.getContext().setLocale(Locale.ITALY);
        assertEquals(Locale.ITALY, as.getLocale());

        ActionContext.setContext(new ActionContext(new HashMap<String, Object>()));
        assertEquals(defLocale, as.getLocale()); // ActionContext will create a new context, when it was set to null before
    }

    public void testMyActionSupport() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        assertEquals("santa", mas.doDefault());
        assertNotNull(mas.getTexts());

        assertEquals(false, mas.hasActionMessages());
        mas.validate();
        assertEquals(true, mas.hasActionMessages());
    }

    public void testSimpleGetTexts() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        checkGetTexts(mas);
    }

    public void testSimpleGetTextsWithInjectedTextProvider() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        TextProvider textProvider = container.getInstance(TextProvider.class, "system");

        assertNotNull(textProvider);

        container.inject(mas);

        checkGetTexts(mas);
    }

    private void checkGetTexts(MyActionSupport mas) {
        assertEquals("Hello World", mas.getText("hello"));
        assertEquals("not.in.bundle", mas.getText("not.in.bundle"));

        assertEquals("Hello World", mas.getText("hello", "this is default"));
        assertEquals("this is default", mas.getText("not.in.bundle", "this is default"));

        List nullList = null;
        assertEquals("Hello World", mas.getText("hello", nullList));

        String[] nullStrings = null;
        assertEquals("Hello World", mas.getText("hello", nullStrings));
    }

    public void testGetTextsWithArgs() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        assertEquals("Hello World", mas.getText("hello", "this is default", "from me")); // no args in bundle
        assertEquals("Hello World from me", mas.getText("hello.0", "this is default", "from me"));
        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", "from me"));
        assertEquals("this is default from me", mas.getText("not.in.bundle", "this is default {0}", "from me"));

        assertEquals("not.in.bundle", mas.getText("not.in.bundle"));
    }

    public void testGetTextsWithListArgs() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        List<Object> args = new ArrayList<Object>();
        args.add("Santa");
        args.add("loud");
        assertEquals("Hello World", mas.getText("hello", "this is default", args)); // no args in bundle
        assertEquals("Hello World Santa", mas.getText("hello.0", "this is default", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", "this is default", args));

        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", args));
        assertEquals("this is default Santa", mas.getText("not.in.bundle", "this is default {0}", args));
        assertEquals("this is default Santa speaking loud", mas.getText("not.in.bundle", "this is default {0} speaking {1}", args));

        assertEquals("Hello World", mas.getText("hello", args)); // no args in bundle
        assertEquals("Hello World Santa", mas.getText("hello.0", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", args));

        assertEquals("not.in.bundle", mas.getText("not.in.bundle", args));

        assertEquals("Hello World", mas.getText("hello", "this is default", (List) null));
        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", (List) null));
    }

    public void testGetTextsWithArrayArgs() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        String[] args = {"Santa", "loud"};
        assertEquals("Hello World", mas.getText("hello", "this is default", args)); // no args in bundle
        assertEquals("Hello World Santa", mas.getText("hello.0", "this is default", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", "this is default", args));

        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", args));
        assertEquals("this is default Santa", mas.getText("not.in.bundle", "this is default {0}", args));
        assertEquals("this is default Santa speaking loud", mas.getText("not.in.bundle", "this is default {0} speaking {1}", args));

        assertEquals("Hello World", mas.getText("hello", args)); // no args in bundle
        assertEquals("Hello World Santa", mas.getText("hello.0", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", args));

        assertEquals("not.in.bundle", mas.getText("not.in.bundle", args));

        assertEquals("Hello World", mas.getText("hello", "this is default", (String[]) null));
        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", (String[]) null));
    }

    public void testGetTextsWithListAndStack() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        ValueStack stack = ActionContext.getContext().getValueStack();

        List<Object> args = new ArrayList<Object>();
        args.add("Santa");
        args.add("loud");
        assertEquals("Hello World", mas.getText("hello", "this is default", args, stack)); // no args in bundle
        assertEquals("Hello World Santa", mas.getText("hello.0", "this is default", args, stack)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", "this is default", args, stack));

        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", args, stack));
        assertEquals("this is default Santa", mas.getText("not.in.bundle", "this is default {0}", args, stack));
        assertEquals("this is default Santa speaking loud", mas.getText("not.in.bundle", "this is default {0} speaking {1}", args, stack));
    }

    public void testGetTextsWithArrayAndStack() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        ValueStack stack = ActionContext.getContext().getValueStack();

        String[] args = {"Santa", "loud"};
        assertEquals("Hello World", mas.getText("hello", "this is default", args, stack)); // no args in bundle
        assertEquals("Hello World Santa", mas.getText("hello.0", "this is default", args, stack)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", mas.getText("hello.1", "this is default", args, stack));

        assertEquals("this is default", mas.getText("not.in.bundle", "this is default", args, stack));
        assertEquals("this is default Santa", mas.getText("not.in.bundle", "this is default {0}", args, stack));
        assertEquals("this is default Santa speaking loud", mas.getText("not.in.bundle", "this is default {0} speaking {1}", args, stack));
    }

    public void testGetBundle() throws Exception {
        ActionContext.getContext().setLocale(new Locale("da"));
        MyActionSupport mas = new MyActionSupport();

        ResourceBundle rb = ResourceBundle.getBundle(MyActionSupport.class.getName(), new Locale("da"));
        assertEquals(rb, mas.getTexts(MyActionSupport.class.getName()));
    }

    private class MyActionSupport extends ActionSupport {

        @Override
        public String doDefault() throws Exception {
            return "santa";
        }

        @Override
        public void validate() {
            super.validate(); // to have code coverage
            addActionMessage("validation was called");
        }
    }

}
