package com.opensymphony.webwork.views.util;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.webwork.RequestUtils;

/**
 * User: plightbo
 * Date: May 15, 2005
 * Time: 6:36:59 PM
 */
public class ResourceUtil {
    public static String getResourceBase(HttpServletRequest req) {
        String path = RequestUtils.getServletPath(req);
        if (path == null || "".equals(path)) {
            return "";
        }

        return path.substring(0, path.lastIndexOf('/'));
    }
}
