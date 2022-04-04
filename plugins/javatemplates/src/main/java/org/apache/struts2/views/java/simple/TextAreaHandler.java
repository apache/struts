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

import org.apache.struts2.views.java.TagGenerator;
import org.apache.struts2.views.java.Attributes;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

public class TextAreaHandler extends AbstractTagHandler implements TagGenerator {

    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Attributes attrs = new Attributes();

        attrs.addDefaultToEmpty("name", params.get("name"))
                .addDefaultToEmpty("cols", params.get("cols"))
                .addDefaultToEmpty("rows", params.get("rows"))
                .addIfExists("wrap", params.get("wrap"))
                .addIfTrue("disabled", params.get("disabled"))
                .addIfTrue("readonly", params.get("readonly"))
                .addIfExists("tabindex", params.get("tabindex"))
                .addIfExists("id", params.get("id"))
                .addIfExists("class", params.get("cssClass"))
                .addIfExists("style", params.get("cssStyle"))
                .addIfExists("title", params.get("title"));
        start("textarea", attrs);
        String value = (String) params.get("nameValue");
        if (StringUtils.isNotEmpty(value))
            characters(value);
        end("textarea");
    }
}