/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.freemarker;

import com.opensymphony.xwork.util.OgnlValueStack;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Simple Hash model that also searches other scopes.
 * <p/>
 * If the key doesn't exist in this hash, this template model tries to
 * resolve the key within the attributes of the following scopes,
 * in the order stated: Request, Session, Servlet Context
 */
public class ScopesHashModel extends SimpleHash {

    private HttpServletRequest request;
    private ObjectWrapper objectWraper;
    private ServletContext servletContext;
    private OgnlValueStack stack;


    public ScopesHashModel(ObjectWrapper objectWrapper, ServletContext context, HttpServletRequest request, OgnlValueStack stack) {
        super(objectWrapper);
        this.servletContext = context;
        this.request = request;
        this.stack = stack;
    }


    public TemplateModel get(String key) throws TemplateModelException {
        // Lookup in default scope
        TemplateModel model = super.get(key);

        if (model != null) {
            return model;
        }


        if (stack != null) {
            Object obj = stack.findValue(key);

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

        return null;
    }

    public void put(String string, boolean b) {
        super.put(string, b);
    }

    public void put(String string, Object object) {
        super.put(string, object);
    }
}
