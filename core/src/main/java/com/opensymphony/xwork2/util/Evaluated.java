package com.opensymphony.xwork2.util;

public class Evaluated {

    private Object value;

    public Evaluated(Object value) {
        this.value = value;
    }

    public boolean isDefined() {
        return value != null;
    }

    public Object get() {
        return value;
    }
}
