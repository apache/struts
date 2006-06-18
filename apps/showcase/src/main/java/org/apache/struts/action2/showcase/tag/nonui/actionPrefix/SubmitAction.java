package org.apache.struts.action2.showcase.tag.nonui.actionPrefix;

import com.opensymphony.xwork.ActionSupport;

public class SubmitAction extends ActionSupport {

	private static final long serialVersionUID = -7832803019378213087L;
	
	private String text;
	
	public String getText() { return text; }
	public void setText(String text) { this.text = text; }
	
	public String execute() throws Exception {
		return SUCCESS;
	}
	
	public String alternateMethod() {
		return "methodPrefixResult";
	}
	
}
