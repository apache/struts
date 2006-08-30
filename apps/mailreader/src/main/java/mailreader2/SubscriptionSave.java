package mailreader2;

/**
 * <p> Save a subscriptioin, resetting checkbox in prepare.</p>
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
