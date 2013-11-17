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

package org.apache.struts2.dojo.views;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dojo.views.freemarker.tags.DojoModels;
import org.apache.struts2.dojo.views.velocity.components.AnchorDirective;
import org.apache.struts2.dojo.views.velocity.components.AutocompleterDirective;
import org.apache.struts2.dojo.views.velocity.components.BindDirective;
import org.apache.struts2.dojo.views.velocity.components.DateTimePickerDirective;
import org.apache.struts2.dojo.views.velocity.components.DivDirective;
import org.apache.struts2.dojo.views.velocity.components.HeadDirective;
import org.apache.struts2.dojo.views.velocity.components.SubmitDirective;
import org.apache.struts2.dojo.views.velocity.components.TabbedPanelDirective;
import org.apache.struts2.dojo.views.velocity.components.TextAreaDirective;
import org.apache.struts2.dojo.views.velocity.components.TreeDirective;
import org.apache.struts2.dojo.views.velocity.components.TreeNodeDirective;
import org.apache.struts2.views.TagLibraryDirectiveProvider;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.TagLibraryModelProvider;

public class DojoTagLibrary implements TagLibraryDirectiveProvider, TagLibraryModelProvider {

    public Object getModels(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        
        return new DojoModels(stack, req, res);
    }

    public List<Class> getDirectiveClasses() {
        Class[] directives = new Class[] {
            DateTimePickerDirective.class,
            DivDirective.class,
            AutocompleterDirective.class,
            AnchorDirective.class,
            SubmitDirective.class,
            TabbedPanelDirective.class,
            TreeDirective.class,
            TreeNodeDirective.class,
            HeadDirective.class,
            BindDirective.class,
            TextAreaDirective.class
        };
        return Arrays.asList(directives);
    }

}