package org.apache.struts2.showcase.action;

import com.opensymphony.xwork2.ActionSupport;

public class Html5Action extends ActionSupport {

    @Override
    public String execute() throws Exception {
        addActionError("Action error: only html5");
        addActionMessage("Action message: only html5");
        addFieldError("testField","Field error: only html5");
        return super.execute();
    }
}
