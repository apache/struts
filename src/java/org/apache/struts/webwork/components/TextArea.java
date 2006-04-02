package org.apache.struts.webwork.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render HTML textarea tag.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;a:textarea label="Comments" name="comments" cols="30" rows="8"/&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision: 1.8 $
 * @since 2.2
 *
 * @see TabbedPanel
 *
 * @a2.tag name="textarea" tld-body-content="JSP" tld-tag-class="org.apache.struts.webwork.views.jsp.ui.TextareaTag"
 * description="Render HTML textarea tag."
 */
public class TextArea extends UIBean {
    final public static String TEMPLATE = "textarea";

    protected String cols;
    protected String readonly;
    protected String rows;
    protected String wrap;

    public TextArea(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (readonly != null) {
            addParameter("readonly", findValue(readonly, Boolean.class));
        }

        if (cols != null) {
            addParameter("cols", findString(cols));
        }

        if (rows != null) {
            addParameter("rows", findString(rows));
        }

        if (wrap != null) {
            addParameter("wrap", findString(wrap));
        }
    }

    /**
     * HTML cols attribute
     * @a2.tagattribute required="false" type="Integer"
     */
    public void setCols(String cols) {
        this.cols = cols;
    }

    /**
     * Whether the textarea is readonly
     * @a2.tagattribute required="false" type="Boolean" default="false"
     */
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    /**
     * HTML rows attribute
     * @a2.tagattribute required="false" type="Integer"
     */
    public void setRows(String rows) {
        this.rows = rows;
    }

    /**
     * HTML wrap attribute
     * @a2.tagattribute required="false" type="String"
     */
    public void setWrap(String wrap) {
        this.wrap = wrap;
    }
}
