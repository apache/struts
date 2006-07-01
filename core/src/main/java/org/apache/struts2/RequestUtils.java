/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2;

import javax.servlet.http.HttpServletRequest;


/**
 * Request handling utility class.
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
