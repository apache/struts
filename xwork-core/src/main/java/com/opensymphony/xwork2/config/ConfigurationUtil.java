/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ConfigurationUtil
 * 
 * @author Jason Carreira Created May 23, 2003 11:22:49 PM
 */
public class ConfigurationUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationUtil.class);

    private ConfigurationUtil() {
    }

    /**
     * Get the {@link PackageConfig} elements with the specified names.
     * @param configuration Configuration from which to find the package elements
     * @param parent Comma separated list of parent package names
     * @return The package elements that correspond to the names in the {@code parent} parameter.
     */
    public static List<PackageConfig> buildParentsFromString(Configuration configuration, String parent) {
        List<String> parentPackageNames = buildParentListFromString(parent);
        List<PackageConfig> parentPackageConfigs = new ArrayList<PackageConfig>();
        for (String parentPackageName : parentPackageNames) {
            PackageConfig parentPackageContext = configuration.getPackageConfig(parentPackageName);

            if (parentPackageContext != null) {
                parentPackageConfigs.add(parentPackageContext);
            }
        }

        return parentPackageConfigs;
    }

    /**
     * Splits the string into a list using a comma as the token separator.
     * @param parent The comma separated string.
     * @return A list of tokens from the specified string.
     */
    public static List<String> buildParentListFromString(String parent) {
        if ((parent == null) || ("".equals(parent))) {
            return Collections.emptyList();
        }

        StringTokenizer tokenizer = new StringTokenizer(parent, ",");
        List<String> parents = new ArrayList<String>();

        while (tokenizer.hasMoreTokens()) {
            String parentName = tokenizer.nextToken().trim();

            if (!"".equals(parentName)) {
                parents.add(parentName);
            }
        }

        return parents;
    }
}
