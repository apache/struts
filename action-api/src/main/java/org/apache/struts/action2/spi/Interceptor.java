package org.apache.struts.action2.spi;

/**
 * Intercepts an action request.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface Interceptor {

    /**
     * Intercepts an action request.
     *
     * @param request current request
     */
    String intercept(Request request) throws Exception;
}
