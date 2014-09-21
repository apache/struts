package org.apache.struts2.interceptor.httpmethod;

/**
 * Action when implements this interface is notified about what method was used to perform request,
 * it works in connection with {@link HttpMethodInterceptor}
 *
 * Another function of this interface is to return result which should be returned when action
 * was called with wrong http method
 *
 * @see HttpMethodInterceptor
 * @since 2.5
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
     * in {@link HttpMethodInterceptor}
     *
     * @return result name or null
     */
    public String getBadRequestResultName();

}
