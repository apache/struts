package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Creates a series of checkboxes from a list. Setup is like &lt;a:select /&gt; or &lt;a:radio /&gt;, but creates checkbox tags.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;a:checkboxlist name="foo" list="bar"/&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision: 1.6 $
 * @since 2.2
 *
 * @ww.tag name="checkboxlist" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.CheckboxListTag"
 * description="Render a list of checkboxes"
  */
public class CheckboxList extends ListUIBean {
    final public static String TEMPLATE = "checkboxlist";

    public CheckboxList(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
}
