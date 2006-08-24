package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Debug;
import com.opensymphony.xwork2.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DebugTag extends AbstractUITag {

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Debug(stack, req, res);
    }

}
