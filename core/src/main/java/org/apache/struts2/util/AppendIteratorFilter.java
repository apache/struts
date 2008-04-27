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

import com.opensymphony.xwork2.Action;


/**
 * A bean that takes several iterators and outputs them in sequence
 *
 * @see org.apache.struts2.components.AppendIterator
 * @see org.apache.struts2.views.jsp.iterator.AppendIteratorTag
 */
public class AppendIteratorFilter extends IteratorFilterSupport implements Iterator, Action {

    List iterators = new ArrayList();

    // Attributes ----------------------------------------------------
    List sources = new ArrayList();


    // Public --------------------------------------------------------
    public void setSource(Object anIterator) {
        sources.add(anIterator);
    }

    // Action implementation -----------------------------------------
    public String execute() {
        // Make source transformations
        for (int i = 0; i < sources.size(); i++) {
            Object source = sources.get(i);
            iterators.add(getIterator(source));
        }

        return SUCCESS;
    }

    // Iterator implementation ---------------------------------------
    public boolean hasNext() {
        if (iterators.size() > 0) {
            return (((Iterator) iterators.get(0)).hasNext());
        } else {
            return false;
        }
    }

    public Object next() {
        try {
            return ((Iterator) iterators.get(0)).next();
        } finally {
            if (iterators.size() > 0) {
                if (!((Iterator) iterators.get(0)).hasNext()) {
                    iterators.remove(0);
                }
            }
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
