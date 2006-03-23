/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

import com.opensymphony.xwork.Action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A bean that generates an iterator filled with a given object depending on the count,
 * separator and converter defined. It is being used by IteratorGeneratorTag. 
 *
 * @author Rickard �berg (rickard@middleware-company.com)
 * @author tm_jee ( tm_jee(at)yahoo.co.uk )
 * @version $Revision: 1.7 $
 */
public class IteratorGenerator implements Iterator, Action {
	
	private static final Log _log = LogFactory.getLog(IteratorGenerator.class);

    List values;
    Object value;
    String separator;
    Converter converter;

    // Attributes ----------------------------------------------------
    int count = 0;
    int currentCount = 0;


    public void setCount(int aCount) {
        this.count = aCount;
    }

    public boolean getHasNext() {
        return hasNext();
    }

    public Object getNext() {
        return next();
    }

    public void setSeparator(String aChar) {
        separator = aChar;
    }
    
    public void setConverter(Converter aConverter) {
    	converter = aConverter;
    }

    // Public --------------------------------------------------------
    public void setValues(Object aValue) {
        value = aValue;
    }

    // Action implementation -----------------------------------------
    public String execute() {
        if (value == null) {
            return ERROR;
        } else {
            values = new ArrayList();

            if (separator != null) {
                StringTokenizer tokens = new StringTokenizer(value.toString(), separator);

                while (tokens.hasMoreTokens()) {
                	String token = tokens.nextToken().trim();
                	if (converter != null) {
                		try {
                			Object convertedObj = converter.convert(token);
                			values.add(convertedObj);
                		}
                		catch(Exception e) { // make sure things, goes on, we just ignore the bad ones
                			_log.warn("unable to convert ["+token+"], skipping this token, it will not appear in the generated iterator", e);
                		}
                	}
                	else {
                		values.add(token);
                	}
                }
            } else {
                values.add(value.toString());
            }

            // Count default is the size of the list of values
            if (count == 0) {
                count = values.size();
            }

            return SUCCESS;
        }
    }

    // Iterator implementation ---------------------------------------
    public boolean hasNext() {
        return (value == null) ? false : ((currentCount < count) || (count == -1));
    }

    public Object next() {
        try {
            return values.get(currentCount % values.size());
        } finally {
            currentCount++;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported in IteratorGenerator.");
    }
    
    
    // Inner class --------------------------------------------------
    /**
     * Interface for converting each separated token into an Object of choice.
     */
    public static interface Converter {
    	Object convert(String token) throws Exception;
    }
}
