package org.apache.struts2.spi;

import org.apache.struts2.Messages;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Request context. A single request may span multiple actions with action chaining.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface RequestContext {

    /**
     * Gets context of the currently executing action.
     *
     * @return current action context
     */
    ActionContext getActionContext();

    /**
     * Convenience method.&nbsp;Equivalent to {@code getActionContext().getAction()}.
     *
     * @return currently executing action
     */
    Object getAction();

    /**
     * Gets map of request parameters.
     */
    Map<String, String[]> getParameterMap();

    /**
     * Gets map of request attributes.
     */
    Map<String, Object> getAttributeMap();

    /**
     * Gets map of session attributes.
     */
    Map<String, Object> getSessionMap();

    /**
     * Gets map of application (servlet context) attributes.
     */
    Map<String, Object> getApplicationMap();

    /**
     * Finds cookies with the given name,
     */
    List<Cookie> findCookiesForName(String name);

    /**
     * Gets locale.
     */
    Locale getLocale();

    /**
     * Sets locale. Stores the locale in the session for future requests.
     */
    void setLocale(Locale locale);

    /**
     * Gets messages.
     */
    Messages getMessages();

    /**
     * Gets the servlet request.
     */
    HttpServletRequest getServletRequest();

    /**
     * Gets the servlet response.
     */
    HttpServletResponse getServletResponse();

    /**
     * Gets the servlet context.
     */
    ServletContext getServletContext();

    /**
     * Gets the value stack.
     */
    ValueStack getValueStack();

    /**
     * Invokes the next interceptor or the action method if no more interceptors remain.
     *
     * @return result name
     * @throws IllegalStateException if already invoked or called from the action
     */
    String proceed() throws Exception;
}
