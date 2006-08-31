package org.apache.struts2.showcase.tutorial;

import com.opensymphony.xwork2.Action;

import java.text.DateFormat;
import java.util.Date;

public class HelloWorld implements Action {

    public String execute() {
        message = "Hello, World!\n";
        message += "The time is:\n";
        message += DateFormat.getDateInstance().format(new Date());
        return SUCCESS;
    }

    private String message;

    public String getMessage() {
        return message;
    }

}