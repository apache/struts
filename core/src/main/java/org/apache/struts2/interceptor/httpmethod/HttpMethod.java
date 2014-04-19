package org.apache.struts2.interceptor.httpmethod;

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
