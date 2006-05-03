package org.apache.struts.action2.spi;

/**
 * Implemented by actions that need access to the current {@link Request}. Use judiciously.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface RequestAware {

    /**
     * Sets {@link Request}.
     *
     * @param request
     */
    void setRequest(Request request);
}
