package org.apache.struts.action2.views.velocity.components;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Date;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <code>DateDirective</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public class DateDirective extends AbstractDirective {

    public String getBeanName() {
        return "date";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Date(stack);
    }
}
