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

package org.apache.struts2.config;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * DelegatingSettings stores an internal list of {@link Settings} objects
 * to update settings or retrieve settings values.
 * <p>
 * Each time a Settings method is called (get, set, list, and so forth),
 * this class goes through the list of Settings objects
 * and calls that method for each delegate,
 * withholding any exception until all delegates have been called.
 *
 */
class DelegatingSettings extends Settings {

    /**
     * The Settings objects.
     */
    Settings[] delegates;

    /**
     * Creates a new DelegatingSettings object utilizing the list of {@link Settings} objects.
     *
     * @param delegates The Settings objects to use as delegates
     */
    public DelegatingSettings(Settings[] delegates) {
        this.delegates = delegates;
    }

    // See superclass for Javadoc
    public void setImpl(String name, String value) throws IllegalArgumentException, UnsupportedOperationException {
        IllegalArgumentException e = null;

        for (Settings delegate : delegates) {
            try {
                delegate.getImpl(name); // Throws exception if not found
                delegate.setImpl(name, value); // Found it
                return; // Done
            } catch (IllegalArgumentException ex) {
                e = ex;

                // Try next delegate
            }
        }

        throw e;
    }

    // See superclass for Javadoc
    public String getImpl(String name) throws IllegalArgumentException {

        IllegalArgumentException e = null;

        for (Settings delegate : delegates) {
            try {
                return delegate.getImpl(name);  // Throws exception if not found
            } catch (IllegalArgumentException ex) {
                e = ex;

                // Try next delegate
            }
        }

        throw e;
    }

    // See superclass for Javadoc
    public boolean isSetImpl(String aName) {
        for (Settings delegate : delegates) {
            if (delegate.isSetImpl(aName)) {
                return true;
            }
        }

        return false;
    }

    // See superclass for Javadoc
    public Iterator listImpl() {
        boolean workedAtAll = false;

        Set<Object> settingList = new HashSet<Object>();
        UnsupportedOperationException e = null;

        for (Settings delegate : delegates) {
            try {
                Iterator list = delegate.listImpl();

                while (list.hasNext()) {
                    settingList.add(list.next());
                }

                workedAtAll = true;
            } catch (UnsupportedOperationException ex) {
                e = ex;

                // Try next delegate
            }
        }

        if (!workedAtAll) {
            throw (e == null) ? new UnsupportedOperationException() : e;
        } else {
            return settingList.iterator();
        }
    }
}
