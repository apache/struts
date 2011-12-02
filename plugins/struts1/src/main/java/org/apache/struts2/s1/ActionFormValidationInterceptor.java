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

package org.apache.struts2.s1;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.ValidatorResources;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.validator.ValidatorPlugIn;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.xml.sax.SAXException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.ScopedModelDriven;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 *  Calls the validate() method on the ActionForm, if it exists.  The errors are handled
 *  like regular XWork validation errors.  Action-level validation flag supported if the
 *  action is a subclass of Struts1Action.
 */
public class ActionFormValidationInterceptor extends AbstractInterceptor {

    private String pathnames;
    private boolean stopOnFirstError;
    private boolean initialized = false;
    
    private static final Logger LOG = LoggerFactory.getLogger(ActionFormValidationInterceptor.class);
    
    /**
     * Delimitter for Validator resources.
     */
    private final static String RESOURCE_DELIM = ",";
    
    protected Configuration configuration;

    @Inject
    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }
    
    /**
     * Initializes the validation resources
     */
    private void initResources(ServletContext servletContext) {
        if (pathnames != null) {
            ActionContext ctx = ActionContext.getContext();
            try {
                
                ValidatorResources resources = this.loadResources(servletContext);
    
                
                String prefix = ctx.getActionInvocation().getProxy().getNamespace();
                
                
                servletContext.setAttribute(ValidatorPlugIn.VALIDATOR_KEY + prefix, resources);
    
                servletContext.setAttribute(ValidatorPlugIn.STOP_ON_ERROR_KEY + '.'
                    + prefix,
                    (this.stopOnFirstError ? Boolean.TRUE : Boolean.FALSE));
            } catch (Exception e) {
                throw new StrutsException(
                    "Cannot load a validator resource from '" + pathnames + "'", e);
            }
        }
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        // Lazy load the resources because the servlet context isn't available at init() time
        synchronized (this) {
            if (!initialized) {
                initResources(ServletActionContext.getServletContext());
                initialized = true;
            }
        }
        Object action = invocation.getAction();

        
        if ((action instanceof ScopedModelDriven) &&
            (!(action instanceof Struts1Action) || ((Struts1Action)action).isValidate())) {
            ScopedModelDriven modelDriven = (ScopedModelDriven) action;
            Object model = modelDriven.getModel();
            if (model != null) {
                HttpServletRequest req = ServletActionContext.getRequest();
                Struts1Factory strutsFactory = new Struts1Factory(configuration);
                ActionMapping mapping = strutsFactory.createActionMapping(invocation.getProxy().getConfig());
                ModuleConfig moduleConfig = strutsFactory.createModuleConfig(invocation.getProxy().getConfig().getPackageName());
                req.setAttribute(Globals.MODULE_KEY, moduleConfig);
                req.setAttribute(Globals.MESSAGES_KEY, new WrapperMessageResources((TextProvider)invocation.getAction()));
                
                mapping.setAttribute(modelDriven.getScopeKey());
                
                ActionForm form = (ActionForm) model;
                form.setServlet(new ActionServlet(){
                    public ServletContext getServletContext() {
                        return ServletActionContext.getServletContext();
                    }
                });
                ActionErrors errors = form.validate(mapping, req);
                strutsFactory.convertErrors(errors, action);                
            }
        }
        return invocation.invoke();
    }
    
    /**
     * Initialize the validator resources for this module.
     *
     * @throws IOException      if an input/output error is encountered
     * @throws ServletException if we cannot initialize these resources
     */
    protected ValidatorResources loadResources(ServletContext ctx)
        throws IOException, ServletException {
        if ((pathnames == null) || (pathnames.length() <= 0)) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(pathnames, RESOURCE_DELIM);

        List urlList = new ArrayList();
        ValidatorResources resources = null;
        try {
            while (st.hasMoreTokens()) {
                String validatorRules = st.nextToken().trim();

                if (LOG.isInfoEnabled()) {
                    LOG.info("Loading validation rules file from '"
                        + validatorRules + "'");
                }

                URL input =
                    ctx.getResource(validatorRules);

                // If the config isn't in the servlet context, try the class
                // loader which allows the config files to be stored in a jar
                if (input == null) {
                    input = getClass().getResource(validatorRules);
                }

                if (input != null) {
                    urlList.add(input);
                } else {
                    throw new ServletException(
                        "Skipping validation rules file from '"
                        + validatorRules + "'.  No url could be located.");
                }
            }

            int urlSize = urlList.size();
            String[] urlArray = new String[urlSize];

            for (int urlIndex = 0; urlIndex < urlSize; urlIndex++) {
                URL url = (URL) urlList.get(urlIndex);

                urlArray[urlIndex] = url.toExternalForm();
            }

            resources =  new ValidatorResources(urlArray);
        } catch (SAXException sex) {
            LOG.error("Skipping all validation", sex);
            throw new StrutsException("Skipping all validation because the validation files cannot be loaded", sex);
        }
        return resources;
    }

    /**
     * @return the pathnames
     */
    public String getPathnames() {
        return pathnames;
    }

    /**
     * @param pathNames the pathnames to set
     */
    public void setPathnames(String pathNames) {
        this.pathnames = pathNames;
    }

    /**
     * @return the stopOnFirstError
     */
    public boolean isStopOnFirstError() {
        return stopOnFirstError;
    }

    /**
     * @param stopOnFirstError the stopOnFirstError to set
     */
    public void setStopOnFirstError(boolean stopOnFirstError) {
        this.stopOnFirstError = stopOnFirstError;
    }

}
