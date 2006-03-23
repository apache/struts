/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on Sep 20, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.webwork.dispatcher;

import com.opensymphony.webwork.ServletActionContext;
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
