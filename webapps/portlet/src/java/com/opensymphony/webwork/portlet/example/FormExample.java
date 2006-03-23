/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.example;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author Nils-Helge Garli
 */
public class FormExample extends ActionSupport {
    
    String firstName = null;
    String lastName = null;
    public String execute() throws Exception {
        // TODO Auto-generated method stub
        return super.execute();
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
