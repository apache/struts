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

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;

import java.io.IOException;

public class HeadHandler extends AbstractTagHandler implements TagGenerator {

    public void generate() throws IOException {
        Attributes attrs = new Attributes();
        attrs.put("type", "text/javascript");

        String base = ServletActionContext.getRequest().getContextPath();
        attrs.put("base", base);
        
        StringBuilder sb = new StringBuilder();
        if (base != null) {
            sb.append(base);
        }
        
        sb.append("/struts/utils.js");
        attrs.put("src", sb.toString());

        super.start("script", attrs);
        super.end("script");
    }
}
