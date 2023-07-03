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

package org.apache.struts2.freemarker.ext.jsp;

import freemarker.log.Logger;
import freemarker.template.utility.ClassUtil;

import jakarta.el.ArrayELResolver;
import jakarta.el.BeanELResolver;
import jakarta.el.CompositeELResolver;
import jakarta.el.ELContext;
import jakarta.el.ELContextEvent;
import jakarta.el.ELContextListener;
import jakarta.el.ELResolver;
import jakarta.el.ExpressionFactory;
import jakarta.el.FunctionMapper;
import jakarta.el.ListELResolver;
import jakarta.el.MapELResolver;
import jakarta.el.ResourceBundleELResolver;
import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import jakarta.servlet.jsp.JspApplicationContext;
import jakarta.servlet.jsp.el.ImplicitObjectELResolver;
import jakarta.servlet.jsp.el.ScopedAttributeELResolver;
import java.util.Iterator;
import java.util.LinkedList;

/**
 */
class FreeMarkerJspApplicationContext implements JspApplicationContext {
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");
    private static final ExpressionFactory expressionFactoryImpl = findExpressionFactoryImplementation();
    
    private final LinkedList listeners = new LinkedList();
    private final CompositeELResolver elResolver = new CompositeELResolver();
    private final CompositeELResolver additionalResolvers = new CompositeELResolver();
    {
        elResolver.add(new ImplicitObjectELResolver());
        elResolver.add(additionalResolvers);
        elResolver.add(new MapELResolver());
        elResolver.add(new ResourceBundleELResolver());
        elResolver.add(new ListELResolver());
        elResolver.add(new ArrayELResolver());
        elResolver.add(new BeanELResolver());
        elResolver.add(new ScopedAttributeELResolver());
    }
    
    @Override
    public void addELContextListener(ELContextListener listener) {
        synchronized (listeners) {
            listeners.addLast(listener);
        }
    }

    private static ExpressionFactory findExpressionFactoryImplementation() {
        ExpressionFactory ef = tryExpressionFactoryImplementation("com.sun");
        if (ef == null) {
            ef = tryExpressionFactoryImplementation("org.apache");
            if (ef == null) {
                LOG.warn("Could not find any implementation for " + 
                        ExpressionFactory.class.getName());
            }
        }
        return ef;
    }

    private static ExpressionFactory tryExpressionFactoryImplementation(String packagePrefix) {
        String className = packagePrefix + ".el.ExpressionFactoryImpl";
        try {
            Class cl = ClassUtil.forName(className);
            if (ExpressionFactory.class.isAssignableFrom(cl)) {
                LOG.info("Using " + className + " as implementation of " + 
                        ExpressionFactory.class.getName());
                return (ExpressionFactory) cl.newInstance();
            }
            LOG.warn("Class " + className + " does not implement " + 
                    ExpressionFactory.class.getName());
        } catch (ClassNotFoundException e) {
        } catch (Exception e) {
            LOG.error("Failed to instantiate " + className, e);
        }
        return null;
    }

    @Override
    public void addELResolver(ELResolver resolver) {
        additionalResolvers.add(resolver);
    }

    @Override
    public ExpressionFactory getExpressionFactory() {
        return expressionFactoryImpl;
    }
    
    ELContext createNewELContext(final FreeMarkerPageContext pageCtx) {
        ELContext ctx = new FreeMarkerELContext(pageCtx);
        ELContextEvent event = new ELContextEvent(ctx);
        synchronized (listeners) {
            for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
                ELContextListener l = (ELContextListener) iter.next();
                l.contextCreated(event);
            }
        }
        return ctx;
    }

    private class FreeMarkerELContext extends ELContext {
        private final FreeMarkerPageContext pageCtx;
        
        FreeMarkerELContext(FreeMarkerPageContext pageCtx) {
            this.pageCtx = pageCtx;
        }
        
        @Override
        public ELResolver getELResolver() {
            return elResolver;
        }

        @Override
        public FunctionMapper getFunctionMapper() {
            return null;
        }

        @Override
        public VariableMapper getVariableMapper() {
            return new VariableMapper() {
                @Override
                public ValueExpression resolveVariable(String name) {
                    Object obj = pageCtx.findAttribute(name);
                    if (obj == null) {
                        return null;
                    }
                    return expressionFactoryImpl.createValueExpression(obj, 
                            obj.getClass());
                }

                @Override
                public ValueExpression setVariable(String name, 
                        ValueExpression value) {
                    ValueExpression prev = resolveVariable(name);
                    pageCtx.setAttribute(name, value.getValue(
                            FreeMarkerELContext.this));
                    return prev;
                }
            };
        }
    }
}