package com.opensymphony.webwork.views;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 8:27:24 AM
 */
public class JspSupportServlet extends HttpServlet {
    public static JspSupportServlet jspSupportServlet;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        jspSupportServlet = this;
    }
}
