package mailreader2.subscription;

import mailreader2.Constants;

/**
 * <p> Workaround class. Submitting to an alias doesn't seem to work. </p>
 */
public final class Update extends Edit {

    public void prepare() {
        super.prepare();
        // checkbox workaround
        getSubscription().setAutoConnect(false);
    }

    /**
     * <p> Examine the Task property and DELETE, CREATE, or save the User
     * Support, as appropriate. </p>
     *
     * @return SUCCESS
     * @throws Exception on a database error
     */
    public String execute() throws Exception {

        if (Constants.DELETE.equals(getTask())) {
            removeSubscription();
        }

        if (Constants.CREATE.equals(getTask())) {
            copySubscription(getHost());
        }

        if (hasErrors()) return INPUT;

        saveUser();
        return SUCCESS;
    }
}
