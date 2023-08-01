/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.autotag.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tiles.autotag.core.AutotagRuntimeException;

/**
 * A Velocity tools to manipulate strings.
 */
public class StringTool {

    /**
     * Maps a primitive type to its default value as a string.
     */
    private Map<String, String> type2default;

    /**
     * Maps a primitive type to its boxed version.
     */
    private Map<String, String> primitive2wrapped;

    /**
     * Constructor.
     */
    public StringTool() {
        type2default = new HashMap<String, String>();
        type2default.put("byte", "0");
        type2default.put("short", "0");
        type2default.put("int", "0");
        type2default.put("long", "0L");
        type2default.put("float", "0.0f");
        type2default.put("double", "0.0d");
        type2default.put("char", "'\\u0000'");
        type2default.put("boolean", "false");

        primitive2wrapped = new HashMap<String, String>();
        primitive2wrapped.put("byte", Byte.class.getName());
        primitive2wrapped.put("short", Short.class.getName());
        primitive2wrapped.put("int", Integer.class.getName());
        primitive2wrapped.put("long", Long.class.getName());
        primitive2wrapped.put("float", Float.class.getName());
        primitive2wrapped.put("double", Double.class.getName());
        primitive2wrapped.put("char", Character.class.getName());
        primitive2wrapped.put("boolean", Boolean.class.getName());
    }

    /**
     * Creates a list of strings, separating a string when a newline is encountered.
     *
     * @param toSplit The string to split.
     * @return The list of splitted strings.
     */
    public List<String> splitOnNewlines(String toSplit) {
        List<String> retValue = new ArrayList<String>();
        if (toSplit == null) {
            return retValue;
        }
        Reader reader = new StringReader(toSplit);
        BufferedReader bufReader = new BufferedReader(reader);
        try {
            String line;
            while ((line = bufReader.readLine()) != null) {
                retValue.add(line);
            }
        } catch (IOException e) {
            throw new AutotagRuntimeException("Cannot read the string completely", e);
        }
        return retValue;
    }

    /**
     * Creates a string in which the first character is capitalized.
     *
     * @param string The string to use.
     * @return The same string with the first character capitalized.
     */
    public String capitalizeFirstLetter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Returns the default value for a type.
     *
     * @param type                   The type.
     * @param overriddenDefaultValue The default value, as specified by developers.
     * @return The default value to use.
     */
    public String getDefaultValue(String type, String overriddenDefaultValue) {
        if (overriddenDefaultValue != null) {
            return overriddenDefaultValue;
        }

        String retValue = type2default.get(type);
        if (retValue == null) {
            retValue = "null";
        }
        return retValue;
    }

    /**
     * Returns the class to be used to cast an Object.
     *
     * @param type The type to use, even a primitive type.
     * @return The class to be used in casts.
     */
    public String getClassToCast(String type) {
        String retValue = primitive2wrapped.get(type);
        if (retValue == null) {
            retValue = type;
        }
        return retValue;
    }
}
