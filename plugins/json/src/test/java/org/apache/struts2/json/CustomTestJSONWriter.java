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
package org.apache.struts2.json;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Marker writer proving that a user-configured {@code struts.json.writer} override
 * wins over the default {@link StrutsJSONWriter}. Emits a sentinel so output-level
 * assertions are unambiguous.
 */
public class CustomTestJSONWriter implements JSONWriter {

    public static final String SENTINEL = "{\"__customWriter__\":true}";

    @Override
    public String write(Object object) throws JSONException {
        return SENTINEL;
    }

    @Override
    public String write(Object object, Collection<Pattern> excludeProperties,
                        Collection<Pattern> includeProperties,
                        boolean excludeNullProperties) throws JSONException {
        return SENTINEL;
    }

    // Configuration setters are intentionally ignored: this marker always emits SENTINEL.
    @Override public void setIgnoreHierarchy(boolean ignoreHierarchy) { /* no-op marker */ }
    @Override public void setEnumAsBean(boolean enumAsBean) { /* no-op marker */ }
    @Override public void setDateFormatter(String defaultDateFormat) { /* no-op marker */ }
    @Override public void setCacheBeanInfo(boolean cacheBeanInfo) { /* no-op marker */ }
    @Override public void setExcludeProxyProperties(boolean excludeProxyProperties) { /* no-op marker */ }
}
