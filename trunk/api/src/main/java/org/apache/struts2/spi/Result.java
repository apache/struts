package org.apache.struts2.spi;

import org.apache.struts2.spi.RequestContext;

/**
 * The result of an action request. Struts creates a new {@code Result} instance for each request.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface Result {

    /**
     * Executes result.
     *
     * @param requestContext
     */
    void execute(RequestContext requestContext) throws Exception;
}
