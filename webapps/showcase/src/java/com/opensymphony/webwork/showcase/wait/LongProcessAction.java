package com.opensymphony.webwork.showcase.wait;

import com.opensymphony.xwork.ActionSupport;

/**
 * Example to illustrate the <code>execAndWait</code> interceptor.
 *
 * @author Claus Ibsen
 */
public class LongProcessAction extends ActionSupport {

    private int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String execute() throws Exception {
        System.err.println("time: " + time);
        Thread.sleep(time);

        return SUCCESS;
    }

}
