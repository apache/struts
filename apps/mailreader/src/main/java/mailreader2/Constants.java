/*
 * $Id: Constants.java 360442 2005-12-31 20:10:04Z husted $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mailreader2;

/**
 * <p> Manifest constants for the MailReader application. </p>
 */
public final class Constants {

    // -- Statements --

    public static final Integer DB_FALSE = 0;
    public static final Integer DB_TRUE = 1;

    public static final String LOCALE_LIST = "LOCALE_LIST";
    public static final String REGISTRATION_INSERT_ASSERT = "REGISTRATION_INSERT_ASSERT";
    public static final String REGISTRATION_INSERT = "REGISTRATION_INSERT";
    public static final String REGISTRATION_PASSWORD = "REGISTRATION_PASSWORD";
    public static final String REGISTRATION_FULLNAME = "REGISTRATION_FULLNAME";
    public static final String REGISTRATION_EDIT = "REGISTRATION_EDIT";
    public static final String REGISTRATION_UPDATE = "REGISTRATION_UPDATE";
    public static final String SUBSCRIPTION_INSERT_ASSERT = "SUBSCRIPTION_INSERT_ASSERT";
    public static final String SUBSCRIPTION_INSERT = "SUBSCRIPTION_INSERT";
    public static final String SUBSCRIPTION_LIST = "SUBSCRIPTION_LIST";
    public static final String SUBSCRIPTION_EDIT = "SUBSCRIPTION_EDIT";
    public static final String SUBSCRIPTION_UPDATE = "SUBSCRIPTION_UPDATE";

    // -- Tokens --

    /**
     * <p> The token representing a "cancel" request. </p>
     */
    public static final String CANCEL = "cancel";

    /**
     * <p> The token representing a "create" task. </p>
     */
    public static final String CREATE = "Create";

    /**
     * <p> The application scope attribute under which our user database is
     * stored. </p>
     */
    public static final String DATABASE_KEY = "database";

    /**
     * <p> The token representing a "edit" task. </p>
     */
    public static final String DELETE = "Delete";

    /**
     * <p> The token representing a "edit" task. </p>
     */
    public static final String EDIT = "Edit";

    /**
     * <p> The package name for this application. </p>
     */
    public static final String PACKAGE = "org.apache.struts.apps.mailreader";

    /**
     * <p> The session scope attribute under which the Support object
     * currently selected by our logged-in User is stored. </p>
     */
    public static final String SUBSCRIPTION_KEY = "subscription";

    /**
     * <p> The session scope attribute under which the User object for the
     * currently logged in user is stored. </p>
     */
    public static final String USER_KEY = "user";

    /**
     * <p>The token representing the "Host" property.
     */
    public static final String HOST = "host";

    // ---- Error Messages ----

    /**
     * <p/>
     * A static message in case message resource is not loaded.
     * </p>
     */
    public static final String ERROR_MESSAGES_NOT_LOADED =
            "ERROR:  Message resources not loaded -- check servlet container logs for error messages.";

    /**
     * <p/>
     * A static message in case database resource is not loaded.
     * <p/>
     */
    public static final String ERROR_DATABASE_NOT_LOADED =
            "ERROR:  User database not loaded -- check servlet container logs for error messages.";

    /**
     * <p/>
     * A standard key from the message resources file, to test if it is available.
     * <p/>
     */
    public static final String ERROR_DATABASE_MISSING = "error.database.missing";

    /**
     * <p/>
     * A "magic" username to trigger an ExpiredPasswordException for testing.
     * </p>
     */
    public static final String EXPIRED_PASSWORD_EXCEPTION = "ExpiredPasswordException";

    /**
     * <p/>
     * Name of field to associate with authentification errors.
     * <p/>
     */
    public static final String PASSWORD_MISMATCH_FIELD = "password";

    /**
     * <p/>
     * A static message in case message resource is not loaded.
     * </p>
     */
    public static final String ERROR_INVALID_WORKFLOW =
            "ERROR:  Action is being executed out of sequence!";

    // ---- Log Messages ----

    /**
     * <p> Message to log if saving a user fails. </p>
     */
    public static final String LOG_DATABASE_SAVE_ERROR =
            " Unexpected error when saving User: ";


}
