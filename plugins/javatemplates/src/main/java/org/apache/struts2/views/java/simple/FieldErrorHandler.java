/*
 * $Id$
 *
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
package org.apache.struts2.views.java.simple;

import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FieldErrorHandler extends AbstractTagHandler implements TagGenerator {
    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Map<String, List<String>> errors = (Map<String, List<String>>) findValue("fieldErrors");
        List<String> fieldErrorFieldNames = (List<String>) params.get("errorFieldNames");

        if (fieldErrorFieldNames != null && !fieldErrorFieldNames.isEmpty()) {
            //wrapping ul
            Attributes attrs = new Attributes();
            attrs.addIfExists("style", params.get("cssStyle"))
                    .add("class", params.containsKey("cssClass") ? (String) params.get("cssClass") : "errorMessage");
            start("ul", attrs);

            //iterate over field error names
            for (String fieldErrorFieldName : fieldErrorFieldNames) {
                List<String> fieldErrors = errors.get(fieldErrorFieldName);
                if (fieldErrors != null) {
                    for (String fieldError : fieldErrors) {
                        start("li", null);
                        start("span", null);
                        characters(fieldError);
                        end("span");
                        end("li");
                    }
                }
            }

            end("ul");
        } else if (errors != null && !errors.isEmpty()) {
            //wrapping ul
            Attributes attrs = new Attributes();
            attrs.addIfExists("style", params.get("cssStyle"))
                    .add("class", params.containsKey("cssClass") ? (String) params.get("cssClass") : "errorMessage");
            start("ul", attrs);

            for (Map.Entry<String, List<String>> errorEntry : errors.entrySet()) {
                for (String fieldError : errorEntry.getValue()) {
                    start("li", null);
                    start("span", null);
                    characters(fieldError);
                    end("span");
                    end("li");
                }
            }

            end("ul");
        }
    }
}
