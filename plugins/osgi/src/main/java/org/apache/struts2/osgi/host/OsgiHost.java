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
package org.apache.struts2.osgi.host;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Implementations of this class start an OSGi container. They must also add the BundleContext to
 * the ServletContext under the key  OsgiHost.OSGI_BUNDLE_CONTEXT;
 */
public interface OsgiHost {
    String OSGI_BUNDLE_CONTEXT = "__struts_osgi_bundle_context"; 
    String OSGI_HEADER_STRUTS_ENABLED = "Struts2-Enabled";

    void destroy() throws Exception;
    void init(ServletContext servletContext);
    Map<String, Bundle> getBundles();
    Map<String, Bundle> getActiveBundles();
    BundleContext getBundleContext();
}
