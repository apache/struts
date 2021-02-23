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
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class CRUDTest {
    @Test
    public void testCreate() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/skill/edit.action");

            final HtmlForm form = page.getForms().get(0);

            final HtmlTextInput textField = form.getInputByName("currentSkill.name");
            textField.type("somename1");
            final HtmlTextInput textField2 = form.getInputByName("currentSkill.description");
            textField2.type("somedescription1");

            final HtmlSubmitInput button = form.getInputByValue("Save");
            final HtmlPage page2 = button.click();
            final String page2Text = page2.asText();

            Assert.assertTrue(page2Text.contains("somename1"));
            Assert.assertTrue(page2Text.contains("somedescription1"));
        }
    }
}
