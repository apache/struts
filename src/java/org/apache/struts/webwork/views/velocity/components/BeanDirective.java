package org.apache.struts.webwork.views.velocity.components;

import org.apache.struts.webwork.components.Bean;
import org.apache.struts.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Bean
 */
public class BeanDirective extends AbstractDirective {
    public String getBeanName() {
        return "bean";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Bean(stack);
    }
}
