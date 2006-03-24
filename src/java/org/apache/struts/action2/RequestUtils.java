/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork;

import javax.servlet.http.HttpServletRequest;


/**
 * Request handling utility class.
 * 
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 */
public class RequestUtils {

    /**
     * Retrieves the current request servlet path.
     * Deals with differences between servlet specs (2.2 vs 2.3+)
     * 
     * @param request the request
     * @return the servlet path
     */
    public static String getServletPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        
        if (null != servletPath && !"".equals(servletPath)) {
            return servletPath;
        }
        
        String requestUri = request.getRequestURI();
        int startIndex = request.getContextPath().equals("") ? 0 : request.getContextPath().length();
        int endIndex = request.getPathInfo() == null ? requestUri.length() : requestUri.lastIndexOf(request.getPathInfo());
        
        if (startIndex > endIndex) { // this should not happen
            endIndex = startIndex;
        }
        
        return requestUri.substring(startIndex, endIndex);
    }
    
}
