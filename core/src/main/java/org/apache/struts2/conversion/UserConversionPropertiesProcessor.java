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
package org.apache.struts2.conversion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.opensymphony.xwork2.inject.Initializable;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Late initialization processor for user conversion properties.
 * <p>
 * Processes struts-conversion.properties and xwork-conversion.properties
 * after the full container is built, allowing Spring bean name resolution.
 * This enables users to reference Spring bean names instead of only fully
 * qualified class names in their conversion property files.
 * </p>
 *
 * @see <a href="https://issues.apache.org/jira/browse/WW-4291">WW-4291</a>
 * @see UserConversionPropertiesProvider
 * @since 6.9.0
 */
public class UserConversionPropertiesProcessor implements Initializable {

    private static final Logger LOG = LogManager.getLogger(UserConversionPropertiesProcessor.class);

    private UserConversionPropertiesProvider provider;

    @Inject
    public void setUserConversionPropertiesProvider(UserConversionPropertiesProvider provider) {
        this.provider = provider;
    }

    @Override
    public void init() {
        LOG.debug("Initializing user conversion properties via late initialization");
        provider.initUserConversions();
    }
}
