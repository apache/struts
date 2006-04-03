package org.apache.struts.action2.views.freemarker.tags;

import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.ActionError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ActionError
 */
public class ActionErrorModel extends TagModel {
    public ActionErrorModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new ActionError(stack, req, res);
    }
}
