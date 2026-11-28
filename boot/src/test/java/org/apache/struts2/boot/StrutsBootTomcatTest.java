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
import org.apache.struts2.boot.fixture.CookieProbeAction;
import org.apache.struts2.boot.fixture.HelloApp;
import org.apache.struts2.boot.fixture.UserAction;
import org.apache.struts2.config.ConfigurationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assume.assumeTrue;

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
    public void testUnregisteredMethodNotInvokableViaMethodPrefix() throws Exception {
        UserAction.deleteInvoked = false;
        get(server.baseUrl() + "/xml/legacyUsers.action?method:delete=");
        assertThat(UserAction.deleteInvoked).isTrue();

        UserAction.deleteInvoked = false;
        HttpResponse<String> response = get(server.baseUrl() + "/admin/users.action?method:delete=");

        assertThat(UserAction.deleteInvoked).isFalse();
        assertThat(response.statusCode()).isNotEqualTo(200);
    }

    @Test
    public void testErrorPageHidesStackTraceAndServerInfo() throws Exception {
        HttpResponse<String> response = get(server.baseUrl() + "/boom.action");

        assertThat(response.statusCode()).isEqualTo(500);
        assertThat(response.body()).doesNotContain("at org.apache").doesNotContain("Apache Tomcat/");
    }

    /** By default the session cookie carries {@code SameSite=Lax} and no {@code Secure}. */
    @Test
    public void testSessionCookieDefaultsToSameSiteLaxWithoutSecure() throws Exception {
        assertThat(sessionCookieAttributes(server)).contains("HttpOnly", "SameSite=Lax").doesNotContain("Secure");
    }

    @Test
    public void testSessionCookieSecureTrueAddsSecure() throws Exception {
        assertThat(sessionCookieAttributesWith("--struts.boot.session.cookie.secure=true"))
                .contains("Secure", "HttpOnly", "SameSite=Lax");
    }

    @Test
    public void testSessionCookieSecureFalseOmitsSecure() throws Exception {
        assertThat(sessionCookieAttributesWith("--struts.boot.session.cookie.secure=false"))
                .contains("HttpOnly", "SameSite=Lax").doesNotContain("Secure");
    }

    @Test
    public void testSessionCookieSameSiteLax() throws Exception {
        assertThat(sameSiteAttributesWith("--struts.boot.session.cookie.sameSite=lax")).containsExactly("SameSite=Lax");
    }

    @Test
    public void testSessionCookieSameSiteStrict() throws Exception {
        assertThat(sameSiteAttributesWith("--struts.boot.session.cookie.sameSite=strict")).containsExactly("SameSite=Strict");
    }

    @Test
    public void testSessionCookieSameSiteNone() throws Exception {
        assertThat(sameSiteAttributesWith("--struts.boot.session.cookie.sameSite=none")).containsExactly("SameSite=None");
    }

    /** {@code unset} sends no {@code SameSite} attribute at all. */
    @Test
    public void testSessionCookieSameSiteUnset() throws Exception {
        assertThat(sameSiteAttributesWith("--struts.boot.session.cookie.sameSite=unset")).isEmpty();
    }

    @Test
    public void testSessionCookieSecureWithSameSiteNone() throws Exception {
        assertThat(sessionCookieAttributesWith("--struts.boot.session.cookie.secure=true",
                "--struts.boot.session.cookie.sameSite=none")).contains("Secure", "HttpOnly", "SameSite=None");
    }

    /** By default a cookie the application adds without its own {@code SameSite} gets {@code SameSite=Lax} too. */
    @Test
    public void testApplicationCookieDefaultsToSameSiteLax() throws Exception {
        HttpResponse<String> response = get(server.baseUrl() + "/cookieProbe.action");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(cookieAttributes(response, CookieProbeAction.PLAIN_COOKIE)).containsExactly("SameSite=Lax");
        assertThat(cookieAttributes(response, CookieProbeAction.OWN_SAME_SITE_COOKIE)).containsExactly("SameSite=Strict");
    }

    /**
     * {@code sameSite} is the default for every cookie without its own {@code SameSite}, while
     * {@code secure} only reaches the session cookie.
     */
    @Test
    public void testSameSiteSettingAppliesToApplicationCookiesButSecureDoesNot() throws Exception {
        try (StrutsBootServer configured = StrutsBoot.start(HelloApp.class, loopbackWith(
                "--struts.boot.session.cookie.secure=true", "--struts.boot.session.cookie.sameSite=none"))) {
            HttpResponse<String> response = get(configured.baseUrl() + "/cookieProbe.action");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(cookieAttributes(response, CookieProbeAction.PLAIN_COOKIE)).containsExactly("SameSite=None");
            assertThat(cookieAttributes(response, CookieProbeAction.OWN_SAME_SITE_COOKIE)).containsExactly("SameSite=Strict");
        }
    }

    /** {@code unset} adds nothing, and leaves a {@code SameSite} the application set itself alone. */
    @Test
    public void testSameSiteUnsetKeepsApplicationCookieAttributes() throws Exception {
        try (StrutsBootServer configured = StrutsBoot.start(HelloApp.class, loopbackWith(
                "--struts.boot.session.cookie.sameSite=unset"))) {
            HttpResponse<String> response = get(configured.baseUrl() + "/cookieProbe.action");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(cookieAttributes(response, CookieProbeAction.PLAIN_COOKIE)).isEmpty();
            assertThat(cookieAttributes(response, CookieProbeAction.OWN_SAME_SITE_COOKIE)).containsExactly("SameSite=Strict");
        }
    }

    @Test
    public void testInvalidSessionCookieSecureFailsStartup() throws Exception {
        Set<Path> before = bootTempDirs();

        assertThatThrownBy(() -> startAndClose("--struts.boot.session.cookie.secure=yes"))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("struts.boot.session.cookie.secure")
                .hasMessageContaining("'yes'");

        assertThat(bootTempDirs()).isEqualTo(before);
    }

    @Test
    public void testInvalidSessionCookieSameSiteFailsStartup() throws Exception {
        Set<Path> before = bootTempDirs();

        assertThatThrownBy(() -> startAndClose("--struts.boot.session.cookie.sameSite=bogus"))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("struts.boot.session.cookie.sameSite")
                .hasMessageContaining("'bogus'");

        assertThat(bootTempDirs()).isEqualTo(before);
    }

    /** A session id supplied only in the URL is not resumed; the cookie-based flow still is. */
    @Test
    public void testUrlSuppliedSessionIdIsNotHonored() throws Exception {
        HttpResponse<String> first = get(server.baseUrl() + "/sessionProbe.action");
        assertThat(first.body()).contains("COUNTER=1");
        String sessionId = extractSessionId(first);

        HttpResponse<String> viaUrl = get(server.baseUrl() + "/sessionProbe.action;jsessionid=" + sessionId);
        assertThat(viaUrl.body()).contains("COUNTER=1");
        assertThat(extractSessionId(viaUrl)).isNotEqualTo(sessionId);

        // Control: the ordinary cookie-based flow still resumes the same session.
        HttpResponse<String> viaCookie = CLIENT.send(
                HttpRequest.newBuilder(URI.create(server.baseUrl() + "/sessionProbe.action"))
                        .header("Cookie", "JSESSIONID=" + sessionId)
                        .GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(viaCookie.body()).contains("COUNTER=2");
    }

    private static String extractSessionId(HttpResponse<String> response) {
        String setCookie = response.headers().firstValue("Set-Cookie")
                .orElseThrow(() -> new AssertionError("Expected a Set-Cookie header"));
        return setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(';'));
    }

    private static List<String> sameSiteAttributesWith(String... arguments) throws IOException, InterruptedException {
        return sessionCookieAttributesWith(arguments).stream()
                .filter(attribute -> attribute.startsWith("SameSite"))
                .collect(Collectors.toList());
    }

    private static List<String> sessionCookieAttributesWith(String... arguments) throws IOException, InterruptedException {
        try (StrutsBootServer configured = StrutsBoot.start(HelloApp.class, loopbackWith(arguments))) {
            return sessionCookieAttributes(configured);
        }
    }

    /** The attributes of the session cookie a new session gets, without the {@code JSESSIONID=...} pair. */
    private static List<String> sessionCookieAttributes(StrutsBootServer target) throws IOException, InterruptedException {
        return cookieAttributes(get(target.baseUrl() + "/sessionProbe.action"), "JSESSIONID");
    }

    /** The attributes of the one {@code Set-Cookie} header for the named cookie, without the name=value pair. */
    private static List<String> cookieAttributes(HttpResponse<String> response, String cookieName) {
        List<String> headers = response.headers().allValues("Set-Cookie").stream()
                .filter(header -> header.startsWith(cookieName + "="))
                .collect(Collectors.toList());
        assertThat(headers).as("Set-Cookie headers for %s", cookieName).hasSize(1);
        return Stream.of(headers.get(0).split(";")).skip(1).map(String::trim).collect(Collectors.toList());
    }

    /** Starts a server and closes it at once, so a start that wrongly succeeds leaves nothing running. */
    private static void startAndClose(String... arguments) {
        StrutsBoot.start(HelloApp.class, loopbackWith(arguments)).close();
    }

    private static String[] loopbackWith(String... arguments) {
        return Stream.concat(Stream.of("--struts.boot.port=0", "--struts.boot.address=127.0.0.1"), Stream.of(arguments))
                .toArray(String[]::new);
    }

    @Test
    public void testAddressRestrictsListeningInterface() throws Exception {
        try (StrutsBootServer local = StrutsBoot.start(HelloApp.class, "--struts.boot.port=0", "--struts.boot.address=127.0.0.1")) {
            assertThat(get("http://127.0.0.1:" + local.port() + "/hello.action").statusCode()).isEqualTo(200);

            Optional<InetAddress> external = nonLoopbackIpv4Address();
            assumeTrue("no non-loopback IPv4 interface to probe", external.isPresent());
            assertThatThrownBy(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(external.get(), local.port()), 2000);
                }
            }).isInstanceOf(IOException.class);
        }
    }

    @Test
    public void testXmlAndRegistryCoexist() throws Exception {
        assertThat(get(server.baseUrl() + "/xml/legacy.action").statusCode()).isEqualTo(200);
        assertThat(get(server.baseUrl() + "/hello.action").statusCode()).isEqualTo(200);
    }

    @Test
    public void testBaseDirFilesAreNotServed() throws Exception {
        Files.writeString(server.baseDir().resolve("webapp").resolve("doc-base-file.txt"), "public");
        Files.writeString(server.baseDir().resolve("base-dir-file.txt"), "not public");

        assertThat(get(server.baseUrl() + "/doc-base-file.txt").statusCode()).isEqualTo(200);
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
            String portArgument = "--struts.boot.port=" + busy.getLocalPort();
            assertThatThrownBy(() -> StrutsBoot.start(HelloApp.class, portArgument))
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

    @Test
    public void testBaseDirIsOwnerOnly() throws IOException {
        assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));

        assertThat(Files.getPosixFilePermissions(server.baseDir()))
                .isEqualTo(PosixFilePermissions.fromString("rwx------"));
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

    private static Optional<InetAddress> nonLoopbackIpv4Address() throws SocketException {
        return NetworkInterface.networkInterfaces()
                .filter(StrutsBootTomcatTest::isUp)
                .flatMap(NetworkInterface::inetAddresses)
                .filter(address -> address instanceof Inet4Address && !address.isLoopbackAddress())
                .findFirst();
    }

    private static boolean isUp(NetworkInterface networkInterface) {
        try {
            return networkInterface.isUp() && !networkInterface.isLoopback();
        } catch (SocketException e) {
            return false;
        }
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
