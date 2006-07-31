package mailreader2;

/**
 * <p/>
 * Provide properties to transfer data values between
 * the view and model layers (data transfer object).
 * </p>
 *
 * @author Ted Husted
 * @version $Revision: 1.0 $ $Date: Jul 30, 2006 9:29:50 AM $
 */
public class AppData {

    private boolean nominal;

    private String locale_key;
    private String locale_code;
    private String locale_name;

    private String registration_key;
    private String username;
    private String password;
    private String password2;
    private String fullname;
    private String email_from;
    private String email_replyto;

    private String protocol_key;
    private String protocol_code;
    private String protocol_name;

    private String subscription_key;
    private String subscription_host;
    private String host_user;
    private String host_pass;
    private Integer host_auto;

    /**
     * <p>Adapt internal Integer value to external boolean value. </p>
     * @return False if host_auto==0, True otherwise
     */
    public boolean isHost_auto_checkbox() {
        Integer _host_auto = getHost_auto();
        if (_host_auto == null) _host_auto = Constants.DB_FALSE;
        return (_host_auto == Constants.DB_TRUE);
    }

    /**
     * <p>Adapt internal Integer value to external boolean value. </p>
     */
    public void setHost_auto_checkbox(boolean host_auto_checkbox) {
        if (host_auto_checkbox)
            setHost_auto(Constants.DB_TRUE);
        else setHost_auto(Constants.DB_FALSE);
    }

    /**
     * <p>Return true if a logic or state test passed,
     * such as whether a record already exists.</p>
     * @return True if a business logic test passed.
     */
    public boolean isNominal() {
        return nominal;
    }

    /**
     * <p>Record outcome of a logic or state test,
     * such as whether a record already exists.</p>
     */
    public void setNominal(boolean nominal) {
        this.nominal = nominal;
    }

    public String getLocale_key() {
        return locale_key;
    }

    public void setLocale_key(String locale_key) {
        this.locale_key = locale_key;
    }

    public String getLocale_code() {
        return locale_code;
    }

    public void setLocale_code(String locale_code) {
        this.locale_code = locale_code;
    }

    public String getLocale_name() {
        return locale_name;
    }

    public void setLocale_name(String locale_name) {
        this.locale_name = locale_name;
    }

    public String getRegistration_key() {
        return registration_key;
    }

    public void setRegistration_key(String registration_key) {
        this.registration_key = registration_key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail_from() {
        return email_from;
    }

    public void setEmail_from(String email_from) {
        this.email_from = email_from;
    }

    public String getEmail_replyto() {
        return email_replyto;
    }

    public void setEmail_replyto(String email_replyto) {
        this.email_replyto = email_replyto;
    }

    public String getProtocol_key() {
        return protocol_key;
    }

    public void setProtocol_key(String protocol_key) {
        this.protocol_key = protocol_key;
    }

    public String getProtocol_code() {
        return protocol_code;
    }

    public void setProtocol_code(String protocol_code) {
        this.protocol_code = protocol_code;
    }

    public String getSubscription_key() {
        return subscription_key;
    }

    public void setSubscription_key(String subscription_key) {
        this.subscription_key = subscription_key;
    }

    public String getSubscription_host() {
        return subscription_host;
    }

    public void setSubscription_host(String subscription_host) {
        this.subscription_host = subscription_host;
    }

    public String getHost_user() {
        return host_user;
    }

    public void setHost_user(String host_user) {
        this.host_user = host_user;
    }

    public String getHost_pass() {
        return host_pass;
    }

    public void setHost_pass(String host_pass) {
        this.host_pass = host_pass;
    }

    public String getProtocol_name() {
        return protocol_name;
    }

    public void setProtocol_name(String protocol_name) {
        this.protocol_name = protocol_name;
    }

    public Integer getHost_auto() {
        return host_auto;
    }

    public void setHost_auto(Integer host_auto) {
        this.host_auto = host_auto;
    }

}
