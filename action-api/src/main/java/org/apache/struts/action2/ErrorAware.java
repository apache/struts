package org.apache.struts.action2;

/**
 * Implemented by actions that may need to record error messages. For example:
 *
 * <pre>
 *   static import ResultNames.*;
 *
 *   public class SetName implements ErrorAware {
 *
 *     Messages errors;
 *     String name;
 *
 *     public String execute() {
 *       if ("".equals(name) {
 *         errors.add("name.required");
 *         return INPUT;
 *       }
 *
 *       ...
 *       return SUCCESS;
 *     }
 *
 *     public void setErrors(Messages errors) {
 *       this.errors = errors;
 *     }
 *
 *     public void setName(String name) {
 *       this.name = name;
 *     }
 *   }
 * </pre>
 *
 * @see MessageAware
 * @author crazybob@google.com (Bob Lee)
 */
public interface ErrorAware {

    /**
     * Sets error messages.
     *
     * @param errors error messages
     */
    void setErrors(Messages errors);
}
