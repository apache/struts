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

package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.util.ValueStack;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


/**
 * Simple Hash model that also searches other scopes.
 * <p/>
 * If the key doesn't exist in this hash, this template model tries to
 * resolve the key within the attributes of the following scopes,
 * in the order stated: Request, Session, Servlet Context
 *
 * Updated to subclass AllHttpScopesHashModel.java to incorporate invisible scopes and compatibility with freemarker.
 */
public class ScopesHashModel extends SimpleHash implements TemplateModel {

    private static final long serialVersionUID = 5551686380141886764L;

    private HttpServletRequest request;
    private ServletContext servletContext;
    private ValueStack stack;
    private final Map<String,TemplateModel> unlistedModels = new HashMap<String,TemplateModel>();
    private volatile Object parametersCache;

    public ScopesHashModel(ObjectWrapper objectWrapper, ServletContext context, HttpServletRequest request, ValueStack stack) {
        super(objectWrapper);
        this.servletContext = context;
        this.request = request;
        this.stack = stack;
    }

    // This constructor is for Freemarker Sitemesh integration where the model is somehow lost...
    public ScopesHashModel(ObjectWrapper objectWrapper, ServletContext context, HttpServletRequest request) {
         super(objectWrapper);
         this.servletContext = context;
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

    public TemplateModel get(String key) throws TemplateModelException {
        // Lookup in default scope
        TemplateModel model = super.get(key);

        if (model != null) {
            return model;
        }


        if (stack != null) {
            Object obj = findValueOnStack(key);

            if (obj != null) {
                return wrap(obj);
            }

            // ok, then try the context
            obj = stack.getContext().get(key);
            if (obj != null) {
                return wrap(obj);
            }
        }

        if (request != null) {
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
        }

        if (servletContext != null) {
            // Lookup in application scope
            Object obj = servletContext.getAttribute(key);

            if (obj != null) {
                return wrap(obj);
            }
        }

        // Look in unlisted models
        model = unlistedModels.get(key);
        if(model != null) {
            return wrap(model);
        }


        return null;
    }

    private Object findValueOnStack(final String key) {
        if ("parameters".equals(key)) {
            if (parametersCache != null) {
                return parametersCache;
            }
            Object parametersLocal = stack.findValue(key);
            parametersCache = parametersLocal;
            return parametersLocal;
        }
        return stack.findValue(key);
    }

    public void put(String string, boolean b) {
        super.put(string, b);
    }

    public void put(String string, Object object) {
        super.put(string, object);
    }
}
