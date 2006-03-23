/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.interceptor;

import javax.servlet.http.HttpServletResponse;


/**
 * All Actions that want to have access to the servlet response object must implement this interface.<p>
 * <p/>
 * This interface is only relevant if the Action is used in a servlet environment.<p>
 * <p/>
 * Note that using this interface makes the Action tied to a servlet environment, so it should be
 * avoided if possible since things like unit testing will become more difficult.
 *
 * @author <a href="mailto:rickard@middleware-company.com">Rickard Öberg</a>
 */
public interface ServletResponseAware {

    /**
     * Sets the HTTP response object in implementing classes.
     *
     * @param response the HTTP response.
     */
    public void setServletResponse(HttpServletResponse response);
}
