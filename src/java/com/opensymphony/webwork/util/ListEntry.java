/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

/**
 * Entry in a list.
 *
 * @author CameronBraid
 */
public class ListEntry {

    final private Object key;
    final private Object value;
    final private boolean isSelected;


    public ListEntry(Object key, Object value, boolean isSelected) {
        this.key = key;
        this.value = value;
        this.isSelected = isSelected;
    }


    public boolean getIsSelected() {
        return isSelected;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
