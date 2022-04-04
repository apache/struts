package com.opensymphony.xwork2.validator.validators;

import java.util.Date;

public class ValidationAction {

    private Integer intRange;
    private Integer intMinValue;
    private Integer intMaxValue;

    private Short shortRange;
    private Short shortMinValue;
    private Short shortMaxValue;

    private Long longRange;
    private Long longMinValue;
    private Long longMaxValue;

    private Date dateRange;
    private Date dateMinValue;
    private Date dateMaxValue;
    private String dateFormat;
    private String stringValue;

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
}
