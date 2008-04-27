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

package org.apache.struts2.views.jsp.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.tagext.BodyTagSupport;


/**
 */
public class MockTag extends BodyTagSupport {

    private static final long serialVersionUID = 2694367759647164641L;

    private static String s;
    private static Integer i;
    private static Double d;
    private static Long l;
    private static Float f;
    private static Date date;
    private static Calendar cal;
    private static HashMap params;
    private static MockTag instance = new MockTag();


    public static MockTag getInstance() {
        return instance;
    }

    public void setCal(Calendar cal) {
        MockTag.cal = cal;
    }

    public Calendar getCal() {
        return cal;
    }

    public void setDate(Date date) {
        MockTag.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDouble(Double d) {
        MockTag.d = d;
    }

    public Double getDouble() {
        return d;
    }

    public void setFloat(Float f) {
        MockTag.f = f;
    }

    public Float getFloat() {
        return f;
    }

    public void setInteger(Integer i) {
        MockTag.i = i;
    }

    public Integer getInteger() {
        return i;
    }

    public void setLong(Long l) {
        MockTag.l = l;
    }

    public Long getLong() {
        return l;
    }

    public Map getParameters() {
        return MockTag.params;
    }

    public void setString(String s) {
        MockTag.s = s;
    }

    public String getString() {
        return s;
    }

    public void addParameter(String key, Object value) {
        MockTag.params.put(key, value);
    }

    /**
     * resets all the static variables to their initial state.  this must be called before each test!
     */
    public void reset() {
        s = null;
        i = null;
        l = null;
        f = null;
        date = null;
        cal = null;
        params = new HashMap();
    }
}
