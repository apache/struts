package org.apache.struts.webwork.views.freemarker.tags;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Include;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Include
 */
public class IncludeModel extends TagModel {
    public IncludeModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Include(stack, req, res);
    }
}
