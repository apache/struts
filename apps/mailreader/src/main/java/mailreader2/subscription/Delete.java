package mailreader2.subscription;

import mailreader2.Constants;

/**
 * <p>Prepare to present a confirmation page before removing
 * subscription.</p>
 */
public class Delete extends Edit {

    public String execute() throws Exception {
        setTask(Constants.DELETE);
        return find();
    }
}
