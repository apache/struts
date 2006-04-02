package org.apache.struts.webwork.views.freemarker.tags;

import org.apache.struts.webwork.components.ActionComponent;
import org.apache.struts.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ActionComponent
 */
public class ActionModel extends TagModel {
    public ActionModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new ActionComponent(stack, req, res);
    }
}
