/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a reset button. The reset tag is used together with the form tag to provide form resetting.
 * The reset can have two different types of rendering:
 * <ul>
 * <li>input: renders as html &lt;input type="reset"...&gt;</li>
 * <li>button: renders as html &lt;button type="reset"...&gt;</li>
 * </ul>
 * Please note that the button type has advantages by adding the possibility to seperate the submitted value from the
 * text shown on the button face, but has issues with Microsoft Internet Explorer at least up to 6.0
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;a:reset value="%{'Reset'}" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * Render an button reset:
 * &lt;a:reset type="button" value="%{'Reset'}" label="Reset the form"/&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * @author Rene Gielen
 * @version $Revision$
 * @since 2.2.2
 *
 * @a2.tag name="reset" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.ResetTag"
 * description="Render a reset button"
 */
public class Reset extends FormButton {
    final public static String TEMPLATE = "reset";

    protected String action;
    protected String method;
    protected String align;
    protected String type;

    public Reset(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return Reset.TEMPLATE;
    }

    public void evaluateParams() {

        if (value == null) {
            value = "Reset";
        }

        super.evaluateParams();
    }

    /**
     * Indicate whether the concrete button supports the type "image".
     *
     * @return <tt>false</tt> to indicate type image is supported.
     */
    protected boolean supportsImageType() {
        return false;
    }

    /**
     * Supply a reset button text apart from reset value. Will have no effect for <i>input</i> type reset, since button
     * text will always be the value parameter.
     *
     * @a2.tagattribute required="false"
     */
    public void setLabel(String label) {
        super.setLabel(label);
    }

}
