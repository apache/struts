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

public class ConversionTest {
    @Test
    public void testList() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient
                    .getPage(ParameterUtils.getBaseUrl() + "/conversion/enterPersonsInfo.action");

            final HtmlForm form = page.getForms().get(0);

            form.getInputByName("persons[0].name").type("name0");
            form.getInputByName("persons[0].age").type("0");
            form.getInputByName("persons[1].name").type("name1");
            form.getInputByName("persons[1].age").type("1");
            form.getInputByName("persons[2].name").type("name2");
            form.getInputByName("persons[2].age").type("2");

            final HtmlSubmitInput button = form.getInputByValue("Submit");
            final HtmlPage page2 = button.click();
            final String page2Text = page2.asText();

            Assert.assertTrue(page2Text.contains("SET 0 Name: name0"));
            Assert.assertTrue(page2Text.contains("SET 0 Age: 0"));
            Assert.assertTrue(page2Text.contains("SET 1 Name: name1"));
            Assert.assertTrue(page2Text.contains("SET 1 Age: 1"));
            Assert.assertTrue(page2Text.contains("SET 2 Name: name2"));
            Assert.assertTrue(page2Text.contains("SET 2 Age: 2"));
        }
    }

    @Test
    public void testSet() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient
                    .getPage(ParameterUtils.getBaseUrl() + "/conversion/enterAddressesInfo.action");

            final HtmlForm form = page.getForms().get(0);

            form.getInputByName("addresses('id0').address").type("address0");
            form.getInputByName("addresses('id1').address").type("address1");
            form.getInputByName("addresses('id2').address").type("address2");

            final HtmlSubmitInput button = form.getInputByValue("Submit");
            final HtmlPage page2 = button.click();
            final String page2Text = page2.asText();

            Assert.assertTrue(page2Text.contains("id0 -> address0"));
            Assert.assertTrue(page2Text.contains("id1 -> address1"));
            Assert.assertTrue(page2Text.contains("id2 -> address2"));
        }
    }

    @Test
    public void testEnum() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient
                    .getPage(ParameterUtils.getBaseUrl() + "/conversion/enterOperationEnumInfo.action");

            final HtmlForm form = page.getForms().get(0);

            form.getInputByValue("ADD").setChecked(true);
            form.getInputByValue("MINUS").setChecked(true);

            final HtmlSubmitInput button = form.getInputByValue("Submit");
            final HtmlPage page2 = button.click();
            final String page2Text = page2.asText();

            Assert.assertTrue(page2Text.contains("ADD"));
            Assert.assertTrue(page2Text.contains("MINUS"));
        }
    }
}
