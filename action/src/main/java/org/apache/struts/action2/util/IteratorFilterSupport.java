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

import java.util.Enumeration;
import java.util.Iterator;


/**
 * A base class for iterator filters
 *
 * @author Rickard �berg (rickard@middleware-company.com)
 * @version $Revision$
 */
public abstract class IteratorFilterSupport {

    // Protected implementation --------------------------------------
    protected Object getIterator(Object source) {
        return MakeIterator.convert(source);
    }


    // Wrapper for enumerations
    public class EnumerationIterator implements Iterator {
        Enumeration enumeration;

        public EnumerationIterator(Enumeration aEnum) {
            enumeration = aEnum;
        }

        public boolean hasNext() {
            return enumeration.hasMoreElements();
        }

        public Object next() {
            return enumeration.nextElement();
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported in IteratorFilterSupport.");
        }
    }
}
