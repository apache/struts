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
package org.apache.struts.action2.config_browser;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.config.entities.ActionConfig;
import ognl.OgnlRuntime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.util.Set;
import java.util.TreeSet;

/**
 * ShowConfigAction
 *
 * @author Jason Carreira Created Aug 11, 2003 9:42:12 PM
 */
public class ShowConfigAction extends ActionNamesAction {
	
	private static final long serialVersionUID = -1630527489407671652L;

	private static final PropertyDescriptor[] PDSAT = new PropertyDescriptor[0];

    private String namespace;
    private String actionName;
    private ActionConfig config;
    private Set actionNames;
    private String detailView = "results";
    private PropertyDescriptor[] properties;
    private static Log log = LogFactory.getLog(ShowConfigAction.class);

    public String getDetailView() {
        return detailView;
    }

    public void setDetailView(String detailView) {
        this.detailView = detailView;
    }

    public Set getActionNames() {
        return actionNames;
    }

    public String getNamespace() {
        return namespace;
    }

    public String stripPackage(Class clazz) {
        return clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public ActionConfig getConfig() {
        return config;
    }

    public PropertyDescriptor[] getProperties() {
        return properties;
    }

    public String execute() throws Exception {
        super.execute();
        config = ConfigurationHelper.getActionConfig(namespace, actionName);
        actionNames =
                new TreeSet(ConfigurationHelper.getActionNames(namespace));
        try {
            Class clazz = ObjectFactory.getObjectFactory().getClassInstance(getConfig().getClassName());
            java.util.Collection pds = OgnlRuntime.getPropertyDescriptors(clazz).values();
            properties = (PropertyDescriptor[]) pds.toArray(PDSAT);
        } catch (Exception e) {
            log.error("Unable to get properties for action " + actionName, e);
            addActionError("Unable to retrieve action properties: " + e.toString());
        }

        if (hasErrors()) //super might have set some :)
            return ERROR;
        else
            return SUCCESS;
    }
}

