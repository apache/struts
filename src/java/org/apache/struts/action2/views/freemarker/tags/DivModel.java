package org.apache.struts.action2.views.freemarker.tags;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Div;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Div
 */
public class DivModel extends TagModel {
    public DivModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Div(stack, req, res);
    }
}
