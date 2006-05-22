package org.apache.struts.action2.spi;

/**
 * Implemented by actions that need access to the current {@link org.apache.struts.action2.spi.RequestContext}. Use
 * judiciously.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface RequestContextAware {

    /**
     * Sets {@link org.apache.struts.action2.spi.RequestContext}.
     *
     * @param requestContext
     */
    void setRequestContext(RequestContext requestContext);
}
