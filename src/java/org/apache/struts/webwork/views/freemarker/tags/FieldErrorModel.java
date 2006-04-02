package org.apache.struts.webwork.views.freemarker.tags;

import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.FieldError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see FieldError
 */
public class FieldErrorModel extends TagModel {
    public FieldErrorModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new FieldError(stack, req, res);
    }
}
