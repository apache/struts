/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.interceptor;

import java.util.Map;


/**
 * Actions that want to be aware of the application Map object should implement this interface.
 * This will give them access to a Map where they can put objects that should be available
 * to other parts of the application. <p>
 * <p/>
 * Typical uses are configuration objects and caches.
 *
 * @author <a href="mailto:rickard@middleware-company.com">Rickard Öberg</a>
 */
public interface ApplicationAware {

    /**
     * Sets the map of application properties in the implementing class.
     *
     * @param application a Map of application properties.
     */
    public void setApplication(Map application);
}
