/*
 * Created on Jan 31, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.opensymphony.webwork.portlet.example;

import java.util.Date;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.opensymphony.xwork.ActionSupport;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
 * @author Nils-Helge Garli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ChartExampleAction extends ActionSupport {
    private DatasetProducer pieChartProducer;

    private DatasetProducer lineChartProducer;

    private final String[] categories = { "mon", "tue", "wen", "thu", "fri",
            "sat", "sun" };

    private final String[] seriesNames = { "cewolfset.jsp", "tutorial.jsp",
            "testpage.jsp", "performancetest.jsp" };

    public ChartExampleAction() {
        pieChartProducer = new DatasetProducer() {

            public Object produceDataset(Map params)
                    throws DatasetProduceException {
                DefaultPieDataset pieDataSet = new DefaultPieDataset();
                pieDataSet.setValue("Series 1", 0.3);
                pieDataSet.setValue("Series 2", 0.7);
                return pieDataSet;
            }

            public boolean hasExpired(Map params, Date date) {
                return true;
            }

            public String getProducerId() {
                return "pieChartProducer";
            }

        };
        lineChartProducer = new DatasetProducer() {

            public Object produceDataset(Map params)
                    throws DatasetProduceException {
                DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
                for (int series = 0; series < seriesNames.length; series++) {
                    int lastY = (int) (Math.random() * 1000 + 1000);
                    for (int i = 0; i < categories.length; i++) {
                        final int y = lastY + (int) (Math.random() * 200 - 100);
                        lastY = y;
                        dataSet.addValue(y, seriesNames[series], categories[i]);
                    }
                }
                return dataSet;
            }

            public boolean hasExpired(Map params, Date date) {
                return true;
            }

            public String getProducerId() {
                return "lineChartProducer";
            }

        };
    }

    public DatasetProducer getLineChartProducer() {
        return lineChartProducer;
    }

    public DatasetProducer getPieChartProducer() {
        return pieChartProducer;
    }
}
