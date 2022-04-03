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

import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

import java.util.Map;

public class HttpParametersPropertyAccessor extends ObjectPropertyAccessor {

    @Override
    public Object getProperty(Map context, Object target, Object oname) throws OgnlException {
        HttpParameters parameters = (HttpParameters) target;
        return parameters.get(String.valueOf(oname)).getObject();
    }

    @Override
    public void setProperty(Map context, Object target, Object oname, Object value) throws OgnlException {
        throw new OgnlException("Access to " + target.getClass().getName() + " is read-only!");
    }
}