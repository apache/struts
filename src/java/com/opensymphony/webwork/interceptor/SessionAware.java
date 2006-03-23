/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.interceptor;

import java.util.Map;


/**
 * Actions that want access to the user's HTTP session should implement this interface.<p>
 * <p/>
 * This interface is only relevant if the Action is used in a servlet environment.<p>
 * <p/>
 * Note that using this interface makes the Action tied to a servlet environment, so it should be
 * avoided if possible since things like unit testing will become more difficult.
 *
 * @author <a href="mailto:rickard@middleware-company.com">Rickard Öberg</a>
 */
public interface SessionAware {

    /**
     * Sets the Map of session attributes in the implementing class.
     *
     * @param session a Map of HTTP session attribute name/value pairs.
     */
    public void setSession(Map session);
}
