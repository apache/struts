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
 * A bean that takes several iterators and outputs the merge of them. Used by
 * MergeIteratorTag.
 *
 * @see org.apache.struts2.views.jsp.iterator.MergeIteratorTag
 * @see org.apache.struts2.components.MergeIterator
 */
public class MergeIteratorFilter extends IteratorFilterSupport implements Iterator, Action {

    List iterators = new ArrayList();

    // Attributes ----------------------------------------------------
    List sources = new ArrayList();
    int idx = 0;


    // Public --------------------------------------------------------
    public void setSource(Object anIterator) {
        sources.add(anIterator);
    }

    // Action implementation -----------------------------------------
    @Override
    public String execute() {
        // Make source transformations
        for (Object source : sources) {
            iterators.add(getIterator(source));
        }

        return SUCCESS;
    }

    // Iterator implementation ---------------------------------------
    @Override
    public boolean hasNext() {
        while (!iterators.isEmpty()) {
            if (((Iterator) iterators.get(idx)).hasNext()) {
                return true;
            } else {
                iterators.remove(idx);

                if (!iterators.isEmpty()) {
                    idx = idx % iterators.size();
                }
            }
        }

        return false;
    }

    public Object next() {
        try {
            return ((Iterator) iterators.get(idx)).next();
        } finally {
            idx = (idx + 1) % iterators.size();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported in MergeIteratorFilter.");
    }
}
