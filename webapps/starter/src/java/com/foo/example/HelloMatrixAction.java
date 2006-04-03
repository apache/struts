/*
 * Copyright (c) 2006, Your Corporation. All Rights Reserved.
 */

package com.foo.example;

import com.opensymphony.xwork.ActionSupport;

/**
 * <code>HelloMatrixAction</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class HelloMatrixAction extends ActionSupport {

    /** Spring managed bean reference */
    private CounterBean counterBean;

    /**
     * IoC setter for the spring managed CounterBean.
     *
     * @param counterBean
     */
    public void setCounterBean(CounterBean counterBean) {
        this.counterBean = counterBean;
    }

    private String hello;
    private String message;

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return counterBean.getCount();
    }

    /**
     * A default implementation that does nothing an returns "success".
     *
     * @return {@link #SUCCESS}
     */
    public String execute() throws Exception {
        return SUCCESS;
    }

    /**
     * Sample sayHello method.
     *
     * @return {@link #SUCCESS}
     */
    public String sayHello() throws Exception {

        message = "users already took the red pill...";
        counterBean.increment();
        return SUCCESS;
    }
}
