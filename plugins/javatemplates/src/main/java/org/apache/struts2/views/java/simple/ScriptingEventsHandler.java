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

import java.io.IOException;
import java.util.Map;

/**
 * Adds attributes from scripting-event.ftl
 */
public class ScriptingEventsHandler extends AbstractTagHandler {

    /* (non-Javadoc)
     * @see org.apache.struts2.views.java.simple.AbstractTagHandler#start(java.lang.String, org.apache.struts2.views.java.Attributes)
     */
    @Override
    public void start(String name, Attributes a) throws IOException {
        Map params = context.getParameters();
        a.addIfExists("onclick", params.get("onclick"));
        a.addIfExists("ondblclick", params.get("ondblclick"));
        a.addIfExists("onmousedown", params.get("onmousedown"));
        a.addIfExists("onmouseup", params.get("onmouseup"));
        a.addIfExists("onmouseover", params.get("onmouseover"));
        a.addIfExists("onmousemove", params.get("onmousemove"));
        a.addIfExists("onmouseout", params.get("onmouseout"));
        a.addIfExists("onfocus", params.get("onfocus"));
        a.addIfExists("onblur", params.get("onblur"));
        a.addIfExists("onkeypress", params.get("onkeypress"));
        a.addIfExists("onkeydown", params.get("onkeydown"));
        a.addIfExists("onkeyup", params.get("onkeyup"));
        a.addIfExists("onselect", params.get("onselect"));
        a.addIfExists("onchange", params.get("onchange"));

        super.start(name, a);
    }

}
