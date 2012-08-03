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

package org.apache.struts2.plexus;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Utility methods for dealing with Plexus
 */
public class PlexusUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PlexusObjectFactory.class);

    /**
     * Configures the container with the configuration file
     *
     * @param pc The plexus container
     * @param file The file path
     * @throws PlexusConfigurationResourceException If the plexus configuration can't be loaded
     */
    public static void configure(PlexusContainer pc, String file) throws PlexusConfigurationResourceException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (is == null) {
            if (LOG.isInfoEnabled()) {
        	LOG.info("Could not find " + file + ", skipping");
            }
            is = new ByteArrayInputStream("<plexus><components></components></plexus>".getBytes());
        }
        pc.setConfigurationResource(new InputStreamReader(is));
    }
}
