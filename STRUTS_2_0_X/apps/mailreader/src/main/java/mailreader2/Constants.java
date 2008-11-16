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

/**
 * <p> Manifest constants for the MailReader application. </p>
 */
public final class Constants {

    // --- Tokens ----

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
     * <p> The session scope attribute under which the Subscription object
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
     * <p>
     * A static message in case message resource is not loaded.
     * </p>
     */
    public static final String ERROR_MESSAGES_NOT_LOADED =
            "ERROR:  Message resources not loaded -- check servlet container logs for error messages.";

    /**
     * <p>
     * A static message in case database resource is not loaded.
     * <p>
     */
    public static final String ERROR_DATABASE_NOT_LOADED =
            "ERROR:  User database not loaded -- check servlet container logs for error messages.";

    /**
     * <p>
     * A standard key from the message resources file, to test if it is available.
     * <p>
     */
    public static final String ERROR_DATABASE_MISSING = "error.database.missing";

    /**
     * <P>
     * A "magic" username to trigger an ExpiredPasswordException for testing.
     *</p>
     */
    public static final String EXPIRED_PASSWORD_EXCEPTION = "ExpiredPasswordException";

    /**
     * <p>
     * Name of field to associate with authentification errors.
     * <p>
     */
    public static final String PASSWORD_MISMATCH_FIELD = "password";

    // ---- Log Messages ----

    /**
     * <p> Message to log if saving a user fails. </p>
     */
    public static final String LOG_DATABASE_SAVE_ERROR =
            " Unexpected error when saving User: ";


}
