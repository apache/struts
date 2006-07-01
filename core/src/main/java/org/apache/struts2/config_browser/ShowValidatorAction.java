/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.config_browser;

import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.validator.Validator;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * ShowValidatorAction
 *
 */
public class ShowValidatorAction extends ListValidatorsAction {
    private static Log log = LogFactory.getLog(ShowValidatorAction.class);

    Set properties = Collections.EMPTY_SET;
    int selected = 0;

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public Set getProperties() {
        return properties;
    }

    public Validator getSelectedValidator() {
        return (Validator) validators.get(selected);
    }

    public String execute() throws Exception {
        loadValidators();
        Validator validator = getSelectedValidator();
        properties = new TreeSet();
        try {
            Map context = Ognl.createDefaultContext(validator);
            BeanInfo beanInfoFrom = null;
            try {
                beanInfoFrom = Introspector.getBeanInfo(validator.getClass(), Object.class);
            } catch (IntrospectionException e) {
                log.error("An error occurred", e);
                addActionError("An error occurred while introspecting a validator of type " + validator.getClass().getName());
                return ERROR;
            }

            PropertyDescriptor[] pds = beanInfoFrom.getPropertyDescriptors();

            for (int i = 0; i < pds.length; i++) {
                PropertyDescriptor pd = pds[i];
                String name = pd.getName();
                Object value = null;
                if (pd.getReadMethod() == null) {
                    value = "No read method for property";
                } else {
                    try {
                        Object expr = OgnlUtil.compile(name);
                        value = Ognl.getValue(expr, context, validator);
                    } catch (OgnlException e) {
                        addActionError("Caught OGNL exception while getting property value for '" + name + "' on validator of type " + validator.getClass().getName());
                    }
                }
                properties.add(new PropertyInfo(name, pd.getPropertyType(), value));
            }
        } catch (Exception e) {
            log.warn("Unable to retrieve properties.", e);
            addActionError("Unable to retrieve properties: " + e.toString());
        }

        if (hasErrors())
            return ERROR;
        else
            return SUCCESS;
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

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PropertyInfo)) return false;

            final PropertyInfo propertyInfo = (PropertyInfo) o;

            if (!name.equals(propertyInfo.name)) return false;
            if (!type.equals(propertyInfo.type)) return false;
            if (value != null ? !value.equals(propertyInfo.value) : propertyInfo.value != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = name.hashCode();
            result = 29 * result + type.hashCode();
            result = 29 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        public int compareTo(Object o) {
            PropertyInfo other = (PropertyInfo) o;
            return this.name.compareTo(other.name);
        }
    }
}
