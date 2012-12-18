package ${package};

import javax.portlet.PortletPreferences;

import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.apache.struts2.portlet.interceptor.PortletPreferencesAware;

public class UpdateNameAction extends DefaultActionSupport implements PortletPreferencesAware {

	private static final long serialVersionUID = 1L;

	private String firstName;

	private String lastName;

	private PortletPreferences preferences;

	@Override
	public String execute() throws Exception {
		preferences.setValue("firstName", firstName);
		preferences.setValue("lastName", lastName);
		preferences.store();
		getActionMessages().add("Name updated");
		return SUCCESS;
	}
	
	@Override
	public String input() throws Exception {
		firstName = preferences.getValue("firstName", "");
		lastName = preferences.getValue("lastName", "");
		return INPUT;
	}

	public void setPortletPreferences(PortletPreferences preferences) {
		this.preferences = preferences;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getLastName() {
		return lastName;
	}

}
