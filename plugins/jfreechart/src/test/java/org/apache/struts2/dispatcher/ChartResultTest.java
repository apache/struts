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

package org.apache.struts2.dispatcher;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsTestCase;
import org.easymock.EasyMock;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.util.ValueStack;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 */
public class ChartResultTest extends StrutsTestCase {

    private ActionInvocation actionInvocation;
    private JFreeChart mockChart;
    private MockServletOutputStream os;
    private ValueStack stack;
    private ActionProxy mockActionProxy;
    private HttpServletResponse responseMock;


    public void testChart() throws Exception {
        EasyMock.expect(responseMock.getOutputStream()).andReturn(os);
        EasyMock.replay(responseMock, mockActionProxy, actionInvocation);

        ChartResult result = new ChartResult();

        result.setChart(mockChart);

        result.setHeight("10");
        result.setWidth("10");
        result.execute(actionInvocation);

        EasyMock.verify(responseMock);
        assertTrue(os.isWritten());
    }

    public void testContentTypePng() throws Exception {
        EasyMock.expect(responseMock.getOutputStream()).andReturn(os);
        responseMock.setContentType("image/png");
        EasyMock.replay(responseMock, mockActionProxy, actionInvocation);
        ChartResult result = new ChartResult();

        result.setChart(mockChart);

        result.setHeight("10");
        result.setWidth("10");
        result.setType("png");
        result.execute(actionInvocation);

        EasyMock.verify(responseMock);
        assertTrue(os.isWritten());
    }

    public void testContentTypeJpg() throws Exception {
        EasyMock.expect(responseMock.getOutputStream()).andReturn(os);
        responseMock.setContentType("image/jpg");
        EasyMock.replay(responseMock, mockActionProxy, actionInvocation);
        ChartResult result = new ChartResult();

        result.setChart(mockChart);

        result.setHeight("10");
        result.setWidth("10");
        result.setType("jpg");
        result.execute(actionInvocation);

        EasyMock.verify(responseMock);
        assertTrue(os.isWritten());
    }


    public void testChartNotSet() {
        ChartResult result = new ChartResult();
        EasyMock.replay(responseMock, mockActionProxy, actionInvocation);
        
        // expect exception if chart not set.
        result.setChart(null);

        try {
            result.execute(actionInvocation);
            fail();
        } catch (Exception e) {
        }

        EasyMock.verify(responseMock);
        assertFalse(os.isWritten());
    }


    public void testChartWithOGNLProperties() throws Exception {
        EasyMock.expect(responseMock.getOutputStream()).andReturn(os);
        EasyMock.replay(responseMock, mockActionProxy, actionInvocation);


        ChartResult result = new ChartResult();

        result.setChart(mockChart);

        result.setHeight("${myHeight}");
        result.setWidth("${myWidth}");

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.set("myHeight", 250);
        stack.set("myWidth", 150);

        result.execute(actionInvocation);

        EasyMock.verify(responseMock);
        assertEquals(result.getHeight(), stack.findValue("myHeight").toString());
        assertEquals(result.getWidth(), stack.findValue("myWidth").toString());
        assertEquals("250", result.getHeight().toString());
        assertEquals("150", result.getWidth().toString());
        assertTrue(os.isWritten());
    }
    
    protected void setUp() throws Exception {
        super.setUp();

        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Java", new Double(43.2));
        data.setValue("Visual Basic", new Double(0.0));
        data.setValue("C/C++", new Double(17.5));
        mockChart = ChartFactory.createPieChart("Pie Chart", data, true, true, false);


        stack = ActionContext.getContext().getValueStack();
        ActionContext.getContext().setValueStack(stack);


        mockActionProxy = EasyMock.createNiceMock(ActionProxy.class);
        EasyMock.expect(mockActionProxy.getNamespace()).andReturn("/html");

        actionInvocation = EasyMock.createMock(ActionInvocation.class);

        EasyMock.expect(actionInvocation.getStack()).andReturn(stack).anyTimes();
        
        
        os = new MockServletOutputStream();
        responseMock = EasyMock.createNiceMock(HttpServletResponse.class);

        ServletActionContext.setResponse((HttpServletResponse) responseMock);
    }

    protected void tearDown() throws Exception {
        actionInvocation = null;
        os = null;
        responseMock = null;
        stack = null;
        mockActionProxy = null;
    }


    private class MockServletOutputStream extends ServletOutputStream {
        // very simple check that outputStream was written to.
        private boolean written = false;

        /**
         * @return Returns the written.
         */
        public boolean isWritten() {
            return written;
        }

        public void write(int arg0) throws IOException {
            written = true;
        }
    }
}
