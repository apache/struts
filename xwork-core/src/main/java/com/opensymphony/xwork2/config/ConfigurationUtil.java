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
 * @author Jason Carreira
 *         Created May 23, 2003 11:22:49 PM
 */
public class ConfigurationUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationUtil.class);


    private ConfigurationUtil() {
    }


    public static List<PackageConfig> buildParentsFromString(Configuration configuration, String parent) {
        if ((parent == null) || ("".equals(parent))) {
            return Collections.emptyList();
        }

        StringTokenizer tokenizer = new StringTokenizer(parent, ", ");
        List<PackageConfig> parents = new ArrayList<PackageConfig>();

        while (tokenizer.hasMoreTokens()) {
            String parentName = tokenizer.nextToken().trim();

            if (!"".equals(parentName)) {
                PackageConfig parentPackageContext = configuration.getPackageConfig(parentName);

                if (parentPackageContext != null) {
                    parents.add(parentPackageContext);
                }
            }
        }

        return parents;
    }
}
