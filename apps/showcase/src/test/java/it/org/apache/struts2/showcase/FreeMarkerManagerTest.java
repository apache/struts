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
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class FreeMarkerManagerTest {
    @Test
    public void testCustomManager() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient
                    .getPage(ParameterUtils.getBaseUrl() + "/freemarker/customFreemarkerManagerDemo.action");

            final DomElement date = page.getElementById("todaysDate");
            Assert.assertNotNull(date);
            Assert.assertTrue(date.asText().length() > 0);

            final DomElement time = page.getElementById("timeNow");
            Assert.assertNotNull(time);
            Assert.assertTrue(time.asText().length() > 0);
        }
    }

    @Test
    public void testTags() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/freemarker/standardTags.action");

            final DomElement date = page.getElementById("test_name");
            Assert.assertNotNull(date);

            final DomElement time = page.getElementById("test");
            Assert.assertNotNull(time);
        }
    }
}
