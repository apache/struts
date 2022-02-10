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
package org.apache.struts2.views.java.simple;

import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;

import java.io.IOException;
import java.util.Map;

public class LinkHandler extends AbstractTagHandler implements TagGenerator {

    @Override
    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Attributes attrs = new Attributes();

        attrs.addIfExists("href", params.get(("href")))
                .addIfExists("hreflang", params.get("hreflang"))
                .addIfExists("rel", params.get("rel"))
                .addIfExists("media", params.get("media"))
                .addIfExists("sizes", params.get("sizes"))
                .addIfExists("crossorigin", params.get("crossorigin"))
                .addIfExists("referrerpolicy", params.get("referrerpolicy"))
                .addIfExists("type", params.get("type"))
                .addIfExists("as", params.get("as"))
                .addIfExists("title", params.get("title"));

        // see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/link#attr-disabled
        if ("stylesheet".equals(params.get("rel"))) {
            attrs.addIfTrue("disabled", params.get("disabled"));
        }

        start("link", attrs);
        end("link");
    }
}
