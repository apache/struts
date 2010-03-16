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

package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Date
 */
public class DateTag extends ContextBeanTag {

    private static final long serialVersionUID = -6216963123295613440L;

    protected String name;
    protected String format;
    protected boolean nice;
    protected String timezone;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Date(stack);
    }

    protected void populateParams() {
        super.populateParams();
        Date d = (Date)component;
        d.setName(name);
        d.setFormat(format);
        d.setNice(nice);
        d.setTimezone(timezone);
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setNice(boolean nice) {
        this.nice = nice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

}
