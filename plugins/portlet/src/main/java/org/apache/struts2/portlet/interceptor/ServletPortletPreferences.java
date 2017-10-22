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
package org.apache.struts2.portlet.interceptor;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

/**
 * Simple portlet preferences implementation that uses a map in the Session
 * as storage.
 */
public class ServletPortletPreferences implements PortletPreferences {

    private Map session;
    private String PREFERENCES_KEY = "_portlet-preferences";
    
    public ServletPortletPreferences(Map session) {
        this.session = session;
    }
    
    public Map getMap() {
        Map map = (Map) session.get(PREFERENCES_KEY);
        if (map == null) {
            map = new HashMap();
            session.put(PREFERENCES_KEY, map);
        }
        return map;
    }

    public Enumeration getNames() {
        return new Vector(getMap().keySet()).elements();
    }

    public String getValue(String key, String def) {
        String val = (String) getMap().get(key);
        if (val == null) {
            val = def;
        }
        return val;
    }

    public String[] getValues(String key, String[] def) {
        String[] val = (String[]) getMap().get(key);
        if (val == null) {
            val = def;
        }
        return val;
    }

    public boolean isReadOnly(String arg0) {
        return false;
    }

    public void reset(String arg0) throws ReadOnlyException {
        session.put(PREFERENCES_KEY, new HashMap());
    }

    public void setValue(String key, String value) throws ReadOnlyException {
        getMap().put(key, value);
    }

    public void setValues(String key, String[] value) throws ReadOnlyException {
        getMap().put(key, value);
    }

    public void store() throws IOException, ValidatorException {
        
    }

}
