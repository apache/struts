package org.apache.struts.action2;

import java.util.List;
import java.util.Set;

/**
 * Request and field-scoped messages or errors. Uses keys instead of actual messages to decouple code from messages.
 * Messages may come from multiple actions and interceptors.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface Messages {

    /**
     * Adds request-scoped message.
     *
     * @param key message key
     */
    void add(String key);

    /**
     * Adds request-scoped message.
     *
     * @param key message key
     * @param arguments message arguments
     */
    void add(String key, Object... arguments);

    /**
     * Adds field-scoped message.
     *
     * @param fieldName name of field to attach message to
     * @param key message key
     */
    void add(String fieldName, String key);

    /**
     * Adds field-scoped message.
     *
     * @param fieldName name of field to attach message to
     * @param key message key
     * @param arguments message arguments
     */
    void add(String fieldName, String key, Object... arguments);

    /**
     * Gets request-scoped messages.
     *
     * @return unmodifiable list of messages for this request.
     */
    List<String> forRequest();

    /**
     * Gets field-scoped messages.
     *
     * @param fieldName field name
     * @return unmodifiable list of messages for the given field name.
     */
    List<String> forField(String fieldName);

    /**
     * Gets names of fields which have messages attached.
     *
     * @return unmodifiable set of field names with messages attached.
     */
    Set<String> getFieldNames();

    /**
     * Returns true if no request or field-scoped messages have been added.
     */
    boolean isEmpty();
}
