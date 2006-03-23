/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * TestConfiguration
 *
 * @author Jason Carreira
 *         Created Apr 9, 2003 11:12:54 PM
 */
public class TestConfiguration extends Configuration {

    /**
     * Get a named setting.
     *
     * @throws IllegalArgumentException if there is no configuration parameter with the given name.
     */
    public Object getImpl(String aName) throws IllegalArgumentException {
        return aName;
    }

    /**
     * List setting names
     */
    public Iterator listImpl() {
        List testList = new ArrayList();
        testList.add("123");
        testList.add("testValue");

        return testList.iterator();
    }
}
