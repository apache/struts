package org.apache.struts.action2.views.freemarker.tags;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Submit;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Submit
 */
public class SubmitModel extends TagModel {
    public SubmitModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Submit(stack, req, res);
    }
}
