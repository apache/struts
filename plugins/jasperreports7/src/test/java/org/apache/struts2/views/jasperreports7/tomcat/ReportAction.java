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
package org.apache.struts2.views.jasperreports7.tomcat;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.util.List;
import java.util.Map;

public class ReportAction extends ActionSupport {

    private String format;

    public List<Map<String, String>> getPeople() {
        return List.of(Map.of("firstName", "Foo", "lastName", "Bar"));
    }

    public Map<String, Object> getReportParameters() {
        return Map.of("title", "Tomcat");
    }

    public String getFormat() {
        return format;
    }

    @StrutsParameter
    public void setFormat(String format) {
        this.format = format;
    }
}
