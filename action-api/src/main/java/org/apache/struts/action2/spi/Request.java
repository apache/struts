package org.apache.struts.action2.spi;

import org.apache.struts.action2.Messages;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Method;

/**
 * A Struts request. A single request may span multiple actions with action chaining.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface Request {

    /**
     * Gets current action instance.
     */
    Object getAction();

    /**
     * Gets current action method.
     */
    Method getMethod();

    /**
     * Gets current action name.
     */
    String getActionName();

    /**
     * Gets the path for the current action's namespace.
     */
    String getNamespacePath();

    /**
     * Gets the {@link Result} instance for the current action.
     *
     * @return {@link Result} instance or {@code null} if we don't have a result yet.
     */
    Result getResult();

    /**
     * Adds a result interceptor for the current action. Enables executing code before and after a result, executing
     * an alternate result, etc.
     */
    void addResultInterceptor(Result interceptor);

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
     * Gets error messages.
     */
    Messages getErrors();

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
