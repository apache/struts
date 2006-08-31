package org.apache.struts2.showcase.tutorial;

import com.opensymphony.xwork2.ActionSupport;

public class HelloName extends ActionSupport {

    public String execute() throws Exception {
        if (getName() == null || getName().length() == 0)
            return ERROR;
        else
            return SUCCESS;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}