package com.opensymphony.webwork.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders an HTML input element of type hidden, populated by the specified property from the OgnlValueStack.
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;-- example one --&gt;
 * &lt;ww:hidden name="foo" /&gt;
 * &lt;-- example two --&gt;
 * &lt;ww:hidden name="foo" value="bar" /&gt;
 *
 * Example One Resulting HTML (if foo evaluates to bar):
 * &lt;input type="hidden" name="foo" value="bar" /&gt;
 * Example Two Resulting HTML (if getBar method of the action returns 'bar')
 * &lt;input type="hidden" name="foo" value="bar" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision: 1.5 $
 * @since 2.2
 *
 * @ww.tag name="hidden" tld-body-content="JSP" tld-tag-class="com.opensymphony.webwork.views.jsp.ui.HiddenTag"
 * description="Render a hidden input field"
  */
public class Hidden extends UIBean {
    final public static String TEMPLATE = "hidden";

    public Hidden(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
}
