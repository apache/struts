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

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.struts2.StrutsConstants;

import java.util.Set;
import java.util.TreeSet;

/**
 * ActionNamesAction
 */
public class ActionNamesAction extends ActionSupport {

    private static final long serialVersionUID = -5389385242431387840L;

    private Set<String> actionNames;
    private String namespace = "";
    private Set<String> namespaces;
    private String extension;

    protected ConfigurationHelper configHelper;

    @Inject
    public void setConfigurationHelper(ConfigurationHelper cfg) {
        this.configHelper = cfg;
    }

    public Set<String> getActionNames() {
        return actionNames;
    }

    public String getNamespace() {
        return StringEscapeUtils.escapeHtml4(namespace);
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Inject(StrutsConstants.STRUTS_ACTION_EXTENSION)
    public void setExtension(String ext) {
        this.extension = ext;
    }

    public ActionConfig getConfig(String actionName) {
        return configHelper.getActionConfig(namespace, actionName);
    }

    public Set<String> getNamespaces() {
        return namespaces;
    }

    public String getExtension() {
        if (extension == null) {
            return "action";
        }
        if (extension.contains(",")) {
            return extension.substring(0, extension.indexOf(","));
        }
        return extension;
    }

    public String execute() throws Exception {
        namespaces = configHelper.getNamespaces();
        if (namespaces.size() == 0) {
            addActionError("There are no namespaces in this configuration");
            return ERROR;
        }
        if (namespace == null) {
            namespace = "";
        }
        actionNames = new TreeSet<String>(configHelper.getActionNames(namespace));
        return SUCCESS;
    }

    /**
     * Index action to support cooperation with REST plugin
     *
     * @return action result
     * @throws Exception
     */
    public String index() throws Exception {
        return execute();
    }

    public String redirect() {
        return SUCCESS;
    }

}
