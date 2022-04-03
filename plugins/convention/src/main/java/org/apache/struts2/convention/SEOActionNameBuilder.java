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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * <p>
 * This class converts the class name into a SEO friendly name by recognizing
 * camel casing and inserting dashes. This also converts everything to
 * lower case if desired. And this will also strip off the word <b>Action</b>
 * from the class name.
 * </p>
 */
public class SEOActionNameBuilder extends AbstractActionNameBuilder {

    private static final Logger LOG = LogManager.getLogger(SEOActionNameBuilder.class);

    private boolean lowerCase;
    private String separator;

    @Inject
    public SEOActionNameBuilder(
            @Inject(ConventionConstants.CONVENTION_ACTION_NAME_LOWERCASE) String lowerCase,
            @Inject(ConventionConstants.CONVENTION_ACTION_NAME_SEPARATOR) String separator
    ) {
        this.lowerCase = Boolean.parseBoolean(lowerCase);
        this.separator = separator;
    }

    public String build(String className) {
        String actionName = className;

        checkActionName(actionName);

        LOG.trace("Truncate Action suffix if found");
        actionName = truncateSuffixIfMatches(actionName);

        LOG.trace("Convert to underscores");
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

        LOG.trace("Changed action name from [{}] to [{}]", className, actionName);

        return actionName;
    }

}