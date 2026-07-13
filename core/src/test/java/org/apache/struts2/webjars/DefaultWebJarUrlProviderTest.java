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
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultWebJarUrlProviderTest {

    private DefaultWebJarUrlProvider provider;
    private HttpServletRequest request;

    @Before
    public void setUp() {
        provider = new DefaultWebJarUrlProvider();
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
}
