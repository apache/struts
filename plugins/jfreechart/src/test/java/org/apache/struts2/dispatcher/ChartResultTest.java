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
package org.apache.struts2.dispatcher;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.junit.StrutsTestCase;
import org.apache.struts2.util.ValueStack;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChartResultTest extends StrutsTestCase {

    private ChartResult result;

    private ActionInvocation actionInvocation;
    private JFreeChart mockChart;
    private MockServletOutputStream os;
    private HttpServletResponse responseMock;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        result = new ChartResult();

        var data = new DefaultPieDataset<String>();
        data.setValue("Java", Double.valueOf(43.2));
        data.setValue("Visual Basic", Double.valueOf(0.0));
        data.setValue("C/C++", Double.valueOf(17.5));
        mockChart = ChartFactory.createPieChart("Pie Chart", data, true, true, false);

        actionInvocation = mock(ActionInvocation.class);
        when(actionInvocation.getStack()).thenReturn(ActionContext.getContext().getValueStack());

        os = new MockServletOutputStream();
        responseMock = mock(HttpServletResponse.class);
        when(responseMock.getOutputStream()).thenReturn(os);

        ServletActionContext.setResponse(responseMock);
    }

    public void testChart() throws Exception {
        result.setChart(mockChart);

        result.setHeight("10");
        result.setWidth("10");
        result.execute(actionInvocation);

        assertTrue(os.isWritten());
    }

    public void testContentTypePng() throws Exception {
        result.setChart(mockChart);

        result.setHeight("10");
        result.setWidth("10");
        result.setType("png");
        result.execute(actionInvocation);

        verify(responseMock).setContentType("image/png");
        assertTrue(os.isWritten());
    }

    public void testContentTypeJpg() throws Exception {
        result.setChart(mockChart);

        result.setHeight("10");
        result.setWidth("10");
        result.setType("jpg");
        result.execute(actionInvocation);

        verify(responseMock).setContentType("image/jpg");
        assertTrue(os.isWritten());
    }

    public void testChartNotSet() {
        result.setChart(null);

        assertThrows(NullPointerException.class, () -> result.execute(actionInvocation));

        assertFalse(os.isWritten());
    }

    public void testChartWithOGNLProperties() throws Exception {
        result.setChart(mockChart);

        result.setHeight("${myHeight}");
        result.setWidth("${myWidth}");

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.set("myHeight", 250);
        stack.set("myWidth", 150);

        result.execute(actionInvocation);

        assertEquals(result.getHeight(), stack.findValue("myHeight").toString());
        assertEquals(result.getWidth(), stack.findValue("myWidth").toString());
        assertEquals("250", result.getHeight());
        assertEquals("150", result.getWidth());
        assertTrue(os.isWritten());
    }

    private static class MockServletOutputStream extends ServletOutputStream {
        // very simple check that outputStream was written to.
        private boolean written = false;

        /**
         * @return Returns the written.
         */
        public boolean isWritten() {
            return written;
        }

        @Override
        public void write(int arg0) throws IOException {
            written = true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // no-op
        }
    }
}
