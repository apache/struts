/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.example;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author Nils-Helge Garli
 */
public class FormTestAction extends ActionSupport {

    private String name = null;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
