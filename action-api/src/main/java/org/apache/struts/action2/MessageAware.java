package org.apache.struts.action2;

/**
 * Implemented by actions that may need to record messages.
 *
 * <pre>
 *   static import ResultNames.*;
 *
 *   public class Welcome implements MessageAware {
 *
 *     Messages messages;
 *
 *     public String execute() {
 *       messages.add("welcome");
 *       return SUCCESS;
 *     }
 *
 *     public void setMessages(Messages messages) {
 *       this.messages = messages;
 *     }
 *   }
 * </pre>
 *
 * @see ErrorAware
 * @author crazybob@google.com (Bob Lee)
 */
public interface MessageAware {

    /**
     * Sets messages.
     *
     * @param messages messages
     */
    void setMessages(Messages messages);
}
