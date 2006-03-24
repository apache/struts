package com.opensymphony.webwork.config_browser;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.WebWorkConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import java.util.TreeSet;

/**
 * ActionNamesAction
 *
 * @author Jason Carreira Created Aug 11, 2003 9:35:15 PM
 * @author Rainer Hermanns
 */
public class ActionNamesAction extends ActionSupport {
    private Set actionNames;
    private String namespace = "";
    private Set namespaces;
    private String extension;
    private static Log log = LogFactory.getLog(ActionNamesAction.class);

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
            String ext = (String) Configuration.get(WebWorkConstants.WEBWORK_ACTION_EXTENSION);
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
