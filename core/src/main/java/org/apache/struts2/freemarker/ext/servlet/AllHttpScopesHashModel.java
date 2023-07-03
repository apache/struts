/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.freemarker.ext.servlet;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.NullArgumentException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * An extension of SimpleHash that looks up keys in the hash, then in the
 * request, session, and servlet context scopes. Makes "Application", "Session"
 * and "Request" keys largely obsolete, however we keep them for backward
 * compatibility (also, "Request" is required for proper operation of JSP
 * taglibs).
 * It is on purpose that we didn't override <tt>keys</tt> and <tt>values</tt>
 * methods. That way, only those variables assigned into the hash directly by a
 * subclass of <tt>FreemarkerServlet</tt> that overrides
 * <tt>preTemplateProcess</tt>) are discovered as "page" variables by the FM
 * JSP PageContext implementation.
 */
public class AllHttpScopesHashModel extends SimpleHash {
    private final ServletContext context;
    private final HttpServletRequest request;
    private final Map unlistedModels = new HashMap();
     
    /**
     * Creates a new instance of AllHttpScopesHashModel for handling a single 
     * HTTP servlet request.
     * @param objectWrapper the object wrapper to use; not {@code null}.
     * @param context the servlet context of the web application
     * @param request the HTTP servlet request being processed
     */
    public AllHttpScopesHashModel(ObjectWrapper objectWrapper,
            ServletContext context, HttpServletRequest request) {
        super(objectWrapper);
        NullArgumentException.check("wrapper", objectWrapper);
        this.context = context;
        this.request = request;
    }
    
    /**
     * Stores a model in the hash so that it doesn't show up in <tt>keys()</tt>
     * and <tt>values()</tt> methods. Used to put the Application, Session,
     * Request, RequestParameters and JspTaglibs objects.
     * @param key the key under which the model is stored
     * @param model the stored model
     */
    public void putUnlistedModel(String key, TemplateModel model) {
        unlistedModels.put(key, model);
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        // Lookup in page scope
        TemplateModel model = super.get(key);
        if (model != null) {
            return model;
        }

        // Look in unlisted models
        model = (TemplateModel) unlistedModels.get(key);
        if (model != null) {
            return model;
        }
        
        // Lookup in request scope
        Object obj = request.getAttribute(key);
        if (obj != null) {
            return wrap(obj);
        }

        // Lookup in session scope
        HttpSession session = request.getSession(false);
        if (session != null) {
            obj = session.getAttribute(key);
            if (obj != null) {
                return wrap(obj);
            }
        }

        // Lookup in application scope
        obj = context.getAttribute(key);
        if (obj != null) {
            return wrap(obj);
        }

        // return wrapper's null object (probably null).        
        return wrap(null);
    }
}
