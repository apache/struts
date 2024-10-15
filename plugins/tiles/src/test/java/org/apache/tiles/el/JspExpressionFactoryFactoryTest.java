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
import org.junit.Test;

import jakarta.el.ExpressionFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.jsp.JspApplicationContext;
import jakarta.servlet.jsp.JspFactory;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link JspExpressionFactoryFactory}.
 */
public class JspExpressionFactoryFactoryTest {

    /**
     * Test method for {@link JspExpressionFactoryFactory#getExpressionFactory()}.
     */
    @Test
    public void testGetExpressionFactory() {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        ServletContext servletContext = createMock(ServletContext.class);
        JspFactory jspFactory = createMock(JspFactory.class);
        JspApplicationContext jspApplicationContext = createMock(JspApplicationContext.class);
        ExpressionFactory expressionFactory = createMock(ExpressionFactory.class);

        expect(applicationContext.getContext()).andReturn(servletContext);
        expect(jspFactory.getJspApplicationContext(servletContext)).andReturn(jspApplicationContext);
        expect(jspApplicationContext.getExpressionFactory()).andReturn(expressionFactory);

        replay(applicationContext, servletContext, jspFactory,
            jspApplicationContext, expressionFactory);
        JspFactory.setDefaultFactory(jspFactory);
        JspExpressionFactoryFactory factory = new JspExpressionFactoryFactory();
        factory.setApplicationContext(applicationContext);
        assertEquals(expressionFactory, factory.getExpressionFactory());
        verify(applicationContext, servletContext, jspFactory,
            jspApplicationContext, expressionFactory);
    }

    /**
     * Test method for {@link JspExpressionFactoryFactory#getExpressionFactory()}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetApplicationContextIllegal() {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Integer servletContext = 1;

        expect(applicationContext.getContext()).andReturn(servletContext);

        replay(applicationContext);
        try {
            JspExpressionFactoryFactory factory = new JspExpressionFactoryFactory();
            factory.setApplicationContext(applicationContext);
        } finally {
            verify(applicationContext);
        }
    }

}
