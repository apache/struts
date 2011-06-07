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

import org.apache.struts2.util.IteratorFilterSupport.EnumerationIterator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;


/**
 * MakeIterator.
 */
public class MakeIterator {

    /**
     * Determine whether a given object can be made into an <code>Iterator</code>
     *
     * @param object the object to check
     * @return <code>true</code> if the object can be converted to an iterator and
     *         <code>false</code> otherwise
     */
    public static boolean isIterable(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Map) {
            return true;
        } else if (object instanceof Iterable) {
            return true;
        } else if (object.getClass().isArray()) {
            return true;
        } else if (object instanceof Enumeration) {
            return true;
        } else if (object instanceof Iterator) {
            return true;
        } else {
            return false;
        }
    }

    public static Iterator convert(Object value) {
        Iterator iterator;

        if (value instanceof Iterator) {
            return (Iterator) value;
        }

        if (value instanceof Map) {
            value = ((Map) value).entrySet();
        }

        if (value == null) {
            return null;
        }

        if (value instanceof Iterable) {
            iterator = ((Iterable) value).iterator();
        } else if (value.getClass().isArray()) {
            //need ability to support primitives; therefore, cannot
            //use Object[] casting.
            ArrayList list = new ArrayList(Array.getLength(value));

            for (int j = 0; j < Array.getLength(value); j++) {
                list.add(Array.get(value, j));
            }

            iterator = list.iterator();
        } else if (value instanceof Enumeration) {
            iterator = new EnumerationIterator((Enumeration) value);
        } else {
            iterator = Arrays.asList(value).iterator();
        }

        return iterator;
    }
}
