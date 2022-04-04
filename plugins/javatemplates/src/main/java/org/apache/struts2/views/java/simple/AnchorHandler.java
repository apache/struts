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

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;

import java.io.IOException;
import java.util.Map;

public class AnchorHandler extends AbstractTagHandler implements TagGenerator {
    public void generate() throws IOException {
        //all rendering must happend at the end of the tag, so we can support nested params
    }

    public static class CloseHandler extends AbstractTagHandler implements TagGenerator {
        public void generate() throws IOException {
            Map<String, Object> params = context.getParameters();

            Attributes attrs = new Attributes();

            attrs.addIfExists("name", params.get("name"))
                    .addIfExists("id", params.get("id"))
                    .addIfExists("class", params.get("cssClass"))
                    .addIfExists("style", params.get("cssStyle"))
                    .addIfExists("href", params.get("href"), false)
                    .addIfExists("title", params.get("title"))
                    .addIfExists("tabindex", params.get("tabindex"));
            start("a", attrs);
            String body = (String) params.get("body");
            if (StringUtils.isNotEmpty(body))
                characters(body, false);
            end("a");
        }
    }
}
