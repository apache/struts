package org.apache.struts.action2.views.freemarker.tags;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Select;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Select
 */
public class SelectModel extends TagModel {
    public SelectModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Select(stack, req, res);
    }
}
