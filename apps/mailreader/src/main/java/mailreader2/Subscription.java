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

import com.opensymphony.xwork2.Preparable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p> Provide an Edit method for retrieving an existing subscription, and a
 * Save method for updating or inserting a subscription. </p>
 */
public class Subscription extends MailreaderSupport
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
     * the User Subscription (if any). </p>
     */
    public void prepare() {

        Map m = new LinkedHashMap();
        m.put("imap", "IMAP Protocol");
        m.put("pop3", "POP3 Protocol");
        types = m;

        setHost(getSubscriptionHost());
    }

    /**
     * <p>Setup a temporary User Subscription object to capture input
     * values.</p>
     *
     * @return INPUT
     */
    public String input() {
        createInputSubscription();
        setTask(Constants.CREATE);
        return INPUT;
    }

    /**
     * <p>Load User Subscription for the local Host property.</p>
     * <p/>
     * <p>Usually, the Host is being set from the request by a link to an Edit
     * or Delete task.</p>
     *
     * @return INPUT or Error, if Subscription is not found
     */
    public String find() {

        org.apache.struts.apps.mailreader.dao.Subscription
                sub = findSubscription();

        if (sub == null) {
            return ERROR;
        }

        setSubscription(sub);

        return INPUT;

    }

    /**
     * <p>Prepare to present a confirmation page before removing
     * Subscription.</p>
     *
     * @return INPUT or Error, if Subscription is not found
     */
    public String delete() {

        setTask(Constants.DELETE);
        return find();
    }

    /**
     * <p>Prepare to edit User Subscription.</p>
     *
     * @return INPUT or Error, if Subscription is not found
     */
    public String edit() {

        setTask(Constants.EDIT);
        return find();
    }

    /**
     * <p> Examine the Task property and DELETE, CREATE, or save the User
     * Subscription, as appropriate. </p>
     *
     * @return SUCCESS
     * @throws Exception on a database error
     */
    public String save() throws Exception {

        if (Constants.DELETE.equals(getTask())) {
            removeSubscription();
        }

        if (Constants.CREATE.equals(getTask())) {
            copySubscription(getHost());
        }

        if (hasErrors()) return INPUT;

        saveUser();
        return SUCCESS;
    }

}
