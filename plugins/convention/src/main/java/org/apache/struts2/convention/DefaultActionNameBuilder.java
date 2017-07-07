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
package org.apache.struts2.convention;

import com.opensymphony.xwork2.inject.Inject;

/**
 * <p>
 * This class strips the word <b>Action</b> from the end of the class name
 * and possibly lowercases the name as well depending on the value of the
 * constant <strong>struts.convention.action.name.lowercase</strong>. If the
 * constant is set to <strong>true</strong>, this class will lowercase all
 * action names.
 * </p>
 */
public class DefaultActionNameBuilder extends AbstractActionNameBuilder {

    private boolean lowerCase;

    @Inject
    public DefaultActionNameBuilder(
            @Inject(ConventionConstants.CONVENTION_ACTION_NAME_LOWERCASE) String lowerCase
    ) {
        this.lowerCase = Boolean.parseBoolean(lowerCase);
    }

    public String build(String className) {
        String actionName = className;

        checkActionName(actionName);

        // Truncate Action suffix if found
        actionName = truncateSuffixIfMatches(actionName);

        // Force initial letter of action to lowercase, if desired
        if ((lowerCase) && (actionName.length() > 1)) {
            int lowerPos = actionName.lastIndexOf('/') + 1;
            StringBuilder sb = new StringBuilder();
            sb.append(actionName.substring(0, lowerPos));
            sb.append(Character.toLowerCase(actionName.charAt(lowerPos)));
            sb.append(actionName.substring(lowerPos + 1));
            actionName = sb.toString();
        }

        return actionName;
    }

}