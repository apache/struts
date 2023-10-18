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
package org.apache.struts2.views.util;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope.Strategy;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.url.StrutsQueryStringBuilder;
import org.apache.struts2.url.StrutsUrlEncoder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Test case for DefaultUrlHelper.
 */
public class DefaultUrlHelperTest extends StrutsInternalTestCase {

    private DefaultUrlHelper urlHelper;

    public void testForceAddSchemeHostAndPort() {
        String expectedUrl = "http://localhost/contextPath/path1/path2/myAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath", "/contextPath");
        mockHttpServletRequest.expectAndReturn("getServerPort", 80);

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);

        String result = urlHelper.buildUrl("/path1/path2/myAction.action", (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), null, "http", true, true, true);
        assertEquals(expectedUrl, result);
        mockHttpServletRequest.verify();
    }

    public void testDoNotForceAddSchemeHostAndPort() {
        String expectedUrl = "/contextPath/path1/path2/myAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath", "/contextPath");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);

        String result = urlHelper.buildUrl("/path1/path2/myAction.action", (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), null, "http", true, true, false);

        assertEquals(expectedUrl, result);
    }

    public void testForceAddSchemeHostAndPortWithNonStandardPort() {
        String expectedUrl = "http://localhost:9090/contextPath/path1/path2/myAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath", "/contextPath");
        mockHttpServletRequest.expectAndReturn("getServerPort", 9090);

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);

        String result = urlHelper.buildUrl("/path1/path2/myAction.action", (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), null, "http", true, true, true);
        assertEquals(expectedUrl, result);
        mockHttpServletRequest.verify();
    }

    public void testForceAddNullSchemeHostAndPort() {
        String expectedUrl = "http://localhost/contextPath/path1/path2/myAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath",
            "/contextPath");
        mockHttpServletRequest.expectAndReturn("getServerPort", 80);

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl,
            expectedUrl);

        String result = urlHelper.buildUrl("/path1/path2/myAction.action",
            (HttpServletRequest) mockHttpServletRequest.proxy(),
            (HttpServletResponse) mockHttpServletResponse.proxy(), null,
            null, true, true, true);
        assertEquals(expectedUrl, result);
        mockHttpServletRequest.verify();
    }

    public void testForceAddNullSchemeHostAndPort2() {
        String expectedUrl = "http://localhost:8080/contextPath/path1/path2/myAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath",
            "/contextPath");
        mockHttpServletRequest.expectAndReturn("getServerPort", 8080);

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl,
            expectedUrl);

        String result = urlHelper.buildUrl("/path1/path2/myAction.action",
            (HttpServletRequest) mockHttpServletRequest.proxy(),
            (HttpServletResponse) mockHttpServletResponse.proxy(), null,
            null, true, true, true);
        assertEquals(expectedUrl, result);
        mockHttpServletRequest.verify();
    }

    public void testBuildWithRootContext() {
        String expectedUrl = "/MyAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/");
        mockHttpServletRequest.expectAndReturn("getScheme", "http");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);

        String actualUrl = urlHelper.buildUrl(expectedUrl, (HttpServletRequest) mockHttpServletRequest.proxy(),
            (HttpServletResponse) mockHttpServletResponse.proxy(), new HashMap<>());
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * just one &, not &amp;
     */
    public void testBuildUrlCorrectlyAddsAmp() {
        String expectedString = "my.actionName?foo=bar&amp;hello=world";
        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "my.actionName";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("hello", "world");
        params.put("foo", "bar");

        String urlString = urlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params);
        assertEquals(expectedString, urlString);
    }

    /**
     * just one &, not &amp;
     */
    public void testBuildUrlCorrectlyAddsDoNotEscapeAmp() {
        String expectedString = "my.actionName?foo=bar&hello=world";
        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "my.actionName";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("hello", "world");
        params.put("foo", "bar");

        String urlString = urlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, null, true, true, false, false);
        assertEquals(expectedString, urlString);
    }

    public void testBuildUrlWithStringArray() {
        String expectedString = "my.actionName?foo=bar&amp;hello=earth&amp;hello=mars";
        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "my.actionName";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = urlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params);
        assertEquals(expectedString, urlString);
    }

    /**
     * The UrlHelper should build a URL that starts with "https" followed by the server name when the scheme of the
     * current request is "http" and the port for the "https" scheme is 443.
     */
    public void testSwitchToHttpsScheme() {
        String expectedString = "https://www.mydomain.com/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerPort", 80);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = urlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "https", true, true);
        assertEquals(expectedString, urlString);
    }

    /**
     * The UrlHelper should build a URL that starts with "http" followed by the server name when the scheme of the
     * current request is "https" and the port for the "http" scheme is 80.
     */
    public void testSwitchToHttpScheme() {
        String expectedString = "http://www.mydomain.com/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "https");
        mockHttpServletRequest.expectAndReturn("getServerPort", 443);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = urlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "http", true, true);
        assertEquals(expectedString, urlString);
    }

    /**
     * This test is similar to {@link #testSwitchToHttpsScheme()} with the HTTP port equal to 7001 and the HTTPS port
     * equal to 7002.
     */
    public void testSwitchToHttpsNonDefaultPort() {

        String expectedString = "https://www.mydomain.com:7002/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        urlHelper.setHttpPort("7001");
        urlHelper.setHttpsPort("7002");

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerPort", 7001);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = urlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "https", true, true);
        assertEquals(expectedString, urlString);
    }

    /**
     * This test is similar to {@link #testSwitchToHttpScheme()} with the HTTP port equal to 7001 and the HTTPS port
     * equal to port 7002.
     */
    public void testSwitchToHttpNonDefaultPort() {

        String expectedString = "http://www.mydomain.com:7001/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        urlHelper.setHttpPort("7001");
        urlHelper.setHttpsPort("7002");

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "https");
        mockHttpServletRequest.expectAndReturn("getServerPort", 7002);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = urlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "http", true, true);
        assertEquals(expectedString, urlString);
    }

    /**
     * The UrlHelper should build a URL that starts with "https" followed by the server name when the scheme of the
     * current request is "http" and the port for the "https" scheme is 443. When the request has been forwarded
     * in a Servlet 2.4 container, the UrlHelper should use the javax.servlet.forward.request_uri request attribute
     * instead of a call to HttpServletRequest#getRequestURI().
     */
    public void testForwardedRequest() {
        String expectedString = "https://www.example.com/mywebapp/product/widget/promo.html";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.example.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerPort", 80);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");
        mockHttpServletRequest.expectAndReturn("getAttribute", "javax.servlet.forward.request_uri", "/mywebapp/product/widget/");
        mockHttpServletRequest.expectAndReturn("getRequestURI", "/mywebapp/");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "promo.html";
        TreeMap<String, Object> params = new TreeMap<>();

        String urlString = urlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "https", true, true);
        assertEquals(expectedString, urlString);
    }

    public void setUp() throws Exception {
        super.setUp();
        StubContainer stubContainer = new StubContainer(container);
        ActionContext.getContext().withContainer(stubContainer);
        urlHelper = new DefaultUrlHelper();
        StrutsUrlEncoder encoder = new StrutsUrlEncoder();
        urlHelper.setQueryStringBuilder(new StrutsQueryStringBuilder(encoder));
    }

    static class StubContainer implements Container {

        Container parent;

        public StubContainer(Container parent) {
            super();
            this.parent = parent;
        }

        public Map<String, Object> overrides = new HashMap<>();

        @SuppressWarnings("unchecked")
        public <T> T getInstance(Class<T> type, String name) {
            if (String.class.isAssignableFrom(type) && overrides.containsKey(name)) {
                return (T) overrides.get(name);
            } else {
                return parent.getInstance(type, name);
            }
        }

        public <T> T getInstance(Class<T> type) {
            return parent.getInstance(type);
        }

        public Set<String> getInstanceNames(Class<?> type) {
            return parent.getInstanceNames(type);
        }

        public void inject(Object o) {
            parent.inject(o);
        }

        public <T> T inject(Class<T> implementation) {
            return parent.inject(implementation);
        }

        public void removeScopeStrategy() {
            parent.removeScopeStrategy();

        }

        public void setScopeStrategy(Strategy scopeStrategy) {
            parent.setScopeStrategy(scopeStrategy);
        }
    }
}
