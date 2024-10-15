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

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTextArea;
import org.htmlunit.html.HtmlTextInput;
import org.junit.Assert;
import org.junit.Test;

public class UITagExampleTest {
    @Test
    public void testInputForm() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/tags/ui/example!input.action");

            final HtmlForm form = page.getFormByName("exampleSubmit");
            Assert.assertNotNull(form);

            final HtmlTextInput textField = form.getInputByName("name");
            final HtmlTextArea textField2 = form.getTextAreaByName("bio");
            final HtmlSelect textField3 = form.getSelectByName("favouriteColor");
            final HtmlCheckBoxInput textField4 = form.getInputByValue("Patrick");
            final HtmlCheckBoxInput textField41 = form.getInputByValue("Jason");
            final HtmlCheckBoxInput textField5 = form.getInputByName("legalAge");

            Assert.assertNotNull(textField);
            Assert.assertNotNull(textField2);
            Assert.assertNotNull(textField3);
            Assert.assertNotNull(textField4);
            Assert.assertNotNull(textField41);
            Assert.assertNotNull(textField5);

            textField.type("name");
            textField2.type("bio");
            textField3.setSelectedAttribute("Red", true);
            textField4.setChecked(true);
            textField41.setChecked(true);
            textField5.setChecked(true);

            final HtmlSubmitInput button = form.getInputByValue("Submit");
            final HtmlPage page2 = button.click();

            Assert.assertEquals("name", page2.getElementById("name").asNormalizedText());
            Assert.assertEquals("bio", page2.getElementById("bio").asNormalizedText());
            Assert.assertEquals("Red", page2.getElementById("favouriteColor").asNormalizedText());
            Assert.assertEquals("[Patrick, Jason]", page2.getElementById("friends").asNormalizedText());
            Assert.assertEquals("true", page2.getElementById("legalAge").asNormalizedText());
        }
    }
}
