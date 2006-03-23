package com.opensymphony.webwork.views.velocity.components;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.TabbedPanel;
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
