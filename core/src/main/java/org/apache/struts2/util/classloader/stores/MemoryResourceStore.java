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
package org.apache.struts2.util.classloader.stores;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;


/**
 */
public final class MemoryResourceStore implements ResourceStore {

    private final static Log log = LogFactory.getLog(MemoryResourceStore.class);

    private final Map store = new HashMap();

    public byte[] read(final String resourceName) {
        return (byte[]) store.get(resourceName);
    }

    public void write(final String resourceName, final byte[] clazzData) {
        log.debug("storing resource " + resourceName + "(" + clazzData.length + ")");
        store.put(resourceName, clazzData);
    }

    public void remove(final String resourceName) {
        log.debug("removing resource " + resourceName);
        store.remove(resourceName);
    }

    public String toString() {
        return store.keySet().toString();
    }
}
