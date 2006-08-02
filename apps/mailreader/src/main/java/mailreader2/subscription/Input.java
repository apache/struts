package mailreader2.subscription;

import mailreader2.Constants;

/**
 * <p>Setup a temporary User Support object to capture input
 * values.</p>
 */
public class Input extends Support {

    public String execute() {
        createInputSubscription();
        setTask(Constants.CREATE);
        return SUCCESS;
    }

}
