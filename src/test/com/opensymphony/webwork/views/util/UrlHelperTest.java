/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.util;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.webwork.WebWorkTestCase;
import com.opensymphony.webwork.WebWorkConstants;
import com.opensymphony.webwork.config.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;


/**
 * Test case for UrlHelper.
 * 
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 * @author tm_jee
 * @version $Date: 2006/03/08 18:09:28 $ $Id: UrlHelperTest.java,v 1.18 2006/03/08 18:09:28 rainerh Exp $
 */
public class UrlHelperTest extends WebWorkTestCase {
	
	
	
	public void testForceAddSchemeHostAndPort() throws Exception {
		String expectedUrl = "http://localhost/contextPath/path1/path2/myAction.action";
		
		Mock mockHttpServletRequest = new Mock(HttpServletRequest.class);
		mockHttpServletRequest.expectAndReturn("getScheme", "http");
		mockHttpServletRequest.expectAndReturn("getServerName", "localhost");
        mockHttpServletRequest.expectAndReturn("getContextPath", "/contextPath");

        Mock mockHttpServletResponse = new Mock(HttpServletResponse.class);
        mockHttpServletResponse.expectAndReturn("encodeURL", expectedUrl, expectedUrl);
		
		String result = UrlHelper.buildUrl("/path1/path2/myAction.action", (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse)mockHttpServletResponse.proxy(), null, "http", true, true, true);
		assertEquals(expectedUrl, result);
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

        Configuration.set(WebWorkConstants.WEBWORK_URL_HTTP_PORT, "7001");
        Configuration.set(WebWorkConstants.WEBWORK_URL_HTTPS_PORT, "7002");

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

        Configuration.set(WebWorkConstants.WEBWORK_URL_HTTP_PORT, "7001");
        Configuration.set(WebWorkConstants.WEBWORK_URL_HTTPS_PORT, "7002");

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
     * A check to verify that the scheme, server, and port number are omitted when the scheme of the current request
     * matches the scheme supplied when building the URL.
     */
    public void testBuildWithSameScheme() {
        String expectedString = "/mywebapp/MyAction.action?foo=bar&amp;hello=earth&amp;hello=mars";

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

        String urlString = UrlHelper.buildUrl(actionName, (HttpServletRequest) mockHttpServletRequest.proxy(), (HttpServletResponse) mockHttpServletResponse.proxy(), params, "https", true, true);
        assertEquals(expectedString, urlString);
    }
}
