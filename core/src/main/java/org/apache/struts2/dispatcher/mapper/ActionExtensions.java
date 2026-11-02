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
package org.apache.struts2.dispatcher.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helpers for the ordered extension lists accepted by {@code struts.action.extension} and the
 * per-package / per-action {@code extension} attribute. The first entry is the default and an empty
 * entry stands for "no extension".
 *
 * @since 7.5.0
 */
public final class ActionExtensions {

    private ActionExtensions() {
    }

    /**
     * Parses a comma separated extension list; a trailing comma adds an empty entry.
     *
     * @param value the raw value, may be {@code null}
     * @return an unmodifiable list in declared order, or {@code null} when {@code value} is {@code null}
     * @since 7.5.0
     */
    public static List<String> parse(String value) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return List.of("");
        }
        List<String> list = new ArrayList<>(List.of(value.split(",")));
        if (value.endsWith(",")) {
            list.add("");
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * @param allowed   the allowed extensions
     * @param extension the extension to check, {@code null} counts as an empty one
     * @return whether {@code extension} is in {@code allowed}
     * @since 7.5.0
     */
    public static boolean allows(List<String> allowed, String extension) {
        return allowed.contains(extension == null ? "" : extension);
    }

    /**
     * @param allowed the allowed extensions, must not be empty
     * @param current the preferred extension, {@code null} counts as an empty one
     * @return {@code current} when allowed, otherwise the first allowed extension
     * @since 7.5.0
     */
    public static String select(List<String> allowed, String current) {
        String candidate = current == null ? "" : current;
        return allows(allowed, candidate) ? candidate : allowed.get(0);
    }
}
