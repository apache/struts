/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.util;

import java.util.Enumeration;
import java.util.Iterator;


/**
 * A base class for iterator filters
 *
 * @author Rickard �berg (rickard@middleware-company.com)
 * @version $Revision: 1.5 $
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
