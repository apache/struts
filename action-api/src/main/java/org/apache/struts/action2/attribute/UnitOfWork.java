package org.apache.struts.action2.attribute;

import java.util.Map;

/**
 * Unit of work which should be executed atomically.
 *
 * @author crazybob@google.com (Bob Lee)
 */
interface UnitOfWork<T> {

    T execute(Map<String, Object> map);
}
