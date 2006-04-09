/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.util;

import com.opensymphony.xwork.Action;
import org.apache.commons.logging.LogFactory;

import java.util.*;


/**
 * A bean that takes a source and comparator then attempt to sort the source
 * utilizing the comparator. It is being used in SortIteratorTag.
 *
 * @author Rickard �berg (rickard@middleware-company.com)
 * @author tm_jee 
 * @version $Revision$
 * @see org.apache.struts.action2.views.jsp.iterator.SortIteratorTag
 */
public class SortIteratorFilter extends IteratorFilterSupport implements Iterator, Action {

    Comparator comparator;
    Iterator iterator;
    List list;

    // Attributes ----------------------------------------------------
    Object source;


    public void setComparator(Comparator aComparator) {
        this.comparator = aComparator;
    }

    public List getList() {
        return list;
    }

    // Public --------------------------------------------------------
    public void setSource(Object anIterator) {
        source = anIterator;
    }

    // Action implementation -----------------------------------------
    public String execute() {
        if (source == null) {
            return ERROR;
        } else {
            try {
                if (!MakeIterator.isIterable(source)) {
                    LogFactory.getLog(SortIteratorFilter.class.getName()).warn("Cannot create SortIterator for source " + source);

                    return ERROR;
                }

                list = new ArrayList();

                Iterator i = MakeIterator.convert(source);

                while (i.hasNext()) {
                    list.add(i.next());
                }

                // Sort it
                Collections.sort(list, comparator);
                iterator = list.iterator();

                return SUCCESS;
            } catch (Exception e) {
                LogFactory.getLog(SortIteratorFilter.class.getName()).warn("Error creating sort iterator.", e);

                return ERROR;
            }
        }
    }

    // Iterator implementation ---------------------------------------
    public boolean hasNext() {
        return (source == null) ? false : iterator.hasNext();
    }

    public Object next() {
        return iterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported in SortIteratorFilter.");
    }
}
