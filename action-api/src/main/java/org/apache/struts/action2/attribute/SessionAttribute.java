package org.apache.struts.action2.attribute;

import org.apache.struts.action2.spi.Request;
import org.apache.struts.action2.spi.ThreadLocalRequest;

/**
 * A session attribute. Synchronizes on the underlying {@code HttpSession}.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public class SessionAttribute<T> extends AbstractAttribute<T> {

    /**
     * Creates a new attribute.
     *
     * @param name attribute name
     */
    public SessionAttribute(String name) {
        super(name);
    }

    T execute(UnitOfWork<T> unitOfWork) {
        Request request = ThreadLocalRequest.get();
        synchronized (request.getServletRequest().getSession()) {
            return unitOfWork.execute(request.getSessionMap());
        }
    }
}
