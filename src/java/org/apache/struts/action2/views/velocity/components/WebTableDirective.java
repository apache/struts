package org.apache.struts.action2.views.velocity.components;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.table.WebTable;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see WebTable
 */
public class WebTableDirective extends AbstractDirective {
    public String getBeanName() {
        return "table";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new WebTable(stack, req, res);
    }
}
