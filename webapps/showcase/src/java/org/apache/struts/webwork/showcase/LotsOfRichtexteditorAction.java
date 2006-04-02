/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.showcase;

import com.opensymphony.xwork.ActionSupport;

/**
 * 
 * @author tm_jee
 * @version $Date: 2006/02/17 15:21:00 $ $Id: LotsOfRichtexteditorAction.java,v 1.1 2006/02/17 15:21:00 tmjee Exp $
 */
public class LotsOfRichtexteditorAction extends ActionSupport {

	public String description1;
	public String description2 = "This is Description 2";
	public String description3;
	public String description4 = "This is Description 4";
	
	public String getDescription1() {
		return this.description1;
	}
	public void setDescription1(String description1) {
		this.description1 = description1;
	}
	
	
	public String getDescription2() {
		return this.description2;
	}
	public void setDescription2(String description2) {
		this.description2 = description2;
	}
	
	
	public String getDescription3() {
		return this.description3;
	}
	public void setDescription3(String description3) {
		this.description3 = description3;
	}
	
	
	
	
	public String getDescription4() {
		return this.description4;
	}
	public void setDescription4(String description4) {
		this.description4 = description4;
	}
	
	
	
	
	public String input() throws Exception {
		return SUCCESS;
	}
	
	public String submit() throws Exception {
		return SUCCESS;
	}
}
