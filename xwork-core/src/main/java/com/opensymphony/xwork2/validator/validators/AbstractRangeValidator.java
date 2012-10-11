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

import com.opensymphony.xwork2.validator.ValidationException;


/**
 * Base class for range based validators.
 *
 * @author Jason Carreira
 * @author Cameron Braid
 */
public abstract class AbstractRangeValidator<T extends Comparable> extends FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        Object obj = getFieldValue(getFieldName(), object);
        Comparable<T> value = (Comparable<T>) obj;

        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        if (value == null) {
            return;
        }

        // only check for a minimum value if the min parameter is set
        T minComparatorValue = getMinComparatorValue();
        if ((minComparatorValue != null) && (value.compareTo(minComparatorValue) < 0)) {
            addFieldError(getFieldName(), object);
        }

        // only check for a maximum value if the max parameter is set
        T maxComparatorValue = getMaxComparatorValue();
        if ((maxComparatorValue != null) && (value.compareTo(maxComparatorValue) > 0)) {
            addFieldError(getFieldName(), object);
        }
    }

    protected abstract T getMaxComparatorValue();

    protected abstract T getMinComparatorValue();

    protected String safeConditionalParse(String expression) {
        Object value = conditionalParse(expression);
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }
}
