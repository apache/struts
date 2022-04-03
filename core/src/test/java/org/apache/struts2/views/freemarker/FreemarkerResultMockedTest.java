/*
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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.jsp.StrutsMockHttpServletResponse;
import org.easymock.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.struts2.views.jsp.AbstractUITagTest.normalize;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class FreemarkerResultMockedTest extends StrutsInternalTestCase {

    ValueStack stack;
    MockActionInvocation invocation;
    ActionContext context;
    StrutsMockHttpServletResponse response;
    PrintWriter writer;
    StringWriter stringWriter;
    ServletContext servletContext;
    MockHttpServletRequest request;

    Configuration freemarkerConfig;

    public void testActionThatThrowsExceptionTag() throws Exception {
        File file = new File(FreeMarkerResultTest.class.getResource("callActionFreeMarker2.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/callActionFreeMarker.ftl")).andReturn(file.getAbsolutePath());
        file = new File(FreeMarkerResultTest.class.getResource("nested.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/nested.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.replay(servletContext);

        init();

        request.setRequestURI("/tutorial/test2.action");
        ActionMapping mapping = container.getInstance(ActionMapper.class).getMapping(request, configurationManager);
        dispatcher.serviceAction(request, response, mapping);
        assertEquals("beforenestedafter", stringWriter.toString());
    }

    public void testActionThatSucceedsTag() throws Exception {
        File file = new File(FreeMarkerResultTest.class.getResource("callActionFreeMarker2.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/callActionFreeMarker2.ftl")).andReturn(file.getAbsolutePath());
        file = new File(FreeMarkerResultTest.class.getResource("nested.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/nested.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.replay(servletContext);

        init();

        request.setRequestURI("/tutorial/test5.action");
        ActionMapping mapping = container.getInstance(ActionMapper.class).getMapping(request, configurationManager);
        dispatcher.serviceAction(request, response, mapping);
        assertEquals("beforenestedafter", stringWriter.toString());
    }

    public void testDynamicAttributesSupport() throws Exception {
        File file = new File(FreeMarkerResultTest.class.getResource("dynaAttributes.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/dynaAttributes.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/text.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/text.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/css.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/css.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/css.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/scripting-events.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/scripting-events.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/scripting-events.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/common-attributes.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/common-attributes.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/common-attributes.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/dynamic-attributes.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/dynamic-attributes.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/dynamic-attributes.ftl")).andReturn(file.getAbsolutePath());

        EasyMock.replay(servletContext);

        init();

        request.setRequestURI("/tutorial/test6.action");
        ActionMapping mapping = container.getInstance(ActionMapper.class).getMapping(request, configurationManager);
        dispatcher.serviceAction(request, response, mapping);

        String result = stringWriter.toString();
        assertThat(result, allOf(startsWith("<input type=\"text\" name=\"test\" value=\"\" id=\"test\""),
                                 containsString("foo=\"bar\""),
                                 containsString("placeholder=\"input\""),
                                 endsWith("<input type=\"text\" name=\"test\" value=\"\" id=\"test\" break=\"true\"/>"
                                        + "<input type=\"text\" name=\"required\" value=\"\" id=\"required\" required=\"true\"/>")));
    }

    public void testManualListInTemplate() throws Exception {
        File file = new File(FreeMarkerResultTest.class.getResource("manual-list.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/manual-list.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/radiomap.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/radiomap.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/css.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/css.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/css.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/scripting-events.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/scripting-events.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/scripting-events.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/common-attributes.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/common-attributes.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/common-attributes.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/dynamic-attributes.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/dynamic-attributes.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/dynamic-attributes.ftl")).andReturn(file.getAbsolutePath());

        EasyMock.replay(servletContext);

        init();

        request.setRequestURI("/tutorial/test7.action");
        ActionMapping mapping = container.getInstance(ActionMapper.class).getMapping(request, configurationManager);
        dispatcher.serviceAction(request, response, mapping);
        String expected = "<input type=\"radio\" name=\"client\" id=\"client_foo\" value=\"foo\"/><label for=\"client_foo\">foo</label>\n"
            + "<input type=\"radio\" name=\"client\" id=\"client_bar\" value=\"bar\"/><label for=\"client_bar\">bar</label>\n"
            + "\n"
            + "<input type=\"radio\" name=\"car\" id=\"carford\" value=\"ford\"/><label for=\"carford\">Ford Motor Co</label>\n"
            + "<input type=\"radio\" name=\"car\" id=\"cartoyota\" value=\"toyota\"/><label for=\"cartoyota\">Toyota</label>\n";
        assertEquals(normalize(expected), normalize(stringWriter.toString()));
    }

    public void testDynamicAttributesInTheme() throws Exception {
        File file = new File(FreeMarkerResultTest.class.getResource("customTextField.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/customTextField.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/test/text.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/test/text.ftl")).andReturn(file.getAbsolutePath());

        EasyMock.replay(servletContext);

        init();

        request.setRequestURI("/tutorial/test8.action");
        ActionMapping mapping = container.getInstance(ActionMapper.class).getMapping(request, configurationManager);
        dispatcher.serviceAction(request, response, mapping);
        String expected = "<input type=\"text\"autofocus=\"autofocus\"/>";
        assertEquals(expected, stringWriter.toString());
    }

    public void testSequenceForSelect() throws Exception {
        File file = new File(FreeMarkerResultTest.class.getResource("select.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/select.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/select.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/select.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/select.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/optgroup.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/optgroup.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/optgroup.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/css.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/css.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/css.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/scripting-events.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/scripting-events.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/scripting-events.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/common-attributes.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/common-attributes.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/common-attributes.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/dynamic-attributes.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/dynamic-attributes.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/dynamic-attributes.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/empty.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/empty.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/empty.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/xhtml/empty.ftl")).andReturn(file.getAbsolutePath());

        EasyMock.replay(servletContext);

        init();

        request.setRequestURI("/tutorial/test9.action");
        ActionMapping mapping = container.getInstance(ActionMapper.class).getMapping(request, configurationManager);
        dispatcher.serviceAction(request, response, mapping);
        String result = stringWriter.toString();
        assertTrue(result.contains("<option value=\"a\">a</option>"));
        assertTrue(result.contains("<option value=\"1\">1</option>"));
        assertTrue(result.contains("<option value=\"key\">value</option>"));
        assertTrue(result.contains("<option value=\"optgroupKey1\">optgroupValue1</option>"));
        assertTrue(result.contains("<option value=\"optgroupKey3\">optgroupKey3</option>"));
        assertTrue(result.contains("<option value=\"2\">2</option>"));
    }

    public void testNonce() throws Exception {
        File file = new File(ClassLoaderUtil.getResource("template/simple/common-attributes.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/xhtml/common-attributes.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~xhtml/common-attributes.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/dynamic-attributes.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/xhtml/dynamic-attributes.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~xhtml/dynamic-attributes.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/nonce.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/nonce.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/script.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/script.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/script-close.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/script-close.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/link.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/link.ftl")).andReturn(file.getAbsolutePath());

        file = new File(FreeMarkerResultTest.class.getResource("nonceTest.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/nonceTest.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.replay(servletContext);

        init();

        request.setRequestURI("/tutorial/test10.action");
        ActionMapping mapping = container.getInstance(ActionMapper.class).getMapping(request, configurationManager);
        dispatcher.serviceAction(request, response, mapping);

        assertTrue(stringWriter.toString().contains("<link nonce=\""));
        assertTrue(stringWriter.toString().contains("<script nonce=\""));
    }

    public void testIterator() throws Exception {
        File file = new File(FreeMarkerResultTest.class.getResource("iterator.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/iterator.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/text.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/text.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/css.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/css.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/css.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/scripting-events.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/scripting-events.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/scripting-events.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/common-attributes.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/common-attributes.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/common-attributes.ftl")).andReturn(file.getAbsolutePath());

        file = new File(ClassLoaderUtil.getResource("template/simple/dynamic-attributes.ftl", getClass()).toURI());
        EasyMock.expect(servletContext.getRealPath("/template/simple/dynamic-attributes.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getRealPath("/template/~~~simple/dynamic-attributes.ftl")).andReturn(file.getAbsolutePath());

        EasyMock.replay(servletContext);

        init();

        stack.push(new Object() {
            List<Object> items = null;

            public List<Object> getItems() {
                if (items == null) {
                    items = new ArrayList<>(3);
                    for (int i = 0; i < 3; i++) {
                        int finalI = i;
                        items.add(new Object() {
                            public String getName() {
                                return "value" + finalI;
                            }
                        });
                    }
                }
                return items;
            }
        });

        request.setRequestURI("/tutorial/test11.action");
        ActionMapping mapping = container.getInstance(ActionMapper.class).getMapping(request, configurationManager);
        dispatcher.serviceAction(request, response, mapping);
        String result = stringWriter.toString();
        for (int i = 0; i < 3; i++) {
            assertTrue(result.contains("id=\"itemId" + i + "\""));
            assertTrue(result.contains("name=\"items[" + i + "].name\""));
            assertTrue(result.contains("value=\"value" + i + "\""));
        }
    }

    private void init() {
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        response = new StrutsMockHttpServletResponse();
        response.setWriter(writer);
        request = new MockHttpServletRequest();
        stack = ActionContext.getContext().getValueStack();

        context = ActionContext.of(stack.getContext())
            .withServletResponse(response)
            .withServletRequest(request)
            .withServletContext(servletContext)
            .bind();

        servletContext.setAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY, null);

        invocation = new MockActionInvocation();
        invocation.setStack(stack);
        invocation.setInvocationContext(context);

        //get fm config to use it in mock servlet context
        FreemarkerManager freemarkerManager = container.getInstance(FreemarkerManager.class);

        freemarkerConfig = freemarkerManager.getConfiguration(ServletActionContext.getServletContext());
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfig.setServletContextForTemplateLoading(servletContext, null);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        servletContext = EasyMock.createNiceMock(ServletContext.class);
        EasyMock.expect(servletContext.getInitParameter("TemplatePath")).andReturn(null);
        EasyMock.expect(servletContext.getInitParameter("templatePath")).andReturn(null);

        EasyMock.expect(servletContext.getAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY)).andReturn(freemarkerConfig).anyTimes();
    }

}
