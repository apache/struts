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

import com.opensymphony.xwork2.util.location.Location;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
class DelegatingSettings implements Settings {

    /**
     * The Settings objects.
     */
    List<Settings> delegates;

    /**
     * Creates a new DelegatingSettings object utilizing the list of {@link Settings} objects.
     *
     * @param delegates The Settings objects to use as delegates
     */
    public DelegatingSettings(List<Settings> delegates) {
        this.delegates = delegates;
    }

    public String get(String name) throws IllegalArgumentException {
        for (Settings delegate : delegates) {
            String value = delegate.get(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    public Iterator list() {
        boolean workedAtAll = false;

        Set<Object> settingList = new HashSet<Object>();
        UnsupportedOperationException e = null;

        for (Settings delegate : delegates) {
            try {
                Iterator list = delegate.list();

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

    public Location getLocation(String name) {
        for (Settings delegate : delegates) {
            Location loc = delegate.getLocation(name);
            if (loc != null) {
                return loc;
            }
        }
        return Location.UNKNOWN;
    }
}
