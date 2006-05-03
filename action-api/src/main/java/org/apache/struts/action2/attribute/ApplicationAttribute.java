package org.apache.struts.action2.attribute;

import org.apache.struts.action2.spi.Request;
import org.apache.struts.action2.spi.ThreadLocalRequest;

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
        Request request = ThreadLocalRequest.get();
        synchronized (request.getServletContext()) {
            return unitOfWork.execute(request.getApplicationMap());
        }
    }
}
