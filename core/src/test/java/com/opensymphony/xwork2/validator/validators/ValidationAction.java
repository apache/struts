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
package com.opensymphony.xwork2.validator.validators;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ValidationAction {

    private Integer intRange;
    private Integer intMinValue;
    private Integer intMaxValue;
    private Integer[] ints;

    private Short shortRange;
    private Short shortMinValue;
    private Short shortMaxValue;
    private List<Short> shorts;

    private Long longRange;
    private Long longMinValue;
    private Long longMaxValue;

    private Date dateRange;
    private Date dateMinValue;
    private Date dateMaxValue;

    private String dateFormat;
    private String stringValue;
    private String[] strings;
    private Collection<String> stringCollection;

    public Integer getIntRange() {
        return intRange;
    }

    public void setIntRange(Integer intRange) {
        this.intRange = intRange;
    }

    public Integer getIntMinValue() {
        return intMinValue;
    }

    public void setIntMinValue(Integer intMinValue) {
        this.intMinValue = intMinValue;
    }

    public Integer getIntMaxValue() {
        return intMaxValue;
    }

    public void setIntMaxValue(Integer intMaxValue) {
        this.intMaxValue = intMaxValue;
    }

    public Short getShortRange() {
        return shortRange;
    }

    public void setShortRange(Short shortRange) {
        this.shortRange = shortRange;
    }

    public Short getShortMinValue() {
        return shortMinValue;
    }

    public void setShortMinValue(Short shortMinValue) {
        this.shortMinValue = shortMinValue;
    }

    public Short getShortMaxValue() {
        return shortMaxValue;
    }

    public void setShortMaxValue(Short shortMaxValue) {
        this.shortMaxValue = shortMaxValue;
    }

    public Long getLongRange() {
        return longRange;
    }

    public void setLongRange(Long longRange) {
        this.longRange = longRange;
    }

    public Long getLongMinValue() {
        return longMinValue;
    }

    public void setLongMinValue(Long longMinValue) {
        this.longMinValue = longMinValue;
    }

    public Long getLongMaxValue() {
        return longMaxValue;
    }

    public void setLongMaxValue(Long longMaxValue) {
        this.longMaxValue = longMaxValue;
    }

    public Date getDateRange() {
        return dateRange;
    }

    public void setDateRange(Date dateRange) {
        this.dateRange = dateRange;
    }

    public Date getDateMinValue() {
        return dateMinValue;
    }

    public void setDateMinValue(Date dateMinValue) {
        this.dateMinValue = dateMinValue;
    }

    public Date getDateMaxValue() {
        return dateMaxValue;
    }

    public void setDateMaxValue(Date dateMaxValue) {
        this.dateMaxValue = dateMaxValue;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public Integer[] getInts() {
        return ints;
    }

    public void setInts(Integer[] ints) {
        this.ints = ints;
    }

    public List<Short> getShorts() {
        return shorts;
    }

    public void setShorts(List<Short> shorts) {
        this.shorts = shorts;
    }

    public String[] getStrings() {
        return strings;
    }

    public void setStrings(String[] strings) {
        this.strings = strings;
    }

    public Collection<String> getStringCollection() {
        return stringCollection;
    }

    public void setStringCollection(Collection<String> stringCollection) {
        this.stringCollection = stringCollection;
    }
}
