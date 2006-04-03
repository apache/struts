package org.apache.struts.action2.views.velocity.components;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.File;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see File
 */
public class FileDirective extends AbstractDirective {
    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new File(stack, req, res);
    }

    public String getBeanName() {
        return "file";
    }
}
