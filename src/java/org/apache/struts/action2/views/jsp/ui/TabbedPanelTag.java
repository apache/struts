package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Panel;
import org.apache.struts.action2.components.TabbedPanel;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @see TabbedPanel
 */
public class TabbedPanelTag extends AbstractClosingTag {
	
	private static final long serialVersionUID = -4719930205515386252L;

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
