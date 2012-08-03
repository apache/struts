package org.apache.struts2.portlet.test;

public class Struts2PortletTest extends BasePortletTest {
	
	private final static String PORTLET_NAME = "StrutsPortlet";

    public void testNone() {}
	
	public void testIndexPage() throws Exception {
		beginAt("pluto/index.jsp");
		assertTextPresent("Welcome to the Struts example portlet");
		assertLinkPresentWithExactText("A simple form");
		assertLinkPresentWithExactText("Validation");
	}
	
	public void testFormExample() throws Exception {
		beginAt("pluto/index.jsp");
		clickLinkWithExactText("A simple form");
		assertFormPresent("processFormExample");
		assertTextPresent("Input your name");
		setWorkingForm("processFormExample");
		setTextField("firstName", "Nils-Helge");
		setTextField("lastName", "Garli");
		submit();
		assertTextPresent("Hello Nils-Helge Garli");
	}
	
	public void testValidationExample() throws Exception {
		beginAt("pluto/index.jsp");
		clickLinkWithExactText("Validation");
		assertFormPresent("processValidationExample");
		assertTextPresent("Input your name");
		setWorkingForm("processValidationExample");
		setTextField("firstName", "Nils-Helge");
		submit();
		assertTextFieldEquals("firstName", "Nils-Helge");
		assertTextPresent("You must enter a last name");
		setTextField("lastName", "Garli");
		submit();
		assertTextPresent("Hello Nils-Helge Garli");
	}
	
	public void testValidationErrorMessagesStickBetweenWindowStateChanges() throws Exception {
		beginAt("pluto/index.jsp");
		clickLinkWithExactText("Validation");
		assertFormPresent("processValidationExample");
		assertTextPresent("Input your name");
		setWorkingForm("processValidationExample");
		setTextField("firstName", "Nils-Helge");
		submit();
		assertTextFieldEquals("firstName", "Nils-Helge");
		assertTextPresent("You must enter a last name");
		minimizeWindow();
		assertTextNotPresent("Input your name");
		restoreWindow();
		assertTextPresent("Input your name");
		assertTextPresent("You must enter a last name");
	}
	
	public void testTokenExample() throws Exception {
		beginAt("pluto/index.jsp");
		clickLinkWithText("Token");
		setWorkingForm(0);
		setTextField("theValue", "something");
		submit();
		assertTextPresent("ERROR");
		setWorkingForm(1);
		setTextField("theValue", "somethingElse");
		submit();
		assertTextPresent("The form was successfully submitted with a valid token");
	}
	
	public void testSwitchFromViewToEditShouldGoToDefaultEditPage() throws Exception {
		beginAt("pluto/index.jsp");
		assertTextPresent("Welcome to the Struts example portlet");
		switchEdit();
		assertTextPresent("Back to view mode");
	}

	@Override
	public String getPortletName() {
		return PORTLET_NAME;
	}
	
}
