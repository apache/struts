package org.apache.struts2.views.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Helper class used to build Urls or parse request params
 */
public interface UrlHelper {

    /**
     * Default HTTP port (80).
     */
    static final int DEFAULT_HTTP_PORT = 80;

    /**
     * Default HTTPS port (443).
     */
    static final int DEFAULT_HTTPS_PORT = 443;

    static final String AMP = "&amp;";

    String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params);

    String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme,
                    boolean includeContext, boolean encodeResult);

    String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme,
                    boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort);

    String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme,
                    boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort, boolean escapeAmp);

    void buildParametersString(Map<String, Object> params, StringBuilder link, String paramSeparator);

    Map<String, Object> parseQueryString(String queryString, boolean forceValueArray);

}
