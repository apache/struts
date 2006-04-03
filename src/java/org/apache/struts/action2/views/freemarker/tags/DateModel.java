package org.apache.struts.action2.views.freemarker.tags;

import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <code>DateModel</code>
 *
 * @author Rainer Hermanns
 * @version $Id: DateModel.java,v 1.1 2006/02/26 16:49:42 rainerh Exp $
 */
public class DateModel extends TagModel {

    public DateModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Date(stack);
    }
}
