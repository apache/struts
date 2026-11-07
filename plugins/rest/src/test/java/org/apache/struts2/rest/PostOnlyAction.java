package org.apache.struts2.rest;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.interceptor.httpmethod.HttpPost;

/** State-changing action that declares itself POST-only. */
@HttpPost
public class PostOnlyAction extends ActionSupport {
    public static volatile boolean invoked;

    @Override
    public String execute() {
        invoked = true;
        return NONE;
    }
}
