/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.example;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author Nils-Helge Garli
 */
public class FormExampleWithValidation extends ActionSupport {
    private String firstName = null;
    private String lastName = null;
    
    public String input() {
        return SUCCESS;
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
