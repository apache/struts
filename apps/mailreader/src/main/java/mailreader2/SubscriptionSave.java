package mailreader2;

/**
 * <p> Workaround class. Submitting to an alias doesn't seem to work. </p>
 */
public final class SubscriptionSave extends Subscription {

    public void prepare() {
        super.prepare();
            // checkbox workaround
        getSubscription().setAutoConnect(false);
    }

    public String execute() throws Exception {
        return save();
    }



}
