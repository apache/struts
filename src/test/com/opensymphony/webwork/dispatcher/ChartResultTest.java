/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on Nov 10, 2003
 *
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.opensymphony.webwork.dispatcher;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import junit.framework.TestCase;
import ognl.Ognl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author bchoi
 *         <p/>
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ChartResultTest extends TestCase {

    private ActionInvocation actionInvocation;
    private JFreeChart mockChart;
    private Mock responseMock;
    private MockServletOutputStream os;


    public void testChart() throws Exception {
        responseMock.expectAndReturn("getOutputStream", os);

        ChartResult result = new ChartResult();

        result.setChart(mockChart);

        result.setHeight(10);
        result.setWidth(10);
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

    protected void setUp() throws Exception {
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Java", new Double(43.2));
        data.setValue("Visual Basic", new Double(0.0));
        data.setValue("C/C++", new Double(17.5));
        mockChart = ChartFactory.createPieChart("Pie Chart", data, true, true, false);

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        actionInvocation = (ActionInvocation) mockActionInvocation.proxy();
        os = new MockServletOutputStream();
        responseMock = new Mock(HttpServletResponse.class);

        ActionContext.setContext(new ActionContext(Ognl.createDefaultContext(null)));
        ServletActionContext.setResponse((HttpServletResponse) responseMock.proxy());
    }

    protected void tearDown() throws Exception {
        actionInvocation = null;
        os = null;
        responseMock = null;
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
