/*
 * Copyright (c) 2006, Your Corporation. All Rights Reserved.
 */

package com.foo.example;

/**
 * <code>CounterBean</code>
 *
 * @author Rainer Hermanns
 */
public class CounterBean {

    private static int count = 0;

    public int getCount() {
        return count;
    }

    public void increment() {
        count++;
    }
}
