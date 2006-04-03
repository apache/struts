package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render an HTML input field of type text</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <p/>
 * <!-- START SNIPPET: exdescription -->
 * In this example, a text control is rendered. The label is retrieved from a ResourceBundle by calling
 * ActionSupport's getText() method.<p/>
 * <!-- END SNIPPET: exdescription -->
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;a:textfield label="%{text('user_name')}" name="user" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision: 1.10 $
 * @since 2.2
 *
 * @a2.tag name="textfield" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.TextFieldTag"
 * description="Render an HTML input field of type text"
 */
public class TextField extends UIBean {
    /**
     * The name of the default template for the TextFieldTag
     */
    final public static String TEMPLATE = "text";


    protected String maxlength;
    protected String readonly;
    protected String size;

    public TextField(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (size != null) {
            addParameter("size", findString(size));
        }

        if (maxlength != null) {
            addParameter("maxlength", findString(maxlength));
        }

        if (readonly != null) {
            addParameter("readonly", findValue(readonly, Boolean.class));
        }
    }

    /**
     * HTML maxlength attribute
     * @a2.tagattribute required="false" type="Integer"
     */
    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    /**
     * Deprecated. Use maxlength instead.
     * @a2.tagattribute required="false"
     */
    public void setMaxLength(String maxlength) {
        this.maxlength = maxlength;
    }

    /**
     * Whether the input is readonly
     * @a2.tagattribute required="false" type="Boolean" default="false"
     */
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    /**
     * HTML size attribute
     * @a2.tagattribute required="false" type="Integer"
     */
    public void setSize(String size) {
        this.size = size;
    }
}
