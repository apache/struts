/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Render action messages if they exists, specific rendering layout depends on the 
 * theme itself.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *    &lt;ww:actionmessage /&gt;
 *    &lt;ww:form .... &gt;
 *       ....
 *    &lt;/ww:form&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author tm_jee
 * @version $Date: 2006/01/07 20:43:28 $ $Id: ActionMessage.java,v 1.6 2006/01/07 20:43:28 plightbo Exp $
 * @ww.tag name="actionmessage" tld-body-content="empty" tld-tag-class="org.apache.struts.action2.views.jsp.ui.ActionMessageTag"
 * description="Render action messages if they exists"
 * @since 2.2
 */
public class ActionMessage extends UIBean {

    private static final String TEMPLATE = "actionmessage";

    public ActionMessage(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
}
