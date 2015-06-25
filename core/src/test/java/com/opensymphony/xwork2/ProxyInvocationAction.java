package com.opensymphony.xwork2;

/**
 * Need by the ProxyInvocationTest
 */
public class ProxyInvocationAction extends ActionSupport implements ProxyInvocationInterface {
    public String show() {
        return "proxyResult";
    }
}
