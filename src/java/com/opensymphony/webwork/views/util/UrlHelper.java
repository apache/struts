package com.opensymphony.webwork.views.util;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.WebWorkConstants;
import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.TextParseUtil;
import com.opensymphony.xwork.util.XWorkContinuationConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;


/**
 * UrlHelper
 *
 * @author Jason Carreira Created Apr 19, 2003 9:32:19 PM
 */
public class UrlHelper {
    private static final Log LOG = LogFactory.getLog(UrlHelper.class);

    /**
     * Default HTTP port (80).
     */
    private static final int DEFAULT_HTTP_PORT = 80;

    /**
     * Default HTTPS port (443).
     */
    private static final int DEFAULT_HTTPS_PORT = 443;

    private static final String AMP = "&amp;";

    public static String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map params) {
        return buildUrl(action, request, response, params, null, true, true);
    }
    
    public static String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map params, String scheme, boolean includeContext, boolean encodeResult) {
    	return buildUrl(action, request, response, params, scheme, includeContext, encodeResult, false);
    }

    public static String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map params, String scheme, boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort) {
        StringBuffer link = new StringBuffer();

        boolean changedScheme = false;

        int httpPort = DEFAULT_HTTP_PORT;

        try {
            httpPort = Integer.parseInt((String) Configuration.get(WebWorkConstants.WEBWORK_URL_HTTP_PORT));
        } catch (Exception ex) {
        }

        int httpsPort = DEFAULT_HTTPS_PORT;

        try {
            httpsPort = Integer.parseInt((String) Configuration.get(WebWorkConstants.WEBWORK_URL_HTTPS_PORT));
        } catch (Exception ex) {
        }

        // only append scheme if it is different to the current scheme *OR*
        // if we explicity want it to be appended by having forceAddSchemeHostAndPort = true
        if (forceAddSchemeHostAndPort) {
        	String reqScheme = request.getScheme();
        	changedScheme = true;
        	link.append(scheme != null ? scheme : reqScheme);
        	link.append("://");
        	link.append(request.getServerName());
        	
        	if ((scheme.equals("http") && (httpPort != DEFAULT_HTTP_PORT)) || (scheme.equals("https") && httpsPort != DEFAULT_HTTPS_PORT))
            {
                link.append(":");
                link.append(scheme.equals("http") ? httpPort : httpsPort);
            }
        }
        else if (  
           (scheme != null) && !scheme.equals(request.getScheme())) {
            changedScheme = true;
            link.append(scheme);
            link.append("://");
            link.append(request.getServerName());

            if ((scheme.equals("http") && (httpPort != DEFAULT_HTTP_PORT)) || (scheme.equals("https") && httpsPort != DEFAULT_HTTPS_PORT))
            {
                link.append(":");
                link.append(scheme.equals("http") ? httpPort : httpsPort);
            }
        }

        if (action != null) {
            // Check if context path needs to be added
            // Add path to absolute links
            if (action.startsWith("/") && includeContext) {
                String contextPath = request.getContextPath();
                if (!contextPath.equals("/")) {
                    link.append(contextPath);
                }
            } else if (changedScheme) {
                String uri = request.getRequestURI();
                link.append(uri.substring(0, uri.lastIndexOf('/')));
            }

            // Add page
            link.append(action);
        } else {
            // Go to "same page"
            String requestURI = (String) request.getAttribute("webwork.request_uri");

            if (requestURI == null) {
                requestURI = request.getRequestURI();
            }

            link.append(requestURI);
        }

        // tie in the continuation parameter
        String continueId = (String) ActionContext.getContext().get(XWorkContinuationConfig.CONTINUE_KEY);
        if (continueId != null) {
            if (params == null) {
                params = Collections.singletonMap(XWorkContinuationConfig.CONTINUE_PARAM, continueId);
            } else {
                params.put(XWorkContinuationConfig.CONTINUE_PARAM, continueId);
            }
        }

        //if the action was not explicitly set grab the params from the request
        buildParametersString(params, link);

        String result;

        try {
            result = encodeResult ? response.encodeURL(link.toString()) : link.toString();
        } catch (Exception ex) {
            // Could not encode the URL for some reason
            // Use it unchanged
            result = link.toString();
        }

        return result;
    }

    public static void buildParametersString(Map params, StringBuffer link) {
        if ((params != null) && (params.size() > 0)) {
            if (link.toString().indexOf("?") == -1) {
                link.append("?");
            } else {
                link.append(AMP);
            }

            // Set params
            Iterator iter = params.entrySet().iterator();

            String[] valueHolder = new String[1];

            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                Object value = entry.getValue();

                String[] values;

                if (value instanceof String[]) {
                    values = (String[]) value;
                } else {
                    valueHolder[0] = value.toString();
                    values = valueHolder;
                }

                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null) {
                        link.append(name);
                        link.append('=');
                        link.append(translateAndEncode(values[i]));
                    }

                    if (i < (values.length - 1)) {
                        link.append(AMP);
                    }
                }

                if (iter.hasNext()) {
                    link.append(AMP);
                }
            }
        }
    }

    /**
     * Translates any script expressions using {@link com.opensymphony.xwork.util.TextParseUtil#translateVariables} and
     * encodes the URL using {@link java.net.URLEncoder#encode} with the encoding specified in the configuration.
     *
     * @param input
     * @return the translated and encoded string
     */
    public static String translateAndEncode(String input) {
        OgnlValueStack valueStack = ServletActionContext.getContext().getValueStack();
        String output = TextParseUtil.translateVariables(input, valueStack);

        final String encoding;
        if (Configuration.isSet(WebWorkConstants.WEBWORK_I18N_ENCODING)) {
            encoding = Configuration.getString(WebWorkConstants.WEBWORK_I18N_ENCODING);
        } else {
            encoding = "UTF-8";
        }

        try {
            return URLEncoder.encode(output, encoding);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Could not encode URL parameter '" + input + "', returning value un-encoded");
            return output;
        }
    }
}
