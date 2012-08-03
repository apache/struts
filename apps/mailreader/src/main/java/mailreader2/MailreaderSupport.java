/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package mailreader2;

import java.util.Map;

import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.apps.mailreader.dao.Subscription;
import org.apache.struts.apps.mailreader.dao.User;
import org.apache.struts.apps.mailreader.dao.UserDatabase;
import org.apache.struts.apps.mailreader.dao.impl.memory.MemorySubscription;
import org.apache.struts.apps.mailreader.dao.impl.memory.MemoryUser;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <p> Base Action for MailreaderSupport application. </p>
 * <p/>
 * <p> Note that this class does NOT implement model driven because of the way
 * the pre-existing model is designed. The MailReader DAO includes immutable
 * fields that can only be set on construction, and some objects do not have a
 * default construction. One approach would be to mirror all the DAO
 * properties on the Actions. As an alternative, this implementations uses the
 * DAO properties where possible, and uses local Action properties only as
 * needed. To create new objects, a blank temporary object is constructed, and
 * the page uses a mix of local Action properties and DAO properties. When the
 * new object is to be saved, the local Action properties are used to create
 * the object using the DAO factory methods, the input values are copied from
 * the temporary object, and the new object is saved. It's kludge, but it
 * avoids creating unnecessary local properties. Pick your poison.</p>
 */
