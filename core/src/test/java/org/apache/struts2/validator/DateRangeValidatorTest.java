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

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.interceptor.ValidationAware;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.providers.MockConfigurationProvider;
import org.apache.struts2.validator.validators.DateRangeFieldValidator;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.*;


/**
 * DateRangeValidatorTest
 *
 * @author Jason Carreira
 *         Created Feb 9, 2003 1:25:42 AM
 */
public class DateRangeValidatorTest extends XWorkTestCase {

    /**
     * Tests whether the date range validation is working. Should produce an validation error,
     * because the action config sets date to 12/20/2002 while expected range is Dec 22-25.
     */
    public void testRangeValidation() throws Exception {
        Calendar date = Calendar.getInstance();
        date.set(2002, Calendar.NOVEMBER, 20);
        HashMap<String, Object> params = new HashMap<>();
        params.put("date", date.getTime());
        ActionContext context = ActionContext.of().withParameters(HttpParameters.create(params).build())
                .withLocale(Locale.US);  // Force US Locale for date conversion tests on JDK9+

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, null, context.getContextMap());
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();

        List<String> errorMessages = errors.get("date");
        assertNotNull("Expected date range validation error message.", errorMessages);
        assertEquals(1, errorMessages.size());

        String errorMessage = errorMessages.get(0);
        assertEquals("The date must be between 12-22-2002 and 12-25-2002.", errorMessage);
    }

    public void testGetSetMinMax() throws Exception {
        DateRangeFieldValidator val = new DateRangeFieldValidator();
        Date max = new Date();
        val.setMax(max);
        assertEquals(max, val.getMax());

        Date min = new Date();
        val.setMin(min);
        assertEquals(min, val.getMin());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new MockConfigurationProvider());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
