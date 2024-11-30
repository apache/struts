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
package it.org.apache.struts2.showcase;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSubmitInput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConventionTest {

    private WebClient webClient;

    @Before
    public void setUp() throws Exception {
        webClient = new WebClient();
    }

    @After
    public void tearDown() throws Exception {
        webClient.close();
    }

    @Test
    public void listPeople() throws Exception {
        HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/person/list-people.action");

        assertThat(page.asNormalizedText()).contains(
                "3\tAlexandru\tPapesco\n" +
                "4\tJay\tBoss\n" +
                "5\tRainer\tHermanos\n"
        );
    }

    @Test
    public void editPeople() throws Exception {
        HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/person/edit-person.action");
        HtmlForm form = page.getForms().get(0);

        form.getInputByName("persons(1).name").setValue("Lukasz");
        form.getInputByName("persons(1).lastName").setValue("Lenart");
        form.getInputByName("persons(2).name").setValue("Kusal");
        form.getInputByName("persons(2).lastName").setValue("Kithul-Godage");

        HtmlSubmitInput button = form.getInputByValue("Save all persons");
        page = button.click();

        assertThat(page.asNormalizedText()).contains(
                "1\tLukasz\tLenart\n" +
                "2\tKusal\tKithul-Godage\n"
        );
    }

    @Test
    public void createPerson() throws Exception {
        HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/person/new-person!input.action");
        HtmlForm form = page.getForms().get(0);

        form.getInputByName("person.name").type("Lukasz");
        form.getInputByName("person.lastName").type("Lenart");

        HtmlSubmitInput button = form.getInputByValue("Create person");
        page = button.click();

        assertThat(page.asNormalizedText()).contains("6\tLukasz\tLenart\n");
    }
}
