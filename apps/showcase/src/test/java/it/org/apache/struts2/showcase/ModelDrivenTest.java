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

public class ModelDrivenTest {

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
    public void submit() throws Exception {
        HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/modelDriven/modelDriven.action");
        HtmlForm form = page.getForms().get(0);

        form.getInputByName("name").setValue("Johannes");
        form.getInputByName("age").setValue("21");
        form.getInputByName("bustedBefore").setChecked(true);
        form.getTextAreaByName("description").setText("Deals bugs");

        HtmlSubmitInput button = form.getInputByValue("Submit");
        page = button.click();

        assertThat(page.getElementById("name").asNormalizedText()).isEqualTo("Johannes");
        assertThat(page.getElementById("age").asNormalizedText()).isEqualTo("21");
        assertThat(page.getElementById("bustedBefore").asNormalizedText()).isEqualTo("true");
        assertThat(page.getElementById("description").asNormalizedText()).isEqualTo("Deals bugs");
    }
}
