/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * WebWork, Web Application Framework
 *
 * Distributable under Apache license.
 * See terms of license at opensource.org
 */
package com.opensymphony.webwork.interceptor;

import java.util.Map;


/**
 * This interface gives actions an alternative way of receiving input parameters. The map will
 * contain all input parameters as name/value entries. Actions that need this should simply implement it. <p>
 * <p/>
 * One common use for this is to have the action propagate parameters to internally instantiated data
 * objects. <p>
 * <p/>
 * Note that all parameter values for a given name will be returned, so the type of the objects in
 * the map is <tt>java.lang.String[]</tt>.
 *
 * @author <a href="mailto:rickard@middleware-company.com">Rickard Öberg</a>
 */
public interface ParameterAware {

    /**
     * Sets the  map of input parameters in the implementing class.
     *
     * @param parameters a Map of parameters (name/value Strings).
     */
    public void setParameters(Map parameters);
}
