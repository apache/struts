package org.apache.struts.action2.attribute;

import org.apache.struts.action2.spi.RequestContext;
import org.apache.struts.action2.spi.ThreadLocalRequestContext;

/**
 * A request attribute. Synchronizes on the underlying {@code HttpServletRequest} instance.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public class RequestAttribute<T> extends AbstractAttribute<T> {

    /**
     * Creates a new attribute.
     *
     * @param name attribute name
     */
    RequestAttribute(String name) {
        super(name);
    }

    T execute(UnitOfWork<T> unitOfWork) {
        RequestContext requestContext = ThreadLocalRequestContext.get();
        synchronized (requestContext.getServletRequest()) {
            return unitOfWork.execute(requestContext.getAttributeMap());
        }
    }
}
