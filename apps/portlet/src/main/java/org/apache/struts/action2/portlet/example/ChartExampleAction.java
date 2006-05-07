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
package org.apache.struts.action2.portlet.example;

import java.util.Date;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.opensymphony.xwork.ActionSupport;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
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
