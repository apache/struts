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

import org.htmlunit.WebClient;

public class ParameterUtils {

    public static String getBaseUrl() {
        String port = System.getProperty("http.port");
        if (port == null) {
            port = "8090";
        }
        return "http://localhost:"+port+"/struts2-showcase";
    }

    /**
     * Creates a {@link WebClient} for the showcase integration tests. HtmlUnit's JavaScript
     * engine cannot parse Bootstrap 5's ES6 syntax (the {@code bootstrap.bundle.min.js} served
     * by the page decorator uses {@code class}), so script errors are not thrown; these tests
     * assert server-rendered output rather than Bootstrap's client-side behaviour.
     */
    public static WebClient createWebClient() {
        WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        return webClient;
    }
}
