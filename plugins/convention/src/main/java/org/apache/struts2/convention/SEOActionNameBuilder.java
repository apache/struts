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
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * This class converts the class name into a SEO friendly name by recognizing
 * camel casing and inserting dashes. This also converts everything to
 * lower case if desired. And this will also strip off the word <b>Action</b>
 * from the class name.
 * </p>
 */
public class SEOActionNameBuilder implements ActionNameBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SEOActionNameBuilder.class);
    private String actionSuffix = "Action";
    private boolean lowerCase;
    private String separator;

    @Inject
    public SEOActionNameBuilder(@Inject(value="struts.convention.action.name.lowercase") String lowerCase,
            @Inject(value="struts.convention.action.name.separator") String separator) {
        this.lowerCase = Boolean.parseBoolean(lowerCase);
        this.separator = separator;
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
        
        if (actionName.equals(actionSuffix))
            throw new IllegalStateException("The action name cannot be the same as the action suffix [" + actionSuffix + "]");

        // Truncate Action suffix if found
        if (actionName.endsWith(actionSuffix)) {
            actionName = actionName.substring(0, actionName.length() - actionSuffix.length());
        }

        // Convert to underscores
        char[] ca = actionName.toCharArray();
        StringBuilder build = new StringBuilder("" + ca[0]);
        boolean lower = true;
        for (int i = 1; i < ca.length; i++) {
            char c = ca[i];
            if (Character.isUpperCase(c) && lower) {
                build.append(separator);
                lower = false;
            } else if (!Character.isUpperCase(c)) {
                lower = true;
            }

            build.append(c);
        }

        actionName = build.toString();
        if (lowerCase) {
            actionName = actionName.toLowerCase();
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Changed action name from [#0] to [#1]", className, actionName);
        }

        return actionName;
    }
}