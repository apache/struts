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

import java.lang.reflect.Array;
import java.util.Map;

/**
 * <code>ContainUtil</code> will check if object 1 contains object 2.
 * Object 1 may be an Object, array, Collection, or a Map
 */
public class ContainUtil {

    /**
     * Determine if <code>obj2</code> exists in <code>obj1</code>.
     *
     * <table border="1" summary="">
     *  <tr>
     *      <td>Type Of obj1</td>
     *      <td>Comparison type</td>
     *  </tr>
     *  <tr>
     *      <td>null<td>
     *      <td>always return false</td>
     *  </tr>
     *  <tr>
     *      <td>Map</td>
     *      <td>Map containsKey(obj2)</td>
     *  </tr>
     *  <tr>
     *      <td>Collection</td>
     *      <td>Collection contains(obj2)</td>
     *  </tr>
     *  <tr>
     *      <td>Array</td>
     *      <td>there's an array element (e) where e.equals(obj2)</td>
     *  </tr>
     *  <tr>
     *      <td>Object</td>
     *      <td>obj1.equals(obj2)</td>
     *  </tr>
     * </table>
     *
     *
     * @param obj1 first object
     * @param obj2 second object
     * @return true if first object contains second object or if the  are equals, otherwise false
     */
    public static boolean contains(Object obj1, Object obj2) {
        if ((obj1 == null) || (obj2 == null)) {
            return false;
        }

        if (obj1 instanceof Map) {
            if (((Map) obj1).containsKey(obj2)) {
                return true;
            }
        } if (obj1 instanceof Iterable) {
            for (Object value : ((Iterable) obj1)) {
                if (obj2.equals(value) || (value != null && obj2.toString().equals(value.toString()))) {
                    return true;
                }
            }
        } else if (obj1.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(obj1); i++) {
                Object value = Array.get(obj1, i);

                if (obj2.equals(value) || (value != null && obj2.toString().equals(value.toString()))) {
                    return true;
                }
            }
        } else if (obj1.toString().equals(obj2.toString())) {
            return true;
        } else if (obj1.equals(obj2)) {
            return true;
        }

        return false;
    }
}
