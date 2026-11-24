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
package org.apache.struts2.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultStaticContentLoaderWebJarTest {

    private final ContentTypeProbe loader = new ContentTypeProbe();

    /** Exposes the protected getContentType for assertion. */
    static class ContentTypeProbe extends DefaultStaticContentLoader {
        String type(String name) {
            return getContentType(name);
        }
    }

    @Test
    public void mapsWebJarAssetTypes() {
        assertThat(loader.type("x.woff2")).isEqualTo("font/woff2");
        assertThat(loader.type("x.woff")).isEqualTo("font/woff");
        assertThat(loader.type("x.ttf")).isEqualTo("font/ttf");
        assertThat(loader.type("x.otf")).isEqualTo("font/otf");
        assertThat(loader.type("x.eot")).isEqualTo("application/vnd.ms-fontobject");
        assertThat(loader.type("x.svg")).isEqualTo("image/svg+xml");
        assertThat(loader.type("x.map")).isEqualTo("application/json");
        assertThat(loader.type("x.json")).isEqualTo("application/json");
        assertThat(loader.type("x.ico")).isEqualTo("image/x-icon");
        assertThat(loader.type("x.mjs")).isEqualTo("text/javascript");
    }

    @Test
    public void preservesExistingTypes() {
        assertThat(loader.type("x.js")).isEqualTo("text/javascript");
        assertThat(loader.type("x.css")).isEqualTo("text/css");
        assertThat(loader.type("x.png")).isEqualTo("image/png");
        assertThat(loader.type("x.unknown")).isNull();
    }

    private DefaultStaticContentLoader newLoader(boolean enabled) {
        DefaultStaticContentLoader webJarLoader = new DefaultStaticContentLoader();
        webJarLoader.setServeStaticContent("true");
        webJarLoader.setStaticContentPath("/static");
        webJarLoader.setServeStaticBrowserCache("true");
        org.apache.struts2.webjars.StrutsWebJarUrlProvider provider =
            new org.apache.struts2.webjars.StrutsWebJarUrlProvider();
        provider.setEnabled(String.valueOf(enabled));
        provider.setAllowlist("");
        provider.setStaticContentPath("/static");
        webJarLoader.setWebJarUrlProvider(provider);
        return webJarLoader;
    }

    @Test
    public void servesKnownWebJarAssetWithContentType() throws Exception {
        DefaultStaticContentLoader webJarLoader = newLoader(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        when(response.getOutputStream())
            .thenReturn(new WebJarTestServletOutputStream(captured));

        webJarLoader.findStaticResource("/static/webjars/jquery/jquery.min.js", request, response);

        verify(response).setContentType("text/javascript");
        verify(response, never())
            .sendError(HttpServletResponse.SC_NOT_FOUND);
        assertThat(captured.size()).isGreaterThan(0);
    }

    @Test
    public void servesSemicolonNamedResourceFromDecodedProviderUrl() throws Exception {
        DefaultStaticContentLoader webJarLoader = newLoader(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/myapp");
        HttpServletResponse response = mock(HttpServletResponse.class);
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new WebJarTestServletOutputStream(captured));
        var provider = new org.apache.struts2.webjars.StrutsWebJarUrlProvider();

        String url = provider.resolveUrl("ww5866-test/theme;dark.css", request).orElseThrow();
        assertThat(url).isEqualTo("/myapp/static/webjars/ww5866-test/1.0.0/theme%3Bdark.css");
        // Model the decoded, context-relative path supplied by a servlet container.
        // This does not exercise a container's path-parameter parsing.
        String decodedPath = URI.create(url).getPath().substring(request.getContextPath().length());
        webJarLoader.findStaticResource(decodedPath, request, response);

        try (var expected = getClass().getResourceAsStream(
                "/META-INF/resources/webjars/ww5866-test/1.0.0/theme;dark.css")) {
            assertThat(expected).isNotNull();
            assertThat(captured.toByteArray()).isEqualTo(expected.readAllBytes());
        }
        assertThat(captured.toString(StandardCharsets.UTF_8)).contains("--ww5866-theme: dark;");
        verify(response).setContentType("text/css");
        verify(response, never()).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void unknownWebJarAssetReturns404() throws Exception {
        DefaultStaticContentLoader webJarLoader = newLoader(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        webJarLoader.findStaticResource("/static/webjars/nope/nope.js", request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void disabledWebJarsReturns404() throws Exception {
        DefaultStaticContentLoader webJarLoader = newLoader(false);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        webJarLoader.findStaticResource("/static/webjars/jquery/jquery.min.js", request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
    @Test
    public void webJarEncodedTraversalReturns404() throws Exception {
        DefaultStaticContentLoader webJarLoader = newLoader(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        webJarLoader.findStaticResource("/static/webjars/jquery/%2e%2e/%2e%2e/etc/passwd", request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
