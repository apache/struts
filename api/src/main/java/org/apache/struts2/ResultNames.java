package org.apache.struts2;

/**
 * Commonly used result names returned by action methods.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public final class ResultNames {

    private ResultNames() {}

    /**
     * The action executed successfully.
     */
    public static final String SUCCESS = "success";

    /**
     * The action requires more input, i.e.&nbsp;a validation error occurred.
     */
    public static final String INPUT = "input";

    /**
     * The action requires the user to log in before executing.
     */
    public static final String LOGIN = "login";

    /**
     * The action execution failed irrecoverably.
     */
    public static final String ERROR = "error";

    /**
     * The action executed successfully, but do not execute a result.
     */
    public static final String NONE = "none";
}
