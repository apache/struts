/*
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
import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractActionNameBuilder implements ActionNameBuilder {

    private Set<String> actionSuffix = Collections.singleton("Action");

    /**
     * @param   actionSuffix (Optional) Classes that end with these value will be mapped as actions
     *          (defaults to "Action")
     */
    @Inject(value = ConventionConstants.CONVENTION_ACTION_SUFFIX, required = false)
    public void setActionSuffix(String actionSuffix) {
        if (StringUtils.isNotBlank(actionSuffix)) {
            this.actionSuffix = TextParseUtil.commaDelimitedStringToSet(actionSuffix);
        }
    }


    protected void checkActionName(String actionName) {
        for (String suffix : actionSuffix) {
            if (actionName.equals(suffix)) {
                throw new IllegalStateException("The action name cannot be the same as the action suffix [" + suffix + "]");
            }
        }
    }

    protected String truncateSuffixIfMatches(String name) {
        String actionName = name;
        for (String suffix : actionSuffix) {
            if (actionName.endsWith(suffix)) {
                actionName = actionName.substring(0, actionName.length() - suffix.length());
            }
        }
        return actionName;
    }

}
