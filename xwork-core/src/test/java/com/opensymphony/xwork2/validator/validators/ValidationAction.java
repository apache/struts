package com.opensymphony.xwork2.validator.validators;

public class ValidationAction {

    private Integer intRange;
    private Integer intMinValue;
    private Integer intMaxValue;

    private Short shortRange;
    private Short shortMinValue;
    private Short shortMaxValue;

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
}
