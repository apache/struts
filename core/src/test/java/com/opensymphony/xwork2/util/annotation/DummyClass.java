package com.opensymphony.xwork2.util.annotation;

@MyAnnotation("class-test")
public class DummyClass {

    public DummyClass() {
    }

    @MyAnnotation("method-test")
    public void methodWithAnnotation() {
    }

}
