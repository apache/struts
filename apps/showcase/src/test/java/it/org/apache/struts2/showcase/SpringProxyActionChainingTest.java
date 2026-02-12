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
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Integration test verifying that Spring AOP proxied actions work correctly
 * with action chaining. This tests the WW-5514 StrutsProxyService integration.
 *
 * <p>The test uses a Spring AOP proxied version of ActionChain1 (proxiedActionChain1)
 * which is wrapped by {@link org.apache.struts2.showcase.proxy.LoggingInterceptor}.
 * The ChainingInterceptor must correctly resolve the target class through
 * StrutsProxyService to copy properties to the next action in the chain.</p>
 */
public class SpringProxyActionChainingTest {

    /**
     * Tests that action chaining works correctly when the first action is a Spring AOP proxy.
     *
     * <p>This verifies that:
     * <ul>
     *   <li>StrutsProxyService correctly identifies the Spring CGLIB proxy</li>
     *   <li>ChainingInterceptor resolves the target class for property copying</li>
     *   <li>Properties from the proxied ActionChain1 are correctly copied to ActionChain2</li>
     * </ul>
     * </p>
     */
    @Test
    public void testProxiedActionChaining() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(
                    ParameterUtils.getBaseUrl() + "/actionchaining/proxiedActionChain1!input"
            );

            final String pageAsText = page.asNormalizedText();

            // Verify properties were chained correctly despite proxy
            assertTrue("ActionChain1 property should be present",
                    pageAsText.contains("Action Chain 1 Property 1: Property Set In Action Chain 1"));
            assertTrue("ActionChain2 property should be present",
                    pageAsText.contains("Action Chain 2 Property 1: Property Set in Action Chain 2"));
            assertTrue("ActionChain3 property should be present",
                    pageAsText.contains("Action Chain 3 Property 1: Property set in Action Chain 3"));
        }
    }
}
