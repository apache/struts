/*
 * $Id$
 *
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

package org.apache.struts2.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


/**
 * Test case for Struts Type Converter.
 *
 */
public class StrutsTypeConverterTest extends TestCase {

    /**
     * Typically form Struts -> html
     *
     * @throws Exception
     */
    public void testConvertToString() throws Exception {
        InternalStrutsTypeConverter strutsTypeConverter = new InternalStrutsTypeConverter();
        strutsTypeConverter.convertValue(new HashMap(), "", String.class);
        assertTrue(strutsTypeConverter.isConvertToString);
        assertEquals(strutsTypeConverter.objToBeConverted, "");
    }

    /**
     * Typically form html -> Struts
     *
     * @throws Exception
     */
    public void testConvertFromString() throws Exception {
        InternalStrutsTypeConverter strutsTypeConverter = new InternalStrutsTypeConverter();
        strutsTypeConverter.convertValue(new HashMap(), "12/12/1997", Date.class);
        assertTrue(strutsTypeConverter.isConvertFromString);
        assertTrue(strutsTypeConverter.objToBeConverted instanceof String[]);
        assertEquals(((String[])strutsTypeConverter.objToBeConverted).length, 1);
    }

    /**
     * Typically from html -> Struts (in array due to the nature of html, param
     * being able to have many values).
     *
     * @throws Exception
     */
    public void testConvertFromStringInArrayForm() throws Exception {
        InternalStrutsTypeConverter strutsTypeConverter = new InternalStrutsTypeConverter();
        strutsTypeConverter.convertValue(new HashMap(), new String[] { "12/12/1997", "1/1/1977" }, Date.class);
        assertTrue(strutsTypeConverter.isConvertFromString);
        assertTrue(strutsTypeConverter.objToBeConverted instanceof String[]);
        assertEquals(((String[])strutsTypeConverter.objToBeConverted).length, 2);
    }


    public void testFallbackConversion() throws Exception {
        InternalStrutsTypeConverter strutsTypeConverter = new InternalStrutsTypeConverter();
        strutsTypeConverter.convertValue(new HashMap(), new Object(), Date.class);
        assertTrue(strutsTypeConverter.fallbackConversion);
    }

    // === internal class for testing
    class InternalStrutsTypeConverter extends StrutsTypeConverter {

        boolean isConvertFromString = false;
        boolean isConvertToString = false;
        boolean fallbackConversion = false;

        Object objToBeConverted;

        public Object convertFromString(Map context, String[] values, Class toClass) {
            isConvertFromString = true;
            objToBeConverted = values;
            return null;
        }

        public String convertToString(Map context, Object o) {
            isConvertToString = true;
            objToBeConverted = o;
            return null;
        }

        protected Object performFallbackConversion(Map context, Object o, Class toClass) {
            fallbackConversion = true;
            return null;
        }

    }

}
