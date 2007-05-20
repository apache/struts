package org.apache.struts2;

public enum SomeEnum {
	
	INIT("init"), COMPLETED("completed");
	
	private String displayName;

	SomeEnum(String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return name();
	}

	public String getDisplayName() {
		return displayName;
	}
}