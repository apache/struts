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
package org.apache.struts2.views.jasperreports7;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.util.ValueStack;

import java.util.Iterator;

/**
 * Ported to Struts.
 */
public class ValueStackDataSource implements JRRewindableDataSource {

    private static final Logger LOG = LogManager.getLogger(ValueStackDataSource.class);

    private final ValueStack valueStack;
    private final String dataSource;
    private final boolean wrapField;

    private Iterator<?> iterator;

    private boolean firstTimeThrough = true;

    /**
     * Create a value stack data source on the given iterable property
     *
     * @param valueStack      The value stack to base the data source on
     * @param dataSourceParam The property to iterate over for the report
     */
    public ValueStackDataSource(ValueStack valueStack, String dataSourceParam, boolean wrapField) {
        this.valueStack = valueStack;
        this.dataSource = dataSourceParam;
        this.wrapField = wrapField;

        Object dataSourceValue = valueStack.findValue(dataSource);

        if (dataSourceValue != null) {
            if (MakeIterator.isIterable(dataSourceValue)) {
                iterator = MakeIterator.convert(dataSourceValue);
            } else {
                Object[] array = new Object[1];
                array[0] = dataSourceValue;
                iterator = MakeIterator.convert(array);
            }
        } else {
            LOG.warn("Data source value for data source: {} was null", dataSource);
        }
    }


    /**
     * Get the value of a given field
     *
     * @param field The field to get the value for. The expression language to get the value
     *              of the field is either taken from the description property or from the name of the field
     *              if the description is <code>null</code>.
     * @return an <code>Object</code> containing the field value or a new
     * <code>ValueStackDataSource</code> object if the field value evaluates to
     * an object that can be iterated over.
     */
    public Object getFieldValue(JRField field) {
        String expression = field.getName();

        Object value = valueStack.findValue(expression);
        LOG.debug("Field [{}] = [{}]", field.getName(), value);

        if (!wrapField && MakeIterator.isIterable(value) && field.getValueClass().isInstance(value)) {
            return value;
        } else if (MakeIterator.isIterable(value)) {
            // wrap value with ValueStackDataSource if not already wrapped
            return new ValueStackDataSource(this.valueStack, expression, wrapField);
        } else {
            return value;
        }
    }

    /**
     * Move to the first item.
     */
    public void moveFirst() {
        Object dataSourceValue = valueStack.findValue(dataSource);
        if (dataSourceValue != null) {
            if (MakeIterator.isIterable(dataSourceValue)) {
                iterator = MakeIterator.convert(dataSourceValue);
            } else {
                Object[] array = new Object[1];
                array[0] = dataSourceValue;
                iterator = MakeIterator.convert(array);
            }
        } else {
            LOG.warn("Data source value for data source [{}] was null", dataSource);
        }
    }

    /**
     * Is there any more data
     *
     * @return <code>true</code> if there are more elements to iterate over and
     * <code>false</code> otherwise
     * @throws JRException if there is a problem determining whether there
     *                     is more data
     */
    public boolean next() throws JRException {
        if (firstTimeThrough) {
            firstTimeThrough = false;
        } else {
            valueStack.pop();
        }

        if ((iterator != null) && (iterator.hasNext())) {
            valueStack.push(iterator.next());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Pushed next value: {}", valueStack.findValue("."));
            }

            return true;
        } else {
            LOG.debug("No more values");

            return false;
        }
    }
}
