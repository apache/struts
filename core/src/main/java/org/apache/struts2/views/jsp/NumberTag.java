package org.apache.struts2.views.jsp;

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

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Number;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Number
 */
public class NumberTag extends ContextBeanTag {

    private static final long serialVersionUID = -6216963123295613440L;

    private String name;
    private String currency;
    private String type;
    private Boolean groupingUsed;
    private Integer maximumFractionDigits;
    private Integer maximumIntegerDigits;
    private Integer minimumFractionDigits;
    private Integer minimumIntegerDigits;
    private Boolean parseIntegerOnly;
    private String roundingMode;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Number(stack);
    }

    protected void populateParams() {
        super.populateParams();
        Number n = (Number) component;
        n.setName(name);
        n.setCurrency(currency);
        n.setType(type);
        n.setGroupingUsed(groupingUsed);
        n.setMaximumFractionDigits(maximumFractionDigits);
        n.setMaximumIntegerDigits(maximumIntegerDigits);
        n.setMinimumFractionDigits(minimumFractionDigits);
        n.setMinimumIntegerDigits(minimumIntegerDigits);
        n.setParseIntegerOnly(parseIntegerOnly);
        n.setRoundingMode(roundingMode);

    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param groupingUsed the groupingUsed to set
     */
    public void setGroupingUsed(Boolean groupingUsed) {
        this.groupingUsed = groupingUsed;
    }

    /**
     * @param maximumFractionDigits the maximumFractionDigits to set
     */
    public void setMaximumFractionDigits(Integer maximumFractionDigits) {
        this.maximumFractionDigits = maximumFractionDigits;
    }

    /**
     * @param maximumIntegerDigits the maximumIntegerDigits to set
     */
    public void setMaximumIntegerDigits(Integer maximumIntegerDigits) {
        this.maximumIntegerDigits = maximumIntegerDigits;
    }

    /**
     * @param minimumFractionDigits the minimumFractionDigits to set
     */
    public void setMinimumFractionDigits(Integer minimumFractionDigits) {
        this.minimumFractionDigits = minimumFractionDigits;
    }

    /**
     * @param minimumIntegerDigits the minimumIntegerDigits to set
     */
    public void setMinimumIntegerDigits(Integer minimumIntegerDigits) {
        this.minimumIntegerDigits = minimumIntegerDigits;
    }

    /**
     * @param parseIntegerOnly the parseIntegerOnly to set
     */
    public void setParseIntegerOnly(Boolean parseIntegerOnly) {
        this.parseIntegerOnly = parseIntegerOnly;
    }

    /**
     * @param roundingMode the roundingMode to set
     */
    public void setRoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
    }

}
