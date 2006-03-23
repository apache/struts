/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: matt
 * Date: May 31, 2003
 * Time: 10:21:36 AM
 */
public class MockTag extends BodyTagSupport {

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
        return this.params;
    }

    public void setString(String s) {
        MockTag.s = s;
    }

    public String getString() {
        return s;
    }

    public void addParameter(String key, Object value) {
        this.params.put(key, value);
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
