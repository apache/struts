/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

import com.opensymphony.xwork.Action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A bean that takes an iterator and outputs a subset of it.
 *
 * @author Rickard �berg (rickard@middleware-company.com)
 * @author tm_jee (tm_jee (at) yahoo.co.uk)
 * @version $Revision: 1.10 $
 */
public class SubsetIteratorFilter extends IteratorFilterSupport implements Iterator, Action {

	private static final Log _log = LogFactory.getLog(SubsetIteratorFilter.class);
	
    Iterator iterator;
    Object source;
    int count = -1;
    int currentCount = 0;
    
    Decider decider;

    // Attributes ----------------------------------------------------
    int start = 0;


    public void setCount(int aCount) {
        this.count = aCount;
    }

    // Public --------------------------------------------------------
    public void setSource(Object anIterator) {
        source = anIterator;
    }

    public void setStart(int aStart) {
        this.start = aStart;
    }
    
    public void setDecider(Decider aDecider) {
    	this.decider = aDecider;
    }

    // Action implementation -----------------------------------------
    public String execute() {
        if (source == null) {
            LogFactory.getLog(SubsetIteratorFilter.class.getName()).warn("Source is null returning empty set.");

            return ERROR;
        }

        // Make source transformations
        source = getIterator(source);

        // Calculate iterator filter
        if (source instanceof Iterator) {
            iterator = (Iterator) source;
            

            // Read away <start> items
            for (int i = 0; (i < start) && iterator.hasNext(); i++) {
                iterator.next();
            }
            
            
            // now let Decider decide if element should be added (if a decider exist)
            if (decider != null) {
            	List list = new ArrayList();
            	while(iterator.hasNext()) {
            		Object currentElement = iterator.next();
            		if (decide(currentElement)) {
            			list.add(currentElement);
            		}
            	}
            	iterator = list.iterator();
            }
            
        } else if (source.getClass().isArray()) {
            ArrayList list = new ArrayList(((Object[]) source).length);
            Object[] objects = (Object[]) source;
            int len = objects.length;

            if (count >= 0) {
            	len = start + count;
            	if (len > objects.length) {
            		len = objects.length;
            	}
            }

            for (int j = start; j < len; j++) {
            	if (decide(objects[j])) {
                    list.add(objects[j]);
            	}
            }

            count = -1; // Don't have to check this in the iterator code
            iterator = list.iterator();
        }

        if (iterator == null) {
            throw new IllegalArgumentException("Source is not an iterator:" + source);
        }

        return SUCCESS;
    }

    // Iterator implementation ---------------------------------------
    public boolean hasNext() {
        return (iterator == null) ? false : (iterator.hasNext() && ((count < 0) || (currentCount < count)));
    }

    public Object next() {
        currentCount++;

        return iterator.next();
    }

    public void remove() {
        iterator.remove();
    }
    
    // inner class ---------------------------------------------------
    /**
     * A decider determines if the given element should be added to the list or not.
     */
    public static interface Decider {

        /**
         * Should the object be added to the list?
         * @param element  the object
         * @return true to add.
         * @throws Exception can be thrown.
         */
        boolean decide(Object element) throws Exception;
    }
    
    // protected -----------------------------------------------------
    protected boolean decide(Object element) {
    	if (decider != null) {
    		try {
    			boolean okToAdd = decider.decide(element);
    			return okToAdd;
    		}
    		catch(Exception e) {
    			_log.warn("decider ["+decider+"] encountered an error while decide adding element ["+element+"], element will be ignored, it will not appeared in subseted iterator", e);
    			return false;
    		}
    	}
    	return true;
    }
}
