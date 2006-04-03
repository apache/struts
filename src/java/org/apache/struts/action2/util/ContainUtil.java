/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;


/**
 * <code>ContainUtil</code> will check if object 1 contains object 2.
 * Object 1 may be an Object, array, Collection, or a Map
 *
 * @author Matt Baldree (matt@smallleap.com)
 * @version $Revision: 1.4 $
 */
public class ContainUtil {

    public static boolean contains(Object obj1, Object obj2) {
        if ((obj1 == null) || (obj2 == null)) {
            //log.debug("obj1 or obj2 are null.");
            return false;
        }

        if (obj1 instanceof Map) {
            if (((Map) obj1).containsValue(obj2)) {
                //log.debug("obj1 is a map and contains obj2");
                return true;
            }
        } else if (obj1 instanceof Collection) {
            if (((Collection) obj1).contains(obj2)) {
                //log.debug("obj1 is a collection and contains obj2");
                return true;
            }
        } else if (obj1.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(obj1); i++) {
                Object value = null;
                value = Array.get(obj1, i);

                if (value.equals(obj2)) {
                    //log.debug("obj1 is an array and contains obj2");
                    return true;
                }
            }
        } else if (obj1.equals(obj2)) {
            //log.debug("obj1 is an object and equals obj2");
            return true;
        }

        //log.debug("obj1 does not contain obj2: " + obj1 + ", " + obj2);
        return false;
    }
}
