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
package org.apache.struts.action2.util.classloader.stores;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author tcurdt
 */
public final class ResourceStoreClassLoader extends ClassLoader {

    private final static Log log = LogFactory.getLog(ResourceStoreClassLoader.class);

    private final ResourceStore store;

    public ResourceStoreClassLoader(final ClassLoader pParent, final ResourceStore pStore) {
        super(pParent);
        store = pStore;
    }

    private Class fastFindClass(final String name) {
        final byte[] clazzBytes = store.read(name);

        if (clazzBytes != null) {
            log.debug("found class " + name + " (" + clazzBytes.length + " bytes)");
            return defineClass(name, clazzBytes, 0, clazzBytes.length);
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
                    log.debug("loaded from parent: " + name);
                } else {
                    throw new ClassNotFoundException(name);
                }

            } else {
                log.debug("loaded from store: " + name);
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
}
