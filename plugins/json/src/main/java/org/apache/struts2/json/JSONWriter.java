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
 * <p>
 * Should serialize an object into JavaScript Object Notation (JSON). If cyclic
 * references are detected they should be nulled out.
 * </p>
 */
public interface JSONWriter {
    boolean ENUM_AS_BEAN_DEFAULT = false;

    String write(Object object) throws JSONException;

    String write(Object object, Collection<Pattern> excludeProperties,
                 Collection<Pattern> includeProperties, boolean excludeNullProperties) throws JSONException;

    void setIgnoreHierarchy(boolean ignoreHierarchy);

    void setEnumAsBean(boolean enumAsBean);

    void setDateFormatter(String defaultDateFormat);

    void setCacheBeanInfo(boolean cacheBeanInfo);

    void setExcludeProxyProperties(boolean excludeProxyProperties);
}
