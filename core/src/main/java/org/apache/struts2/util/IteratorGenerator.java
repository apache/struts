/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * A bean that generates an iterator filled with a given object depending on the count,
 * separator and converter defined. It is being used by IteratorGeneratorTag.
 *
 */
public class IteratorGenerator implements Iterator, Action {

    private static final Logger LOG = LoggerFactory.getLogger(IteratorGenerator.class);

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
                            if (LOG.isWarnEnabled()) {
                        	LOG.warn("unable to convert ["+token+"], skipping this token, it will not appear in the generated iterator", e);
                            }
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
