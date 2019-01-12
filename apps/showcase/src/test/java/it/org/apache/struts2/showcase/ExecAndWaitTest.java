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

public class ExecAndWaitTest {
    @Test
    public void testNodelay() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/wait/example1.action");

            final HtmlForm form = page.getForms().get(0);

            final HtmlTextInput textField = form.getInputByName("time");
            textField.type("7000");

            final HtmlSubmitInput button = form.getInputByValue("submit");
            final HtmlPage page2 = button.click();

            Assert.assertTrue(page2.asText().contains("We are processing your request. Please wait."));

            // hit it again
            final HtmlPage page3 = webClient
                    .getPage(ParameterUtils.getBaseUrl() + "/wait/longProcess1.action?time=1000");
            Assert.assertTrue(page3.asText().contains("We are processing your request. Please wait."));
        }
    }
}
