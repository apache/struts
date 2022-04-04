package org.apache.struts2.dispatcher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of this interface is used to handle internal errors or missing resources.
 * Basically it sends back HTTP error codes or error page depends on requirements.
 */
public interface DispatcherErrorHandler {

    /**
     * Init instance after creating {@link org.apache.struts2.dispatcher.Dispatcher}
     * @param ctx current {@link javax.servlet.ServletContext}
     */
    public void init(ServletContext ctx);

    /**
     * Handle passed error code or exception
     *
     * @param request current {@link javax.servlet.http.HttpServletRequest}
     * @param response current {@link javax.servlet.http.HttpServletResponse}
     * @param code HTTP Error Code, see {@link javax.servlet.http.HttpServletResponse} for possible error codes
     * @param e Exception to report
     */
    public void handleError(HttpServletRequest request, HttpServletResponse response, int code, Exception e);

}
