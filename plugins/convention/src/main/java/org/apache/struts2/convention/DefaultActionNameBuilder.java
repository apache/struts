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
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * This class strips the word <b>Action</b> from the end of the class name
 * and possibly lowercases the name as well depending on the value of the
 * constant <strong>struts.convention.action.name.lowercase</strong>. If the
 * constant is set to <strong>true</strong>, this class will lowercase all
 * action names.
 * </p>
 */
public class DefaultActionNameBuilder implements ActionNameBuilder {
    private String actionSuffix = "Action";
    private boolean lowerCase;

    @Inject
    public DefaultActionNameBuilder(@Inject(value="struts.convention.action.name.lowercase") String lowerCase) {
        this.lowerCase = Boolean.parseBoolean(lowerCase);
    }

    /**
     * @param   actionSuffix (Optional) Classes that end with these value will be mapped as actions
     *          (defaults to "Action")
     */
    @Inject(value = "struts.convention.action.suffix", required = false)
    public void setActionSuffix(String actionSuffix) {
        if (StringUtils.isNotBlank(actionSuffix)) {
            this.actionSuffix = actionSuffix;
        }
    }

    public String build(String className) {
        String actionName = className;

        // Truncate Action suffix if found
        if (actionName.endsWith(actionSuffix)) {
            actionName = actionName.substring(0, actionName.length() - actionSuffix.length());
        }

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