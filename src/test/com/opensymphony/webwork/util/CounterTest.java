/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

import junit.framework.TestCase;


/**
 * User: plightbo
 * Date: Jan 7, 2004
 * Time: 7:55:35 PM
 */
public class CounterTest extends TestCase {

    Counter c = new Counter();


    public void testCurrentAfterNext() {
        long next = c.getNext();
        long current = c.getCurrent();
        assertEquals(next + 1, current);
    }

    public void testCurrentBeforeNext() {
        long current = c.getCurrent();
        long next = c.getNext();
        assertEquals(current, next);
    }

    public void testWrap() {
        c.setWrap(true);
        c.setLast(1);

        long a = c.getNext();
        long b = c.getNext();
        assertEquals(1, a);
        assertEquals(1, b);
    }
}
