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

import org.apache.struts2.action.Action;
import org.apache.struts2.conversion.annotations.Conversion;
import org.apache.struts2.conversion.annotations.ConversionRule;
import org.apache.struts2.conversion.annotations.TypeConversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link MyBeanAction} restated with bare property names as conversion keys. Both must bind
 * identically; {@code MyBeanAction} keeps the spelled-out prefixes so the old form stays covered.
 */
@Conversion(
        conversions = {
                @TypeConversion(key = "annotatedBeanMap", rule = ConversionRule.KEY_PROPERTY, value = "id"),
                @TypeConversion(key = "annotatedBeanMap", rule = ConversionRule.ELEMENT, converterClass = MyBean.class),
                @TypeConversion(key = "annotatedBeanList", rule = ConversionRule.KEY_PROPERTY, value = "id"),
                @TypeConversion(key = "annotatedBeanList", rule = ConversionRule.ELEMENT, converterClass = MyBean.class)
        })
public class MyBeanBareKeyAction implements Action {

    private Map annotatedBeanMap = new HashMap();
    private List annotatedBeanList = new ArrayList();

    public Map getAnnotatedBeanMap() {
        return annotatedBeanMap;
    }

    @TypeConversion(rule = ConversionRule.KEY, converterClass = Long.class)
    public void setAnnotatedBeanMap(Map annotatedBeanMap) {
        this.annotatedBeanMap = annotatedBeanMap;
    }

    public List getAnnotatedBeanList() {
        return annotatedBeanList;
    }

    @TypeConversion(rule = ConversionRule.CREATE_IF_NULL, value = "true")
    public void setAnnotatedBeanList(List annotatedBeanList) {
        this.annotatedBeanList = annotatedBeanList;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }
}
