package org.apache.struts2.interceptor.csp;

import javax.servlet.http.HttpServletResponse;

public interface CspSettings {

    String CSP_HEADER = "Content-Security-Policy";
    String OBJECT_SRC = "object-src";
    String SCRIPT_SRC = "script-src";
    String BASE_URI = "base-uri";
    String REPORT_URI = "report-uri";
    String NONE = "none";
    String STRICT_DYNAMIC = "strict-dynamic";
    String HTTP = "http:";
    String HTTPS = "https:";

    void addCspHeaders(HttpServletResponse response);
}
