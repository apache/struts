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

import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * ClassLoaderInterface instance that delegates to the singleton of DefaultBundleAccessor 
 */
public class BundleClassLoaderInterface implements ClassLoaderInterface {

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return DefaultBundleAccessor.getInstance().loadClass(name);
    }

    public URL getResource(String name) {
        return  DefaultBundleAccessor.getInstance().loadResource(name, true);
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        Collection<URL> coll = DefaultBundleAccessor.getInstance().loadResources(name, true);
        if (coll == null){
            return new Hashtable<Object, URL>().elements();
        }
        return Collections.enumeration(DefaultBundleAccessor.getInstance().loadResources(name, true));
    }

    public InputStream getResourceAsStream(String name) throws IOException {
        return DefaultBundleAccessor.getInstance().loadResourceAsStream(name);
    }

    public ClassLoaderInterface getParent() {
        return null;
    }

}
