package org.apache.struts.webwork.views.freemarker.tags;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.table.WebTable;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see WebTable
 */
public class WebTableModel extends TagModel {
    public WebTableModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new WebTable(stack, req, res);
    }
}
