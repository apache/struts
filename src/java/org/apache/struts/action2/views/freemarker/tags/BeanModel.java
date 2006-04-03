package org.apache.struts.action2.views.freemarker.tags;

import org.apache.struts.action2.components.Bean;
import org.apache.struts.action2.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Bean
 */
public class BeanModel extends TagModel {
    public BeanModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Bean(stack);
    }
}
