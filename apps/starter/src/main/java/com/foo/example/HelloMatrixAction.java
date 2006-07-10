/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foo.example;

import com.opensymphony.xwork2.ActionSupport;

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
