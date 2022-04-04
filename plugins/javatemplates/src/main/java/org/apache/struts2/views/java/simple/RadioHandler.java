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

import com.opensymphony.xwork2.util.ValueStack;

import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class RadioHandler extends AbstractTagHandler implements TagGenerator {

    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();

        Object listObj = params.get("list");
        String listKey = (String) params.get("listKey");
        String listValue = (String) params.get("listValue");
        int cnt = 0;

        ValueStack stack = this.context.getStack();
        if (listObj != null) {
            Iterator itt = MakeIterator.convert(listObj);
            while (itt.hasNext()) {
                Object item = itt.next();
                stack.push(item);

                // key
                Object itemKey = findValue(listKey != null ? listKey : "top");
                String itemKeyStr = StringUtils
                        .defaultString(itemKey == null ? null : itemKey
                                .toString());

                // value
                Object itemValue = findValue(listValue != null ? listValue
                        : "top");
                String itemValueStr = StringUtils
                        .defaultString(itemValue == null ? null : itemValue
                                .toString());

                // Radio button section
                String id = params.get("id") + Integer.toString(cnt++);
                Attributes a = new Attributes();
                a.add("type", "radio")
                        .addDefaultToEmpty("name", params.get("name"))
                        .addIfTrue("checked", isChecked(params, itemKeyStr))
                        .addIfExists("value", itemKeyStr)
                        .addIfTrue("disabled", params.get("disabled"))
                        .addIfExists("tabindex", params.get("tabindex"))
                        .addIfExists("id", id);
                super.start("input", a);
                super.end("input");

                // Label section
                a = new Attributes();
                a.addIfExists("for", id)
                        .addIfExists("class", params.get("cssClass"))
                        .addIfExists("style", params.get("cssStyle"))
                        .addIfExists("title", params.get("title"));
                super.start("label", a);
                if (StringUtils.isNotEmpty(itemValueStr)) {
                    characters(itemValueStr);
                }
                super.end("label");
                stack.pop();
            }
        }
    }

    /**
     * It's set to true if the nameValue (the value associated with the name
     * which is typically set in the action is equal to the current key value.
     * 
     * @param params
     *            the params
     * 
     * @param itemKeyStr
     *            the item key str
     * 
     * @return the boolean
     */
    private Boolean isChecked(Map<String, Object> params, String itemKeyStr) {
        Boolean checked = false;
        if (itemKeyStr != null) {

            // NameValue are the values that is provided by the name property
            // in the action
            Object nameValue = params.get("nameValue");

            if (nameValue != null) {

                Iterator itt = MakeIterator.convert(nameValue);
                while (itt.hasNext()) {

                    String value = itt.next().toString();
                    if (checked = value.equalsIgnoreCase(itemKeyStr)) {
                        break;
                    }

                }

            }

        }
        return checked;
    }

}
