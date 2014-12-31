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

package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes properties supported by the AbstractUITag - base class for all UI tags
 * This bases on HtmlTagSupportBeanInfo class from StripesFramework - thanks!
 */
public class AbstractUITagBeanInfo extends SimpleBeanInfo {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractUITagBeanInfo.class);

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();

            // Add the tricky one first
            Method classSetter = AbstractUITag.class.getMethod("setCssClass", String.class);
            Method styleSetter = AbstractUITag.class.getMethod("setCssStyle", String.class);

            descriptors.add(new PropertyDescriptor("class", null, classSetter));
            descriptors.add(new PropertyDescriptor("cssClass", null, classSetter));

            descriptors.add(new PropertyDescriptor("style", null, styleSetter));
            descriptors.add(new PropertyDescriptor("cssStyle", null, styleSetter));

            for (Field field : AbstractUITag.class.getDeclaredFields()) {
                String fieldName = field.getName();
                if (!"dynamicAttributes".equals(fieldName)) {
                    String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method setter = AbstractUITag.class.getMethod(setterName, String.class);
                    descriptors.add(new PropertyDescriptor(fieldName, null, setter));
                }
            }

            PropertyDescriptor[] array = new PropertyDescriptor[descriptors.size()];
            return descriptors.toArray(array);
        } catch (Exception e) {
            // This is crazy talk, we're only doing things that should always succeed
            LOG.fatal("Could not construct bean info for AbstractUITag. This is very bad.", e);
            return null;
        }
    }

}
