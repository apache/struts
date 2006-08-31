package org.apache.struts2.showcase.tutorial;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ParameterAware;

import java.util.Map;

public class HelloName2 extends ActionSupport implements ParameterAware {

    public static String NAME = "name";

    public String execute() {
        String[] name = (String[]) parameters.get(NAME);
        if (name == null || name[0] == null || name[0].length() == 0)
            return ERROR;
        else
            return SUCCESS;
    }

    Map parameters;

    public Map getParameters() {
        return parameters;
    }

    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }
}
