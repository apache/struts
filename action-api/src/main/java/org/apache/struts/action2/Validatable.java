package org.apache.struts.action2;

/**
 * Implemented by actions which wish to execute some validation logic before their action method. Useful for
 * cross-field validations.
 *
 * @see ErrorAware
 * @author crazybob@google.com (Bob Lee)
 */
public interface Validatable {

    /**
     * Validates input. Executes before action method.
     */
    public void validate();
}
