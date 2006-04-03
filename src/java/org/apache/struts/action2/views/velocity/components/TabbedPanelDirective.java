package org.apache.struts.action2.views.velocity.components;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.TabbedPanel;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see TabbedPanel
 */
public class TabbedPanelDirective extends AbstractDirective {
    public String getBeanName() {
        return "tabbedpanel";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TabbedPanel(stack, req, res);
    }
}
