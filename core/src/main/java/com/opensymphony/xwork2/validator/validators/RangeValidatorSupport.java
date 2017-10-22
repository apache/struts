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
package com.opensymphony.xwork2.validator.validators;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * Base class for range based validators. Use this class to develop any other custom range validators.
 */
public abstract class RangeValidatorSupport<T extends Comparable> extends FieldValidatorSupport {

    private static final Logger LOG = LogManager.getLogger(RangeValidatorSupport.class);

    private final Class<T> type;

    private T min;
    private String minExpression;
    private T max;
    private String maxExpression;

    protected RangeValidatorSupport(Class<T> type) {
        this.type = type;
    }

    public void validate(Object object) throws ValidationException {
        Object obj = getFieldValue(getFieldName(), object);

        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        if (obj == null) {
            return;
        }

        T min = getMin();
        T max = getMax();

        if (obj.getClass().isArray()) {
            Object[] values = (Object[]) obj;
            for (Object objValue : values) {
                validateValue(object, (Comparable<T>) objValue, min, max);
            }
        } else if (Collection.class.isAssignableFrom(obj.getClass())) {
            Collection<?> values = (Collection<?>) obj;
            for (Object objValue : values) {
                validateValue(object, (Comparable<T>) objValue, min, max);
            }
        } else {
            validateValue(object, (Comparable<T>) obj, min, max);
        }
    }

    protected void validateValue(Object object, Comparable<T> value, T min, T max) {
        setCurrentValue(value);

        // only check for a minimum value if the min parameter is set
        if ((min != null) && (value.compareTo(min) < 0)) {
            addFieldError(getFieldName(), object);
        }

        // only check for a maximum value if the max parameter is set
        if ((max != null) && (value.compareTo(max) > 0)) {
            addFieldError(getFieldName(), object);
        }

        setCurrentValue(null);
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMin() {
        return getT(min, minExpression, type);
    }

    public T getMax() {
        return getT(max, maxExpression, type);
    }

    public void setMinExpression(String minExpression) {
        LOG.debug("${minExpression} was defined as [{}]", minExpression);
        this.minExpression = minExpression;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public void setMaxExpression(String maxExpression) {
        LOG.debug("${maxExpression} was defined as [{}]", maxExpression);
        this.maxExpression = maxExpression;
    }

    protected T getT(T minMax, String minMaxExpression, Class<T> toType) {
        if (minMax != null) {
            return minMax;
        } else if (StringUtils.isNotEmpty(minMaxExpression)) {
            return (T) parse(minMaxExpression, toType);
        } else {
            return null;
        }
    }

}
