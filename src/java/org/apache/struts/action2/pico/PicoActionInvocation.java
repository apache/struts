/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.apache.struts.action2.pico;

import java.util.Map;

import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionInvocation;

/**
 * Implementation of {@link com.opensymphony.xwork.ActionInvocation ActionInvocation}
 * which uses a PicoContainer to create Action instances.
 * 
 * @author Chris Sturm
 * @author Aslak Helles&oslash;y
 * @deprecated Use DefaultActionInvocation 
 */
public class PicoActionInvocation extends DefaultActionInvocation {

    public PicoActionInvocation(ActionProxy proxy) throws Exception {
        super(proxy);
    }

    public PicoActionInvocation(ActionProxy proxy, Map extraContext) throws Exception {
        super(proxy, extraContext);
    }

    public PicoActionInvocation(ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        super(proxy, extraContext, pushAction);
    }

}
