package org.apache.struts2.interceptor.httpmethod;

/**
 * Enum represents possible http request types
 *
 * @see HttpMethodInterceptor
 * @since 2.3.18
 */
public enum HttpMethod {

    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    OPTIONS,
    CONNECT,
    PATCH;

    public static HttpMethod parse(String httpRequestMethod) {
        return valueOf(httpRequestMethod.toUpperCase());
    }

}
