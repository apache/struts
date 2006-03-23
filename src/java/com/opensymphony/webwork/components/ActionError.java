/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Render action errors if they exists the specific layout of the rendering depends on 
 * the theme itself.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 *    &lt;ww:actionerror /&gt;
 *    &lt;ww:form .... &gt;>
 *       ....
 *    &lt;/ww:form&gt;
 *    
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author tm_jee
 * @version $Date: 2006/01/01 15:16:03 $ $Id: ActionError.java,v 1.5 2006/01/01 15:16:03 tmjee Exp $
 * @ww.tag name="actionerror" tld-body-content="empty" tld-tag-class="com.opensymphony.webwork.views.jsp.ui.ActionErrorTag"
 * description="Render action errors if they exists"
 * @since 2.2
 */
public class ActionError extends UIBean {

    public static final String TEMPLATE = "actionerror";


    public ActionError(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

}
