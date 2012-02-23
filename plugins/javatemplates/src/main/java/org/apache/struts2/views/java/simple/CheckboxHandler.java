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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.Map;

public class CheckboxHandler extends AbstractTagHandler implements TagGenerator {
    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Attributes attrs = new Attributes();

        String fieldValue = (String) params.get("fieldValue");
        String id = (String) params.get("id");
        String name = (String) params.get("name");
        Object disabled = params.get("disabled");

        attrs.add("type", "checkbox")
                .add("name", name)
                .add("value", fieldValue)
                .addIfTrue("checked", params.get("nameValue"))
                .addIfTrue("readonly", params.get("readonly"))
                .addIfTrue("disabled", disabled)
                .addIfExists("tabindex", params.get("tabindex"))
                .addIfExists("id", id)
                .addIfExists("class", params.get("cssClass"))
                .addIfExists("style", params.get("cssStyle"))
                .addIfExists("title", params.get("title"));
        start("input", attrs);
        end("input");

        //hidden input
        attrs = new Attributes();
        attrs.add("type", "hidden")
                .add("id", "__checkbox_" + StringUtils.defaultString(StringEscapeUtils.escapeHtml4(id)))
                .add("name", "__checkbox_" + StringUtils.defaultString(StringEscapeUtils.escapeHtml4(name)))
                .add("value", "__checkbox_" + StringUtils.defaultString(StringEscapeUtils.escapeHtml4(fieldValue)))
                .addIfTrue("disabled", disabled);
        start("input", attrs);
        end("input");
    }
}
