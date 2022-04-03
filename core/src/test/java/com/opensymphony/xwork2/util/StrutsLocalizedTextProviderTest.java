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
package com.opensymphony.xwork2.util;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.test.ModelDrivenAction2;
import com.opensymphony.xwork2.test.TestBean2;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Unit test for {@link StrutsLocalizedTextProvider}.
 *
 * @author jcarreira
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class StrutsLocalizedTextProviderTest extends XWorkTestCase {

    private LocalizedTextProvider localizedTextProvider;

    public void testNpeWhenClassIsPrimitive() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new MyObject());
        String result = localizedTextProvider.findText(MyObject.class, "someObj.someI18nKey", Locale.ENGLISH, "default message", null, stack);
        System.out.println(result);
    }

    public static class MyObject extends ActionSupport {
        public boolean getSomeObj() {
            return true;
        }
    }

    public void testActionGetTextWithNullObject() throws Exception {
        MyAction action = new MyAction();
        container.inject(action);

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", action);
        ActionContext.getContext()
            .withActionInvocation((ActionInvocation) mockActionInvocation.proxy())
            .getValueStack().push(action);

        String message = action.getText("barObj.title");
        assertEquals("Title:", message);
    }


    public static class MyAction extends ActionSupport {
        private Bar testBean2;

        public Bar getBarObj() {
            return testBean2;
        }

        public void setBarObj(Bar testBean2) {
            this.testBean2 = testBean2;
        }
    }

    public void testActionGetText() throws Exception {
        ModelDrivenAction2 action = new ModelDrivenAction2();
        container.inject(action);

        TestBean2 bean = (TestBean2) action.getModel();
        Bar bar = new Bar();
        bean.setBarObj(bar);

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", action);
        ActionContext.getContext().withActionInvocation((ActionInvocation) mockActionInvocation.proxy());
        ActionContext.getContext().getValueStack().push(action);
        ActionContext.getContext().getValueStack().push(action.getModel());

        String message = action.getText("barObj.title");
        assertEquals("Title:", message);
    }

    public void testNullKeys() {
        localizedTextProvider.findText(this.getClass(), null, Locale.getDefault());
    }

    public void testActionGetTextXXX() throws Exception {
        localizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/util/FindMe");

        SimpleAction action = new SimpleAction();
        container.inject(action);

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", action);
        ActionContext.getContext()
            .withActionInvocation((ActionInvocation) mockActionInvocation.proxy())
            .getValueStack().push(action);

        String message = action.getText("bean.name");
        String foundBean2 = action.getText("bean2.name");

        assertEquals("Okay! You found Me!", foundBean2);
        assertEquals("Haha you cant FindMe!", message);
    }

    public void testAddDefaultResourceBundle() {
        String text = localizedTextProvider.findDefaultText("foo.range", Locale.getDefault());
        assertNull("Found message when it should not be available.", text);

        localizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/SimpleAction");

        String message = localizedTextProvider.findDefaultText("foo.range", Locale.US);
        assertEquals("Foo Range Message", message);
    }

    public void testAddDefaultResourceBundle2() throws Exception {
        localizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/SimpleAction");

        ActionProxy proxy = actionProxyFactory.createActionProxy("/", "packagelessAction", null, new HashMap<String, Object>(), false, true);
        proxy.execute();
    }

    public void testDefaultMessage() throws Exception {
        String message = localizedTextProvider.findDefaultText("xwork.error.action.execution", Locale.getDefault());
        assertEquals("Error during Action invocation", message);
    }

    public void testDefaultMessageOverride() throws Exception {
        String message = localizedTextProvider.findDefaultText("xwork.error.action.execution", Locale.getDefault());
        assertEquals("Error during Action invocation", message);

        localizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/test");

        message = localizedTextProvider.findDefaultText("xwork.error.action.execution", Locale.getDefault());
        assertEquals("Testing resource bundle override", message);
    }

    public void testFindTextInChildProperty() throws Exception {
        ModelDriven action = new ModelDrivenAction2();
        TestBean2 bean = (TestBean2) action.getModel();
        Bar bar = new Bar();
        bean.setBarObj(bar);

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("hashCode", 0);
        mockActionInvocation.expectAndReturn("getAction", action);
        ActionContext.getContext().withActionInvocation((ActionInvocation) mockActionInvocation.proxy());
        ActionContext.getContext().getValueStack().push(action);
        ActionContext.getContext().getValueStack().push(action.getModel());

        String message = localizedTextProvider.findText(ModelDrivenAction2.class, "invalid.fieldvalue.barObj.title", Locale.getDefault());
        assertEquals("Title is invalid!", message);
    }

    public void testFindTextInInterface() throws Exception {
        Action action = new ModelDrivenAction2();
        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", action);
        ActionContext.getContext().withActionInvocation((ActionInvocation) mockActionInvocation.proxy());

        String message = localizedTextProvider.findText(ModelDrivenAction2.class, "test.foo", Locale.getDefault());
        assertEquals("Foo!", message);
    }

    public void testFindTextInPackage() throws Exception {
        ModelDriven action = new ModelDrivenAction2();

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", action);
        ActionContext.getContext().withActionInvocation((ActionInvocation) mockActionInvocation.proxy());

        String message = localizedTextProvider.findText(ModelDrivenAction2.class, "package.properties", Locale.getDefault());
        assertEquals("It works!", message);
    }

    public void testParameterizedDefaultMessage() throws Exception {
        String message = localizedTextProvider.findDefaultText("xwork.exception.missing-action", Locale.getDefault(), new String[]{"AddUser"});
        assertEquals("There is no Action mapped for action name AddUser.", message);
    }

    public void testParameterizedDefaultMessageWithPackage() throws Exception {
        String message = localizedTextProvider.findDefaultText("xwork.exception.missing-package-action", Locale.getDefault(), new String[]{"blah", "AddUser"});
        assertEquals("There is no Action mapped for namespace blah and action name AddUser.", message);
    }

    public void testLocalizedDateFormatIsUsed() throws ParseException {
        localizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/util/LocalizedTextUtilTest");
        Date date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse("01/01/2015");
        Object[] params = new Object[]{date};
        String usDate = localizedTextProvider.findDefaultText("test.format.date", Locale.US, params);
        String germanDate = localizedTextProvider.findDefaultText("test.format.date", Locale.GERMANY, params);
        assertEquals(usDate, "1/1/15");
        assertEquals(germanDate, "01.01.15");
    }

    public void testXW377() {
        localizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/util/LocalizedTextUtilTest");

        String text = localizedTextProvider.findText(Bar.class, "xw377", ActionContext.getContext().getLocale(), "xw377", null, ActionContext.getContext().getValueStack());
        assertEquals("xw377", text); // should not log

        String text2 = localizedTextProvider.findText(StrutsLocalizedTextProviderTest.class, "notinbundle", ActionContext.getContext().getLocale(), "hello", null, ActionContext.getContext().getValueStack());
        assertEquals("hello", text2); // should log WARN

        String text3 = localizedTextProvider.findText(StrutsLocalizedTextProviderTest.class, "notinbundle.key", ActionContext.getContext().getLocale(), "notinbundle.key", null, ActionContext.getContext().getValueStack());
        assertEquals("notinbundle.key", text3); // should log WARN

        String text4 = localizedTextProvider.findText(StrutsLocalizedTextProviderTest.class, "xw377", ActionContext.getContext().getLocale(), "hello", null, ActionContext.getContext().getValueStack());
        assertEquals("xw377", text4); // should not log

        String text5 = localizedTextProvider.findText(StrutsLocalizedTextProviderTest.class, "username", ActionContext.getContext().getLocale(), null, null, ActionContext.getContext().getValueStack());
        assertEquals("Santa", text5); // should not log
    }

    public void testXW404() {
        // This tests will try to load bundles from the 3 locales but we only have files for France and Germany.
        // Before this fix loading the bundle for Germany failed since Italy have previously failed and thus the misses cache
        // contained a false entry

        ResourceBundle rbFrance = localizedTextProvider.findResourceBundle("com/opensymphony/xwork2/util/XW404", Locale.FRANCE);
        ResourceBundle rbItaly = localizedTextProvider.findResourceBundle("com/opensymphony/xwork2/util/XW404", Locale.ITALY);
        ResourceBundle rbGermany = localizedTextProvider.findResourceBundle("com/opensymphony/xwork2/util/XW404", Locale.GERMANY);

        assertNotNull(rbFrance);
        assertEquals("Bonjour", rbFrance.getString("hello"));

        assertNull(rbItaly);

        assertNotNull(rbGermany);
        assertEquals("Hallo", rbGermany.getString("hello"));
    }

    /**
     * Unit test to confirm expected behaviour of "clearing methods" provided to
     * StrutsLocalizedTextProvider (from AbstractLocalizedTextProvider).
     *
     * @since 2.6
     */
    public void testLocalizedTextProviderClearingMethods() {
        TestStrutsLocalizedTextProvider testStrutsLocalizedTextProvider = new TestStrutsLocalizedTextProvider();
        assertTrue("testStrutsLocalizedTextProvider not instance of AbstractLocalizedTextProvider ?",
            testStrutsLocalizedTextProvider instanceof AbstractLocalizedTextProvider);
        assertEquals("testStrutsLocalizedTextProvider starting default bundle map size not 0 before any retrievals ?",
            0, testStrutsLocalizedTextProvider.currentBundlesMapSize());

        // Access the two default bundles to populate their cache entries and test bundle map size.
        ResourceBundle tempBundle = testStrutsLocalizedTextProvider.findResourceBundle(
            TestStrutsLocalizedTextProvider.XWORK_MESSAGES_BUNDLE, Locale.ENGLISH);
        assertNotNull("XWORK_MESSAGES_BUNDLE retrieval null ?", tempBundle);
        tempBundle = testStrutsLocalizedTextProvider.findResourceBundle(
            TestStrutsLocalizedTextProvider.STRUTS_MESSAGES_BUNDLE, Locale.ENGLISH);
        assertNotNull("STRUTS_MESSAGES_BUNDLE retrieval null ?", tempBundle);
        assertEquals("testStrutsLocalizedTextProvider bundle map size not 2 after retrievals ?",
            2, testStrutsLocalizedTextProvider.currentBundlesMapSize());

        // Add and then access four test bundles to populate their cache entries and test bundle map size.
        testStrutsLocalizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/util/LocalizedTextUtilTest");
        testStrutsLocalizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/util/FindMe");
        testStrutsLocalizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/SimpleAction");
        testStrutsLocalizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/test");
        tempBundle = testStrutsLocalizedTextProvider.findResourceBundle(
            "com/opensymphony/xwork2/util/LocalizedTextUtilTest", Locale.ENGLISH);
        assertNotNull("com/opensymphony/xwork2/util/LocalizedTextUtilTest retrieval null ?", tempBundle);
        tempBundle = testStrutsLocalizedTextProvider.findResourceBundle(
            "com/opensymphony/xwork2/util/FindMe", Locale.ENGLISH);
        assertNotNull("com/opensymphony/xwork2/util/FindMe retrieval null ?", tempBundle);
        tempBundle = testStrutsLocalizedTextProvider.findResourceBundle(
            "com/opensymphony/xwork2/SimpleAction", Locale.ENGLISH);
        assertNotNull("com/opensymphony/xwork2/SimpleAction retrieval null ?", tempBundle);
        tempBundle = testStrutsLocalizedTextProvider.findResourceBundle(
            "com/opensymphony/xwork2/test", Locale.ENGLISH);
        assertNotNull("com/opensymphony/xwork2/test retrieval null ?", tempBundle);
        assertEquals("testStrutsLocalizedTextProvider bundle map size not 6 after retrievals ?",
            6, testStrutsLocalizedTextProvider.currentBundlesMapSize());

        // Expect the call to be ineffective due to deprecation and change to a "no-op" (but shouldn't throw an Exception or cause failure).
        testStrutsLocalizedTextProvider.callClearBundleNoLocale("com/opensymphony/xwork2/test");
        assertEquals("testStrutsLocalizedTextProvider bundle map size not 6 after non-locale clear call ?",
            6, testStrutsLocalizedTextProvider.currentBundlesMapSize());

        // Expect the call to function with bundle name + locale.  Remove all four of the non-default
        //   bundles and confirm the bundle map size changes.
        testStrutsLocalizedTextProvider.callClearBundleWithLocale("com/opensymphony/xwork2/test", Locale.ENGLISH);
        assertEquals("testStrutsLocalizedTextProvider bundle map size not 5 after locale clear call ?",
            5, testStrutsLocalizedTextProvider.currentBundlesMapSize());
        testStrutsLocalizedTextProvider.callClearBundleWithLocale("com/opensymphony/xwork2/SimpleAction", Locale.ENGLISH);
        assertEquals("testStrutsLocalizedTextProvider bundle map size not 4 after locale clear call ?",
            4, testStrutsLocalizedTextProvider.currentBundlesMapSize());
        testStrutsLocalizedTextProvider.callClearBundleWithLocale("com/opensymphony/xwork2/util/FindMe", Locale.ENGLISH);
        assertEquals("testStrutsLocalizedTextProvider bundle map size not 3 after locale clear call ?",
            3, testStrutsLocalizedTextProvider.currentBundlesMapSize());
        testStrutsLocalizedTextProvider.callClearBundleWithLocale("com/opensymphony/xwork2/util/LocalizedTextUtilTest", Locale.ENGLISH);
        assertEquals("testStrutsLocalizedTextProvider bundle map size not 2 after locale clear call ?",
            2, testStrutsLocalizedTextProvider.currentBundlesMapSize());

        // Confirm the missing bundles cache clearing method does not produce any Exceptions or failures.
        testStrutsLocalizedTextProvider.callClearMissingBundlesCache();
    }

    /**
     * Unit test to confirm the basic behaviour of bundle reload methods provided to
     * StrutsLocalizedTextProvider (from AbstractLocalizedTextProvider).
     *
     * @since 2.6
     */
    public void testLocalizedTextProviderReloadMethods() {
        TestStrutsLocalizedTextProvider testStrutsLocalizedTextProvider = new TestStrutsLocalizedTextProvider();
        assertTrue("testStrutsLocalizedTextProvider not instance of AbstractLocalizedTextProvider ?",
            testStrutsLocalizedTextProvider instanceof AbstractLocalizedTextProvider);
        assertEquals("testStrutsLocalizedTextProvider starting default bundle map size not 0 before any retrievals ?",
            0, testStrutsLocalizedTextProvider.currentBundlesMapSize());

        // Access the two default bundles to populate their cache entries and test bundle map size.
        ResourceBundle tempBundle = testStrutsLocalizedTextProvider.findResourceBundle(
            TestStrutsLocalizedTextProvider.XWORK_MESSAGES_BUNDLE, Locale.ENGLISH);
        assertNotNull("XWORK_MESSAGES_BUNDLE retrieval null ?", tempBundle);
        tempBundle = testStrutsLocalizedTextProvider.findResourceBundle(
            TestStrutsLocalizedTextProvider.STRUTS_MESSAGES_BUNDLE, Locale.ENGLISH);
        assertNotNull("STRUTS_MESSAGES_BUNDLE retrieval null ?", tempBundle);
        assertEquals("testStrutsLocalizedTextProvider bundle map size not 2 after retrievals ?",
            2, testStrutsLocalizedTextProvider.currentBundlesMapSize());

        // Force a bundle reload call for code coverage and to confirm it causes the bundle map to be emptied.
        assertNotNull("ActionContext is somehow null ?", ActionContext.getContext());
        boolean bundlesReloadedBeforeCall = testStrutsLocalizedTextProvider.getBundlesReloadedIndicatorValue();
        assertFalse("Bundles reload value true before forced reload ?", bundlesReloadedBeforeCall);
        testStrutsLocalizedTextProvider.callReloadBundlesForceReload();
        boolean bundlesReloadedAfterCall = testStrutsLocalizedTextProvider.getBundlesReloadedIndicatorValue();
        assertTrue("Bundles reload value false after forced reload ?", bundlesReloadedAfterCall);
        assertEquals("testStrutsLocalizedTextProvider bundle map size not 0 after reload (which should clear it) ?",
            0, testStrutsLocalizedTextProvider.currentBundlesMapSize());

        // Access the two default bundles again (after reload) to populate their cache entries and test bundle map size.
        tempBundle = testStrutsLocalizedTextProvider.findResourceBundle(
            TestStrutsLocalizedTextProvider.XWORK_MESSAGES_BUNDLE, Locale.ENGLISH);
        assertNotNull("XWORK_MESSAGES_BUNDLE retrieval null ?", tempBundle);
        tempBundle = testStrutsLocalizedTextProvider.findResourceBundle(
            TestStrutsLocalizedTextProvider.STRUTS_MESSAGES_BUNDLE, Locale.ENGLISH);
        assertNotNull("STRUTS_MESSAGES_BUNDLE retrieval null ?", tempBundle);
        assertEquals("testStrutsLocalizedTextProvider bundle map size not 2 after retrievals ?",
            2, testStrutsLocalizedTextProvider.currentBundlesMapSize());
    }

    /**
     * Test the {@link StrutsLocalizedTextProvider#searchDefaultBundlesFirst} flag behaviour for basic correctness.
     */
    public void testSetSearchDefaultBundlesFirst() {
        TestStrutsLocalizedTextProvider testStrutsLocalizedTextProvider = new TestStrutsLocalizedTextProvider();
        assertFalse("Default setSearchDefaultBundlesFirst state is not false ?", testStrutsLocalizedTextProvider.searchDefaultBundlesFirst);
        testStrutsLocalizedTextProvider.setSearchDefaultBundlesFirst(Boolean.TRUE.toString());
        assertTrue("The setSearchDefaultBundlesFirst state is not true after explicit set ?", testStrutsLocalizedTextProvider.searchDefaultBundlesFirst);
        testStrutsLocalizedTextProvider.setSearchDefaultBundlesFirst(Boolean.FALSE.toString());
        assertFalse("The setSearchDefaultBundlesFirst state is not false after explicit set ?", testStrutsLocalizedTextProvider.searchDefaultBundlesFirst);
        testStrutsLocalizedTextProvider.setSearchDefaultBundlesFirst("invalidstring");
        assertFalse("The setSearchDefaultBundlesFirst state is not false after set with invalid value ?", testStrutsLocalizedTextProvider.searchDefaultBundlesFirst);
    }

    /**
     * Test the {@link StrutsLocalizedTextProvider#getDefaultMessageWithAlternateKey(java.lang.String, java.lang.String, java.util.Locale, com.opensymphony.xwork2.util.ValueStack, java.lang.Object[], java.lang.String)}
     * method for basic correctness.
     */
    public void testGetDefaultMessageWithAlternateKey() {
        final String DEFAULT_MESSAGE = "This is the default message.";
        final String DEFAULT_MESSAGE_WITH_PARAMS = DEFAULT_MESSAGE + "  We provide a couple of parameter placeholders: -{0}- and -{1}- for fun.";
        final String param1 = "param1_String";
        final String param2 = "param2_String";
        final String[] paramArray = { param1, param2 };
        TestStrutsLocalizedTextProvider testStrutsLocalizedTextProvider = new TestStrutsLocalizedTextProvider();

        // Load some specific default bundles already provided and used by other tests within this module.
        testStrutsLocalizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/util/LocalizedTextUtilTest");
        testStrutsLocalizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/util/Bar");
        testStrutsLocalizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/util/FindMe");

        // Perform some standard checks on message retrieval using null or nonexistent keys and various default message combinations.
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        AbstractLocalizedTextProvider.GetDefaultMessageReturnArg getDefaultMessageReturnArg = testStrutsLocalizedTextProvider.getDefaultMessageWithAlternateKey(null, null, Locale.ENGLISH, valueStack, null, null);
        assertNull("GetDefaultMessageReturnArg result not null with null keys and null default message ?", getDefaultMessageReturnArg);
        getDefaultMessageReturnArg = testStrutsLocalizedTextProvider.getDefaultMessageWithAlternateKey("key_does_not_exist", "alternateKey_does_not_exist", Locale.ENGLISH, valueStack, null, null);
        assertNull("GetDefaultMessageReturnArg result not null with nonexistent keys and null default message ?", getDefaultMessageReturnArg);
        getDefaultMessageReturnArg = testStrutsLocalizedTextProvider.getDefaultMessageWithAlternateKey("key_does_not_exist", "alternateKey_does_not_exist", Locale.ENGLISH, valueStack, null, DEFAULT_MESSAGE);
        assertNotNull("GetDefaultMessageReturnArg result is null with nonexistent keys and non-null default message ?", getDefaultMessageReturnArg);
        assertFalse("GetDefaultMessageReturnArg result with nonexistent keys indicates message found in bundle ?", getDefaultMessageReturnArg.foundInBundle);
        assertEquals("GetDefaultMessageReturnArg result with nonexistent keys indicates message found in bundle ?", DEFAULT_MESSAGE, getDefaultMessageReturnArg.message);
        getDefaultMessageReturnArg = testStrutsLocalizedTextProvider.getDefaultMessageWithAlternateKey("key_does_not_exist", "alternateKey_does_not_exist", Locale.ENGLISH, valueStack, paramArray, DEFAULT_MESSAGE_WITH_PARAMS);
        assertNotNull("GetDefaultMessageReturnArg result is null with nonexistent keys and non-null default message ?", getDefaultMessageReturnArg);
        assertFalse("GetDefaultMessageReturnArg result with nonexistent keys indicates message found in bundle ?", getDefaultMessageReturnArg.foundInBundle);
        assertNotNull("GetDefaultMessageReturnArg result message is null ?", getDefaultMessageReturnArg.message);
        assertTrue("GetDefaultMessageReturnArg result message does not contain deafult message ?", getDefaultMessageReturnArg.message.contains(DEFAULT_MESSAGE));
        assertTrue("GetDefaultMessageReturnArg result message does not contain param1 ?", getDefaultMessageReturnArg.message.contains(param1));
        assertTrue("GetDefaultMessageReturnArg result message does not contain param2 ?", getDefaultMessageReturnArg.message.contains(param2));

        // Perform some checks where the initial key is null or does not exist in the default bundles, but the alternate key does.
        getDefaultMessageReturnArg = testStrutsLocalizedTextProvider.getDefaultMessageWithAlternateKey(null, "username", Locale.ENGLISH, valueStack, paramArray, null);
        assertNotNull("GetDefaultMessageReturnArg result is null with alternate key that exists ?", getDefaultMessageReturnArg);
        assertTrue("GetDefaultMessageReturnArg result with alternate key that exists indicates message not found in bundle ?", getDefaultMessageReturnArg.foundInBundle);
        assertTrue("GetDefaultMessageReturnArg result with alternate key that exists indicates message is null or empty ?", (getDefaultMessageReturnArg.message != null && !getDefaultMessageReturnArg.message.isEmpty()));
        assertEquals("GetDefaultMessageReturnArg result with alternate key 'username' not as expected ?", "Santa", getDefaultMessageReturnArg.message);
        getDefaultMessageReturnArg = testStrutsLocalizedTextProvider.getDefaultMessageWithAlternateKey("key_does_not_exist", "invalid.fieldvalue.title", Locale.ENGLISH, valueStack, paramArray, null);
        assertNotNull("GetDefaultMessageReturnArg result is null with alternate key that exists ?", getDefaultMessageReturnArg);
        assertTrue("GetDefaultMessageReturnArg result with alternate key that exists indicates message not found in bundle ?", getDefaultMessageReturnArg.foundInBundle);
        assertTrue("GetDefaultMessageReturnArg result with alternate key that exists indicates message is null or empty ?", (getDefaultMessageReturnArg.message != null && !getDefaultMessageReturnArg.message.isEmpty()));
        assertEquals("GetDefaultMessageReturnArg result with alternate key 'invalid.fieldvalue.title' not as expected ?", "Title is invalid!", getDefaultMessageReturnArg.message);

        // Perform some checks where the initial key exists, but the alternate key is null or nonexistent.
        getDefaultMessageReturnArg = testStrutsLocalizedTextProvider.getDefaultMessageWithAlternateKey("username", null, Locale.ENGLISH, valueStack, paramArray, null);
        assertNotNull("GetDefaultMessageReturnArg result is null with key that exists ?", getDefaultMessageReturnArg);
        assertTrue("GetDefaultMessageReturnArg result with key that exists indicates message not found in bundle ?", getDefaultMessageReturnArg.foundInBundle);
        assertTrue("GetDefaultMessageReturnArg result with key that exists indicates message is null or empty ?", (getDefaultMessageReturnArg.message != null && !getDefaultMessageReturnArg.message.isEmpty()));
        assertEquals("GetDefaultMessageReturnArg result with key 'username' not as expected ?", "Santa", getDefaultMessageReturnArg.message);
        getDefaultMessageReturnArg = testStrutsLocalizedTextProvider.getDefaultMessageWithAlternateKey("invalid.fieldvalue.title", "key_does_not_exist", Locale.ENGLISH, valueStack, paramArray, null);
        assertNotNull("GetDefaultMessageReturnArg result is null with key that exists ?", getDefaultMessageReturnArg);
        assertTrue("GetDefaultMessageReturnArg result with key that exists indicates message not found in bundle ?", getDefaultMessageReturnArg.foundInBundle);
        assertTrue("GetDefaultMessageReturnArg result with key that exists indicates message is null or empty ?", (getDefaultMessageReturnArg.message != null && !getDefaultMessageReturnArg.message.isEmpty()));
        assertEquals("GetDefaultMessageReturnArg result with key 'invalid.fieldvalue.title' not as expected ?", "Title is invalid!", getDefaultMessageReturnArg.message);

        // Perform some checks where the initial key exists, and the alternate key exists.  The result found for the initial key should be returned (not the alternate).
        getDefaultMessageReturnArg = testStrutsLocalizedTextProvider.getDefaultMessageWithAlternateKey("username", "invalid.fieldvalue.title", Locale.ENGLISH, valueStack, paramArray, null);
        assertNotNull("GetDefaultMessageReturnArg result is null with key that exists ?", getDefaultMessageReturnArg);
        assertTrue("GetDefaultMessageReturnArg result with key that exists indicates message not found in bundle ?", getDefaultMessageReturnArg.foundInBundle);
        assertTrue("GetDefaultMessageReturnArg result with key that exists indicates message is null or empty ?", (getDefaultMessageReturnArg.message != null && !getDefaultMessageReturnArg.message.isEmpty()));
        assertEquals("GetDefaultMessageReturnArg result with key 'username' not as expected ?", "Santa", getDefaultMessageReturnArg.message);
        getDefaultMessageReturnArg = testStrutsLocalizedTextProvider.getDefaultMessageWithAlternateKey("invalid.fieldvalue.title", "username", Locale.ENGLISH, valueStack, paramArray, null);
        assertNotNull("GetDefaultMessageReturnArg result is null with key that exists ?", getDefaultMessageReturnArg);
        assertTrue("GetDefaultMessageReturnArg result with key that exists indicates message not found in bundle ?", getDefaultMessageReturnArg.foundInBundle);
        assertTrue("GetDefaultMessageReturnArg result with key that exists indicates message is null or empty ?", (getDefaultMessageReturnArg.message != null && !getDefaultMessageReturnArg.message.isEmpty()));
        assertEquals("GetDefaultMessageReturnArg result with key 'invalid.fieldvalue.title' not as expected ?", "Title is invalid!", getDefaultMessageReturnArg.message);
    }

    /**
     * Test the {@link StrutsLocalizedTextProvider#findText(java.lang.Class, java.lang.String, java.util.Locale, java.lang.String, java.lang.Object[], com.opensymphony.xwork2.util.ValueStack) }
     * method for basic correctness.
     * 
     * It is the version of the method that will search the class hierarchy resource bundles first, unless {@link StrutsLocalizedTextProvider#searchDefaultBundlesFirst}
     * is true (in which case it will search the default resource bundles first).  No matter the flag setting, it should search until it finds a match, or fails to find
     * a match and returns the default message parameter that was passed.
     */
    public void testFindText_FullParameterSet_FirstParameterIsClass() {
        final String DEFAULT_MESSAGE = "This is the default message.";
        final String INDEXED_COLLECTION_ONLYGENERALFORM_EXISTS = "title.indexed[20]";  // Only title.indexed[*] exists.
        final String EXISTS_IN_DEFAULT_AND_CLASS_BUNDLES = "compare.sameproperty.differentbundles";  // Exists in LocalizedTextUtilTest properties (default bundles), and Bar properties (class bundles only).
        final String DEFAULT_MESSAGE_WITH_PARAMS = DEFAULT_MESSAGE + "  We provide a couple of parameter placeholders: -{0}- and -{1}- for fun.";
        final String param1 = "param1_String";
        final String param2 = "param2_String";
        final String[] paramArray = { param1, param2 };
        TestStrutsLocalizedTextProvider testStrutsLocalizedTextProvider = new TestStrutsLocalizedTextProvider();

        // Load some specific default bundles already provided and used by other tests within this module.
        // Note: Intentionally not including the Bar properties file as a default bundle so that we can test retrievals of items that are only available via the class
        //       or the default bundles.
        testStrutsLocalizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/util/LocalizedTextUtilTest");
        testStrutsLocalizedTextProvider.addDefaultResourceBundle("com/opensymphony/xwork2/util/FindMe");

        // Perform some standard checks on message retrieval both for correctness checks and code coverage (such as the NONEXISTENT_INDEXED_COLLECTION,
        // which exercises the indexed name logic in findText())
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        Bar bar = new Bar();
        assertFalse("Initial setSearchDefaultBundlesFirst state is not false ?", testStrutsLocalizedTextProvider.searchDefaultBundlesFirst);
        String messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "title", Locale.ENGLISH, DEFAULT_MESSAGE, paramArray, valueStack);
        assertEquals("Bar class title property lookup result does not match expectations (missing or different) ?", "Title:", messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), INDEXED_COLLECTION_ONLYGENERALFORM_EXISTS, Locale.ENGLISH, DEFAULT_MESSAGE, paramArray, valueStack);
        assertEquals("Bar class general indexed collection lookup result does not match expectations (missing or different) ?", "Indexed title text for test!", messageResult);

        // Test lookup with search default bundles first set true.  For properties that exist only with the class bundle, there should be no change.
        // Repeat the tests with properties only in the class bundle.
        testStrutsLocalizedTextProvider.setSearchDefaultBundlesFirst(Boolean.TRUE.toString());
        assertTrue("Updated setSearchDefaultBundlesFirst state is not true ?", testStrutsLocalizedTextProvider.searchDefaultBundlesFirst);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "title", Locale.ENGLISH, DEFAULT_MESSAGE, paramArray, valueStack);
        assertEquals("Bar class title property lookup result does not match expectations (missing or different) ?", "Title:", messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), INDEXED_COLLECTION_ONLYGENERALFORM_EXISTS, Locale.ENGLISH, DEFAULT_MESSAGE, paramArray, valueStack);
        assertEquals("Bar class general indexed collection lookup result does not match expectations (missing or different) ?", "Indexed title text for test!", messageResult);

        // Test with a property that is in both the class bundle and default bundles, with search default bundles first true.
        // The property match from the default bundles should be returned.
        testStrutsLocalizedTextProvider.setSearchDefaultBundlesFirst(Boolean.TRUE.toString());
        assertTrue("Updated setSearchDefaultBundlesFirst state is not true ?", testStrutsLocalizedTextProvider.searchDefaultBundlesFirst);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), EXISTS_IN_DEFAULT_AND_CLASS_BUNDLES, Locale.ENGLISH, DEFAULT_MESSAGE, paramArray, valueStack);
        assertEquals("Result is not the property from the default bundles ?", "This is the value in the LocalizedTextUtilTest properties!", messageResult);

        // Test with a property that is in both the class bundle and default bundles, with search default bundles first false.
        // The property match from the Bar class bundle should be returned.
        testStrutsLocalizedTextProvider.setSearchDefaultBundlesFirst(Boolean.FALSE.toString());
        assertFalse("Updated setSearchDefaultBundlesFirst state is not false ?", testStrutsLocalizedTextProvider.searchDefaultBundlesFirst);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), EXISTS_IN_DEFAULT_AND_CLASS_BUNDLES, Locale.ENGLISH, DEFAULT_MESSAGE, paramArray, valueStack);
        assertEquals("Result is not the property from the Bar bundle ?", "This is the value in the Bar properties!", messageResult);

        // Test with some different properties (including null and nonexistent ones), with search default bundles first false.
        testStrutsLocalizedTextProvider.setSearchDefaultBundlesFirst(Boolean.FALSE.toString());
        assertFalse("Updated setSearchDefaultBundlesFirst state is not false ?", testStrutsLocalizedTextProvider.searchDefaultBundlesFirst);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), null, Locale.ENGLISH, null, paramArray, valueStack);
        assertNull("Result with null key and null default message is not null ?", messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "key_does_not_exist", Locale.ENGLISH, null, paramArray, valueStack);
        assertNull("Result with nonexistent key and null default message is not null ?", messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), null, Locale.ENGLISH, DEFAULT_MESSAGE, paramArray, valueStack);
        assertEquals("Result with null key and non-null default message is not the default message ?", DEFAULT_MESSAGE, messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "key_does_not_exist", Locale.ENGLISH, DEFAULT_MESSAGE, paramArray, valueStack);
        assertEquals("Result with nonexistent key and non-null default message is not the default message ?", DEFAULT_MESSAGE, messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "key_does_not_exist", Locale.ENGLISH, DEFAULT_MESSAGE_WITH_PARAMS, paramArray, valueStack);
        assertNotNull("Result with nonexistent key and non-null default message is null ?", messageResult);
        assertTrue("Result with parameterized default message does not contain deafult message ?", messageResult.contains(DEFAULT_MESSAGE));
        assertTrue("Result with parameterized default message does not contain param1 ?", messageResult.contains(param1));
        assertTrue("Result with parameterized default message does not contain param2 ?", messageResult.contains(param2));
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "username", Locale.ENGLISH, null, paramArray, valueStack);
        assertEquals("Result of username lookup not as expected ?", "Santa", messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "bean.name", Locale.ENGLISH, null, paramArray, valueStack);
        assertEquals("Result of bean.name lookup not as expected ?", "Haha you cant FindMe!", messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "bean2.name", Locale.ENGLISH, null, paramArray, valueStack);
        assertEquals("Result of bean2.name lookup not as expected ?", "Okay! You found Me!", messageResult);

        // Test with some different properties (including null and nonexistent ones), with search default bundles first true.
        testStrutsLocalizedTextProvider.setSearchDefaultBundlesFirst(Boolean.TRUE.toString());
        assertTrue("Updated setSearchDefaultBundlesFirst state is not true ?", testStrutsLocalizedTextProvider.searchDefaultBundlesFirst);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), null, Locale.ENGLISH, null, paramArray, valueStack);
        assertNull("Result with null key and null default message is not null ?", messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "key_does_not_exist", Locale.ENGLISH, null, paramArray, valueStack);
        assertNull("Result with nonexistent key and null default message is not null ?", messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), null, Locale.ENGLISH, DEFAULT_MESSAGE, paramArray, valueStack);
        assertEquals("Result with null key and non-null default message is not the default message ?", DEFAULT_MESSAGE, messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "key_does_not_exist", Locale.ENGLISH, DEFAULT_MESSAGE, paramArray, valueStack);
        assertEquals("Result with nonexistent key and non-null default message is not the default message ?", DEFAULT_MESSAGE, messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "key_does_not_exist", Locale.ENGLISH, DEFAULT_MESSAGE_WITH_PARAMS, paramArray, valueStack);
        assertNotNull("Result with nonexistent key and non-null default message is null ?", messageResult);
        assertTrue("Result with parameterized default message does not contain deafult message ?", messageResult.contains(DEFAULT_MESSAGE));
        assertTrue("Result with parameterized default message does not contain param1 ?", messageResult.contains(param1));
        assertTrue("Result with parameterized default message does not contain param2 ?", messageResult.contains(param2));
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "username", Locale.ENGLISH, null, paramArray, valueStack);
        assertEquals("Result of username lookup not as expected ?", "Santa", messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "bean.name", Locale.ENGLISH, null, paramArray, valueStack);
        assertEquals("Result of bean.name lookup not as expected ?", "Haha you cant FindMe!", messageResult);
        messageResult = testStrutsLocalizedTextProvider.findText(bar.getClass(), "bean2.name", Locale.ENGLISH, null, paramArray, valueStack);
        assertEquals("Result of bean2.name lookup not as expected ?", "Okay! You found Me!", messageResult);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);

        localizedTextProvider = container.getInstance(LocalizedTextProvider.class);

        ActionContext.getContext().withLocale(Locale.US);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        localizedTextProvider = null;
    }

    /**
     * Basic test class to allow specific testing of StrutsLocalizedTextProvider.
     *
     * @since 2.6
     */
    class TestStrutsLocalizedTextProvider extends StrutsLocalizedTextProvider {

        /**
         * Some test correctness depends on this {@link #RELOADED} value matching that of the private ancestor
         * field {@link AbstractLocalizedTextProvider#RELOADED}.  If the ancestor field value changes, ensure this
         * field's value is updated to match it exactly.
         */
        private static final String RELOADED = "com.opensymphony.xwork2.util.LocalizedTextProvider.reloaded";

        public void callClearBundleNoLocale(String bundleName) {
            super.clearBundle(bundleName);
        }

        public void callClearBundleWithLocale(String bundleName, Locale locale) {
            super.clearBundle(bundleName, locale);
        }

        public void callClearMissingBundlesCache() {
            super.clearMissingBundlesCache();
        }

        public int currentBundlesMapSize() {
            return super.bundlesMap.size();
        }

        /**
         * Attempt to force the resource bundles to be reloaded, even if configuration would otherwise prevent it.
         * It will preserve the current reloadBundles state, attempt to force a reload and then restore the 
         * original reloadBundles value.
         */
        public void callReloadBundlesForceReload() {
            final boolean originalReloadState = super.reloadBundles;
            try {
                super.setReloadBundles(Boolean.TRUE.toString());
                super.reloadBundles();
            } finally {
                super.setReloadBundles(Boolean.toString(originalReloadState));
            }
        }

        /**
         * Returns the value of the resource bundles reloaded state from the context, provided that one was 
         * previously set.  If no value is found, the result will be false (same as if bundles had not been reloaded).
         * 
         * @return true if resource bundles reloaded indicator is true, false otherwise (including if value was never set).
         */
        public boolean getBundlesReloadedIndicatorValue() {
            final ActionContext actionContext = ActionContext.getContext();
            final Object reloadedObject = actionContext.get(RELOADED);
            return ((reloadedObject instanceof Boolean) ? ((Boolean) reloadedObject).booleanValue() : false);
        }
    }
}
