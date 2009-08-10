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
package org.apache.struts2.json;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Isolate the process of cleaning JSON data from the Interceptor class itself.
 */
public abstract class JSONCleaner {

    public Object clean(String ognlPrefix, Object data) throws JSONException {
        if (data == null)
            return null;
        else if (data instanceof List)
            return cleanList(ognlPrefix, data);
        else if (data instanceof Map)
            return cleanMap(ognlPrefix, data);
        else
            return cleanValue(ognlPrefix, data);
    }

    protected Object cleanList(String ognlPrefix, Object data) throws JSONException {
        List list = (List) data;
        int count = list.size();
        for (int i = 0; i < count; i++) {
            list.set(i, clean(ognlPrefix + "[" + i + "]", list.get(i)));
        }
        return list;
    }

    protected Object cleanMap(String ognlPrefix, Object data) throws JSONException {
        Map map = (Map) data;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry e = (Map.Entry) iter.next();
            e.setValue(clean((ognlPrefix.length() > 0 ? ognlPrefix + "." : "") + e.getKey(), e.getValue()));
        }
        return map;
    }

    protected abstract Object cleanValue(String ognlName, Object data) throws JSONException;

}
