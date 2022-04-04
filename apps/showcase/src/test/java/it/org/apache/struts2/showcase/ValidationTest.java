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
package it.org.apache.struts2.showcase;

public class ValidationTest extends ITBaseTest {
    public void testFieldValidators() {
        beginAt("/validation/showFieldValidatorsExamples.action");

        setTextField("integerValidatorField", "nonint");
        setTextField("dateValidatorField", "nondate");
        setTextField("emailValidatorField", "!@@#%");
        setTextField("urlValidatorField", "!@@#%");
        setTextField("stringLengthValidatorField", "a");
        setTextField("regexValidatorField", "abc");
        setTextField("fieldExpressionValidatorField", "abc");

        submit();

        assertTextPresent("Invalid field value for field \"dateValidatorField\"");
        assertTextPresent("Invalid field value for field \"integerValidatorField\"");
        assertTextPresent("required and must be string");
        assertTextPresent("must be a valid email if supplied");
        assertTextPresent("must be a valid url if supplied ");
        assertTextPresent("must be a String of a specific greater than 1 less than 5 if specified ");
        assertTextPresent("regexValidatorField must match a regexp (.*\\.txt) if specified ");
        assertTextPresent("must be the same as the Required Validator Field if specified ");
    }
}
