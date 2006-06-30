package org.apache.struts2.spi;

import org.apache.struts2.spi.RequestContext;

/**
 * Implemented by actions that need access to the current {@link org.apache.struts2.spi.RequestContext}. Use
 * judiciously.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface RequestContextAware {

    /**
     * Sets {@link org.apache.struts2.spi.RequestContext}.
     *
     * @param requestContext
     */
    void setRequestContext(RequestContext requestContext);
}
