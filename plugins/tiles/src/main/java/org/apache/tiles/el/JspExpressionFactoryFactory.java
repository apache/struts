/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.el;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationContextAware;

import jakarta.el.ExpressionFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.jsp.JspFactory;

/**
 * Uses the JSP 2.1 {@link ExpressionFactory} to be used in Tiles.
 *
 * @since 2.2.1
 */
public class JspExpressionFactoryFactory implements ExpressionFactoryFactory, ApplicationContextAware {

    /**
     * The servlet context.
     *
     * @since 2.2.1
     */
    protected ServletContext servletContext;

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        Object context = applicationContext.getContext();
        if (context instanceof ServletContext) {
            this.servletContext = (ServletContext) context;
        } else {
            throw new IllegalArgumentException(
                "The application context does not hold an instance of "
                    + "ServletContext, consider using JuelExpressionFactoryFactory");
        }
    }

    /**
     * {@inheritDoc}
     */
    public ExpressionFactory getExpressionFactory() {
        return JspFactory.getDefaultFactory().getJspApplicationContext(
            servletContext).getExpressionFactory();
    }
}
