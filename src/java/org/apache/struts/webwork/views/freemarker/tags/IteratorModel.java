package org.apache.struts.webwork.views.freemarker.tags;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.IteratorComponent;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Patrick Lightbody (plightbo at gmail dot com)
 */
public class IteratorModel extends TagModel {
    public IteratorModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new IteratorComponent(stack);
    }
}
