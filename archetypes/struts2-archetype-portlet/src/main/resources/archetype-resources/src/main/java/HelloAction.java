package ${package};

import javax.portlet.PortletPreferences;

import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.apache.struts2.portlet.interceptor.PortletPreferencesAware;

public class HelloAction extends DefaultActionSupport implements PortletPreferencesAware {

	private static final long serialVersionUID = 1L;

	private String firstName;

	private String lastName;

	private PortletPreferences preferences;

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setPortletPreferences(PortletPreferences preferences) {
		this.preferences = preferences;
	}

	@Override
	public String execute() throws Exception {
		firstName = preferences.getValue("firstName", "");
		lastName = preferences.getValue("lastName", "");
		return SUCCESS;
	}
}
