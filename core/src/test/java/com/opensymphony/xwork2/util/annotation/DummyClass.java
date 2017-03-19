package com.opensymphony.xwork2.util.annotation;

@MyAnnotation("class-test")
public class DummyClass implements DummyInterface {

    public DummyClass() {
    }

    @MyAnnotation("method-test")
    public void methodWithAnnotation() {
    }

	@Override
	public void interfaceMethodWithAnnotation() {
	}

	@MyAnnotation2
    private void privateMethodWithAnnotation() {
    }
}
