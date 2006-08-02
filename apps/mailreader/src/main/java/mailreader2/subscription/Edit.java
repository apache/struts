package mailreader2.subscription;

import mailreader2.Constants;

/**
 * <p>Prepare to edit User Support.</p>
 */
public class Edit extends Support {

    public String execute() throws Exception {
        setTask(Constants.EDIT);
        return find();
    }


}
