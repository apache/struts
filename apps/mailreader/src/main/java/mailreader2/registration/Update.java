package mailreader2.registration;

import mailreader2.Constants;

/**
 * <p> Update Registration object to the persistent store. </p>
 */
public class Update extends Support {

    private String fixNull(String value) {
        if (value == null) return null;
        String buffer = value.trim();
        if (buffer.length() == 0) return null;
        return buffer;
    }

    public String execute()
            throws Exception {

        boolean creating = Constants.CREATE.equals(getTask());
        creating = creating && isCreating(); // trust but verify
        if (creating) {
            addActionError("registration/Update: ");
            addActionError(Constants.ERROR_INVALID_WORKFLOW);            
            return ERROR;
        }
        // FIXME: Any way to call the Update validators from here?
        String newPassword = fixNull(getPassword());
        String confirmPassword = fixNull(getPassword2());
        if (newPassword != null) {
            boolean matches = ((confirmPassword != null)
                    && (confirmPassword.equals(newPassword)));
            if (matches) {
                getUser().setPassword(newPassword);
            } else {
                addActionError(getText("error.password.match"));
                return INPUT;
            }
        }

        saveUser();

        return SUCCESS;
    }
}
