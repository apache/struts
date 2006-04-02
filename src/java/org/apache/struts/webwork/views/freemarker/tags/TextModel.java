package org.apache.struts.webwork.views.freemarker.tags;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Text;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Text
 */
public class TextModel extends TagModel {
    public TextModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Text(stack);
    }
}
