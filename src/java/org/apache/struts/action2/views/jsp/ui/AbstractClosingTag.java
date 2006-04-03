package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.ClosingUIBean;

/**
 * @author Patrick Lightbody (plightbo at gmail dot com)
 */
public abstract class AbstractClosingTag extends AbstractUITag {
    protected String openTemplate;

    protected void populateParams() {
        super.populateParams();

        ((ClosingUIBean) component).setOpenTemplate(openTemplate);
    }

    public void setOpenTemplate(String openTemplate) {
        this.openTemplate = openTemplate;
    }
}
