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

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.config.entities.ActionConfig;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.StrutsConstants;

import java.util.Set;
import java.util.TreeSet;

/**
 * ActionNamesAction
 *
 */
public class ActionNamesAction extends ActionSupport {
	
	private static final long serialVersionUID = -5389385242431387840L;
	
	private Set actionNames;
    private String namespace = "";
    private Set namespaces;
    private String extension;

    public Set getActionNames() {
        return actionNames;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public ActionConfig getConfig(String actionName) {
        return ConfigurationHelper.getActionConfig(namespace, actionName);
    }

    public Set getNamespaces() {
        return namespaces;
    }

    public String getExtension() {
        if ( extension == null) {
            String ext = (String) Configuration.get(StrutsConstants.STRUTS_ACTION_EXTENSION);
            if ( ext == null || ext.equals("")) {
                extension = "action";
            } else {
                extension = ext;
            }
        }
        return extension;
    }

    public String execute() throws Exception {
        namespaces = ConfigurationHelper.getNamespaces();
        if (namespaces.size() == 0) {
            addActionError("There are no namespaces in this configuration");
            return ERROR;
        }
        if (namespace == null) {
            namespace = "";
        }
        actionNames =
                new TreeSet(ConfigurationHelper.getActionNames(namespace));
        return SUCCESS;
    }
}
