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
package com.opensymphony.xwork2.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mimo
 *
 */
public class Indexed {

    public Object[] values = new Object[3];
    public Map<String, Object> map = new HashMap<>();

    public void setSimple(int i, Object v) {
        values[i] = v;
    }

    public Object getSimple(int i) {
        return values[i];
    }



    public void setIntegerMap(String key, Integer value) {
        map.put(key, value);
    }

    public Integer getIntegerMap(String key) {
        return (Integer) map.get(key);
    }

    public void setStringMap(String key, String value) {
        map.put(key, value);
    }

    public String getStringMap(String key) {
        return (String) map.get(key);
    }

}
