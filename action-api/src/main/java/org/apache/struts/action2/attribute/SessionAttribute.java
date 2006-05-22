package org.apache.struts.action2.attribute;

import org.apache.struts.action2.spi.RequestContext;
import org.apache.struts.action2.spi.ThreadLocalRequestContext;

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
        RequestContext requestContext = ThreadLocalRequestContext.get();
        synchronized (requestContext.getServletRequest().getSession()) {
            return unitOfWork.execute(requestContext.getSessionMap());
        }
    }
}
