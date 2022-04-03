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

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.ConversionRule;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>MyBeanAction</code>
 *
 * @author Rainer Hermanns
 */
@Conversion(
        conversions= {
                @TypeConversion(key = "KeyProperty_annotatedBeanMap", rule = ConversionRule.KEY_PROPERTY, value = "id"),
                @TypeConversion(key = "Element_annotatedBeanMap", rule = ConversionRule.ELEMENT, converterClass = MyBean.class),
                @TypeConversion(key = "KeyProperty_annotatedBeanList", rule = ConversionRule.KEY_PROPERTY, value = "id"),
                @TypeConversion(key = "Element_annotatedBeanList", rule = ConversionRule.ELEMENT, converterClass = MyBean.class)
        })
public class MyBeanAction implements Action {

    private List beanList = new ArrayList();
    private Map beanMap = new HashMap();
    private Map annotatedBeanMap = new HashMap();
    private List annotatedBeanList = new ArrayList();

    public List getBeanList() {
        return beanList;
    }

    public void setBeanList(List beanList) {
        this.beanList = beanList;
    }

    public Map getBeanMap() {
        return beanMap;
    }

    public void setBeanMap(Map beanMap) {
        this.beanMap = beanMap;
    }

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
