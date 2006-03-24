/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.webwork.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Renders a tree node within a tree widget with AJAX support.<p/>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;treenode .../&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * Created : Dec 12, 2005 3:53:40 PM
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 *
 * @ww.tag name="treenode" tld-body-content="JSP" tld-tag-class="com.opensymphony.webwork.views.jsp.ui.TreeNodeTag"
 * description="Render a tree node within a tree widget."
 */
public class TreeNode extends ClosingUIBean {
    private static final String TEMPLATE = "treenode-close";
    private static final String OPEN_TEMPLATE = "treenode";

    public TreeNode(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    /**
     * Label expression used for rendering tree node label.
     * @ww.tagattribute required="true"
     */
    public void setLabel(String label) {
        super.setLabel(label);
    }
}
