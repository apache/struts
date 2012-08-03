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
package com.opensymphony.xwork2.util.classloader;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * class taken from Apache JCI
 */
public final class ResourceStoreClassLoader extends ClassLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceStoreClassLoader.class);

    private final ResourceStore[] stores;

    public ResourceStoreClassLoader(final ClassLoader pParent, final ResourceStore[] pStores) {
        super(pParent);

        stores = new ResourceStore[pStores.length];
        System.arraycopy(pStores, 0, stores, 0, stores.length);
    }

    private Class fastFindClass(final String name) {

        if (stores != null) {
            String fileName = name.replace('.', '/') + ".class";
            for (final ResourceStore store : stores) {
                final byte[] clazzBytes = store.read(fileName);
                if (clazzBytes != null) {
                    definePackage(name);
                    return defineClass(name, clazzBytes, 0, clazzBytes.length);
                }
            }
        }

        return null;
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class clazz = findLoadedClass(name);

        if (clazz == null) {
            clazz = fastFindClass(name);

            if (clazz == null) {
                final ClassLoader parent = getParent();
                if (parent != null) {
                    clazz = parent.loadClass(name);
                } else {
                    throw new ClassNotFoundException(name);
                }

            }
        }

        if (resolve) {
            resolveClass(clazz);
        }

        return clazz;
    }

    protected Class findClass(final String name) throws ClassNotFoundException {
        final Class clazz = fastFindClass(name);
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    /**
     * Define the package information associated with a class.
     *
     * @param className the class name of for which the package information
     *                  is to be determined.
     */
    protected void definePackage(String className){
        int classIndex = className.lastIndexOf('.');
        if (classIndex == -1) {
            return;
        }
        String packageName = className.substring(0, classIndex);
        if (getPackage(packageName) != null) {
            return;
        }
        definePackage(packageName, null, null, null, null, null, null, null);
    }
}
