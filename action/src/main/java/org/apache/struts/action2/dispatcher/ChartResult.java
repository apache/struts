/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.dispatcher;

import org.apache.struts.action2.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;


/**
 * A custom Result type for chart data. Built on top of
 * <a href="http://www.jfree.org/jfreechart/" target="_blank">JFreeChart</a>. When executed
 * this Result will write the given chart as a PNG to the servlet output stream.
 *
 * @author Bernard Choi
 */
public class ChartResult implements Result {

	private static final long serialVersionUID = -6484761870055986612L;
	
	JFreeChart chart;
    boolean chartSet = false;
    private int height;
    private int width;


    /**
     * Sets the JFreeChart to use.
     *
     * @param chart a JFreeChart object.
     */
    public void setChart(JFreeChart chart) {
        this.chart = chart;
        chartSet = true;
    }

    /**
     * Sets the chart height.
     *
     * @param height the height of the chart in pixels.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets the chart width.
     *
     * @param width the width of the chart in pixels.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Executes the result. Writes the given chart as a PNG to the servlet output stream.
     *
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when creating or writing the chart to the servlet output stream.
     */
    public void execute(ActionInvocation invocation) throws Exception {
        JFreeChart chart = null;

        if (chartSet) {
            chart = this.chart;
        } else {
            chart = (JFreeChart) invocation.getStack().findValue("chart");
        }

        if (chart == null) {
            throw new NullPointerException("No chart found");
        }

        HttpServletResponse response = ServletActionContext.getResponse();
        OutputStream os = response.getOutputStream();
        ChartUtilities.writeChartAsPNG(os, chart, width, height);
        os.flush();
    }
}
