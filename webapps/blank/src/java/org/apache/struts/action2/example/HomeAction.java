/*
 * Copyright (c) 2006, Your Corporation. All Rights Reserved.
 */

package org.apache.struts.action2.example;

import com.opensymphony.xwork.ActionSupport;

/**
 * <code>HomeAction</code>
 *
 * @author Rainer Hermanns
 */
public class HomeAction extends ActionSupport {

	private static final long serialVersionUID = -3881551454078687096L;

	/**
     * A default implementation that does nothing an returns "success".
     *
     * @return {@link #SUCCESS}
     */
    public String execute() throws Exception {
        return SUCCESS;
    }
}
