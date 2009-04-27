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

package org.apache.struts2.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A bean that can be used to format dates
 *
 */
public class DateFormatter {

    Date date;
    DateFormat format;

    // Attributes ----------------------------------------------------
    DateFormat parser;


    // Public --------------------------------------------------------
    public DateFormatter() {
        this.parser = new SimpleDateFormat();
        this.format = new SimpleDateFormat();
        this.date = new Date();
    }


    public void setDate(String date) {
        try {
            this.date = parser.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void setDate(Date date) {
        this.date = (date == null) ? null : (Date)date.clone();
    }

    public void setDate(int date) {
        setDate(Integer.toString(date));
    }

    public Date getDate() {
        return this.date;
    }

    public void setFormat(String format) {
        this.format = new SimpleDateFormat(format);
    }

    public void setFormat(DateFormat format) {
        this.format = format;
    }

    public String getFormattedDate() {
        return format.format(date);
    }

    public void setParseFormat(String format) {
        this.parser = new SimpleDateFormat(format);
    }

    public void setParser(DateFormat parser) {
        this.parser = parser;
    }

    public void setTime(long time) {
        date.setTime(time);
    }
}
