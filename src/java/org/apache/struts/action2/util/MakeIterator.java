/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.util;

import java.lang.reflect.Array;
import java.util.*;


/**
 * MakeIterator.
 * <p/>
 * Taken from WebWork 1.x by:
 *
 * @author &lt;a href="hermanns@aixcept.de"&gt;Rainer Hermanns&lt;/a&gt;
 * @version $Id: MakeIterator.java,v 1.10 2006/03/11 17:09:32 davsclaus Exp $
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
        } else if (object instanceof Collection) {
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

        if (value instanceof Collection) {
            iterator = ((Collection) value).iterator();
        } else if (value.getClass().isArray()) {
            //need ability to support primitives; therefore, cannot
            //use Object[] casting.
            Object a = Array.newInstance(value.getClass().getComponentType(), (Array.getLength(value)));
            ArrayList list = new ArrayList(Array.getLength(value));

            for (int j = 0; j < Array.getLength(value); j++) {
                list.add(Array.get(value, j));
            }

            iterator = list.iterator();
        } else if (value instanceof Enumeration) {
            Enumeration enumeration = (Enumeration) value;
            ArrayList list = new ArrayList();

            while (enumeration.hasMoreElements()) {
                list.add(enumeration.nextElement());
            }

            iterator = list.iterator();
        } else {
            List list = new ArrayList(1);
            list.add(value);
            iterator = list.iterator();
        }

        return iterator;
    }
}
