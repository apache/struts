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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;


/**
 * <code>ContainUtil</code> will check if object 1 contains object 2.
 * Object 1 may be an Object, array, Collection, or a Map
 *
 * @author Matt Baldree (matt@smallleap.com)
 * @version $Revision$
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
