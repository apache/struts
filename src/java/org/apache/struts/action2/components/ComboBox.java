package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * The combo box is basically an HTML INPUT of type text and HTML SELECT grouped together to give you a combo box
 * functionality. You can place text in the INPUT control by using the SELECT control or type it in directly in
 * the text field.<p/>
 *
 * In this example, the SELECT will be populated from id=year attribute. Counter is itself an Iterator. It will
 * span from first to last. The population is done via javascript, and requires that this tag be surrounded by a
 * &lt;form&gt;.<p/>
 *
 * Note that unlike the &lt;ww:select/&gt; tag, there is no ability to define the individual &lt;option&gt; tags' id attribute
 * or content separately. Each of these is simply populated from the toString() method of the list item. Presumably
 * this is because the select box isn't intended to actually submit useful data, but to assist the user in filling
 * out the text field.<p/>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * JSP:
 * &lt;ww:bean name="webwork.util.Counter" id="year"&gt;
 *   &lt;ww:param name="first" value="text('firstBirthYear')"/&gt;
 *   &lt;ww:param name="last" value="2000"/&gt;
 *
 *   &lt;ww:combobox label="Birth year" size="6" maxlength="4" name="birthYear" list="#year"/&gt;
 * &lt;/ww:bean&gt;
 *
 * Velocity:
 * #tag( ComboBox "label=Birth year" "size=6" "maxlength=4" "name=birthYear" "list=#year" )
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision: 1.8 $
 * @since 2.2
 *
 * @ww.tag name="combobox" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.ComboBoxTag"
 * description="Widget that fills a text box from a select"
  */
public class ComboBox extends TextField {
    final public static String TEMPLATE = "combobox";

    protected String list;

    public ComboBox(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (list != null) {
            addParameter("list", findValue(list));
        }
    }

    /**
     * Iteratable source to populate from. If this is missing, the select widget is simply not displayed.
     * @ww.tagattribute required="true"
      */
    public void setList(String list) {
        this.list = list;
    }
}
