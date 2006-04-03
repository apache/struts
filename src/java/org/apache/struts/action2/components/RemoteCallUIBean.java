package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RemoteCallUIBean is superclass for all components dealing with remote calls.
 *
 * @author Rene Gielen
 * @author Ian Roughley
 * @author Rainer Hermanns
 * @author Nils-Helge Garli
 * @version $Revision: 1.14 $
 * @since 2.2
 */
public abstract class RemoteCallUIBean extends ClosingUIBean {

    protected String href;
    protected String errorText;
    protected String showErrorTransportText;
    protected String afterLoading;

    public RemoteCallUIBean(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (href != null) {
            addParameter("href", findString(href));
        }

        if (showErrorTransportText != null) {
            addParameter("showErrorTransportText", findValue(showErrorTransportText, Boolean.class));
        }

        if (errorText != null) {
            addParameter("errorText", findString(errorText));
        }

        if (afterLoading != null) {
            addParameter("afterLoading", findString(afterLoading));
        }
    }

    /**
     * The theme to use for the element. <b>This tag will usually use the ajax theme.</b>
     * @a2.tagattribute required="false" type="String"
     */
    public void setTheme(String theme) {
        super.setTheme(theme);
    }

    /**
     * The URL to call to obtain the content
     * @a2.tagattribute required="false" type="String"
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * The text to display to the user if the is an error fetching the content
     * @a2.tagattribute required="false" type="String"
     */
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    /**
     * when to show the error message as content when the URL had problems
     * @a2.tagattribute required="false" type="Boolean" default="false"
     */
    public void setShowErrorTransportText(String showErrorTransportText) {
        this.showErrorTransportText = showErrorTransportText;
    }

    /**
     * Javascript code that will be executed after the content has been fetched
     * @a2.tagattribute required="false" type="String"
     */
    public void setAfterLoading(String afterLoading) {
        this.afterLoading = afterLoading;
    }
}
