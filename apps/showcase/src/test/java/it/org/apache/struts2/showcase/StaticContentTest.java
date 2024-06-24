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

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.junit.Assert;
import org.junit.Test;

public class StaticContentTest {

    @Test
    public void testInvalidResources1() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            try {
                webClient.getPage(ParameterUtils.getBaseUrl() + "/struts..");
                Assert.fail("Previous request should have failed");
            } catch (FailingHttpStatusCodeException e) {
                Assert.assertEquals("Not Found", e.getStatusMessage());
                Assert.assertEquals(404, e.getStatusCode());
            }
        }
    }

    @Test
    public void testInvalidResources2() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            try {
                webClient.getPage(ParameterUtils.getBaseUrl() + "/static/..%252f");
                Assert.fail("Previous request should have failed");
            } catch (FailingHttpStatusCodeException e) {
                Assert.assertEquals("Not Found", e.getStatusMessage());
                Assert.assertEquals(404, e.getStatusCode());
            }
        }
    }

}
