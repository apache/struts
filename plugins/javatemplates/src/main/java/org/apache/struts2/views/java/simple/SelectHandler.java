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
import org.apache.struts2.components.ListUIBean;
import org.apache.struts2.components.OptGroup;
import org.apache.struts2.util.ContainUtil;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SelectHandler extends AbstractTagHandler implements TagGenerator {
    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Attributes a = new Attributes();

        Object value = params.get("nameValue");

        a.addDefaultToEmpty("name", params.get("name"))
                .addIfExists("size", params.get("size"))
                .addIfExists("value", value)
                .addIfTrue("disabled", params.get("disabled"))
                .addIfTrue("readonly", params.get("readonly"))
                .addIfTrue("multiple", params.get("multiple"))
                .addIfExists("tabindex", params.get("tabindex"))
                .addIfExists("id", params.get("id"))
                .addIfExists("class", params.get("cssClass"))
                .addIfExists("style", params.get("cssStyle"))
                .addIfExists("title", params.get("title"));
        super.start("select", a);

        //options

        //header
        String headerKey = (String) params.get("headerKey");
        String headerValue = (String) params.get("headerValue");
        if (headerKey != null && headerValue != null) {
            boolean selected = ContainUtil.contains(value, params.get("headerKey"));
            writeOption(headerKey, headerValue, selected);
        }
	
	//emptyoption
        Object emptyOption = params.get("emptyOption");
        if (emptyOption != null && emptyOption.toString().equals(Boolean.toString(true))) {
        	boolean selected = ContainUtil.contains(value, "") || ContainUtil.contains(value, null);
        	writeOption("", "", selected);
        }

        Object listObj = params.get("list");
        String listKey = (String) params.get("listKey");
        String listValue = (String) params.get("listValue");
        ValueStack stack = this.context.getStack();
        if (listObj != null) {
            Iterator itt = MakeIterator.convert(listObj);
            while (itt.hasNext()) {
                Object item = itt.next();
                stack.push(item);

                //key
                Object itemKey = findValue(listKey != null ? listKey : "top");
                String itemKeyStr = StringUtils.defaultString(itemKey == null ? null : itemKey.toString()); 
                //value
                Object itemValue = findValue(listValue != null ? listValue : "top");
                String itemValueStr = StringUtils.defaultString(itemValue == null ? null : itemValue.toString()); 

                boolean selected = ContainUtil.contains(value, itemKey);
                writeOption(itemKeyStr, itemValueStr, selected);

                stack.pop();
            }
        }

        //opt group
        List<ListUIBean> listUIBeans = (List<ListUIBean>) params.get(OptGroup.INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY);
        if (listUIBeans != null) {
            for (ListUIBean listUIBean : listUIBeans) {
                writeOptionGroup(listUIBean, value);
            }
        }

        super.end("select");
    }

    private void writeOption(String value, String text, boolean selected) throws IOException {
        Attributes attrs = new Attributes();
        attrs.addIfExists("value", value)
                .addIfTrue("selected", selected);

        start("option", attrs);
        characters(text);
        end("option");
    }

    private void writeOptionGroup(ListUIBean listUIBean, Object value) throws IOException {
        Map params = listUIBean.getParameters();
        Attributes attrs = new Attributes();
        attrs.addIfExists("label", params.get("label"))
                .addIfTrue("disabled", params.get("disabled"));
        start("optgroup", attrs);

        //options
        ValueStack stack = context.getStack();
        Object listObj = params.get("list");
        if (listObj != null) {
            Iterator itt = MakeIterator.convert(listObj);
            String listKey = (String) params.get("listKey");
            String listValue = (String) params.get("listValue");
            while (itt.hasNext()) {
                Object optGroupBean = itt.next();
                stack.push(optGroupBean);

                Object tmpKey = stack.findValue(listKey != null ? listKey : "top");
                String tmpKeyStr = StringUtils.defaultString(tmpKey.toString());
                Object tmpValue = stack.findValue(listValue != null ? listValue : "top");
                String tmpValueStr = StringUtils.defaultString(tmpValue.toString());
                boolean selected = ContainUtil.contains(value, tmpKeyStr);
                writeOption(tmpKeyStr, tmpValueStr, selected);

                stack.pop();
            }
        }

        end("optgroup");
    }
}
