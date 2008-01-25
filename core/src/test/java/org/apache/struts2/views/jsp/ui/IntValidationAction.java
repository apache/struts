package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.ActionSupport;

/**
 *
 */
public class IntValidationAction extends ActionSupport {
    private int longint;

    public int getLongint() {
        return longint;
    }

    public void setLongint(int longint) {
        this.longint = longint;
    }
}
