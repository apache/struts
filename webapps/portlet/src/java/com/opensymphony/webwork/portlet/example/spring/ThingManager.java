/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.example.spring;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nils-Helge Garli
 */
public class ThingManager {
    private List things = new ArrayList();
    
    public void addThing(String thing) {
        things.add(thing);
    }
    
    public List getThings() {
        return things;
    }
}
