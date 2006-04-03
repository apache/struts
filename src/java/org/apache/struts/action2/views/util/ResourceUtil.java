package org.apache.struts.action2.views.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action2.RequestUtils;

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
