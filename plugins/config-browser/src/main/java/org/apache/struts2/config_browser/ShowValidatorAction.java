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
package org.apache.struts2.config_browser;

import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.reflection.ReflectionContextFactory;
import org.apache.struts2.util.reflection.ReflectionException;
import org.apache.struts2.util.reflection.ReflectionProvider;
import org.apache.struts2.validator.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionContext;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * ShowValidatorAction
 *
 */
public class ShowValidatorAction extends ListValidatorsAction {
    private static final long serialVersionUID = 4061534149317835177L;

    private static final Logger LOG = LogManager.getLogger(ShowValidatorAction.class);

    private Set<PropertyInfo> properties = Collections.emptySet();
    private int selected = 0;

    private ReflectionProvider reflectionProvider;

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    public int getSelected() {
        return selected;
    }

    @StrutsParameter
    public void setSelected(int selected) {
        this.selected = selected;
    }

    public Set getProperties() {
        return properties;
    }

    public Validator getSelectedValidator() {
        return validators.get(selected);
    }

    @Override
    public String execute() throws Exception {
        loadValidators();
        Validator validator = getSelectedValidator();
        properties = new TreeSet<>();
        try {
            BeanInfo beanInfoFrom;
            try {
                beanInfoFrom = Introspector.getBeanInfo(validator.getClass(), Object.class);
            } catch (IntrospectionException e) {
                LOG.error("An error occurred", e);
                addActionError("An error occurred while introspecting a validator of type " + validator.getClass().getName());
                return ERROR;
            }

            PropertyDescriptor[] pds = beanInfoFrom.getPropertyDescriptors();

            Map<String, Object> context = ActionContext.getContext().getContextMap();
            for (PropertyDescriptor pd : pds) {
                String name = pd.getName();
                Object value = null;
                if (pd.getReadMethod() == null) {
                    value = "No read method for property";
                } else {
                    try {
                        value = reflectionProvider.getValue(name, context, validator);
                    } catch (ReflectionException e) {
                        addActionError("Caught exception while getting property value for '" + name + "' on validator of type " + validator.getClass().getName());
                    }
                }
                properties.add(new PropertyInfo(name, pd.getPropertyType(), value));
            }
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Unable to retrieve properties.", e);
            }
            addActionError("Unable to retrieve properties: " + e);
        }

        if (hasErrors()) {
            return ERROR;
        } else {
            return SUCCESS;
        }
    }

    public static class PropertyInfo implements Comparable {
        private String name;
        private Class type;
        private Object value;

        public PropertyInfo(String name, Class type, Object value) {
            if (name == null) {
                throw new IllegalArgumentException("Name must not be null");
            }
            if (type == null) {
                throw new IllegalArgumentException("Type must not be null");
            }
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public Class getType() {
            return type;
        }

        public void setType(Class type) {
            this.type = type;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PropertyInfo)) return false;

            final PropertyInfo propertyInfo = (PropertyInfo) o;

            if (!name.equals(propertyInfo.name)) return false;
            if (!type.equals(propertyInfo.type)) return false;

            return Objects.equals(value, propertyInfo.value);
        }

        @Override
        public int hashCode() {
            int result;
            result = name.hashCode();
            result = 29 * result + type.hashCode();
            result = 29 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(Object o) {
            PropertyInfo other = (PropertyInfo) o;
            return this.name.compareTo(other.name);
        }
    }
}
