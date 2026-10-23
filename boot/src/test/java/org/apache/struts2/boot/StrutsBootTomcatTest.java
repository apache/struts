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
package org.apache.struts2.boot;

import org.apache.struts2.boot.fixture.BrokenApp;
import org.apache.struts2.boot.fixture.HelloApp;
import org.apache.struts2.boot.fixture.UserAction;
import org.apache.struts2.config.ConfigurationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StrutsBootTomcatTest {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    private static StrutsBootServer server;

    @BeforeClass
    public static void startServer() {
        server = StrutsBoot.start(HelloApp.class, "--struts.boot.port=0");
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) {
            server.close();
        }
    }

    @Test
    public void testRendersFreemarkerTemplate() throws Exception {
        assertThat(get(server.baseUrl() + "/hello.action").statusCode()).isEqualTo(200);
    }

    @Test
    public void testStrutsTagRenders() throws Exception {
        assertThat(get(server.baseUrl() + "/hello.action").body()).contains("Hello Boot");
    }

    @Test
    public void testPostOnlyRejectsGet() throws Exception {
        String url = server.baseUrl() + "/admin/saveUser.action";

        assertThat(get(url).statusCode()).isEqualTo(405);

        HttpResponse<String> post = post(url);
        assertThat(post.statusCode()).isEqualTo(302);
        assertThat(post.headers().firstValue("Location")).hasValueSatisfying(location ->
                assertThat(location).endsWith("/admin/users.action"));
    }

    @Test
    public void testUnregisteredMethodNotInvokable() throws Exception {
        UserAction.deleteInvoked = false;
        assertThat(get(server.baseUrl() + "/admin/users!list.action").statusCode()).isEqualTo(200);

        HttpResponse<String> response = get(server.baseUrl() + "/admin/users!delete.action");

        assertThat(UserAction.deleteInvoked).isFalse();
        assertThat(response.statusCode()).isNotEqualTo(200);
    }

    @Test
    public void testXmlAndRegistryCoexist() throws Exception {
        assertThat(get(server.baseUrl() + "/xml/legacy.action").statusCode()).isEqualTo(200);
        assertThat(get(server.baseUrl() + "/hello.action").statusCode()).isEqualTo(200);
    }

    @Test
    public void testBaseDirFilesAreNotServed() throws Exception {
        Files.writeString(server.baseDir().resolve("base-dir-file.txt"), "not public");

        assertThat(get(server.baseUrl() + "/base-dir-file.txt").statusCode()).isNotEqualTo(200);
    }

    @Test
    public void testConfigurationErrorStopsTomcatAndCleansBaseDir() throws Exception {
        Set<Path> before = bootTempDirs();

        assertThatThrownBy(() -> StrutsBoot.start(BrokenApp.class, "--struts.boot.port=0"))
                .isInstanceOf(ConfigurationException.class)
                .satisfies(e -> assertThat(messagesOf(e)).anyMatch(message -> message.contains("templates/missing.ftl")));

        assertThat(bootTempDirs()).isEqualTo(before);
    }

    @Test
    public void testBusyPortFailsCleanly() throws Exception {
        Set<Path> before = bootTempDirs();
        try (ServerSocket busy = new ServerSocket(0)) {
            assertThatThrownBy(() -> StrutsBoot.start(HelloApp.class, "--struts.boot.port=" + busy.getLocalPort()))
                    .isInstanceOf(ConfigurationException.class);
        }

        assertThat(bootTempDirs()).isEqualTo(before);
    }

    @Test
    public void testContextPathAppliesToActionsAndRedirects() throws Exception {
        try (StrutsBootServer ctx = StrutsBoot.start(HelloApp.class, "--struts.boot.port=0", "--struts.boot.context-path=/ctx")) {
            assertThat(ctx.baseUrl()).endsWith("/ctx");
            assertThat(get("http://127.0.0.1:" + ctx.port() + "/ctx/hello.action").statusCode()).isEqualTo(200);

            HttpResponse<String> post = post("http://127.0.0.1:" + ctx.port() + "/ctx/admin/saveUser.action");
            assertThat(post.headers().firstValue("Location")).hasValueSatisfying(location ->
                    assertThat(location).contains("/ctx/admin/users.action"));
        }
    }

    @Test
    public void testCloseDeletesBaseDir() {
        StrutsBootServer other = StrutsBoot.start(HelloApp.class, "--struts.boot.port=0");
        Path baseDir = other.baseDir();
        assertThat(baseDir).exists();

        other.close();
        other.close();

        assertThat(Files.exists(baseDir)).isFalse();
    }

    private static HttpResponse<String> get(String url) throws IOException, InterruptedException {
        return CLIENT.send(HttpRequest.newBuilder(URI.create(url)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> post(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static Set<Path> bootTempDirs() throws IOException {
        try (Stream<Path> entries = Files.list(Path.of(System.getProperty("java.io.tmpdir")))) {
            return entries.filter(path -> path.getFileName().toString().startsWith("struts-boot-")).collect(Collectors.toSet());
        }
    }

    private static Stream<String> messagesOf(Throwable throwable) {
        Stream.Builder<String> messages = Stream.builder();
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            if (current.getMessage() != null) {
                messages.add(current.getMessage());
            }
        }
        return messages.build();
    }
}
