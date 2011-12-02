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
    Map<String,Location> propLocations;
    
    public LocatableProperties() {
        this(null);
    }
    
    public LocatableProperties(Location loc) {
        super();
        this.location = loc;
        this.propLocations = new HashMap<String,Location>();
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
        if (lines != null && lines.size() > 0) {
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
        return propLocations.get(key);
    }
    
    public Location getLocation() {
        return location;
    }

}
