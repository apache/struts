package org.apache.struts.action2.views.freemarker.tags;

import org.apache.struts.action2.components.Checkbox;
import org.apache.struts.action2.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Checkbox
 */
public class CheckboxModel extends TagModel {
    public CheckboxModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Checkbox(stack, req, res);
    }
}
