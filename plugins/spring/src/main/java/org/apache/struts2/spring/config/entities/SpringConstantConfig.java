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
package org.apache.struts2.spring.config.entities;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.config.entities.ConstantConfig;
import org.apache.struts2.spring.SpringConstants;

public class SpringConstantConfig extends ConstantConfig {
    private List<String> classReloadingWatchList;
    private Set<Pattern> classReloadingAcceptClasses;
    private Boolean classReloadingReloadConfig;

    @Override
    public Map<String, String> getAllAsStringsMap() {
        Map<String, String> map = super.getAllAsStringsMap();

        map.put(SpringConstants.SPRING_CLASS_RELOADING_WATCH_LIST, StringUtils.join(classReloadingWatchList, ','));
        map.put(SpringConstants.SPRING_CLASS_RELOADING_ACCEPT_CLASSES, StringUtils.join(classReloadingAcceptClasses, ','));
        map.put(SpringConstants.SPRING_CLASS_RELOADING_RELOAD_CONFIG, Objects.toString(classReloadingReloadConfig, null));

        return map;
    }

    public List<String> getClassReloadingWatchList() {
        return classReloadingWatchList;
    }

    public void setClassReloadingWatchList(List<String> classReloadingWatchList) {
        this.classReloadingWatchList = classReloadingWatchList;
    }

    public Set<Pattern> getClassReloadingAcceptClasses() {
        return classReloadingAcceptClasses;
    }

    public void setClassReloadingAcceptClasses(Set<Pattern> classReloadingAcceptClasses) {
        this.classReloadingAcceptClasses = classReloadingAcceptClasses;
    }

    public Boolean getClassReloadingReloadConfig() {
        return classReloadingReloadConfig;
    }

    public void setClassReloadingReloadConfig(Boolean classReloadingReloadConfig) {
        this.classReloadingReloadConfig = classReloadingReloadConfig;
    }
}
