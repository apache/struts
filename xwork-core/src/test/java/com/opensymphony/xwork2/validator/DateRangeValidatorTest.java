/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.validator.validators.DateRangeFieldValidator;

import java.util.*;


/**
 * DateRangeValidatorTest
 *
 * @author Jason Carreira
 *         Created Feb 9, 2003 1:25:42 AM
 */
public class DateRangeValidatorTest extends XWorkTestCase {

    private Locale origLocale;


    /**
     * Tests whether the date range validation is working. Should produce an validation error,
     * because the action config sets date to 12/20/2002 while expected range is Dec 22-25.
     */
    public void testRangeValidation() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, null);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
        Iterator it = errors.entrySet().iterator();

        List errorMessages = (List) errors.get("date");
        assertNotNull("Expected date range validation error message.", errorMessages);
        assertEquals(1, errorMessages.size());

        String errorMessage = (String) errorMessages.get(0);
        assertNotNull(errorMessage);
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
        origLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        loadConfigurationProviders(new MockConfigurationProvider());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Locale.setDefault(origLocale);
    }
}
