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
package org.apache.struts2.views.jasperreports;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import net.sf.jasperreports.engine.JasperCompileManager;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.easymock.IAnswer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import java.net.URL;
import java.sql.Connection;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class JasperReportsResultTest extends StrutsTestCase {
    private MockActionInvocation invocation;
    private ValueStack stack;

    public void testConnClose() throws Exception {
        JasperReportsResult result = new JasperReportsResult();
        URL url = ClassLoaderUtil.getResource("org/apache/struts2/views/jasperreports/empty.jrxml", this.getClass());
        JasperCompileManager.compileReportToFile(url.getFile(), url.getFile() + ".jasper");
        result.setLocation("org/apache/struts2/views/jasperreports/empty.jrxml.jasper");
        result.setFormat(JasperReportConstants.FORMAT_XML);

        Connection connection = createMock(Connection.class);
        final Boolean[] closed = {false};
        connection.close();
        expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() throws Throwable {
                closed[0] = true;
                return null;
            }
        });
        replay(connection);

        stack.push(connection);
        result.setConnection("top");

        assertFalse(closed[0]);
        result.execute(this.invocation);
        verify(connection);
        assertTrue(closed[0]);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://sumeruri");
        ActionContext context = ActionContext.getContext();
        context.put(StrutsStatics.HTTP_RESPONSE, response);
        context.put(StrutsStatics.HTTP_REQUEST, request);
        this.stack = context.getValueStack();
        MockServletContext servletContext = new MockServletContext();
        context.put(StrutsStatics.SERVLET_CONTEXT, servletContext);
        this.invocation = new MockActionInvocation();
        this.invocation.setInvocationContext(context);
        this.invocation.setStack(this.stack);
    }
}
