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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Base class for ActionError and ActionMessage
 */
public abstract class AbstractMessageListHandler extends AbstractTagHandler implements TagGenerator {
    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Object errorsObj = findValue(getListExpression());

        if (errorsObj != null) {
            Iterator itt = MakeIterator.convert(errorsObj);
            if (itt.hasNext()) {
                boolean escape = BooleanUtils.toBooleanDefaultIfNull((Boolean) params.get("escape"), false);
                Attributes attrs = new Attributes();
                attrs.addIfExists("style", params.get("cssStyle"))
                        .add("class", params.containsKey("cssClass") ? (String) params.get("cssClass") : getDefaultClass());
                start("ul", attrs);
                while (itt.hasNext()) {
                    String error = (String) itt.next();

                    //li for each error
                    start("li", null);

                    //span for error
                    start("span", null);
                    characters(error, escape);
                    end("span");
                    end("li");

                }
                end("ul");
            }
        }
    }

    /*
     * Expression used to get list from stack
     */
    protected abstract String getListExpression();

    /*
    * default class for UL element
    */
    protected abstract String getDefaultClass();
}

