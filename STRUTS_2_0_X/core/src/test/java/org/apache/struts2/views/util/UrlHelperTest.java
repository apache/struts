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
package org.apache.struts2.views.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsTestCase;

import com.mockobjects.dynamic.Mock;


/**
 * Test case for UrlHelper.
 *
 */
public class UrlHelperTest extends StrutsTestCase {

    public void testForceAddSchemeHostAndPort() throws Exception {
        String expectedUrl = "http://localhost/contextPath/path1/path2/myAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath", "/contextPath");
	mockHttpServletRequest.expectAndReturn("getServerPort", 80);
	
        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);

        String result = UrlHelper.buildUrl("/path1/path2/myAction.action", (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse)mockHttpServletResponse.proxy(), null, "http", true, true, true);
        assertEquals(expectedUrl, result);
        mockHttpServletRequest.verify();
    }

    public void testDoNotForceAddSchemeHostAndPort() throws Exception {
        String expectedUrl = "/contextPath/path1/path2/myAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath", "/contextPath");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);

        String result = UrlHelper.buildUrl("/path1/path2/myAction.action", (HttpServletRequest)mockHttpServletRequest.proxy(), (HttpServletResponse)mockHttpServletResponse.proxy(), null, "http", true, true, false);

        assertEquals(expectedUrl, result);
    }

    public void testForceAddSchemeHostAndPortWithNonStandardPort() throws Exception {
        String expectedUrl = "http://localhost:9090/contextPath/path1/path2/myAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath", "/contextPath");
        mockHttpServletRequest.expectAndReturn("getServerPort", 9090);

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);

        String result = UrlHelper.buildUrl("/path1/path2/myAction.action", (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse)mockHttpServletResponse.proxy(), null, "http", true, true, true);
        assertEquals(expectedUrl, result);
        mockHttpServletRequest.verify();
    }
    
    public void testForceAddNullSchemeHostAndPort() throws Exception {
        String expectedUrl = "http://localhost/contextPath/path1/path2/myAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath", "/contextPath");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl,
            expectedUrl);

        String result = UrlHelper.buildUrl("/path1/path2/myAction.action",
            (HttpServletRequest) mockHttpServletRequest.proxy(),
            (HttpServletResponse) mockHttpServletResponse.proxy(), null,
            null, true, true, true);
        assertEquals(expectedUrl, result);
        mockHttpServletRequest.verify();
    }        

    public void testBuildParametersStringWithUrlHavingSomeExistingParameters() throws Exception {
        String expectedUrl = "http://localhost:8080/myContext/myPage.jsp?initParam=initValue&amp;param1=value1&amp;param2=value2";

        Map parameters = new LinkedHashMap();
        parameters.put("param1", "value1");
        parameters.put("param2", "value2");

        StringBuffer url = new StringBuffer("http://localhost:8080/myContext/myPage.jsp?initParam=initValue");

        UrlHelper.buildParametersString(parameters, url);

        assertEquals(
           expectedUrl, url.toString());
    }

    public void testBuildWithRootContext() {
        String expectedUrl = "/MyAction.action";

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/");
        mockHttpServletRequest.expectAndReturn("getScheme", "http");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);

        String actualUrl = UrlHelper.buildUrl(expectedUrl, (HttpServletRequest) mockHttpServletRequest.proxy(),
                (HttpServletResponse) mockHttpServletResponse.proxy(), new HashMap());
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
        TreeMap params = new TreeMap();
        params.put("hello", "world");
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params);
        assertEquals(expectedString, urlString);
    }

    public void testBuildUrlCorrectlyAddsDoNotEscapeAmp() {
        String expectedString = "my.actionName?foo=bar&hello=world";
        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "my.actionName";
        TreeMap params = new TreeMap();
        params.put("hello", "world");
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, null, true, true, false, false);
        assertEquals(expectedString, urlString);
    }

    public void testBuildUrlWithStringArray() {
        String expectedString = "my.actionName?foo=bar&amp;hello=earth&amp;hello=mars";
        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "my.actionName";
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params);
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
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "https", true, true);
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
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "http", true, true);
        assertEquals(expectedString, urlString);
    }

    /**
     * This test is similar to {@link #testSwitchToHttpsScheme()} with the HTTP port equal to 7001 and the HTTPS port
     * equal to 7002.
     */
    public void testSwitchToHttpsNonDefaultPort() {

        String expectedString = "https://www.mydomain.com:7002/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        UrlHelper.setHttpPort("7001");
        UrlHelper.setHttpsPort("7002");

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "http");
        mockHttpServletRequest.expectAndReturn("getServerPort", 7001);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "https", true, true);
        assertEquals(expectedString, urlString);
    }

    /**
     * This test is similar to {@link #testSwitchToHttpScheme()} with the HTTP port equal to 7001 and the HTTPS port
     * equal to port 7002.
     */
    public void testSwitchToHttpNonDefaultPort() {

        String expectedString = "http://www.mydomain.com:7001/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

        UrlHelper.setHttpPort("7001");
        UrlHelper.setHttpsPort("7002");

        Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
        mockHttpServletRequest.expectAndReturn("getServerName", "www.mydomain.com");
        mockHttpServletRequest.expectAndReturn("getScheme", "https");
        mockHttpServletRequest.expectAndReturn("getServerPort", 7002);
        mockHttpServletRequest.expectAndReturn("getContextPath", "/mywebapp");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedString, expectedString);

        String actionName = "/MyAction.action";
        TreeMap params = new TreeMap();
        params.put("hello", new String[]{"earth", "mars"});
        params.put("foo", "bar");

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "http", true, true);
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
        Map params = new TreeMap();

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "https", true, true);
        assertEquals(expectedString, urlString);
    }


    public void testParseQuery() throws Exception {
        Map result = UrlHelper.parseQueryString("aaa=aaaval&bbb=bbbval&ccc=");

        assertEquals(result.get("aaa"), "aaaval");
        assertEquals(result.get("bbb"), "bbbval");
        assertEquals(result.get("ccc"), "");
    }

    public void testParseEmptyQuery() throws Exception {
        Map result = UrlHelper.parseQueryString("");

        assertNotNull(result);
        assertEquals(result.size(), 0);
    }

    public void testParseNullQuery() throws Exception {
        Map result = UrlHelper.parseQueryString(null);

        assertNotNull(result);
        assertEquals(result.size(), 0);
    }

    public void testParseMultiQuery() throws Exception {
        Map result = UrlHelper.parseQueryString("param=1&param=1&param=1");
        
        assertNotNull(result);
        assertEquals(result.size(), 1);
        String values[] = (String[]) result.get("param");
        Arrays.sort(values);
        assertEquals(values.length, 3);
        assertEquals(values[0], "1");
        assertEquals(values[1], "1");
        assertEquals(values[2], "1");
    }

    public void testParseDuplicateQuery() throws Exception {
        Map result = UrlHelper.parseQueryString("param=1&param=2&param=3");
        
        assertNotNull(result);
        assertEquals(result.size(), 1);
        String values[] = (String[]) result.get("param");
        Arrays.sort(values);
        assertEquals(values.length, 3);
        assertEquals(values[0], "1");
        assertEquals(values[1], "2");
        assertEquals(values[2], "3");
    }

    public void testTranslateAndEncode() throws Exception {
        UrlHelper.setCustomEncoding("UTF-8");
        String result = UrlHelper.translateAndEncode("\u65b0\u805e");
        String expectedResult = "%E6%96%B0%E8%81%9E";

        assertEquals(result, expectedResult);
    }

    public void testTranslateAndDecode() throws Exception {
        UrlHelper.setCustomEncoding("UTF-8");
        String result = UrlHelper.translateAndDecode("%E6%96%B0%E8%81%9E");
        String expectedResult = "\u65b0\u805e";

        assertEquals(result, expectedResult);
    }
}
