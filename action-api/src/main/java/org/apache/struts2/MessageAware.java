package org.apache.struts2;

/**
 * Implemented by actions which may need to record errors or messages.
 *
 * <pre>
 *   static import ResultNames.*;
 *
 *   public class SetName implements MessageAware {
 *
 *     Messages messages;
 *     String name;
 *
 *     public String execute() {
 *       return SUCCESS;
 *     }
 *
 *     public void setName(String name) {
 *       if ("".equals(name))
 *         messages.forField("name").addError("name.required");
 *
 *       this.name = name;
 *     }
 *
 *     public void setMessages(Messages messages) {
 *       this.messages = messages;
 *     }
 *   }
 * </pre>
 *
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
