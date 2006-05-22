package org.apache.struts.action2.attribute;

import org.apache.struts.action2.spi.RequestContext;
import org.apache.struts.action2.spi.ThreadLocalRequestContext;

/**
 * A servlet context attribute. Synchronizes on the underlying {@code ServletContext} instance.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public class ApplicationAttribute<T> extends AbstractAttribute<T> {

    /**
     * Creates a new attribute.
     *
     * @param name attribute name
     */
    public ApplicationAttribute(String name) {
        super(name);
    }

    T execute(UnitOfWork<T> unitOfWork) {
        RequestContext requestContext = ThreadLocalRequestContext.get();
        synchronized (requestContext.getServletContext()) {
            return unitOfWork.execute(requestContext.getApplicationMap());
        }
    }
}
