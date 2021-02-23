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

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class ValidationTest {
    @Test
    public void testFieldValidators() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient
                    .getPage(ParameterUtils.getBaseUrl() + "/validation/showFieldValidatorsExamples.action");

            final HtmlForm form = page.getForms().get(0);

            form.getInputByName("integerValidatorField").type("nonint");
            form.getInputByName("dateValidatorField").type("nondate");
            form.getInputByName("emailValidatorField").type("!@@#%");
            form.getInputByName("urlValidatorField").type("!@@#%");
            form.getInputByName("stringLengthValidatorField").type("a");
            form.getInputByName("regexValidatorField").type("abc");
            form.getInputByName("fieldExpressionValidatorField").type("abc");

            final HtmlSubmitInput button = form.getInputByValue("Submit");
            final HtmlPage page2 = button.click();
            final String page2Text = page2.asText();

            Assert.assertTrue(page2Text.contains("Invalid field value for field \"dateValidatorField\""));
            Assert.assertTrue(page2Text.contains("Invalid field value for field \"integerValidatorField\""));
            Assert.assertTrue(page2Text.contains("required and must be string"));
            Assert.assertTrue(page2Text.contains("must be a valid email if supplied"));
            Assert.assertTrue(page2Text.contains("must be a valid url if supplied"));
            Assert.assertTrue(
                    page2Text.contains("must be a String of a specific greater than 1 less than 5 if specified"));
            Assert.assertTrue(page2Text.contains("regexValidatorField must match a regexp (.*\\.txt) if specified"));
            Assert.assertTrue(page2Text.contains("must be the same as the Required Validator Field if specified"));
        }
    }
}
