package mailreader2.registration;

import mailreader2.Constants;
import org.apache.struts.apps.mailreader.dao.User;

/**
 * <p>Insert new user, providing extra validation.</p>
 * <p/>
 * <p>(On an update, the password change is optional.)</p>
 */
public final class Create extends Update {

    public String execute()
            throws Exception {

        // trust but verify
        boolean creating = Constants.CREATE.equals(getTask());
        creating = creating && isCreating();
        if (!creating) {
            addActionError("registration/Create: ");
            addActionError(Constants.ERROR_INVALID_WORKFLOW);
            return ERROR;
        }

        User user = findUser(getUsername(), getPassword());
        boolean haveUser = (user != null);

        if (haveUser) {
            addActionError(getText("error.username.unique"));
            return INPUT;
        }

        copyUser(getUsername(), getPassword());

        return SUCCESS;
    }
}
