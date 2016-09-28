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

package org.apache.struts2.config_browser;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.components.UrlRenderer;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.velocity.VelocityManager;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Shows the beans loaded by the internal Guice container.  Only shows beans that are recognized by Struts as official
 * plugin extension points.
 */
public class ShowBeansAction extends ActionNamesAction {

    Map<String, Set<Binding>> bindings;

    @Inject
    public void setContainer(Container container) {
        super.setContainer(container);
        bindings = new TreeMap<String, Set<Binding>>();
        bindings.put(ObjectFactory.class.getName(), addBindings(container, ObjectFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY));
        bindings.put(XWorkConverter.class.getName(), addBindings(container, XWorkConverter.class, StrutsConstants.STRUTS_XWORKCONVERTER));
        bindings.put(TextProvider.class.getName(), addBindings(container, TextProvider.class, StrutsConstants.STRUTS_XWORKTEXTPROVIDER));
        bindings.put(ActionProxyFactory.class.getName(), addBindings(container, ActionProxyFactory.class, StrutsConstants.STRUTS_ACTIONPROXYFACTORY));
        bindings.put(ObjectTypeDeterminer.class.getName(), addBindings(container, ObjectTypeDeterminer.class, StrutsConstants.STRUTS_OBJECTTYPEDETERMINER));
        bindings.put(ActionMapper.class.getName(), addBindings(container, ActionMapper.class, StrutsConstants.STRUTS_MAPPER_CLASS));
        bindings.put(MultiPartRequest.class.getName(), addBindings(container, MultiPartRequest.class, StrutsConstants.STRUTS_MULTIPART_PARSER));
        bindings.put(FreemarkerManager.class.getName(), addBindings(container, FreemarkerManager.class, StrutsConstants.STRUTS_FREEMARKER_MANAGER_CLASSNAME));
        bindings.put(VelocityManager.class.getName(), addBindings(container, VelocityManager.class, StrutsConstants.STRUTS_VELOCITY_MANAGER_CLASSNAME));
        bindings.put(UrlRenderer.class.getName(), addBindings(container, UrlRenderer.class, StrutsConstants.STRUTS_URL_RENDERER));
    }

    public Map<String, Set<Binding>> getBeans() {
        return bindings;
    }

    protected Set<Binding> addBindings(Container container, Class type, String constName) {
        Set<Binding> bindings = new TreeSet<Binding>();
        String chosenName = container.getInstance(String.class, constName);
        if (chosenName == null) {
            chosenName = "struts";
        }
        Set<String> names = container.getInstanceNames(type);
        if (!names.contains(chosenName)) {
            bindings.add(new Binding(getInstanceClassName(container, type, "default"), chosenName, constName, true));
        }
        for (String name : names) {
            if (!"default".equals(name)) {
                bindings.add(new Binding(getInstanceClassName(container, type, name), name, constName, name.equals(chosenName)));
            }
        }
        return bindings;
    }

    String getInstanceClassName(Container container, Class type, String name) {
        String instName = "Class unable to be loaded";
        try {
            Object inst = container.getInstance(type, name);
            instName = inst.getClass().getName();
        } catch (Exception ex) {
            // Ignoring beans unable to be loaded
        }
        return instName;
    }

    public class Binding implements Comparable<Binding> {
        private String impl;
        private String alias;
        private String constant;
        private boolean isDefault;

        public Binding(String impl, String alias, String constant, boolean def) {
            this.impl = impl;
            this.alias = alias;
            this.constant = constant;
            this.isDefault = def;
        }

        public String getImpl() {
            return impl;
        }

        public String getAlias() {
            return alias;
        }

        public String getConstant() {
            return constant;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public int compareTo(Binding b2) {
            int ret = 0;
            if (isDefault) {
                ret = -1;
            } else if (b2.isDefault()) {
                ret = 1;
            } else {
                ret = alias.compareTo(b2.getAlias());
            }
            return ret;
        }
    }
}
