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

import org.apache.struts.apps.mailreader.dao.User;


/**
 * <p>Insert or update a User object to the persistent store. </p>
 */
public class Registration extends MailreaderSupport {

    /**
     * <p>Double check that there is not a valid User login. </p>
     *
     * @return True if there is not a valid User login
     */
    private boolean isCreating() {
        User user = getUser();
        return (null == user) || (null == user.getDatabase());
    }

    /**
     * <p> Retrieve User object to edit or null if User does not exist. </p>
     *
     * @return The "Success" result for this mapping
     * @throws Exception on any error
     */
    public String input() throws Exception {

        if (isCreating()) {
            createInputUser();
            setTask(Constants.CREATE);
        } else {
            setTask(Constants.EDIT);
            setUsername(getUser().getUsername());
            setPassword(getUser().getPassword());
            setPassword2(getUser().getPassword());
        }

        return INPUT;
    }

    /**
     * <p>Insert or update a Registration.</p>
     *
     * @return The "outcome" result code
     * @throws Exception on any error
     */
    public String save() throws Exception {
        return execute();
    }

    /**
     * <p> Insert or update a User object to the persistent store. </p>
     * <p/>
     * <p> If a User is not logged in, then a new User is created and
     * automatically logged in. Otherwise, the existing User is updated. </p>
     *
     * @return The "outcome" result code
     * @throws Exception on any error
     */
    public String execute()
            throws Exception {

        boolean creating = Constants.CREATE.equals(getTask());
        creating = creating && isCreating(); // trust but verify

        if (creating) {

            User user = findUser(getUsername(), getPassword());
            boolean haveUser = (user != null);

            if (haveUser) {
                addActionError(getText("error.username.unique"));
                return INPUT;
            }

            copyUser(getUsername(), getPassword());

        } else {

            // FIXME: Any way to call the RegisrationSave validators from here?
            String newPassword = getPassword();
            if (newPassword != null) {
                String confirmPassword = getPassword2();
                boolean matches = ((null != confirmPassword)
                        && (confirmPassword.equals(newPassword)));
                if (matches) {
                    getUser().setPassword(newPassword);
                } else {
                    addActionError(getText("error.password.match"));
                    return INPUT;
                }
            }
        }

        saveUser();

        return SUCCESS;
    }

}
