package com.opensymphony.xwork2.interceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


public class DefaultResourceIsolationPolicy implements ResourceIsolationPolicy {

    private List<String> exemptedPaths = new ArrayList<String>();

    public List<String> getExemptedPaths(){
        return this.exemptedPaths;
    }

    public void setExemptedPaths(List<String> paths){
        this.exemptedPaths = paths;
    }

    @Override
    public boolean isRequestAllowed(HttpServletRequest request) {

        String site = request.getHeader(SEC_FETCH_SITE_HEADER);

        // Allow same-site and browser-initiated requests
        if (SAME_ORIGIN.equals(site) || SAME_SITE.equals(site) || NONE.equals(site)) {
            return true;
        }

        // Apply exemptions: paths/endpoints meant to be served cross-origin
        if (this.exemptedPaths.contains(request.getContextPath())) {
            return true;
        }

        // Allow requests from browsers which don't send Fetch Metadata
        if (request.getHeader(SEC_FETCH_SITE_HEADER) == null){
            return true;
        }

        // Allow simple top-level navigations except <object> and <embed>
        return isAllowedTopLevelNavigation(request);
    }

    private boolean isAllowedTopLevelNavigation(HttpServletRequest request)
    {
        String mode = request.getHeader(SEC_FETCH_MODE_HEADER);
        String dest = request.getHeader(SEC_FETCH_DEST_HEADER);

        boolean isSimpleTopLevelNavigation = MODE_NAVIGATE.equals(mode) || "GET".equals(request.getMethod());
        boolean isNotObjectOrEmbedRequest = !DEST_EMBED.equals(dest) && !DEST_OBJECT.equals(dest);

        return isSimpleTopLevelNavigation && isNotObjectOrEmbedRequest;
    }

}
