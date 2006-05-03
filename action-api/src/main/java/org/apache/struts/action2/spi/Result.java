package org.apache.struts.action2.spi;

/**
 * The result of an action request. Struts creates a new {@code Result} instance for each request.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface Result {

    /**
     * Executes result.
     *
     * @param request
     */
    void execute(Request request) throws Exception;
}
