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
package org.apache.struts2.interceptor.coop;

import java.util.HashSet;
import java.util.Set;

public class CoopConfiguration {

    String SAME_ORIGIN = "same-origin";
    String SAME_SITE = "same-site";
    String UNSAFE_NONE = "unsafe-none";
    String COOP_HEADER = "Cross-Origin-Opener-Policy";

    private Set<String> exemptedPaths = new HashSet<>();
    private String defaultMode = SAME_ORIGIN;

    public Set<String> getExemptedPaths() {
        return exemptedPaths;
    }

    public void setExemptedPaths(Set<String> paths) {
        exemptedPaths.addAll(paths);
    }

    public String getDefaultMode() { return defaultMode; }

    public void setDefaultMode(String mode) {
        if (!(mode.equals(SAME_ORIGIN) || mode.equals(SAME_SITE) || mode.equals(UNSAFE_NONE))){
            throw new IllegalArgumentException("Mode not recognized!");
        }
        this.defaultMode = mode;
    }

    public boolean isExempted(String path){
        return exemptedPaths.contains(path);
    }
}