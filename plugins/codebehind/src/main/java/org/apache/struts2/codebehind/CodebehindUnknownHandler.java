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

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorLocator;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.providers.InterceptorBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Uses code-behind conventions to solve the two unknown problems.  
 */
public class CodebehindUnknownHandler implements UnknownHandler {

    protected String defaultPackageName;
    protected ServletContext servletContext;
    protected Map<String,ResultTypeConfig> resultsByExtension;
    protected String templatePathPrefix;
    protected Configuration configuration;
    protected ObjectFactory objectFactory;
    
    protected static final Logger LOG = LoggerFactory.getLogger(CodebehindUnknownHandler.class);

    @Inject
    public CodebehindUnknownHandler(@Inject("struts.codebehind.defaultPackage") String defaultPackage, 
                                    @Inject Configuration configuration) {

        this.configuration = configuration;
        this.defaultPackageName = defaultPackage;
        resultsByExtension = new LinkedHashMap<String,ResultTypeConfig>();
        PackageConfig parentPackage = configuration.getPackageConfig(defaultPackageName);
        if (parentPackage == null) {
            throw new ConfigurationException("Unknown parent package: "+parentPackage);
        }    
        Map<String,ResultTypeConfig> results = parentPackage.getAllResultTypeConfigs();
        
        resultsByExtension.put("jsp", results.get("dispatcher"));
        resultsByExtension.put("vm", results.get("velocity"));
        resultsByExtension.put("ftl", results.get("freemarker"));
       
    }                                

    @Inject("struts.codebehind.pathPrefix")
    public void setPathPrefix(String prefix) {
        this.templatePathPrefix=prefix;
    }

    @Inject
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }
    
    public ActionConfig handleUnknownAction(String namespace, String actionName)
            throws XWorkException {
        String pathPrefix = determinePath(templatePathPrefix, namespace);
        ActionConfig actionConfig = null;
        for (String ext : resultsByExtension.keySet()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to locate unknown action template with extension ."+ext+" in directory "+pathPrefix);
            }
            String path = string(pathPrefix, actionName, "." , ext);
            try {
                if (locateTemplate(path) != null) {
                    actionConfig = buildActionConfig(path, namespace, actionName, resultsByExtension.get(ext));
                    break;
                }
            } catch (MalformedURLException e) {
                LOG.warn("Unable to parse template path: "+path+", skipping...");
            }
        }
        return actionConfig;
    }

    /** Create a new ActionConfig in the default package, with the default interceptor stack and a single result */
    protected ActionConfig buildActionConfig(String path, String namespace, String actionName, ResultTypeConfig resultTypeConfig) {
        final PackageConfig pkg = configuration.getPackageConfig(defaultPackageName);
        return new ActionConfig.Builder(defaultPackageName, "execute", pkg.getDefaultClassRef())
                .addInterceptors(InterceptorBuilder.constructInterceptorReference(new InterceptorLocator() {
                    public Object getInterceptorConfig(String name) {
                        return pkg.getAllInterceptorConfigs().get(name); // recurse package hiearchy
                    }
                }, pkg.getFullDefaultInterceptorRef(),
                Collections.EMPTY_MAP, null, objectFactory))
                .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, resultTypeConfig.getClassName())
                        .addParams(resultTypeConfig.getParams())
                        .addParam(resultTypeConfig.getDefaultResultParam(), path)
                        .build())
                .build();
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
                if (locateTemplate(path) != null) {
                    result = buildResult(path, resultCode, resultsByExtension.get(ext), actionContext);
                    break;
                }
            } catch (MalformedURLException e) {
                LOG.warn("Unable to parse template path: "+path+", skipping...");
            }
            
            path = string(pathPrefix, actionName, "." , ext);
            try {
                if (locateTemplate(path) != null) {
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
        ResultConfig resultConfig = new ResultConfig.Builder(resultCode, config.getClassName())
            .addParams(config.getParams())
            .addParam(config.getDefaultResultParam(), path)
            .build();
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

    protected String joinPaths(boolean leadingSlash, boolean trailingSlash, String... parts) {
        StringBuilder sb = new StringBuilder();
        if (leadingSlash) {
            sb.append("/");
        }
        for (String part : parts) {
            if (sb.length() > 0 && sb.charAt(sb.length()-1) != '/') {
                sb.append("/");
            }
            sb.append(stripSlashes(part));
        }
        if (trailingSlash) {
            if (sb.length() > 0 && sb.charAt(sb.length()-1) != '/') {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    protected String determinePath(String prefix, String ns) {        
        return joinPaths(true, true, prefix, ns);
    }

    protected String stripLeadingSlash(String path) {
        String result;
        if (path != null) {
            if (path.length() > 0) {
                if (path.charAt(0) == '/') {
                    result = path.substring(1);
                } else {
                    result = path;
                }
            } else {
                result = path;
            }
        } else {
            result = "";
        }

        return result;
    }

    protected String stripTrailingSlash(String path) {
        String result;

        if (path != null) {
            if (path.length() > 0) {
                if (path.charAt(path.length() - 1) == '/') {
                    result = path.substring(0, path.length()-1);
                } else {
                    result = path;
                }
            } else {
                result = path;
            }
        } else {
            result = "";
        }

        return result;
    }

    protected String stripSlashes(String path) {
        return stripLeadingSlash(stripTrailingSlash(path));
    }

    URL locateTemplate(String path) throws MalformedURLException {
        URL template = servletContext.getResource(path);
        if (template != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Loaded template '" + path + "' from servlet context.");
            }
        } else {
            template = ClassLoaderUtil.getResource(stripLeadingSlash(path), getClass());
            if (template != null && LOG.isDebugEnabled()) {
                LOG.debug("Loaded template '" + stripLeadingSlash(path) + "' from class path.");
            }
        }
        return template;
    }


    /**
     * Not used
     */
	public Object handleUnknownActionMethod(Object action, String methodName) throws NoSuchMethodException {
		throw new NoSuchMethodException();
	}

}
