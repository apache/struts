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
package org.apache.struts2.convention;

import com.opensymphony.xwork2.config.ConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a String helper.
 * </p>
 */
public class StringTools {
    public static boolean contains(String[] strings, String value, boolean ignoreCase) {
        if (strings != null) {
            for (String string :  strings) {
                if (string.equals(value) || (ignoreCase && string.equalsIgnoreCase(value)))
                    return true;
            }
        }

        return false;
    }

    public static Map<String, String> createParameterMap(String[] parms) {
        Map<String, String> map = new HashMap<String, String>();
        int subtract = parms.length % 2;
        if (subtract != 0) {
            throw new ConfigurationException(
                    "'params' is a string array "
                            + "and they must be in a key value pair configuration. It looks like you"
                            + " have specified an odd number of parameters and there should only be an even number."
                            + " (e.g. params = {\"key\", \"value\"})");
        }

        for (int i = 0; i < parms.length; i = i + 2) {
            String key = parms[i];
            String value = parms[i + 1];
            map.put(key, value);
        }

        return map;
    }
}