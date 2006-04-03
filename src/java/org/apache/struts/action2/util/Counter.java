/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.util;

import java.io.Serializable;


/**
 * A bean that can be used to keep track of a counter.
 * <p/>
 * Since it is an Iterator it can be used by the iterator tag
 *
 * @author Rickard �berg (rickard@middleware-company.com)
 * @version $Revision: 1.4 $
 * @see <related>
 */
public class Counter implements java.util.Iterator, Serializable {

	private static final long serialVersionUID = 2796965884308060179L;

	boolean wrap = false;

    // Attributes ----------------------------------------------------
    long first = 1;
    long current = first;
    long interval = 1;
    long last = -1;


    public void setAdd(long addition) {
        current += addition;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getCurrent() {
        return current;
    }

    public void setFirst(long first) {
        this.first = first;
        current = first;
    }

    public long getFirst() {
        return first;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }

    public void setLast(long last) {
        this.last = last;
    }

    public long getLast() {
        return last;
    }

    // Public --------------------------------------------------------
    public long getNext() {
        long next = current;
        current += interval;

        if (wrap && (current > last)) {
            current -= ((1 + last) - first);
        }

        return next;
    }

    public long getPrevious() {
        current -= interval;

        if (wrap && (current < first)) {
            current += (last - first + 1);
        }

        return current;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public boolean isWrap() {
        return wrap;
    }

    public boolean hasNext() {
        return ((last == -1) || wrap) ? true : (current <= last);
    }

    public Object next() {
        return new Long(getNext());
    }

    public void remove() {
        // Do nothing
    }
}
