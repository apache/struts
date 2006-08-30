package mailreader2.subscription;

import com.opensymphony.xwork2.Preparable;
import mailreader2.MailreaderSupport;
import mailreader2.Constants;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p> Provide an Edit method for retrieving an existing subscription, and a
 * Update method for updating or inserting a subscription. </p>
 */
public class Support extends MailreaderSupport
        implements Preparable {

    /**
     * <p>Field to store list of MailServer types</p>
     */
    private Map types = null;

    /**
     * <p>Provide the list of MailServer types.</p>
     *
     * @return List of MailServer types
     */
    public Map getTypes() {
        return types;
    }

    /**
     * <p>Setup the MailerServer types and set the local Host property from
     * the User Support (if any). </p>
     */
    public void prepare() {

        Map m = new LinkedHashMap();
        m.put("imap", "IMAP Protocol");
        m.put("pop3", "POP3 Protocol");
        types = m;

        setHost(getSubscriptionHost());

    }

    /**
     * <p>Load User Support for the local Host property.</p>
     * <p/>
     * <p>Usually, the Host is being set from the request by a link to an Edit
     * or Delete task.</p>
     *
     * @return INPUT or Error, if Support is not found
     */
    public String find() {

        org.apache.struts.apps.mailreader.dao.Subscription
                sub = findSubscription();

        if (sub == null) {
            addActionError("find (Subscription): ");
            addActionError(Constants.ERROR_INVALID_WORKFLOW);            
            return ERROR;
        }

        setSubscription(sub);

        return SUCCESS;
    }
}
