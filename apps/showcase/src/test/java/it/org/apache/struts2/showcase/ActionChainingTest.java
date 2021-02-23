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
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ActionChainingTest {
    @Test
    public void test() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/actionchaining/actionChain1!input");

            final String pageAsText = page.asText();
            Assert.assertTrue(pageAsText.contains("Action Chain 1 Property 1: Property Set In Action Chain 1"));
            Assert.assertTrue(pageAsText.contains("Action Chain 2 Property 1: Property Set in Action Chain 2"));
            Assert.assertTrue(pageAsText.contains("Action Chain 3 Property 1: Property set in Action Chain 3"));
        }
    }
}
