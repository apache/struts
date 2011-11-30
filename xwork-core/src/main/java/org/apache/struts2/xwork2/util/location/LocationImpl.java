/*
 * Copyright 2005 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util.location;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple immutable and serializable implementation of {@link Location}.
 */
public class LocationImpl implements Location, Serializable {
    private final String uri;
    private final int line;
    private final int column;
    private final String description;
    
    // Package private: outside this package, use Location.UNKNOWN.
    static final LocationImpl UNKNOWN = new LocationImpl(null, null, -1, -1);

    /**
     * Build a location for a given URI, with unknown line and column numbers.
     * 
     * @param uri the resource URI
     */
    public LocationImpl(String description, String uri) {
        this(description, uri, -1, -1);
    }

    /**
     * Build a location for a given URI and line and column numbers.
     * 
     * @param uri the resource URI
     * @param line the line number (starts at 1)
     * @param column the column number (starts at 1)
     */
    public LocationImpl(String description, String uri, int line, int column) {
        if (uri == null || uri.length() == 0) {
            this.uri = null;
            this.line = -1;
            this.column = -1;
        } else {
            this.uri = uri;
            this.line = line;
            this.column = column;
        }
        
        if (description != null && description.length() == 0) {
            description = null;
        }
        this.description = description;
    }
    
    /**
     * Copy constructor.
     * 
     * @param location the location to be copied
     */
    public LocationImpl(Location location) {
        this(location.getDescription(), location.getURI(), location.getLineNumber(), location.getColumnNumber());
    }
    
    /**
     * Create a location from an existing one, but with a different description
     */
    public LocationImpl(String description, Location location) {
        this(description, location.getURI(), location.getLineNumber(), location.getColumnNumber());
    }
    
    /**
     * Obtain a <code>LocationImpl</code> from a {@link Location}. If <code>location</code> is
     * already a <code>LocationImpl</code>, it is returned, otherwise it is copied.
     * <p>
     * This method is useful when an immutable and serializable location is needed, such as in locatable
     * exceptions.
     * 
     * @param location the location
     * @return an immutable and serializable version of <code>location</code>
     */
    public static LocationImpl get(Location location) {
        if (location instanceof LocationImpl) {
            return (LocationImpl)location;
        } else if (location == null) {
            return UNKNOWN;
        } else {
            return new LocationImpl(location);
        }
    }
    
    /**
     * Get the description of this location
     * 
     * @return the description (can be <code>null</code>)
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Get the URI of this location
     * 
     * @return the URI (<code>null</code> if unknown).
     */
    public String getURI() {
        return this.uri;
    }

    /**
     * Get the line number of this location
     * 
     * @return the line number (<code>-1</code> if unknown)
     */
    public int getLineNumber() {
        return this.line;
    }
    
    /**
     * Get the column number of this location
     * 
     * @return the column number (<code>-1</code> if unknown)
     */
    public int getColumnNumber() {
        return this.column;
    }
    
    /**
     * Gets a source code snippet with the default padding
     *
     * @param padding The amount of lines before and after the error to include
     */
    public List<String> getSnippet(int padding) {
        List<String> snippet = new ArrayList<String>();
        if (getLineNumber() > 0) {
            try {
                InputStream in = new URL(getURI()).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                
                int lineno = 0;
                int errno = getLineNumber();
                String line;
                while ((line = reader.readLine()) != null) {
                    lineno++;
                    if (lineno >= errno - padding && lineno <= errno + padding) {
                        snippet.add(line);
                    }
                }
            } catch (Exception ex) {
                // ignoring as snippet not available isn't a big deal
            }
        }
        return snippet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Location) {
            Location other = (Location)obj;
            return this.line == other.getLineNumber() && this.column == other.getColumnNumber()
                   && testEquals(this.uri, other.getURI())
                   && testEquals(this.description, other.getDescription());
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = line ^ column;
        if (uri != null) hash ^= uri.hashCode();
        if (description != null) hash ^= description.hashCode();
        
        return hash;
    }
    
    @Override
    public String toString() {
        return LocationUtils.toString(this);
    }
    
    /**
     * Ensure serialized unknown location resolve to {@link Location#UNKNOWN}.
     */
    private Object readResolve() {
        return this.equals(Location.UNKNOWN) ? Location.UNKNOWN : this;
    }
    
    private boolean testEquals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        if ((object1 == null) || (object2 == null)) {
            return false;
        }
        return object1.equals(object2);
    }
}
