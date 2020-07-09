package com.opensymphony.xwork2.interceptor;

import javax.servlet.http.HttpServletRequest;


@FunctionalInterface
public interface ResourceIsolationPolicy {

    String SEC_FETCH_SITE_HEADER = "sec-fetch-site";
    String SEC_FETCH_MODE_HEADER = "sec-fetch-mode";
    String SEC_FETCH_DEST_HEADER = "sec-fetch-dest";
    String VARY_HEADER = "Vary";
    String SAME_ORIGIN = "same-origin";
    String SAME_SITE = "same-site";
    String NONE = "none";
    String MODE_NAVIGATE = "navigate";
    String DEST_OBJECT = "object";
    String DEST_EMBED = "embed";
    String CROSS_SITE = "cross-site";
    String CORS = "cors";
    String DEST_SCRIPT = "script";
    String DEST_IMAGE = "image";

    boolean isRequestAllowed(HttpServletRequest request);

}
