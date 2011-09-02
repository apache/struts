package com.opensymphony.xwork2.interceptor.annotations;

/**
 * @author jafl
 *
 */
@BlockByDefault
public class BlockingByDefaultModel {
	
	@Allowed
	private String m1;
	private String m2;
	
	public void setM1(String s) {
		m1 = s;
	}
	
	public void setM2(String s) {
		m2 = s;
	}

}
