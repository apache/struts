/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.plexus;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;

/**
 * @author Patrick Lightbody (plightbo at gmail dot com)
 */
public class PlexusUtils {
    private static final Log log = LogFactory.getLog(PlexusObjectFactory.class);

    public static void configure(PlexusContainer pc, String file) throws PlexusConfigurationResourceException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (is == null) {
            log.info("Could not find " + file + ", skipping");
            is = new ByteArrayInputStream("<plexus><components></components></plexus>".getBytes());
        }
        pc.setConfigurationResource(new InputStreamReader(is));
    }
}
