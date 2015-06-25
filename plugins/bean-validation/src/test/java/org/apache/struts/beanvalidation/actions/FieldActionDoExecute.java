package org.apache.struts.beanvalidation.actions;

import org.hibernate.validator.constraints.NotBlank;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAwareSupport;

public class FieldActionDoExecute extends ValidationAwareSupport {
    @NotBlank(message = "canNotBeBlank")
    private String test;

    public String doExecute() {
    	return ActionSupport.SUCCESS;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
