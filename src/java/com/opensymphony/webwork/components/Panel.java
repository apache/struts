package com.opensymphony.webwork.components;

import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a panel for tabbedPanel.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * See the example in {@link TabbedPanel}.
 * <p/>
 *
 * @author Ian Roughley
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision: 1.16 $
 * @since 2.2
 *
 * @see TabbedPanel
 *
 * @ww.tag name="panel" tld-body-content="JSP" tld-tag-class="com.opensymphony.webwork.views.jsp.ui.PanelTag"
 * description="Render a panel for tabbedPanel"
 */
public class Panel extends Div {
    private static final Log LOG = LogFactory.getLog(Panel.class);

    public static final String TEMPLATE = "tab";
    public static final String TEMPLATE_CLOSE = "tab-close";
    public static final String COMPONENT_NAME = Panel.class.getName();

    protected String tabName;
    protected String subscribeTopicName;
    protected String remote;

    public Panel(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE_CLOSE;
    }

    public boolean end(Writer writer, String body) {
        TabbedPanel tabbedPanel = ((TabbedPanel) findAncestor(TabbedPanel.class));
        subscribeTopicName = tabbedPanel.getTopicName();
        tabbedPanel.addTab(this);

        return super.end(writer, body);
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (tabName != null) {
            addParameter("tabName", findString(tabName));
        }

        if (subscribeTopicName != null) {
            addParameter("subscribeTopicName", subscribeTopicName);
        }

        if (remote != null && "true".equalsIgnoreCase(remote)) {
            addParameter("remote", "true");
        } else {
            addParameter("remote", "false");
        }
    }

    public String getTabName() {
        return findString(tabName);
    }

    public String getComponentName() {
        return COMPONENT_NAME;
    }

    /**
     * The text of the tab to display in the header tab list
     * @ww.tagattribute required="true"
     */
    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    /**
     * Set subscribeTopicName attribute
     * @ww.tagattribute required="false"
     */
    public void setSubscribeTopicName(String subscribeTopicName) {
        this.subscribeTopicName = subscribeTopicName;
    }

    /**
     * determines whether this is a remote panel (ajax) or a local panel (content loaded into visible/hidden containers)
     * @ww.tagattribute required="false" type="Boolean" default="false"
     */
    public void setRemote(String remote) {
        this.remote = remote;
    }
}
