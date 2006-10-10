package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Debug;

import com.opensymphony.xwork2.util.ValueStack;

public class DebugTag extends AbstractUITag {

    private static final long serialVersionUID = 3487684841317160628L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Debug(stack, req, res);
    }

}
