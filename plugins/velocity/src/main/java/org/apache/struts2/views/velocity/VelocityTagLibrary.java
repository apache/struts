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
package org.apache.struts2.views.velocity;

import org.apache.struts2.views.TagLibraryDirectiveProvider;
import org.apache.struts2.views.velocity.components.ActionDirective;
import org.apache.struts2.views.velocity.components.ActionErrorDirective;
import org.apache.struts2.views.velocity.components.ActionMessageDirective;
import org.apache.struts2.views.velocity.components.AnchorDirective;
import org.apache.struts2.views.velocity.components.BeanDirective;
import org.apache.struts2.views.velocity.components.CheckBoxDirective;
import org.apache.struts2.views.velocity.components.CheckBoxListDirective;
import org.apache.struts2.views.velocity.components.ComboBoxDirective;
import org.apache.struts2.views.velocity.components.ComponentDirective;
import org.apache.struts2.views.velocity.components.DateDirective;
import org.apache.struts2.views.velocity.components.DoubleSelectDirective;
import org.apache.struts2.views.velocity.components.FieldErrorDirective;
import org.apache.struts2.views.velocity.components.FileDirective;
import org.apache.struts2.views.velocity.components.FormDirective;
import org.apache.struts2.views.velocity.components.HeadDirective;
import org.apache.struts2.views.velocity.components.HiddenDirective;
import org.apache.struts2.views.velocity.components.I18nDirective;
import org.apache.struts2.views.velocity.components.IncludeDirective;
import org.apache.struts2.views.velocity.components.LabelDirective;
import org.apache.struts2.views.velocity.components.OptionTransferSelectDirective;
import org.apache.struts2.views.velocity.components.ParamDirective;
import org.apache.struts2.views.velocity.components.PasswordDirective;
import org.apache.struts2.views.velocity.components.PropertyDirective;
import org.apache.struts2.views.velocity.components.PushDirective;
import org.apache.struts2.views.velocity.components.RadioDirective;
import org.apache.struts2.views.velocity.components.ResetDirective;
import org.apache.struts2.views.velocity.components.SelectDirective;
import org.apache.struts2.views.velocity.components.SetDirective;
import org.apache.struts2.views.velocity.components.SubmitDirective;
import org.apache.struts2.views.velocity.components.TextAreaDirective;
import org.apache.struts2.views.velocity.components.TextDirective;
import org.apache.struts2.views.velocity.components.TextFieldDirective;
import org.apache.struts2.views.velocity.components.TokenDirective;
import org.apache.struts2.views.velocity.components.URLDirective;
import org.apache.struts2.views.velocity.components.UpDownSelectDirective;

import java.util.Arrays;
import java.util.List;

public class VelocityTagLibrary implements TagLibraryDirectiveProvider {

    @Override
    public List<Class<?>> getDirectiveClasses() {
        Class<?>[] directives = new Class[] {
            ActionDirective.class,
            BeanDirective.class,
            CheckBoxDirective.class,
            CheckBoxListDirective.class,
            ComboBoxDirective.class,
            ComponentDirective.class,
            DateDirective.class,
            DoubleSelectDirective.class,
            FileDirective.class,
            FormDirective.class,
            HeadDirective.class,
            HiddenDirective.class,
            AnchorDirective.class,
            I18nDirective.class,
            IncludeDirective.class,
            LabelDirective.class,
            ParamDirective.class,
            PasswordDirective.class,
            PushDirective.class,
            PropertyDirective.class,
            RadioDirective.class,
            SelectDirective.class,
            SetDirective.class,
            SubmitDirective.class,
            ResetDirective.class,
            TextAreaDirective.class,
            TextDirective.class,
            TextFieldDirective.class,
            TokenDirective.class,
            URLDirective.class,
            ActionErrorDirective.class,
            ActionMessageDirective.class,
            FieldErrorDirective.class,
            OptionTransferSelectDirective.class,
            UpDownSelectDirective.class
        };
        return Arrays.asList(directives);
    }

    /**
     * @deprecated please use {#getDirectiveClasses}
     */
    @Deprecated()
    public List<Class<?>> getVelocityDirectiveClasses() {
        return getDirectiveClasses();
    }

}
