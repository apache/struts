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
package org.apache.tiles.web.jsp.taglib;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.jsp.JspRequest;
import org.apache.tiles.web.jsp.taglib.UseAttributeTag;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests UseAttributeTag.
 */
public class UseAttributeTagTest {

    /**
     * The tag to test.
     */
    private UseAttributeTag tag;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        tag = new UseAttributeTag();
    }

    /**
     * Test method for
     * {@link org/apache/tiles/web/jsp/taglib.UseAttributeTag#execute(org.apache.tiles.request.Request)}.
     * 
     * @throws IOException
     * @throws JspException
     */
    @Test
    public void testExecute() throws JspException, IOException {
        JspFragment jspBody = createMock(JspFragment.class);
        PageContext pageContext = createMock(PageContext.class);
        JspTag parent = createMock(JspTag.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        HttpServletRequest httpServletRequest = createMock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = createMock(HttpServletResponse.class);
        Map<String, Object> applicationScope = createMock(Map.class);
        TilesContainer container = createMock(TilesContainer.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Attribute attribute = createMock(Attribute.class);
        expect(pageContext.getAttribute(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE, PageContext.APPLICATION_SCOPE))
                .andReturn(applicationContext);
        expect(applicationContext.getApplicationScope()).andReturn(applicationScope).anyTimes();
        expect(pageContext.getRequest()).andReturn(httpServletRequest);
        expect(pageContext.getResponse()).andReturn(httpServletResponse);
        expect(pageContext.getAttribute(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, PageContext.REQUEST_SCOPE))
                .andReturn(container);
        expect(container.getAttributeContext(isA(JspRequest.class))).andReturn(attributeContext);
        expect(attributeContext.getAttribute("name")).andReturn(attribute);
        expect(container.evaluate(isA(Attribute.class), isA(JspRequest.class))).andReturn(new Integer(1));
        pageContext.setAttribute("id", new Integer(1), PageContext.PAGE_SCOPE);
        replay(jspBody, pageContext, parent, applicationContext, httpServletRequest, httpServletResponse,
                applicationScope, container, attributeContext, attribute);
        tag.setName("name");
        tag.setScope("page");
        tag.setId("id");
        tag.setIgnore(false);
        tag.setJspContext(pageContext);
        tag.setJspBody(jspBody);
        tag.setParent(parent);
        tag.doTag();
        verify(jspBody, pageContext, parent, applicationContext, httpServletRequest, httpServletResponse, container,
                attributeContext, attribute);
    }

    /**
     * Test method for
     * {@link org/apache/tiles/web/jsp/taglib.UseAttributeTag#setId(java.lang.String)}.
     */
    @Test
    public void testSetId() {
        tag.setId("id");
        assertEquals("id", tag.getId());
    }

    /**
     * Test method for
     * {@link org/apache/tiles/web/jsp/taglib.UseAttributeTag#getScope()}.
     */
    @Test
    public void testGetScope() {
        tag.setScope("scope");
        assertEquals("scope", tag.getScope());
    }

    /**
     * Test method for
     * {@link org/apache/tiles/web/jsp/taglib.UseAttributeTag#setName(java.lang.String)}.
     */
    @Test
    public void testSetName() {
        tag.setName("name");
        assertEquals("name", tag.getName());
    }

    /**
     * Test method for
     * {@link org/apache/tiles/web/jsp/taglib.UseAttributeTag#setIgnore(boolean)}.
     */
    @Test
    public void testSetIgnore() {
        tag.setIgnore(true);
        assertTrue(tag.isIgnore());
    }

    /**
     * Test method for
     * {@link org/apache/tiles/web/jsp/taglib.UseAttributeTag#setClassname(java.lang.String)}.
     */
    @Test
    public void testSetClassname() {
        tag.setClassname("classname");
        assertEquals("classname", tag.getClassname());
    }

    /**
     * Test method for
     * {@link org/apache/tiles/web/jsp/taglib.UseAttributeTag#getScriptingVariable()}.
     */
    @Test
    public void testGetScriptingVariable() {
        tag.setName("name");
        assertEquals("name", tag.getScriptingVariable());
        tag.setId("id");
        assertEquals("id", tag.getScriptingVariable());
    }

    /**
     * Tests {@link UseAttributeTag.Tei}.
     */
    @Test
    public void testTei() {
        TagData tagData = createMock(TagData.class);

        expect(tagData.getAttributeString("classname")).andReturn("my.Clazz");
        expect(tagData.getAttributeString("id")).andReturn("id");

        replay(tagData);
        UseAttributeTag.Tei tei = new UseAttributeTag.Tei();
        VariableInfo[] infos = tei.getVariableInfo(tagData);
        assertEquals(1, infos.length);
        VariableInfo info = infos[0];
        assertEquals("id", info.getVarName());
        assertEquals("my.Clazz", info.getClassName());
        assertTrue(info.getDeclare());
        assertEquals(VariableInfo.AT_END, info.getScope());
        verify(tagData);
    }

    /**
     * Tests {@link UseAttributeTag.Tei}.
     */
    @Test
    public void testTeiDefaults() {
        TagData tagData = createMock(TagData.class);

        expect(tagData.getAttributeString("classname")).andReturn(null);
        expect(tagData.getAttributeString("id")).andReturn(null);
        expect(tagData.getAttributeString("name")).andReturn("name");

        replay(tagData);
        UseAttributeTag.Tei tei = new UseAttributeTag.Tei();
        VariableInfo[] infos = tei.getVariableInfo(tagData);
        assertEquals(1, infos.length);
        VariableInfo info = infos[0];
        assertEquals("name", info.getVarName());
        assertEquals("java.lang.Object", info.getClassName());
        assertTrue(info.getDeclare());
        assertEquals(VariableInfo.AT_END, info.getScope());
        verify(tagData);
    }
}
