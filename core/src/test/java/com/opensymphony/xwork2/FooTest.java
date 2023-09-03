package com.opensymphony.xwork2;

public class FooTest {

    public void testDefaultConstructor() {
        Foo foo = new Foo();
        String result = foo.getName();
        String expected = "not set";
        if (!result.equals(expected)) {
            throw new AssertionError("Expected: " + expected + ", but got: " + result);
        }
    }

    public void testParameterizedConstructor() {
        String expectedName = "John";
        Foo foo = new Foo(expectedName);
        String result = foo.getName();
        if (!result.equals(expectedName)) {
            throw new AssertionError("Expected: " + expectedName + ", but got: " + result);
        }
    }

    public void testGetName() {
        String expectedName = "Alice";
        Foo foo = new Foo(expectedName);
        String result = foo.getName();
        if (!result.equals(expectedName)) {
            throw new AssertionError("Expected: " + expectedName + ", but got: " + result);
        }
    }
}