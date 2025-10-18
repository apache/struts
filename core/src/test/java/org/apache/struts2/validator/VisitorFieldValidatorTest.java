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
package org.apache.struts2.validator;

import org.apache.struts2.action.Action;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.TestBean;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.conversion.impl.ConversionData;
import org.easymock.EasyMock;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * VisitorFieldValidatorTest
 *
 * @author Jason Carreira
 * Created Aug 4, 2003 1:26:01 AM
 */
public class VisitorFieldValidatorTest extends XWorkTestCase {

    protected VisitorValidatorTestAction action;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ActionContext.getContext().withLocale(Locale.US);  // Force US Locale for date conversion tests on JDK9+
        action = container.inject(VisitorValidatorTestAction.class);

        TestBean bean = action.getBean();
        Calendar cal = new GregorianCalendar(1900, Calendar.FEBRUARY, 1);
        bean.setBirth(cal.getTime());
        bean.setCount(-1);

        ActionConfig config = new ActionConfig.Builder("", "name", "").build();
        ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
        ActionProxy proxy = EasyMock.createNiceMock(ActionProxy.class);

        EasyMock.expect(invocation.getProxy()).andReturn(proxy).anyTimes();
        EasyMock.expect(invocation.getAction()).andReturn(action).anyTimes();
        EasyMock.expect(invocation.invoke()).andReturn(Action.SUCCESS).anyTimes();
        EasyMock.expect(proxy.getMethod()).andReturn("execute").anyTimes();
        EasyMock.expect(proxy.getConfig()).andReturn(config).anyTimes();


        EasyMock.replay(invocation);
        EasyMock.replay(proxy);

