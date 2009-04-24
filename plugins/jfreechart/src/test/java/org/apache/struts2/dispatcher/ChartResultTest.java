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

import com.mockobjects.dynamic.Mock;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsTestCase;
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
    private Mock responseMock;
    private Mock mockActionProxy;
    private MockServletOutputStream os;
    private ValueStack stack;


    public void testChart() throws Exception {
        responseMock.expectAndReturn("getOutputStream", os);

        ChartResult result = new ChartResult();

        result.setChart(mockChart);

        result.setHeight("10");
        result.setWidth("10");
        result.execute(actionInvocation);

        responseMock.verify();
        assertTrue(os.isWritten());
    }

    public void testChartNotSet() {
        ChartResult result = new ChartResult();

        // expect exception if chart not set.
        result.setChart(null);

        try {
            result.execute(actionInvocation);
            fail();
        } catch (Exception e) {
        }

        responseMock.verify();
        assertFalse(os.isWritten());
    }


    public void testChartWithOGNLProperties() throws Exception {
        responseMock.expectAndReturn("getOutputStream", os);


        ChartResult result = new ChartResult();

        result.setChart(mockChart);

        result.setHeight("${myHeight}");
        result.setWidth("${myWidth}");

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.set("myHeight", 250);
        stack.set("myWidth", 150);

        result.execute(actionInvocation);

        responseMock.verify();
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


        mockActionProxy = new Mock(ActionProxy.class);
        mockActionProxy.expectAndReturn("getNamespace", "/html");

        Mock mockActionInvocation = new Mock(ActionInvocation.class);

        mockActionInvocation.matchAndReturn("getStack", stack);
//        mockActionInvocation.expectAndReturn("getProxy", mockActionProxy.proxy());
        
        actionInvocation = (ActionInvocation) mockActionInvocation.proxy();
        
        os = new MockServletOutputStream();
        responseMock = new Mock(HttpServletResponse.class);

        ServletActionContext.setResponse((HttpServletResponse) responseMock.proxy());
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
