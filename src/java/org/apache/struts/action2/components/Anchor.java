package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * A tag that creates a HTML &lt;a href='' /&gt; that when clicked calls a URL remote XMLHttpRequest call via the dojo
 * framework. The result from the URL is executed as JavaScript. If a "listenTopics" is supplied, it will publish a
 * 'click' message to that topic when the result is returned.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;ww:a id="link1" theme="ajax" href="/DoIt.action" errorText="An error ocurred" showErrorTransportText="true"&gt;
 *     &lt;img border="none" src="&lt;%=request.getContextPath()%&gt;/images/delete.gif"/&gt;
 *     &lt;ww:param name="id" value="1"/&gt;
 * &lt;/ww:a&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 *
 * </p>
 *
 * <!-- START SNIPPET: exampledescription1 -->
 *
 * Results in
 *
 * <!-- END SNIPPET: exampledescription1 -->
 *
 * </p>
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &lt;a dojoType="BindAnchor" evalResult="true" id="link1" href="/DoIt.action?id=1" errorHtml="An error ocurred"
 * showTransportError="true"&gt;&lt;/a&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * </p>
 *
 * <!-- START SNIPPET: exampledescription2 -->
 *
 * Here is an example that uses the postInvokeJS. This example is in altSyntax=true:
 *
 * <!-- END SNIPPET: exampledescription2 -->
 *
 * </p>
 *
 * <pre>
 * <!-- START SNIPPET: example3 -->
 * &lt;ww:a id="test" theme="ajax" href="/simpeResult.action" preInvokeJS="confirm(\'You sure\')"&gt;
 * 	A
 * &lt;/ww:a&gt;
 * <!-- END SNIPPET: example3 -->
 * </pre>
 *
 * @author Ian Roughley
 * @author Rene Gielen
 * @version $Revision: 1.5 $
 * @ww.tag name="a" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.AnchorTag"
 * description="Render a HTML href element that when clicked calls a URL via remote XMLHttpRequest"
 * @since 2.2
 */
public class Anchor extends RemoteCallUIBean {
    final public static String OPEN_TEMPLATE = "a";
    final public static String TEMPLATE = "a-close";
    final public static String COMPONENT_NAME = Anchor.class.getName();

    protected String notifyTopics;
    protected String preInvokeJS;

    public Anchor(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (notifyTopics != null) {
            addParameter("notifyTopics", findString(notifyTopics));
        }

        if (preInvokeJS != null) {
            addParameter("preInvokeJS", findString(preInvokeJS));
        }
    }

    /**
     * The id to assign the component
     * @ww.tagattribute required="false" type="String"
     */
    public void setId(String id) {
        super.setId(id);
    }

    /**
     * Topic names to post an event to after the remote call has been made
     * @ww.tagattribute required="false"
     */
    public void setNotifyTopics(String notifyTopics) {
        this.notifyTopics = notifyTopics;
    }

    /**
     * A javascript snippet that will be invoked prior to the execution of the target href. If provided must return true or false. True indicates to continue executing target, false says do not execute link target. Possible uses are for confirm dialogs.
     * @ww.tagattribute required="false" type="String"
     */
    public void setPreInvokeJS(String preInvokeJS) {
        this.preInvokeJS = preInvokeJS;
    }
}
