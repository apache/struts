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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public interface BundleAccessor {

    String CURRENT_BUNDLE_NAME = "__bundle_name__";

    Class loadClass(String name) throws ClassNotFoundException;

    InputStream loadResourceAsStream(String name) throws IOException;

    InputStream loadResourceFromAllBundlesAsStream(String name) throws IOException;

    URL loadResourceFromAllBundles(String name) throws IOException;

    Set<String> getPackagesByBundle(Bundle bundle);

    Object getService(ServiceReference ref);

    ServiceReference getServiceReference(String className);

    ServiceReference[] getServiceReferences(String className, String params) throws InvalidSyntaxException;

    public ServiceReference[] getAllServiceReferences(String className);

    void addPackageFromBundle(Bundle bundle, String packageName);

    void setBundleContext(BundleContext bundleContext);

    void setOsgiHost(OsgiHost osgiHost);
}
