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
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: description -->
 * <p/>
 * A custom Result type for chart data. Built on top of
 * <a href="http://www.jfree.org/jfreechart/" target="_blank">JFreeChart</a>. When executed
 * this Result will write the given chart as a PNG or JPG to the servlet output stream.
 * <p/>
 * <!-- END SNIPPET: description -->
 * <p/>
 * <b>This result type takes the following parameters:</b>
 * <p/>
 * <!-- START SNIPPET: params -->
 * <p/>
 * <ul>
 * <p/>
 * <li><b>value</b> - the name of the JFreeChart object on the ValueStack, defaults to 'chart'.</li>
 * <p/>
 * <li><b>type</b> - the render type for this chart. Can be jpg (or jpeg) or png. Defaults to png.</li>
 * <p/>
 * <li><b>width (required)</b> - the width (in pixels) of the rendered chart.</li>
 * <p/>
 * <li><b>height (required)</b> - the height (in pixels) of the rendered chart.</li>
 * <p/>
 * </ul>
 * <!-- END SNIPPET: params -->
 * <p/>
 * <b>Example:</b>
 * <p/>
 * <pre><!-- START SNIPPET: example -->
 * public class ExampleChartAction extends ActionSupport {
 *
 *	    private JFreeChart chart;
 *
 *	    public String execute() throws Exception {
 *		    // chart creation logic...
 *		    XYSeries dataSeries = new XYSeries(new Integer(1)); // pass a key for this serie
 *		    for (int i = 0; i <= 100; i++) {
 *			    dataSeries.add(i, RandomUtils.nextInt());
 *		    }
 *		    XYSeriesCollection xyDataset = new XYSeriesCollection(dataSeries);
 *
 *		    ValueAxis xAxis = new NumberAxis("Raw Marks");
 *		    ValueAxis yAxis = new NumberAxis("Moderated Marks");
 *
 *		    // set my chart variable
 *		    chart =
 *			    new JFreeChart( "Moderation Function", JFreeChart.DEFAULT_TITLE_FONT,
 *				    new XYPlot( xyDataset, xAxis, yAxis, new StandardXYItemRenderer(StandardXYItemRenderer.LINES)),
 *				    false);
 *		    chart.setBackgroundPaint(java.awt.Color.white);
 *
 *		    return SUCCESS;
 *	    }
 * 
 *      // this method will get called if we specify &lt;param name="value"&gt;chart&lt;/param&gt;
 *	    public JFreeChart getChart() {
 *		    return chart;
 *	    }
 *  }
 *
 * &lt;result name="success" type="chart"&gt;
 *   &lt;param name="value"&gt;chart&lt;/param&gt;
 *   &lt;param name="type"&gt;png&lt;/param&gt;
 *   &lt;param name="width"&gt;640&lt;/param&gt;
 *   &lt;param name="height"&gt;480&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example --></pre>
 */
public class ChartResult extends StrutsResultSupport {

    private final static Logger LOG = LoggerFactory.getLogger(ChartResult.class);

    private static final long serialVersionUID = -6484761870055986612L;
    private static final String DEFAULT_TYPE = "png";
    private static final String DEFAULT_VALUE = "chart";

    private JFreeChart chart; // the JFreeChart to render
    private boolean chartSet;
    String height, width;
    String type = DEFAULT_TYPE; // supported are jpg, jpeg or png, defaults to png
    String value = DEFAULT_VALUE; // defaults to 'chart'

    // CONSTRUCTORS ----------------------------

    public ChartResult() {
        super();
    }

    public ChartResult(JFreeChart chart, String height, String width) {
        this.chart = chart;
        this.height = height;
        this.width = width;
    }

    // ACCESSORS ----------------------------

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JFreeChart getChart() {
        return chart;
    }

    public void setChart(JFreeChart chart) {
        this.chartSet = true;
        this.chart = chart;
    }

    // OTHER METHODS -----------------------

    // Required by com.opensymphony.xwork2.Result

    /**
     * Executes the result. Writes the given chart as a PNG or JPG to the servlet output stream.
     *
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when creating or writing the chart to the servlet output stream.
     */
    public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

        initializeProperties(invocation);
        
        if (!chartSet) // if our chart hasn't been set (by the testcase), we'll look it up in the value stack
            chart = (JFreeChart) invocation.getStack().findValue(value, JFreeChart.class);
        if (chart == null) // we need to have a chart object - if not, blow up
            throw new NullPointerException("No JFreeChart object found on the stack with name " + value);
        // make sure we have some value for the width and height
        if (height == null)
            throw new NullPointerException("No height parameter was given.");
        if (width == null)
            throw new NullPointerException("No width parameter was given.");

        // get a reference to the servlet output stream to write our chart image to
        HttpServletResponse response = ServletActionContext.getResponse();
        OutputStream os = response.getOutputStream();
        try {
            // check the type to see what kind of output we have to produce
            if ("png".equalsIgnoreCase(type)) {
                response.setContentType("image/png");
                ChartUtilities.writeChartAsPNG(os, chart, getIntValueFromString(width), getIntValueFromString(height));
            }
            else if ("jpg".equalsIgnoreCase(type) || "jpeg".equalsIgnoreCase(type)) {
                response.setContentType("image/jpg");
                ChartUtilities.writeChartAsJPEG(os, chart, getIntValueFromString(width), getIntValueFromString(height));
            }
            else
                throw new IllegalArgumentException(type + " is not a supported render type (only JPG and PNG are).");
        } finally {
            if (os != null) os.flush();
        }
    }

    /**
     * Sets up result properties, parsing etc.
     *
     * @param invocation Current invocation.
     * @throws Exception on initialization error.
     */
    private void initializeProperties(ActionInvocation invocation) throws Exception {

        if (height != null) {
            height = conditionalParse(height, invocation);
        }

        if (width != null) {
            width = conditionalParse(width, invocation);
        }

        if (type != null) {
            type = conditionalParse(type, invocation);
        }
        
        if ( type == null) {
            type = DEFAULT_TYPE;
        }
    }

    private Integer getIntValueFromString(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            LOG.error("Specified value for width or height is not of type Integer...", e);
            return null;
        }
    }

}