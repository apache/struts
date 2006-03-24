package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.ClosingUIBean;

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
