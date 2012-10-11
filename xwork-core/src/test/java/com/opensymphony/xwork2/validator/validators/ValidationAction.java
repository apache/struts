package com.opensymphony.xwork2.validator.validators;

public class ValidationAction {

    private Integer intRange;
    private Integer minValue;
    private Integer maxValue;

    public Integer getIntRange() {
        return intRange;
    }

    public void setIntRange(Integer intRange) {
        this.intRange = intRange;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }
}
