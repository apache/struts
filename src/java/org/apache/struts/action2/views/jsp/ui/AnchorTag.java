package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Anchor;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Anchor
 */
public class AnchorTag extends AbstractClosingTag {
	
	private static final long serialVersionUID = -1034616578492431113L;
	
	protected String href;
    protected String errorText;
    protected String showErrorTransportText;
    protected String notifyTopics;
    protected String afterLoading;
    protected String preInvokeJS;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Anchor(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Anchor link = (Anchor) component;
        
        link.setHref(href);
        link.setErrorText(errorText);
        link.setShowErrorTransportText(showErrorTransportText);
        link.setNotifyTopics(notifyTopics);
        link.setAfterLoading(afterLoading);
        link.setPreInvokeJS(preInvokeJS);
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public void setShowErrorTransportText(String showErrorTransportText) {
        this.showErrorTransportText = showErrorTransportText;
    }

    public void setNotifyTopics(String notifyTopics) {
        this.notifyTopics = notifyTopics;
    }

    public void setAfterLoading(String afterLoading) {
        this.afterLoading = afterLoading;
    }

    public void setPreInvokeJS(String preInvokeJS) {
        this.preInvokeJS = preInvokeJS;
    }
}

