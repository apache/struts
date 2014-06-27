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

import org.apache.struts2.views.java.DefaultTagHandlerFactory;
import org.apache.struts2.views.java.DefaultTheme;
import org.apache.struts2.views.java.TagHandlerFactory;
import org.apache.struts2.views.java.XHTMLTagSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleTheme extends DefaultTheme {

    public SimpleTheme() {
        setHandlerFactories(new HashMap<String, List<TagHandlerFactory>>() {
            {
                put("text", new FactoryList(TextFieldHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("textfield", new FactoryList(TextFieldHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("datetextfield", new FactoryList(DateTextFieldHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class));
                put("select", new FactoryList(SelectHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("form", new FactoryList(FormHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("form-close", new FactoryList(FormHandler.CloseHandler.class));
                put("a", new FactoryList(AnchorHandler.class));
                put("a-close", new FactoryList(AnchorHandler.CloseHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("checkbox", new FactoryList(CheckboxHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("file", new FactoryList(FileHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("password", new FactoryList(PasswordHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("label", new FactoryList(LabelHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("reset", new FactoryList(ResetHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("submit", new FactoryList(SubmitHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("submit-close", new FactoryList(SubmitHandler.CloseHandler.class));
                put("textarea", new FactoryList(TextAreaHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("radiomap", new FactoryList(RadioHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("checkboxlist", new FactoryList(CheckboxListHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("actionerror", new FactoryList(ActionErrorHandler.class));
                put("token", new FactoryList(TokenHandler.class));
                put("actionmessage", new FactoryList(ActionMessageHandler.class));
                put("head", new FactoryList(HeadHandler.class));
                put("hidden", new FactoryList(HiddenHandler.class));
                put("fielderror", new FactoryList(FieldErrorHandler.class));
                put("div", new FactoryList(DivHandler.class, ScriptingEventsHandler.class, CommonAttributesHandler.class, DynamicAttributesHandler.class));
                put("div-close", new FactoryList(DivHandler.CloseHandler.class));
                put("empty", new FactoryList(EmptyHandler.class));
           }
        });
        setName("simple");
    }

    private class FactoryList extends ArrayList<TagHandlerFactory> {

        private static final long serialVersionUID = -1551895041394434032L;

        public FactoryList(Class... classes) {
            super();
            for (Class cls : classes) {
                add(new DefaultTagHandlerFactory(cls));
            }
            add(new DefaultTagHandlerFactory(XHTMLTagSerializer.class));
        }
    }

}
