package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders two HTML select elements with second one changing displayed values depending on selected entry of first one.
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;a:doubleselect label="doubleselect test1" name="menu" list="{'fruit','other'}" doubleName="dishes" doubleList="top == 'fruit' ? {'apple', 'orange'} : {'monkey', 'chicken'}" /&gt;
 * &lt;a:doubleselect label="doubleselect test2" name="menu" list="#{'fruit':'Nice Fruits', 'other':'Other Dishes'}" doubleName="dishes" doubleList="top == 'fruit' ? {'apple', 'orange'} : {'monkey', 'chicken'}" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision: 1.11 $
 * @since 2.2
 *
 * @ww.tag name="doubleselect" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.DoubleSelectTag"
 * description="Renders two HTML select elements with second one changing displayed values depending on selected entry of first one."
 */
public class DoubleSelect extends DoubleListUIBean {
    final public static String TEMPLATE = "doubleselect";


    public DoubleSelect(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        // force the onchange parameter
        addParameter("onchange", getParameters().get("name") + "Redirect(this.options.selectedIndex)");
    }
}
