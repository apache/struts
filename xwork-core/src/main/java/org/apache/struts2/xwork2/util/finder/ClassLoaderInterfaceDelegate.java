/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util.finder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * Default implementation of ClassLoaderInterface, which delegates to an actual ClassLoader
 */
public class ClassLoaderInterfaceDelegate implements ClassLoaderInterface {
    private ClassLoader classLoader;

    public ClassLoaderInterfaceDelegate(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return classLoader.loadClass(name);
    }

    public URL getResource(String className) {
        return classLoader.getResource(className);
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        return classLoader.getResources(name);
    }

    public InputStream getResourceAsStream(String name) {
        return classLoader.getResourceAsStream(name);
    }

    public ClassLoaderInterface getParent() {
        return classLoader.getParent() != null ? new ClassLoaderInterfaceDelegate(classLoader.getParent()) : null;
    }
}
