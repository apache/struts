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
package com.opensymphony.xwork2.util.location;

import com.opensymphony.xwork2.util.PropertiesReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Properties implementation that remembers the location of each property.  When
 * loaded, a custom properties file parser is used to remember both the line number
 * and preceeding comments for each property entry.
 */
public class LocatableProperties extends Properties implements Locatable {

    Location location;
    Map<String, Location> propLocations;

    public LocatableProperties() {
        this(Location.UNKNOWN);
    }

    public LocatableProperties(Location loc) {
        super();
        this.location = loc;
        this.propLocations = new HashMap<>();
    }

    @Override
    public void load(InputStream in) throws IOException {
        Reader reader = new InputStreamReader(in);
        PropertiesReader pr = new PropertiesReader(reader);
        while (pr.nextProperty()) {
            String name = pr.getPropertyName();
            String val = pr.getPropertyValue();
            int line = pr.getLineNumber();
            String desc = convertCommentsToString(pr.getCommentLines());

            Location loc = new LocationImpl(desc, location.getURI(), line, 0);
            setProperty(name, val, loc);
        }
    }

    String convertCommentsToString(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        if (lines != null && !lines.isEmpty()) {
            for (String line : lines) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    public Object setProperty(String key, String value, Object locationObj) {
        Object obj = super.setProperty(key, value);
        if (location != null) {
            Location loc = LocationUtils.getLocation(locationObj);
            propLocations.put(key, loc);
        }
        return obj;
    }

    public Location getPropertyLocation(String key) {
        Location loc = propLocations.get(key);
        if (loc != null) {
            return loc;
        } else {
            return Location.UNKNOWN;
        }
    }

    public Location getLocation() {
        return location;
    }

}
