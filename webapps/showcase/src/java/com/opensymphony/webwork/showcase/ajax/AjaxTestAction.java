package com.opensymphony.webwork.showcase.ajax;

import com.opensymphony.xwork.Action;


/**
 * @author Ian Roughley
 * @version $Id: AjaxTestAction.java,v 1.1 2006/01/09 21:14:15 plightbo Exp $
 */
public class AjaxTestAction implements Action {

    private static int counter = 0;
    private String data;

    public long getServerTime() {
        return System.currentTimeMillis();
    }

    public int getCount() {
        return ++counter;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }
}
