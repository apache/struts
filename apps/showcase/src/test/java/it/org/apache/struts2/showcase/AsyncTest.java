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

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Exercises the {@code async} plugin's server-push chat endpoints directly over HTTP. This verifies
 * the Servlet 3 asynchronous processing path (a {@code Callable} action that blocks until a message
 * is available) without driving a browser, so it does not depend on client-side JavaScript.
 */
public class AsyncTest {

    @Test
    public void testChatRoom() throws Exception {
        final HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        final String baseUrl = ParameterUtils.getBaseUrl();

        // Post a chat message; it is appended to the shared room.
        final HttpResponse<String> send = client.send(
                HttpRequest.newBuilder(URI.create(baseUrl + "/async/sendMessage?message=hello"))
                        .timeout(Duration.ofSeconds(10)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(200, send.statusCode());

        // The async (server-push) endpoint returns the new messages as JSON once available. Since the
        // message was already sent, the blocking Callable returns immediately.
        final HttpResponse<String> receive = client.send(
                HttpRequest.newBuilder(URI.create(baseUrl + "/async/receiveNewMessages?lastIndex=0"))
                        .timeout(Duration.ofSeconds(30)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(200, receive.statusCode());
        Assert.assertTrue("Expected 'hello' in response but got: " + receive.body(),
                receive.body().contains("hello"));
    }
}
