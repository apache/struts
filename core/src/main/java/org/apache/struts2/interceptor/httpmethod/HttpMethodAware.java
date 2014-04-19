package org.apache.struts2.interceptor.httpmethod;

/**
 * Action when implements this interface is notified about what method was used to perform request,
 * it works in connection with {@link org.apache.struts2.interceptor.httpmethod.HttpMethodInterceptor}
 *
 * Another function of this interface is to return result which should be returned when action
 * was called with wrong http method
 *
 * @since 2.3.18
 */
public interface HttpMethodAware {

    /**
     * Notifies action about http method used to perform request
     *
     * @param httpMethod {@link javax.servlet.http.HttpServletRequest#getMethod()} translated to enum
     */
    public void setMethod(HttpMethod httpMethod);

    /**
     * Action name to use when action was requested with wrong http method
     * can return null and then default result name will be used instead defined
     * in {@link org.apache.struts2.interceptor.httpmethod.HttpMethodInterceptor}
     *
     * @return result name or null
     */
    public String getBadRequestResultName();

}
