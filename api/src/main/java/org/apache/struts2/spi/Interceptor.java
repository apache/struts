package org.apache.struts2.spi;

/**
 * Intercepts an action request.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface Interceptor {

    /**
     * Intercepts an action request.
     *
     * @param requestContext current request context
     */
    String intercept(RequestContext requestContext) throws Exception;
}
