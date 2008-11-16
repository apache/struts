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
 * Verify that essential resources are available.
 */
public class Welcome extends MailreaderSupport {

    public String execute() {

        // Confirm message resources loaded
        String message = getText(Constants.ERROR_DATABASE_MISSING);
        if (Constants.ERROR_DATABASE_MISSING.equals(message)) {
            addActionError(Constants.ERROR_MESSAGES_NOT_LOADED);
        }

        // Confirm database loaded
        if (null==getDatabase()) {
             addActionError(Constants.ERROR_DATABASE_NOT_LOADED);
        }

        if (hasErrors()) {
            return ERROR;
        }
        else {
            return SUCCESS;
        }
    }
}
