package org.apache.struts2.spi;

import java.lang.reflect.Method;

/**
 * Context of an action execution.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface ActionContext {

    /**
     * Gets action instance.
     */
    Object getAction();

    /**
     * Gets action method.
     */
    Method getMethod();

    /**
     * Gets action name.
     */
    String getActionName();

    /**
     * Gets the path for the action's namespace.
     */
    String getNamespacePath();

    /**
     * Gets the {@link Result} instance for the action.
     *
     * @return {@link Result} instance or {@code null} if we don't have a result yet.
     */
    Result getResult();

    /**
     * Adds a result interceptor for the action. Enables executing code before and after a result, executing an
     * alternate result, etc.
     */
    void addResultInterceptor(Result interceptor);

    /**
     * Gets context of action which chained to us.
     *
     * @return context of previous action or {@code null} if this is the first action in the chain
     */
    ActionContext getPrevious();

    /**
     * Gets context of action which this action chained to.
     *
     * @return context of next action or {@code null} if we haven't chained to another action yet or this is the last
     *  action in the chain.
     */
    ActionContext getNext();
}
