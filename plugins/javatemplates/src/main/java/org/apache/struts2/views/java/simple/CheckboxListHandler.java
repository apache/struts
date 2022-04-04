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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class CheckboxListHandler extends AbstractTagHandler implements TagGenerator {
    public void generate() throws IOException {

        Map<String, Object> params = context.getParameters();

        // Get parameters
        Object listObj = params.get("list");
        String listKey = (String) params.get("listKey");
        String listValue = (String) params.get("listValue");
        String name = (String) params.get("name");
        Object disabled = params.get("disabled");
        String id = (String) params.get("id");

        int cnt = 1;

        // This will interate through all lists
        ValueStack stack = this.context.getStack();
        if (listObj != null) {
            Iterator itt = MakeIterator.convert(listObj);
            while (itt.hasNext()) {
                Object item = itt.next();
                stack.push(item);

                // key
                Object itemKey = findValue(listKey != null ? listKey : "top");
                String itemKeyStr = StringUtils.defaultString(itemKey == null ? null : itemKey.toString());

                // value
                Object itemValue = findValue(listValue != null ? listValue : "top");
                String itemValueStr = StringUtils.defaultString(itemValue == null ? null : itemValue
                        .toString());

                // Checkbox button section
                Attributes a = new Attributes();
                a.add("type", "checkbox").add("name", name).add("value", itemKeyStr)
                        .addIfTrue("checked", isChecked(params, itemKeyStr))
                        .addIfTrue("readonly", params.get("readonly")).addIfTrue("disabled", disabled)
                        .addIfExists("tabindex", params.get("tabindex"))
                        .addIfExists("id", id + "-" + Integer.toString(cnt));
                start("input", a);
                end("input");

                // Label section
                a = new Attributes();
                a.add("for", id + "-" + Integer.toString(cnt)).addIfExists("class", params.get("cssClass"))
                        .addIfExists("style", params.get("cssStyle"));
                super.start("label", a);
                if (StringUtils.isNotEmpty(itemValueStr))
                    characters(itemValueStr);
                super.end("label");

                // Hidden input section
                a = new Attributes();
                a.add("type", "hidden")
                        .add("id",
                                "__multiselect_"
                                        + StringUtils.defaultString(StringEscapeUtils.escapeHtml4(id)))
                        .add("name",
                                "__multiselect_"
                                        + StringUtils.defaultString(StringEscapeUtils.escapeHtml4(name)))
                        .add("value", "").addIfTrue("disabled", disabled);
                start("input", a);
                end("input");

                stack.pop();
                cnt++;
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
