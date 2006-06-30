package org.apache.struts2;

import java.util.List;
import java.util.Set;
import java.util.Map;

/**
 * Collection of messages. Supports nesting messages by field name.
 *
 * <p>Uses keys when adding instead of actual messages to decouple code from messages.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface Messages {

    /**
     * Message severity.
     */
    public enum Severity {

        /**
         * Informational messages.
         */
        INFO,

        /**
         * Warning messages.
         */
        WARN,

        /**
         * Error messages.
         */
        ERROR,
    }

    /**
     * Gets nested messages for the given field.
     *
     * <p>Supports dot notation to represent nesting. For example:
     *
     * <pre>
     * messages.forField("foo").forField("bar") == messages.forField("foo.bar")
     * </pre>
     *
     * @param fieldName name of the field
     * @return nested {@code Messages} for given field name
     */
    Messages forField(String fieldName);

    /**
     * Gets map of field name to messages for that field.
     *
     * @return map of field name to {@code Messages}
     */
    Map<String, Messages> forFields();

    /**
     * Adds informational message.
     *
     * @param key message key
     * @see Severity.INFO
     */
    void addInformation(String key);

    /**
     * Adds informational message.
     *
     * @param key message key
     * @param arguments message arguments
     * @see Severity.INFO
     */
    void addInformation(String key, Object... arguments);

    /**
     * Adds warning message.
     *
     * @param key message key
     * @see Severity.WARN
     */
    void addWarning(String key);

    /**
     * Adds warning message.
     *
     * @param key message key
     * @param arguments message arguments
     * @see Severity.WARN
     */
    void addWarning(String key, Object... arguments);

    /**
     * Adds error message.
     *
     * @param key message key
     * @see Severity.ERROR
     */
    void addError(String key);

    /**
     * Adds error message.
     *
     * @param key message key
     * @param arguments message arguments
     * @see Severity.ERROR
     */
    void addError(String key, Object... arguments);

    /**
     * Adds message.
     *
     * @param severity message severity
     * @param key message key
     */
    void add(Severity severity, String key);

    /**
     * Adds request-scoped message.
     *
     * @param severity message severity
     * @param key message key
     * @param arguments message arguments
     */
    void add(Severity severity, String key, Object... arguments);

    /**
     * Gets set of severities for which this {@code Messages} instance has messages. Not recursive.
     *
     * @return unmodifiable set of {@link Severity} sorted from least to most severe
     */
    Set<Severity> getSeverities();

    /**
     * Gets message strings for the given severity. Not recursive.
     *
     * @param severity message severity
     * @return unmodifiable list of messages
     */
    List<String> forSeverity(Severity severity);

    /**
     * Gets error message strings for this {@code Messages} instance. Not recursive.
     *
     * @return unmodifiable list of messages
     */
    List<String> getErrors();

    /**
     * Gets error message strings for this {@code Messages} instance. Not recursive.
     *
     * @return unmodifiable list of messages
     */
    List<String> getWarnings();

    /**
     * Gets informational message strings for this {@code Messages} instance. Not recursive.
     *
     * @return unmodifiable list of messages
     */
    List<String> getInformation();

    /**
     * Returns true if this or a nested {@code Messages} instance has error messages.
     *
     * @see Severity.ERROR
     */
    boolean hasErrors();

    /**
     * Returns true if this or a nested {@code Messages} instance has warning messages.
     *
     * @see Severity.WARN
     */
    boolean hasWarnings();

    /**
     * Returns true if this or a nested {@code Messages} instance has informational messages.
     *
     * @see Severity.INFO
     */
    boolean hasInformation();

    /**
     * Returns true if this and all nested {@code Messages} instances have no messages.
     */
    boolean isEmpty();

    /**
     * Returns true if this and all nested {@code Messages} instances have no messages for the given severity.
     *
     * @param severity message severity
     */
    boolean isEmpty(Severity severity);
}