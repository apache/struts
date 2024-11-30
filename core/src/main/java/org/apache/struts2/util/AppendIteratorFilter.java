/*
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

import org.apache.struts2.action.Action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A bean that takes several iterators and outputs them in sequence
 *
 * @see org.apache.struts2.components.AppendIterator
 * @see org.apache.struts2.views.jsp.iterator.AppendIteratorTag
 */
public class AppendIteratorFilter extends IteratorFilterSupport implements Iterator<Object>, Action {

    private final List<Object> iterators = new ArrayList<>();
    private final List<Object> sources = new ArrayList<>();

    public void setSource(Object anIterator) {
        sources.add(anIterator);
    }

    @Override
    public String execute() {
        // Make source transformations
        for (Object source : sources) {
            iterators.add(getIterator(source));
        }

        return SUCCESS;
    }

    @Override
    public boolean hasNext() {
        if (!iterators.isEmpty()) {
            return (((Iterator<?>) iterators.get(0)).hasNext());
        } else {
            return false;
        }
    }

    @Override
    public Object next() {
        try {
            return ((Iterator<?>) iterators.get(0)).next();
        } finally {
            if (!iterators.isEmpty()) {
                if (!((Iterator<?>) iterators.get(0)).hasNext()) {
                    iterators.remove(0);
                }
            }
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
