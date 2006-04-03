/*
 * Created on Aug 14, 2004 by mgreer
 */
package org.apache.struts.action2.sitegraph;

import org.apache.struts.action2.sitegraph.collectors.ArbitraryXMLConfigurationProvider;
import org.apache.struts.action2.sitegraph.entities.FreeMarkerView;
import org.apache.struts.action2.sitegraph.entities.JspView;
import org.apache.struts.action2.sitegraph.entities.VelocityView;
import org.apache.struts.action2.sitegraph.entities.View;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Initializes and retrieves XWork config elements
 */
public class XWorkConfigRetriever {

    private static final Log LOG = LogFactory.getLog(XWorkConfigRetriever.class);
    private static String configDir;
    private static String[] views;
    private static boolean isXWorkStarted = false;
    private static Map viewCache = new LinkedHashMap();

    /**
     * Returns a Map of all action names/configs
     *
     * @return Map of all action names/configs
     */
    public static Map getActionConfigs() {
        if (!isXWorkStarted)
            initXWork();
        return ConfigurationManager.getConfiguration().getRuntimeConfiguration().getActionConfigs();
    }

    private static void initXWork() {
        String configFilePath = configDir + "/xwork.xml";
        File configFile = new File(configFilePath);
        try {
            ConfigurationProvider configProvider = new ArbitraryXMLConfigurationProvider(configFile.getCanonicalPath());
            ConfigurationManager.addConfigurationProvider(configProvider);
            isXWorkStarted = true;
        } catch (IOException e) {
            LOG.error("IOException", e);
        }
    }

    public static Set getNamespaces() {
        Set namespaces = Collections.EMPTY_SET;
        Map allActionConfigs = getActionConfigs();
        if (allActionConfigs != null) {
            namespaces = allActionConfigs.keySet();
        }
        return namespaces;
    }

    /**
     * Return a Set of the action names for this namespace.
     *
     * @param namespace
     * @return Set of the action names for this namespace.
     */
    public static Set getActionNames(String namespace) {
        Set actionNames = Collections.EMPTY_SET;
        Map allActionConfigs = getActionConfigs();
        if (allActionConfigs != null) {
            Map actionMappings = (Map) allActionConfigs.get(namespace);
            if (actionMappings != null) {
                actionNames = actionMappings.keySet();
            }
        }
        return actionNames;
    }

    /**
     * Returns the ActionConfig for this action name at this namespace.
     *
     * @param namespace
     * @param actionName
     * @return The ActionConfig for this action name at this namespace.
     */
    public static ActionConfig getActionConfig(String namespace, String actionName) {
        ActionConfig config = null;
        Map allActionConfigs = getActionConfigs();
        if (allActionConfigs != null) {
            Map actionMappings = (Map) allActionConfigs.get(namespace);
            if (actionMappings != null) {
                config = (ActionConfig) actionMappings.get(actionName);
            }
        }
        return config;
    }

    public static ResultConfig getResultConfig(String namespace, String actionName,
                                               String resultName) {
        ResultConfig result = null;
        ActionConfig actionConfig = getActionConfig(namespace, actionName);
        if (actionConfig != null) {
            Map resultMap = actionConfig.getResults();
            result = (ResultConfig) resultMap.get(resultName);
        }
        return result;
    }

    public static File getViewFile(String namespace, String actionName, String resultName) {
        ResultConfig result = getResultConfig(namespace, actionName, resultName);
        String location = (String) result.getParams().get("location");
        for (int i = 0; i < views.length; i++) {
            String viewRoot = views[i];
            File viewFile = getViewFileInternal(viewRoot, location, namespace);
            if (viewFile != null) {
                return viewFile;
            }
        }

        return null;
    }

    private static File getViewFileInternal(String root, String location, String namespace) {
        StringBuffer filePath = new StringBuffer(root);
        if (!location.startsWith("/")) {
            filePath.append(namespace + "/");
        }
        filePath.append(location);
        File viewFile = new File(filePath.toString());
        if (viewFile.exists()) {
            return viewFile;
        } else {
            return null;
        }
    }

    public static View getView(String namespace, String actionName, String resultName, int type) {
        String viewId = namespace + "/" + actionName + "/" + resultName;
        View view = (View) viewCache.get(viewId);
        if (view == null) {
            File viewFile = XWorkConfigRetriever.getViewFile(namespace, actionName, resultName);
            if (viewFile != null) {
                switch (type) {
                    case View.TYPE_JSP:
                        view = new JspView(viewFile);
                        break;
                    case View.TYPE_FTL:
                        view = new FreeMarkerView(viewFile);
                        break;
                    case View.TYPE_VM:
                        view = new VelocityView(viewFile);
                        break;
                    default:
                        return null;
                }

                viewCache.put(viewId, view);
            }
        }
        return view;
    }

    public static void setConfiguration(String configDir, String[] views) {
        XWorkConfigRetriever.configDir = configDir;
        XWorkConfigRetriever.views = views;
        isXWorkStarted = false;
        viewCache = new LinkedHashMap();
    }
}
