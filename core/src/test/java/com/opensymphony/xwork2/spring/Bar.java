/*
 * Created on Nov 12, 2003
 */
package com.opensymphony.xwork2.spring;

/**
 * @author Mike
 */
public class Bar {
	
	private Foo foo;
	private String thing;
	private int value;
	
	/**
	 * @return Returns the foo.
	 */
	public Foo getFoo() {
		return foo;
	}

	/**
	 * @param foo The foo to set.
	 */
	public void setFoo(Foo foo) {
		this.foo = foo;
	}

	/**
	 * @return Returns the thing.
	 */
	public String getThing() {
		return thing;
	}

	/**
	 * @param thing The thing to set.
	 */
	public void setThing(String thing) {
		this.thing = thing;
	}

	/**
	 * @return Returns the value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(int value) {
		this.value = value;
	}
}
