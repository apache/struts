package com.opensymphony.xwork2.spring;

public class TestAspect {
	protected String log = "";
	
	private String issueId;
	private int count;
	private String name;
	private int count2;
    private boolean exposeProxy;

	String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		log = log + "setIssueId(" + issueId + ")-";
		this.issueId = issueId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		log = log + "setCount(" + count + ")-";
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		log = log + "setName(" + name + ")-";
		this.name = name;
	}

	int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		log = log + "setCount2(" + count2 + ")-";
		this.count2 = count2;
	}

    public void setExposeProxy(boolean exposeProxy) {
        this.exposeProxy = exposeProxy;
    }
}