        ActionContext.getContext().withActionInvocation(invocation);
    }

    public void testArrayValidation() throws Exception {
        TestBean[] beanArray = action.getTestBeanArray();
        TestBean testBean = beanArray[0];
        testBean.setName("foo");
        validate("validateArray");

        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        //4 errors for the array, one for context
        assertEquals(5, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("testBeanArray[1].name"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));

        List<String> errors = fieldErrors.get("testBeanArray[1].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanArray[2].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanArray[3].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanArray[4].name");
        assertEquals(1, errors.size());
    }

    public void testBeanMessagesUseBeanResourceBundle() throws Exception {
        validate("beanMessageBundle");
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertTrue(fieldErrors.containsKey("bean.count"));

        List<String> beanCountMessages = fieldErrors.get("bean.count");
        assertEquals(1, beanCountMessages.size());

        String beanCountMessage = beanCountMessages.get(0);
        assertEquals("bean: TestBean model: Count must be between 1 and 100, current value is -1.", beanCountMessage);
    }

    public void testCollectionValidation() throws Exception {
        List<TestBean> testBeanList = action.getTestBeanList();
        TestBean testBean = testBeanList.get(0);
        testBean.setName("foo");
        validate("validateList");

        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        //4 for the list, 1 for context
        assertEquals(5, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("testBeanList[1].name"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));

        List<String> errors = fieldErrors.get("testBeanList[1].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanList[2].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanList[3].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanList[4].name");
        assertEquals(1, errors.size());
    }

    public void testDateValidation() throws Exception {
        action.setBirthday(Date.valueOf(LocalDate.now().minusYears(20)));
        action.setContext("birthday");

        validate("birthday");

        assertFalse(action.hasFieldErrors());
    }

    public void testContextIsOverriddenByContextParamInValidationXML() throws Exception {
        validate("visitorValidationAlias");
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertEquals(3, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("bean.count"));
        assertTrue(fieldErrors.containsKey("bean.name"));
        assertFalse(fieldErrors.containsKey("bean.birth"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));
    }

    public void testContextIsPropagated() throws Exception {
        validate("visitorValidation");
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertEquals(3, fieldErrors.size());
        assertFalse(fieldErrors.containsKey("bean.count"));
        assertTrue(fieldErrors.containsKey("bean.name"));
        assertTrue(fieldErrors.containsKey("bean.birth"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));
    }

    public void testVisitorChildValidation() throws Exception {
        validate("visitorChildValidation");
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertEquals(5, fieldErrors.size());
        assertFalse(fieldErrors.containsKey("bean.count"));
        assertTrue(fieldErrors.containsKey("bean.name"));
        assertTrue(fieldErrors.containsKey("bean.birth"));

        assertTrue(fieldErrors.containsKey("bean.child.name"));
        assertTrue(fieldErrors.containsKey("bean.child.birth"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));
    }

    public void testVisitorChildConversionValidation() throws Exception {
        //add conversion error
        Map<String, ConversionData> conversionErrors = new HashMap<>();
        conversionErrors.put("bean.child.count", new ConversionData("bar", Integer.class));
        ActionContext.getContext().withConversionErrors(conversionErrors);

        validate("visitorChildValidation");
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertEquals(6, fieldErrors.size());
        assertFalse(fieldErrors.containsKey("bean.count"));
        assertTrue(fieldErrors.containsKey("bean.name"));
        assertTrue(fieldErrors.containsKey("bean.birth"));

        assertTrue(fieldErrors.containsKey("bean.child.name"));
        assertTrue(fieldErrors.containsKey("bean.child.birth"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));

        //nested visitor conversion error
        assertTrue(fieldErrors.containsKey("bean.child.count"));
    }

    /**
     * Tests that conversion errors in indexed array properties trigger validation errors
     * with proper field names (e.g., testBeanArray[0].count, testBeanArray[2].count).
     * <p>
     * This test verifies recommendation #4 from the visitor pattern research:
     * "Test repopulation behavior specifically with indexed properties to confirm it works as expected."
     * <p>
     * Expected behavior:
     * - Conversion errors are detected for indexed array elements
     * - Field error keys use correct indexed notation
     * - repopulateField parameter causes the invalid value to be preserved
     */
    public void testArrayConversionErrorRepopulation() throws Exception {
        // Setup: Set names and valid count values for array elements
        TestBean[] beanArray = action.getTestBeanArray();
        beanArray[0].setName("Valid Name 0");
        // count[0] will have conversion error, so don't set a valid value
        beanArray[1].setName("Valid Name 1");
        beanArray[1].setCount(50); // Set valid count to avoid validation error
        beanArray[2].setName("Valid Name 2");
        // count[2] will have conversion error, so don't set a valid value
        beanArray[3].setName("Valid Name 3");
        beanArray[3].setCount(75); // Set valid count to avoid validation error
        beanArray[4].setName("Valid Name 4");
        // count[4] will have conversion error, so don't set a valid value

        // Add conversion errors for indexed array properties
        // Simulating invalid input like "abc" for integer field
        Map<String, ConversionData> conversionErrors = new HashMap<>();
        conversionErrors.put("testBeanArray[0].count", new ConversionData("abc", Integer.class));
        conversionErrors.put("testBeanArray[2].count", new ConversionData("xyz", Integer.class));
        conversionErrors.put("testBeanArray[4].count", new ConversionData("invalid", Integer.class));
        ActionContext.getContext().withConversionErrors(conversionErrors);

        // Execute validation with visitor pattern
        validate("validateArrayWithConversion");

        // Verify validation errors were created
        assertTrue("Action should have field errors", action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        // Verify conversion errors for indexed properties are properly detected
        assertTrue("Should have error for testBeanArray[0].count",
                fieldErrors.containsKey("testBeanArray[0].count"));
        assertTrue("Should have error for testBeanArray[2].count",
                fieldErrors.containsKey("testBeanArray[2].count"));
        assertTrue("Should have error for testBeanArray[4].count",
                fieldErrors.containsKey("testBeanArray[4].count"));

        // Verify error messages exist (may be multiple due to conversion + other validators)
        List<String> errors0 = fieldErrors.get("testBeanArray[0].count");
        assertNotNull("Should have error messages", errors0);
        assertTrue("Should have at least one error message", errors0.size() >= 1);

        List<String> errors2 = fieldErrors.get("testBeanArray[2].count");
        assertNotNull("Should have error messages", errors2);
        assertTrue("Should have at least one error message", errors2.size() >= 1);

        List<String> errors4 = fieldErrors.get("testBeanArray[4].count");
        assertNotNull("Should have error messages", errors4);
        assertTrue("Should have at least one error message", errors4.size() >= 1);

        // Elements without conversion errors should not have count field errors
        assertFalse("Should not have error for testBeanArray[1].count",
                fieldErrors.containsKey("testBeanArray[1].count"));
        assertFalse("Should not have error for testBeanArray[3].count",
                fieldErrors.containsKey("testBeanArray[3].count"));
    }

    /**
     * Tests that conversion errors in indexed list properties trigger validation errors
     * with proper field names (e.g., testBeanList[0].count, testBeanList[2].count).
     * <p>
     * This test verifies recommendation #4 from the visitor pattern research:
     * "Test repopulation behavior specifically with indexed properties to confirm it works as expected."
     * <p>
     * Expected behavior:
     * - Conversion errors are detected for indexed list elements
     * - Field error keys use correct indexed notation
     * - repopulateField parameter causes the invalid value to be preserved
     */
    public void testListConversionErrorRepopulation() throws Exception {
        // Setup: Set names and valid count values for list elements
        List<TestBean> testBeanList = action.getTestBeanList();
        testBeanList.get(0).setName("Valid Name 0");
        testBeanList.get(0).setCount(25); // Set valid count to avoid validation error
        testBeanList.get(1).setName("Valid Name 1");
        // count[1] will have conversion error, so don't set a valid value
        testBeanList.get(2).setName("Valid Name 2");
        testBeanList.get(2).setCount(50); // Set valid count to avoid validation error
        testBeanList.get(3).setName("Valid Name 3");
        // count[3] will have conversion error, so don't set a valid value
        testBeanList.get(4).setName("Valid Name 4");
        testBeanList.get(4).setCount(100); // Set valid count to avoid validation error

        // Add conversion errors for indexed list properties
        // Simulating invalid input like "not-a-number" for integer field
        Map<String, ConversionData> conversionErrors = new HashMap<>();
        conversionErrors.put("testBeanList[1].count", new ConversionData("not-a-number", Integer.class));
        conversionErrors.put("testBeanList[3].count", new ConversionData("also-invalid", Integer.class));
        ActionContext.getContext().withConversionErrors(conversionErrors);

        // Execute validation with visitor pattern
        validate("validateListWithConversion");

        // Verify validation errors were created
        assertTrue("Action should have field errors", action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        // Verify conversion errors for indexed list properties are properly detected
        assertTrue("Should have error for testBeanList[1].count",
                fieldErrors.containsKey("testBeanList[1].count"));
        assertTrue("Should have error for testBeanList[3].count",
                fieldErrors.containsKey("testBeanList[3].count"));

        // Verify error messages exist (may be multiple due to conversion + other validators)
        List<String> errors1 = fieldErrors.get("testBeanList[1].count");
        assertNotNull("Should have error messages", errors1);
        assertTrue("Should have at least one error message", errors1.size() >= 1);

        List<String> errors3 = fieldErrors.get("testBeanList[3].count");
        assertNotNull("Should have error messages", errors3);
        assertTrue("Should have at least one error message", errors3.size() >= 1);

        // Elements without conversion errors should not have count field errors
        assertFalse("Should not have error for testBeanList[0].count",
                fieldErrors.containsKey("testBeanList[0].count"));
        assertFalse("Should not have error for testBeanList[2].count",
                fieldErrors.containsKey("testBeanList[2].count"));
        assertFalse("Should not have error for testBeanList[4].count",
                fieldErrors.containsKey("testBeanList[4].count"));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.clear();
    }

    private void validate(String context) throws ValidationException {
        ActionContext.getContext().withActionName(context);
        container.getInstance(ActionValidatorManager.class).validate(action, context);
    }
}
