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
package org.apache.struts2.osgi;

import org.apache.struts2.osgi.host.FelixOsgiHost;
import org.apache.struts2.osgi.host.GlassfishOSGiHost;
import org.apache.struts2.osgi.host.OsgiHost;

/**
 * OsgiHostFactory that creates proper OsgiHost implementation according to
 * context param from web.xml
 * <p/>
 * Two implementations are supported right now:
 * - Apache Felix
 * - Glassfish (which contains Apache Felix already)
 */
public class OsgiHostFactory {

    public static final String GLASSFISH = "Glassfish";

    private OsgiHostFactory() {
    }

    public static OsgiHost createOsgiHost(String platform) {
        if (GLASSFISH.equalsIgnoreCase(platform)) {
            return new GlassfishOSGiHost();
        }
        return new FelixOsgiHost();
    }

}