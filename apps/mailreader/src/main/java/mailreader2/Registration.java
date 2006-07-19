package mailreader2;

import org.apache.struts.apps.mailreader.dao.User;


/**
 * <p>Insert or update a User object to the persistent store. </p>
 */
public class Registration extends MailreaderSupport {

    /**
     * <p>Double check that there is not a valid User logon. </p>
     *
     * @return True if there is not a valid User logon
     */
    private boolean isCreating() {
        User user = getUser();
        return (null == user) || (null == user.getDatabase());
    }

    /**
     * <p> Retrieve User object to edit or null if User does not exist. </p>
     *
     * @return The "Success" result for this mapping
     * @throws Exception on any error
     */
    public String input() throws Exception {

        if (isCreating()) {
            createInputUser();
            setTask(Constants.CREATE);
        } else {
            setTask(Constants.EDIT);
            setUsername(getUser().getUsername());
            setPassword(getUser().getPassword());
            setPassword2(getUser().getPassword());
        }

        return INPUT;
    }

    /**
     * <p> Insert or update a User object to the persistent store. </p>
     * <p/>
     * <p> If a User is not logged in, then a new User is created and
     * automatically logged in. Otherwise, the existing User is updated. </p>
     *
     * @return The "Success" result for this mapping
     * @throws Exception on any error
     */
    public String execute()
            throws Exception {

        boolean creating = Constants.CREATE.equals(getTask());
        creating = creating && isCreating(); // trust but verify

        if (creating) {

            User user = findUser(getUsername(), getPassword());
            boolean haveUser = (user != null);

            if (haveUser) {
                addActionError(getText("error.username.unique"));
                return INPUT;
            }

            copyUser(getUsername(), getPassword());

        } else {

            // FIXME: Any way to call the RegisrationSave validators from here?
            String newPassword = getPassword();
            if (newPassword != null) {
                String confirmPassword = getPassword2();
                boolean matches = ((null != confirmPassword)
                        && (confirmPassword.equals(newPassword)));
                if (matches) {
                    getUser().setPassword(newPassword);
                } else {
                    addActionError(getText("error.password.match"));
                    return INPUT;
                }
            }
        }

        saveUser();

        return SUCCESS;
    }

}
