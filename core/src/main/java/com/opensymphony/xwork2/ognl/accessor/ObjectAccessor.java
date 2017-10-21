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
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import java.util.Map;

public class ObjectAccessor extends ObjectPropertyAccessor {
    @Override
    public Object getProperty(Map map, Object o, Object o1) throws OgnlException {
        Object obj = super.getProperty(map, o, o1);

        map.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, o.getClass());
        map.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, o1.toString());
        ReflectionContextState.updateCurrentPropertyPath(map, o1);
        return obj;
    }

    @Override
    public void setProperty(Map map, Object o, Object o1, Object o2) throws OgnlException {
        super.setProperty(map, o, o1, o2);
    }
}