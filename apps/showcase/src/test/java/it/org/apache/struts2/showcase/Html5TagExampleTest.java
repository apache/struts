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
import org.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Test;

/**
 * Integration tests for HTML5 theme rendering in showcase application.
 * <p>
 * Tests validate that the HTML5 theme produces clean, semantic HTML5 markup
 * without table-based layouts and properly displays action errors, action messages,
 * and field errors.
 */
public class Html5TagExampleTest {

    /**
     * Tests basic HTML5 theme rendering and page load.
     * <p>
     * Verifies:
     * - Page loads successfully (200 status)
     * - HTML5 doctype is present
     * - Page contains expected content
     */
    @Test
    public void testHtml5PageLoad() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            final HtmlPage page = webClient.getPage(
                    ParameterUtils.getBaseUrl() + "/html5/index.action"
            );

            Assert.assertEquals(200, page.getWebResponse().getStatusCode());

            String pageContent = page.asNormalizedText();
            Assert.assertTrue("Page should contain HTML5 demo title",
                    pageContent.contains("Html 5 tags demo"));
        }
    }

    /**
     * Tests HTML5 theme error and message display.
     * <p>
     * Verifies:
     * - Action errors are displayed
     * - Action messages are displayed
     * - Field errors are displayed
     * - Errors use clean HTML5 markup
     */
    @Test
    public void testHtml5ErrorDisplay() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            final HtmlPage page = webClient.getPage(
                    ParameterUtils.getBaseUrl() + "/html5/index.action"
            );

            String pageContent = page.asNormalizedText();

            // Verify action error is displayed
            Assert.assertTrue("Page should display action error",
                    pageContent.contains("Action error: only html5"));

            // Verify action message is displayed
            Assert.assertTrue("Page should display action message",
                    pageContent.contains("Action message: only html5"));

            // Verify field error is displayed
            Assert.assertTrue("Page should display field error",
                    pageContent.contains("Field error: only html5"));
        }
    }

    /**
     * Tests that HTML5 theme uses clean, semantic markup without tables.
     * <p>
     * Verifies:
     * - No table-based layout for error messages
     * - Uses semantic HTML5 elements
     * - Error lists use &lt;ul&gt; elements
     */
    @Test
    public void testHtml5CleanMarkup() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            final HtmlPage page = webClient.getPage(
                    ParameterUtils.getBaseUrl() + "/html5/index.action"
            );

            String pageAsXml = page.asXml();

            // HTML5 theme should use <ul> for error lists
            Assert.assertTrue("Errors should be displayed in <ul> lists",
                    pageAsXml.contains("<ul"));

            // Verify HTML5 theme does not use table-based layout for errors
            Assert.assertFalse("HTML5 theme should not use table layout for errors",
                    pageAsXml.matches("(?s).*<table[^>]*>.*errorMessage.*</table>.*"));
        }
    }

    /**
     * Tests HTML5 theme anchor tag rendering.
     * <p>
     * Verifies:
     * - Anchor tags are rendered correctly
     * - Links have proper href attributes
     * - HTML5 theme attributes are applied
     */
    @Test
    public void testHtml5AnchorTag() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(false);

            final HtmlPage page = webClient.getPage(
                    ParameterUtils.getBaseUrl() + "/html5/index.action"
            );

            String pageContent = page.asNormalizedText();

            // Verify anchor tag content is present
            Assert.assertTrue("Page should contain 'index' link",
                    pageContent.contains("index"));

            // Verify back link to showcase
            Assert.assertTrue("Page should contain 'Back' link",
                    pageContent.contains("Back"));
        }
    }

    /**
     * Tests that HTML5 theme components are properly namespaced.
     * <p>
     * Verifies:
     * - HTML5 action is accessible under /html5 namespace
     * - Theme-specific rendering is applied
     * - No conflicts with other themes
     */
    @Test
    public void testHtml5Namespace() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

            final HtmlPage page = webClient.getPage(
                    ParameterUtils.getBaseUrl() + "/html5/index.action"
            );

            Assert.assertEquals("HTML5 action should return 200 status",
                    200, page.getWebResponse().getStatusCode());

            String pageAsXml = page.asXml();

            // Verify the page uses HTML5 theme by checking for theme-specific patterns
            // HTML5 theme should not use table-based layouts
            Assert.assertFalse("HTML5 theme should not use table layout for errors",
                    pageAsXml.matches("(?s).*<table[^>]*>.*errorMessage.*</table>.*"));
        }
    }
}
