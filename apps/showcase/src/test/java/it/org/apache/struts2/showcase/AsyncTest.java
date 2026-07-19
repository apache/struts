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
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTextInput;
import org.junit.Assert;
import org.junit.Test;

public class AsyncTest {
    @Test
    public void testChatRoom() throws Exception {
        try (final WebClient webClient = ParameterUtils.createWebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/async/index.html");

            final HtmlForm form = page.getForms().get(0);

            final HtmlTextInput textField = form.getInputByName("msg");
            textField.type("hello");

            final HtmlSubmitInput button = form.getInputByValue("Send");
            final HtmlPage page2 = button.click();

            final DomElement msgs = page2.getElementById("msgs");

            // The message is delivered asynchronously via a server-push long-poll; poll for the
            // result rather than relying on a single fixed delay (slower/newer JVMs need more time).
            for (int i = 0; i < 30 && !"hello".equals(msgs.asNormalizedText()); i++) {
                webClient.waitForBackgroundJavaScript(1000);
            }

            Assert.assertEquals("hello", msgs.asNormalizedText());
        }
    }
}
