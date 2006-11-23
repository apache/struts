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
package org.apache.struts2.codebehind;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.providers.InterceptorBuilder;
import com.opensymphony.xwork2.inject.Inject;

public class CodebehindUnknownHandler implements UnknownHandler {

    protected String defaultPackageName = "codebehind-default";
    protected ServletContext servletContext;
    protected Map<String,ResultTypeConfig> resultsByExtension = new HashMap<String,ResultTypeConfig>();
    protected String templatePathPrefix = "/";
    protected Configuration configuration;
    protected ObjectFactory objectFactory;
    
    protected static final Log LOG = LogFactory.getLog(CodebehindUnknownHandler.class);
    
    @Inject("struts.codebehind.defaultPackage")
    public void setDefaultPackage(String pkg) {
        this.defaultPackageName = pkg;
    }
    
    @Inject
    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }
    
    @Inject
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }
    
    protected Map<String,ResultTypeConfig> loadResultTypes(Configuration config) {
        Map<String,ResultTypeConfig> resultTypes = new LinkedHashMap<String,ResultTypeConfig>();
        PackageConfig parentPackage = config.getPackageConfig(defaultPackageName);
        Map<String,ResultTypeConfig> results = parentPackage.getAllResultTypeConfigs();
        
        resultTypes.put("jsp", results.get("dispatcher"));
        resultTypes.put("vm", results.get("velocity"));
        resultTypes.put("ftl", results.get("freemarker"));
        return resultTypes;
    }
    
    public ActionConfig handleUnknownAction(String namespace, String actionName)
            throws XWorkException {
        if (resultsByExtension == null) {
            resultsByExtension = loadResultTypes(configuration);
        }
        String pathPrefix = determinePath(templatePathPrefix, namespace);
        ActionConfig actionConfig = null;
        for (String ext : resultsByExtension.keySet()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to locate unknown action template with extension ."+ext+" in directory "+pathPrefix);
            }
            String path = string(pathPrefix, actionName, "." , ext);
            try {
                if (servletContext.getResource(path) != null) {
                    actionConfig = buildActionConfig(path, namespace, actionName, resultsByExtension.get(ext));
                    break;
                }
            } catch (MalformedURLException e) {
                LOG.warn("Unable to parse template path: "+path+", skipping...");
            }
        }
        return actionConfig;
    }

    protected ActionConfig buildActionConfig(String path, String namespace, String actionName, ResultTypeConfig resultTypeConfig) {
        if (resultsByExtension == null) {
            resultsByExtension = loadResultTypes(configuration);
        }
        Map<String,ResultConfig> results = new HashMap<String,ResultConfig>();
        HashMap params = new HashMap();
        if (resultTypeConfig.getParams() != null) {
            params.putAll(resultTypeConfig.getParams());
        }
        params.put(resultTypeConfig.getDefaultResultParam(), path);
        
        PackageConfig pkg = configuration.getPackageConfig(defaultPackageName);
        List interceptors = InterceptorBuilder.constructInterceptorReference(pkg, pkg.getFullDefaultInterceptorRef(), 
                Collections.EMPTY_MAP, null, objectFactory); 
        ResultConfig config = new ResultConfig(Action.SUCCESS, resultTypeConfig.getClazz(), params);
        results.put(Action.SUCCESS, config);
        return new ActionConfig("execute", ActionSupport.class.getName(), defaultPackageName, new HashMap(), results, interceptors);
    }

    public Result handleUnknownResult(ActionContext actionContext, String actionName, 
            ActionConfig actionConfig, String resultCode) throws XWorkException {
        
        Result result = null;
        PackageConfig pkg = configuration.getPackageConfig(actionConfig.getPackageName());
        String ns = pkg.getNamespace();
        String pathPrefix = determinePath(templatePathPrefix, ns);
        
        for (String ext : resultsByExtension.keySet()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to locate result with extension ."+ext+" in directory "+pathPrefix);
            }
            String path = string(pathPrefix, actionName, "-", resultCode, "." , ext);
            try {
                if (servletContext.getResource(path) != null) {
                    result = buildResult(path, resultCode, resultsByExtension.get(ext), actionContext);
                    break;
                }
            } catch (MalformedURLException e) {
                LOG.warn("Unable to parse template path: "+path+", skipping...");
            }
            
            path = string(pathPrefix, actionName, "." , ext);
            try {
                if (servletContext.getResource(path) != null) {
                    result = buildResult(path, resultCode, resultsByExtension.get(ext), actionContext);
                    break;
                }
            } catch (MalformedURLException e) {
                LOG.warn("Unable to parse template path: "+path+", skipping...");
            }
        }
        
        return result;
    }
    
    protected Result buildResult(String path, String resultCode, ResultTypeConfig config, ActionContext invocationContext) {
        String resultClass = config.getClazz();

        Map<String,String> params = new LinkedHashMap<String,String>();
        if (config.getParams() != null) {
            params.putAll(config.getParams());
        }
        params.put(config.getDefaultResultParam(), path);

        ResultConfig resultConfig = new ResultConfig(resultCode, resultClass, params);
        try {
            return objectFactory.buildResult(resultConfig, invocationContext.getContextMap());
        } catch (Exception e) {
            throw new XWorkException("Unable to build codebehind result", e, resultConfig);
        }
    }

    protected String string(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part);
        }
        return sb.toString();
    }
    
    protected String determinePath(String prefix, String ns) {
        if (ns == null || "/".equals(ns)) {
            ns = "";
        }
        if (ns.length() > 0) {
            if (ns.charAt(0) == '/') {
                ns = ns.substring(1);
            }
            if (ns.charAt(ns.length() - 1) != '/') {
                ns += "/";
            }
        }
        return prefix + ns;
    }

}
