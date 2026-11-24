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
package org.apache.struts2.webjars;

import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StrutsWebJarUrlProviderTest {

    private StrutsWebJarUrlProvider provider;
    private HttpServletRequest request;

    @Before
    public void setUp() {
        provider = new StrutsWebJarUrlProvider();
        provider.setEnabled("true");
        provider.setAllowlist("");
        provider.setStaticContentPath("/static");
        request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/myapp");
    }

    @Test
    public void resolvesKnownResourceToVersionedClasspathPath() {
        assertThat(provider.resolveResourcePath("jquery/jquery.min.js"))
            .hasValueSatisfying(p -> assertThat(p)
                .startsWith("META-INF/resources/webjars/jquery/")
                .endsWith("/jquery.min.js"));
    }

    @Test
    public void resolvesKnownResourceToServableUrl() {
        assertThat(provider.resolveUrl("jquery/jquery.min.js", request))
            .hasValueSatisfying(u -> assertThat(u)
                .startsWith("/myapp/static/webjars/jquery/")
                .endsWith("/jquery.min.js"));
    }

    @Test
    public void unknownWebjarResolvesEmpty() {
        assertThat(provider.resolveResourcePath("no-such-lib/x.js")).isEmpty();
        assertThat(provider.resolveUrl("no-such-lib/x.js", request)).isEmpty();
    }

    @Test
    public void traversalIsRejected() {
        assertThat(provider.resolveResourcePath("jquery/../../../etc/passwd")).isEmpty();
        assertThat(provider.resolveResourcePath("../jquery/jquery.min.js")).isEmpty();
    }

    @Test
    public void resolveUrlRejectsTraversal() {
        assertThat(provider.resolveUrl("jquery/../../../etc/passwd", request)).isEmpty();
        assertThat(provider.resolveUrl("../jquery/jquery.min.js", request)).isEmpty();
    }

    @Test
    public void allowlistBlocksNonListedWebjar() {
        provider.setAllowlist("bootstrap");
        assertThat(provider.resolveResourcePath("jquery/jquery.min.js")).isEmpty();
    }

    @Test
    public void allowlistPermitsListedWebjar() {
        provider.setAllowlist("jquery, bootstrap");
        assertThat(provider.resolveResourcePath("jquery/jquery.min.js")).isPresent();
    }

    @Test
    public void disabledResolvesEmpty() {
        provider.setEnabled("false");
        assertThat(provider.isEnabled()).isFalse();
        assertThat(provider.resolveResourcePath("jquery/jquery.min.js")).isEmpty();
        assertThat(provider.resolveUrl("jquery/jquery.min.js", request)).isEmpty();
    }

    @Test
    public void rootContextPathIsNotDuplicated() {
        when(request.getContextPath()).thenReturn("/");
        assertThat(provider.resolveUrl("jquery/jquery.min.js", request))
            .hasValueSatisfying(u -> assertThat(u).startsWith("/static/webjars/jquery/"));
    }

    @Test
    public void blankOrSingleSegmentPathResolvesEmpty() {
        assertThat(provider.resolveResourcePath("")).isEmpty();
        assertThat(provider.resolveResourcePath("jquery")).isEmpty();
    }

    @Test
    public void encodesFilePathWithoutChangingSegmentSeparators() {
        String normalUrl = provider.resolveUrl("jquery/jquery.min.js", request).orElseThrow();
        String prefix = normalUrl.substring(0, normalUrl.lastIndexOf('/') + 1);
        String[][] paths = {
            {"a b.js", "a%20b.js"},
            {"a\"b.js", "a%22b.js"},
            {"a?b.js", "a%3Fb.js"},
            {"a#b.js", "a%23b.js"},
            {"a%b.js", "a%25b.js"},
            {"a+b.js", "a+b.js"},
            {"\u65b0\u805e.js", "%E6%96%B0%E8%81%9E.js"},
            {"e\u0301.js", "e%CC%81.js"},
            {"\ud83d\ude00.js", "%F0%9F%98%80.js"},
            {"a[b].js", "a%5Bb%5D.js"},
            {"dir one/dir two/a b.js", "dir%20one/dir%20two/a%20b.js"},
            {"a<b>.js", "a%3Cb%3E.js"},
            {"a%20b.js", "a%2520b.js"},
            {"nested/jquery.min.js", "nested/jquery.min.js"},
            {"a-._~!$&'()*+,;=:@b.js", "a-._~!$&'()*+,%3B=:@b.js"}
        };
        SoftAssertions.assertSoftly(softly -> {
            for (String[] path : paths) {
                softly.assertThat(provider.resolveUrl("jquery/" + path[0], request))
                    .as("logical file path %s", path[0])
                    .hasValue(prefix + path[1]);
            }
        });
    }

    @Test
    public void encodesSemicolonInUrlButKeepsItInResourcePath() {
        String logicalPath = "ww5866-test/theme;dark.css";

        assertThat(provider.resolveUrl(logicalPath, request))
            .hasValue("/myapp/static/webjars/ww5866-test/1.0.0/theme%3Bdark.css");
        assertThat(provider.resolveResourcePath(logicalPath))
            .hasValue("META-INF/resources/webjars/ww5866-test/1.0.0/theme;dark.css");
    }

    @Test
    public void keepsResourceLookupPathUnencoded() {
        String filePath = "dir one/a\"?#%+\u65b0\u805e.js";
        assertThat(provider.resolveResourcePath("jquery/" + filePath))
            .hasValueSatisfying(p -> assertThat(p)
                .startsWith("META-INF/resources/webjars/jquery/")
                .endsWith("/" + filePath));
        assertThat(provider.resolveResourcePath("jquery/a%20b.js"))
            .hasValueSatisfying(p -> assertThat(p).endsWith("/a%20b.js"));
    }

    @Test
    public void encodingDoesNotBypassExistingRejections() {
        assertThat(provider.resolveUrl("jquery/../../../a b.js", request)).isEmpty();
        assertThat(provider.resolveUrl("no-such-lib/a b.js", request)).isEmpty();
        provider.setAllowlist("bootstrap");
        assertThat(provider.resolveUrl("jquery/a b.js", request)).isEmpty();
        assertThat(provider.resolveResourcePath("jquery/a b.js")).isEmpty();
        provider.setAllowlist("jquery");
        provider.setEnabled("false");
        assertThat(provider.resolveUrl("jquery/a b.js", request)).isEmpty();
        assertThat(provider.resolveResourcePath("jquery/a b.js")).isEmpty();
    }

    @Test
    public void preservesContextStaticPathAndCanonicalization() {
        when(request.getContextPath()).thenReturn("");
        provider.setStaticContentPath("/assets");
        assertThat(provider.resolveUrl("jquery/dir/../jquery.min.js", request))
            .hasValueSatisfying(u -> assertThat(u)
                .startsWith("/assets/webjars/jquery/")
                .endsWith("/jquery.min.js"));
    }
}
