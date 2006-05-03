package org.apache.struts.action2;

/**
 * Default action interface. Provided purely for user convenience. Struts does not require actions to implement any
 * interfaces. Actions need only implement a public, no argument method which returns {@code String}. If a user does
 * not specify a method name, Struts defaults to {@code execute()}.
 *
 * <p>For example:
 *
 * <pre>
 *   static import ResultNames.*;
 *
 *   public class MyAction <b>implements Action</b> {
 *
 *     public String execute() {
 *       return SUCCESS;
 *     }
 *   }
 * </pre>
 *
 * <p>is equivalent to:
 *
 * <pre>
 *   static import ResultNames.*;
 *
 *   public class MyAction {
 *
 *     public String execute() {
 *       return SUCCESS;
 *     }
 *   }
 * </pre>
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface Action {

    /**
     * Executes this action.
     *
     * @return result name which matches a result name from the action mapping in the configuration file. See {@link
     *  ResultNames} for common suggestions.
     */
    String execute();
}
