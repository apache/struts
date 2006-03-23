package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Panel;
import com.opensymphony.webwork.components.TabbedPanel;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @see TabbedPanel
 */
public class TabbedPanelTag extends AbstractClosingTag {
    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TabbedPanel(stack, req, res);
    }

    public List getTabs() {
        return ((TabbedPanel) component).getTabs();
    }

    public void addTab(Panel pane) {
        ((TabbedPanel) component).addTab(pane);
    }

}
