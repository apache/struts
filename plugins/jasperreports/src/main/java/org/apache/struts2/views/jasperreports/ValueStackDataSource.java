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

package org.apache.struts2.views.jasperreports;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import org.apache.struts2.util.MakeIterator;

import java.util.Iterator;

/**
 * Ported to Struts.
 */
public class ValueStackDataSource implements JRRewindableDataSource {

    /**
     * Logger for this class
     */
    private static Logger LOG = LoggerFactory.getLogger(ValueStackDataSource.class);

    private Iterator iterator;
    private ValueStack valueStack;
    private String dataSource;
    private boolean wrapField;

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
            if (LOG.isWarnEnabled()) {
                LOG.warn("Data source value for data source " + dataSource + " was null");
            }
        }
    }


    /**
     * Get the value of a given field
     *
     * @param field The field to get the value for. The expression language to get the value
     *              of the field is either taken from the description property or from the name of the field
     *              if the description is <code>null</code>.
     * @return an <code>Object</code> containing the field value or a new
     *         <code>ValueStackDataSource</code> object if the field value evaluates to
     *         an object that can be iterated over.
     * @throws JRException if there is a problem obtaining the value
     */
    public Object getFieldValue(JRField field) throws JRException {
        //TODO: move the code to return a ValueStackDataSource to a seperate
        //      method when and if the JRDataSource interface is updated to support
        //      this.
        String expression = field.getDescription();

        if (expression == null) {
            //Description is optional so use the field name as a default
            expression = field.getName();
        }

        Object value = valueStack.findValue(expression);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Field [#0] = [#1]", field.getName(), value);
        }

        if (!wrapField && MakeIterator.isIterable(value) && !field.getValueClass().isInstance(value)) {
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
     *
     * @throws JRException if there is a problem with moving to the first
     *                     data element
     */
    public void moveFirst() throws JRException {
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
            if (LOG.isWarnEnabled()) {
                LOG.warn("Data source value for data source [" + dataSource + "] was null");
            }
        }
    }

    /**
     * Is there any more data
     *
     * @return <code>true</code> if there are more elements to iterate over and
     *         <code>false</code> otherwise
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
                LOG.debug("Pushed next value: " + valueStack.findValue("."));
            }

            return true;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No more values");
            }

            return false;
        }
    }
}
