/*
 * Created on Nov 11, 2003
 */
package com.opensymphony.xwork2.spring;

/**
 * @author Mike
 */
public class Foo 
{	
	String name = null;
	
	public Foo() {
		name = "not set";
	}
	
	public Foo(String name) {
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
}
