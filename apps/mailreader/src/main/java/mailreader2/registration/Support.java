package mailreader2.registration;

import mailreader2.MailreaderSupport;
import org.apache.struts.apps.mailreader.dao.User;

/**
 * <p>Base class to store shared methods.</p>
 */
public class Support extends MailreaderSupport {

    /**
     * <p>Double check that there is not a valid User logon. </p>
     *
     * @return True if there is not a valid User logon
     */
    protected boolean isCreating() {
        User user = getUser();
        return (null == user) || (null == user.getDatabase());
    }
}
