package org.apache.struts.action2.views.freemarker.tags;

import org.apache.struts.action2.components.CheckboxList;
import org.apache.struts.action2.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see CheckboxList
 */
public class CheckboxListModel extends TagModel {
    public CheckboxListModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new CheckboxList(stack, req, res);
    }
}
