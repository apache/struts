package com.opensymphony.webwork.views.velocity.components;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Date;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <code>DateDirective</code>
 *
 * @author Rainer Hermanns
 * @version $Id: DateDirective.java,v 1.1 2006/02/26 16:49:42 rainerh Exp $
 */
public class DateDirective extends AbstractDirective {

    public String getBeanName() {
        return "date";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Date(stack);
    }
}