public class MailreaderSupport extends ActionSupport
        implements SessionAware, ApplicationAware {

    /**
     * Return CANCEL so apropriate result can be selected.
     * @return "cancel" so apropriate result can be selected.
     */
    public String cancel() {
        return Constants.CANCEL;
    }

    /**
     * Convenience method to copy User properties.
     **/
    protected void copyUser(User source, User target) {
      if ((source==null) || (target==null)) return;
      target.setFromAddress(source.getFromAddress());
      target.setFullName(source.getFullName());
      target.setPassword(source.getPassword());
      target.setReplyToAddress(source.getReplyToAddress());
    }

    /**
     * Convenience method to copy Subscription properties.
     **/
    protected void copySubscription(Subscription source, Subscription target) {
      if ((source==null) || (target==null)) return;
      target.setAutoConnect(source.getAutoConnect());
      target.setPassword(source.getPassword());
      target.setType(source.getType());
      target.setUsername(source.getUsername());
    }


    // ---- ApplicationAware ----

    /**
     * <p>Field to store application context or its proxy.</p>
     * <p/>
     * <p>The application context lasts for the life of the application. A
     * reference to the database is stored in the application context at
     * startup.</p>
     */
    private Map application;

    /**
     * <p>Store a new application context.</p>
     *
     * @param value A Map representing application state
     */
    public void setApplication(Map value) {
        application = value;
    }

    /**
     * <p>Provide application context.</p>
     */
    public Map getApplication() {
        return application;
    }

    // ---- SessionAware ----

    /**
     * <p>Field to store session context, or its proxy.</p>
     */
    private Map session;

    /**
     * <p>Store a new session context.</p>
     *
     * @param value A Map representing session state
     */
    public void setSession(Map value) {
        session = value;
    }

    /**
     * <p>Provide session context.</p>
     *
     * @return session context
     */
    public Map getSession() {
        return session;
    }

    // ---- Task property (utilized by UI) ----

    /**
     * <p>Field to store workflow task.</p>
     * <p/>
     * <p>The Task is used to track the state of the CRUD workflows. It can be
     * set to Constant.CREATE, Constant.EDIT, or Constant.DELETE as
     * needed.</p>
     */
    private String task = null;


    /**
     * <p>Provide worklow task.</p>
     *
     * @return Returns the task.
     */
    public String getTask() {
        return task;
    }

    /**
     * <p>Store new workflow task.</p>
     *
     * @param value The task to set.
     */
    public void setTask(String value) {
        task =  value;
    }

    // ---- Token property (utilized by UI) ----

    /**
     * <p>Field to store double-submit guard.</p>
     */
    private String token = null;


    /**
     * <p>Provide Token.</p>
     *
     * @return Returns the token.
     */
    public String getToken() {
        return token;
    }

    /**
     * <p>Store new Token.</p>
     *
     * @param value The token to set.
     */
    public void setToken(String value) {
        token =  value;
    }


    // ---- Host property ----

    /**
     * <p>Field to store Subscription host.</p>
     * <p/>
     * <p> The host is an immutable property of the Subscrtion DAP object, so
     * we need to store it locally until we are ready to create the
     * Subscription. </p>
     */
    private String host;

    /**
     * <p>Provide tSubscription host.</p>
     *
     * @return host property
     */
    public String getHost() {
        return host;
    }

    /**
     * <p>Store new Subscription host.</p>
     *
     * @param value
     */
    public void setHost(String value) {
        host = value;
    }

    // ---- Password property ----

    /**
     * <p>Field to store User password property.</p>
     * <p/>
     * <p>The User DAO object password proerty is immutable, so we store it
     * locally until we are ready to create the object.</p>
     */
    private String password = null;


    /**
     * <p>Provide User password</p>
     *
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * <p>Store new User Password</p>
     *
     * @param value The password to set.
     */
    public void setPassword(String value) {
        password = value;
    }

    // ---- Password2 property (confirmation) ----

    /**
     * <p>Field to store the User password confirmation.</p>
     * <p/>
     * <p>When a User object is created, we ask the client to enter the
     * password twice, to help ensure the password is being typed
     * correctly.</p>
     */
    private String password2 = null;


    /**
     * <p>Provide the User password confirmation.</p>
     *
     * @return Returns the confirmationpassword.
     */
    public String getPassword2() {
        return password2;
    }

    /**
     * <p>Store a new User password confirmation.</p>
     *
     * @param value The confirmation password to set.
     */
    public void setPassword2(String value) {
        password2 = value;
    }

    // ---- Username property ----

    /**
     * <p>Field to store User username.</p>
     * <p/>
     * <p>The User DAO object password proerty is immutable, so we store it
     * locally until we are ready to create the object.</p>
     */
    private String username = null;


    /**
     * <p>Provide User username.</p>
     *
     * @return Returns the User username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * <p>Store new User username</p>
     *
     * @param value The username to set.
     */
    public void setUsername(String value) {
        username = value;
    }

    // ---- Database property ----

    /**
     * <p>Provide reference to UserDatabase, or null if the database is not
     * available. </p>
     *
     * @return a reference to the UserDatabase or null if the database is not
     *         available
     */
    public UserDatabase getDatabase() {
        Object db = getApplication().get(Constants.DATABASE_KEY);
        if (db == null) {
            this.addActionError(getText("error.database.missing"));
        }
        return (UserDatabase) db;
    }

    /**
     * <p>Store a new reference to UserDatabase</p>
     *
     * @param database
     */
    public void setDatabase(UserDatabase database) {
        getApplication().put(Constants.DATABASE_KEY, database);
    }

    // ---- User property ----

    /**
     * <p>Provide reference to User object for authenticated user.</p>
     *
     * @return User object for authenticated user.
     */
    public User getUser() {
        return (User) getSession().get(Constants.USER_KEY);
    }

    /**
     * <p>Store new reference to User Object.</p>
     *
     * @param user User object for authenticated user
     */
    public void setUser(User user) {
        getSession().put(Constants.USER_KEY, user);
    }

    /**
     * <p>Obtain User object from database, or return null if the credentials
     * are not found or invalid.</p>
     *
     * @param username User username
     * @param password User password
     * @return User object or null if not found
     * @throws ExpiredPasswordException
     */
    public User findUser(String username, String password)
            throws ExpiredPasswordException {
        // FIXME: Stupid testing hack to compensate for inadequate DAO layer
        if (Constants.EXPIRED_PASSWORD_EXCEPTION.equals(username)) {
            throw new ExpiredPasswordException(Constants.EXPIRED_PASSWORD_EXCEPTION);
        }

        User user = getDatabase().findUser(username);
        if ((user != null) && !user.getPassword().equals(password)) {
            user = null;
        }
        if (user == null) {
            this.addFieldError(Constants.PASSWORD_MISMATCH_FIELD,
                    getText("error.password.mismatch"));
        }
        return user;
    }

    /**
     * <p><code>Log</code> instance for this application. </p>
     */
    protected Logger log = LoggerFactory.getLogger(Constants.PACKAGE);

    /**
     * <p> Persist the User object, including subscriptions, to the database.
     * </p>
     *
     * @throws java.lang.Exception on database error
     */
    public void saveUser() throws Exception {
        try {
            getDatabase().save();
        } catch (Exception e) {
            String message = Constants.LOG_DATABASE_SAVE_ERROR + getUser()
                    .getUsername();
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void createInputUser() {
        User user = new MemoryUser(null, null);
        setUser(user);
    }

    /**
     * <p> Verify input for creating a new user, create the user, and process
     * the login. </p>
     *
     * @return A new User and empty Errors if create succeeds, or null and
     *         Errors if create fails
     */
    public User createUser(String username, String password) {

        UserDatabase database = getDatabase();
        User user;

        try {
            user = database.findUser(username);
         }

        catch (ExpiredPasswordException e) {
            user = getUser(); // Just so that it is not null
        }

        if (user != null) {
            this.addFieldError("username", "error.username.unique");
            return null;
        }

        return database.createUser(username);
    }

    // Since user.username is immutable, we have to use some local properties

    /**
     * <p>Use the current User object to create a new User object, and make
     * the new User object the authenticated user.</p>
     * <p/>
     * <p>The "current" User object is usually a temporary object being used
     * to capture input.</p>
     *
     * @param _username User username
     * @param _password User password
     */
    public void copyUser(String _username, String _password) {
        User input = getUser();
        input.setPassword(_password);
        User user = createUser(_username, _password);
        if (null != user) {
            copyUser(input,user);
            setUser(user);
        }
    }

    // ---- Subscription property ----

    /**
     * <p>Obtain the cached Subscription object, if any. </p>
     *
     * @return Cached Subscription object or null
     */
    public Subscription getSubscription() {
        return (Subscription) getSession().get(Constants.SUBSCRIPTION_KEY);
    }

    /**
     * <p>Store new User Subscription.</p>
     *
     * @param subscription
     */
    public void setSubscription(Subscription subscription) {
        getSession().put(Constants.SUBSCRIPTION_KEY, subscription);
    }

    /**
     * <p> Obtain User Subscription object for the given host, or return null
     * if not found. </p>
     *
     * <p>It would be possible for this code to throw a NullPointerException,
     * but the ExceptionHandler in the xwork.xml will catch that for us.</p>
     *
     * @return The matching Subscription or null
     */
    public Subscription findSubscription(String host) {
        Subscription subscription;
        subscription = getUser().findSubscription(host);
        return subscription;
    }

    /**
     * <p>Obtain uSER Subscription for the local Host property.</p>
     * <p/>
     * <p>Usually, the host property will be set from the client request,
     * because it was embedded in a link to the Subcription action.
     *
     * @return Subscription or null if not found
     */
    public Subscription findSubscription() {
        return findSubscription(getHost());
    }

    /**
     * <p>Provide a "temporary" User Subscription object that can be used to
     * capture input values.</p>
     */
    public void createInputSubscription() {
        Subscription sub = new MemorySubscription(getUser(), null);
        setSubscription(sub);
        setHost(sub.getHost());
    }

    /**
     * <p>Provide new User Subscription object for the given host, or null if
     * the host is not unique.</p>
     *
     * @param host
     * @return New User Subscription object or null
     */
    public Subscription createSubscription(String host) {

        Subscription sub;

        sub = findSubscription(host);

        if (null != sub) {
            // FIXME - localization - "error.host.unique")
            addFieldError(Constants.HOST,"That hostname is already defined");
            return null;
        }

        return getUser().createSubscription(host);
    }

    /**
     * <p>Create a new Subscription from the current Subscription object,
     * making the new Subscription the current Subscription. </p>
     * <p/>
     * <p>Usually, the "current" Subscription is a temporary object being used
     * to capture input values.</p>
     *
     * @param host
     */
    public void copySubscription(String host) {
        Subscription input = getSubscription();
        Subscription sub = createSubscription(host);
        if (null != sub) {
            copySubscription(input, sub);
            setSubscription(sub);
            setHost(sub.getHost());
        }
    }

    /**
     * <p>Delete the current Subscription object from the database.</p>
     */
    public void removeSubscription()  {
        getUser().removeSubscription(getSubscription());
        getSession().remove(Constants.SUBSCRIPTION_KEY);
    }

    /**
     * <p>Provide MailServer Host for current User Subscription.</p>
     *
     * @return MailServer Host for current User Subscription
     */
    public String getSubscriptionHost() {
        Subscription sub = getSubscription();
        if (null == sub) {
            return null;
        }
        return sub.getHost();
    }

}
