package mailreader2.registration;

import mailreader2.Constants;

/**
 * <p> Retrieve User object to edit or null if User does not exist. </p>
 */
public class Input extends Support {

    /**
     * <p> Retrieve User object to edit or null if User does not exist. </p>
     *
     * @return The "Success" result for this mapping
     * @throws Exception on any error
     */
    public String execute() throws Exception {

        if (isCreating()) {
            createInputUser();
            setTask(Constants.CREATE);
        } else {
            setTask(Constants.EDIT);
            setUsername(getUser().getUsername());
            setPassword(getUser().getPassword());
            setPassword2(getUser().getPassword());
        }

        return SUCCESS;
    }

}
