/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;

/**
 * Base class for range based validators. Use this class to develop any other custom range validators.
 */
public abstract class RangeValidatorSupport<T extends Comparable> extends FieldValidatorSupport {

    private static final Logger LOG = LoggerFactory.getLogger(RangeValidatorSupport.class);

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
        Comparable<T> value = (Comparable<T>) obj;

        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        if (value == null) {
            return;
        }

        // only check for a minimum value if the min parameter is set
        T minComparatorValue = getMin();
        if ((minComparatorValue != null) && (value.compareTo(minComparatorValue) < 0)) {
            addFieldError(getFieldName(), object);
        }

        // only check for a maximum value if the max parameter is set
        T maxComparatorValue = getMax();
        if ((maxComparatorValue != null) && (value.compareTo(maxComparatorValue) > 0)) {
            addFieldError(getFieldName(), object);
        }
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMin() {
        if (min != null) {
            return min;
        } else if (StringUtils.isNotEmpty(minExpression)) {
            return (T) parse(minExpression, type);
        } else {
            return null;
        }
    }

    public void setMinExpression(String minExpression) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("${minExpression} was defined as [#0]", minExpression);
        }
        this.minExpression = minExpression;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public T getMax() {
        if (max != null) {
            return max;
        } else if (StringUtils.isNotEmpty(maxExpression)) {
            return (T) parse(maxExpression, type);
        } else {
            return null;
        }
    }

    public void setMaxExpression(String maxExpression) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("${maxExpression} was defined as [#0]", maxExpression);
        }
        this.maxExpression = maxExpression;
    }

}
