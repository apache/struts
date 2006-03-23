/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 1/10/2003
 *
 */
package com.opensymphony.webwork.views.freemarker;


/**
 * @author CameronBraid
 */
public class TestBean {

    private String pName = null;
    private String pValue = null;


    /**
     *
     */
    public TestBean(String name, String value) {
        super();
        setName(name);
        setValue(value);
    }


    public void setName(String aName) {
        pName = aName;
    }

    /**
     * Bean Property String Name
     */
    public String getName() {
        return pName;
    }

    public void setValue(String aValue) {
        pValue = aValue;
    }

    /**
     * Bean Property String Value
     */
    public String getValue() {
        return pValue;
    }
}
