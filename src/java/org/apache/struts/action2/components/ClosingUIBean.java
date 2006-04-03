package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

/**
 * ClosingUIBean is the standard superclass for UI components such as div etc.
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision$
 * @since 2.2
 */
public abstract class ClosingUIBean extends UIBean {
    private static final Log LOG = LogFactory.getLog(ClosingUIBean.class);

    protected ClosingUIBean(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    String openTemplate;

    public abstract String getDefaultOpenTemplate();

    /**
     * Set template to use for opening the rendered html.
     * @a2.tagattribute required="false"
     */
    public void setOpenTemplate(String openTemplate) {
        this.openTemplate = openTemplate;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        try {
            evaluateParams();

            mergeTemplate(writer, buildTemplateName(openTemplate, getDefaultOpenTemplate()));
        } catch (Exception e) {
            LOG.error("Could not open template", e);
            e.printStackTrace();
        }

        return result;
    }
}
