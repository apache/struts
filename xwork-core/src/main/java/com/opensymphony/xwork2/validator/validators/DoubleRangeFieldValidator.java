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
 * <!-- START SNIPPET: javadoc -->
 * Field Validator that checks if the double specified is within a certain range.
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 *                 <li>fieldName - The field name this validator is validating. Required if using
Plain-Validator Syntax otherwise not required</li>
 *                 <li>minInclusive - the minimum inclusive value in FloatValue format specified by Java language (if none is specified, it will
not be checked) </li>
 *                 <li>maxInclusive - the maximum inclusive value in FloatValue format specified by Java language (if none is specified, it will
not be checked) </li>
 *                 <li>minExclusive - the minimum exclusive value in FloatValue format specified by Java language (if none is specified, it will
not be checked) </li>
 *                 <li>maxExclusive - the maximum exclusive value in FloatValue format specified by Java language (if none is specified, it will
not be checked) </li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: examples -->
 *                 &lt;validators>
 *           &lt;!-- Plain Validator Syntax --&gt;
 *           &lt;validator type="double">
 *               &lt;param name="fieldName"&gt;percentage&lt;/param&gt;
 *               &lt;param name="minInclusive"&gt;20.1&lt;/param&gt;
 *               &lt;param name="maxInclusive"&gt;50.1&lt;/param&gt;
 *               &lt;message&gt;Age needs to be between ${minInclusive} and
${maxInclusive} (inclusive)&lt;/message&gt;
 *           &lt;/validator&gt;
 *
 *           &lt;!-- Field Validator Syntax --&gt;
 *           &lt;field name="percentage"&gt;
 *               &lt;field-validator type="double"&gt;
 *                   &lt;param name="minExclusive"&gt;0.123&lt;/param&gt;
 *                   &lt;param name="maxExclusive"&gt;99.98&lt;/param&gt;
 *                   &lt;message&gt;Percentage needs to be between ${minExclusive}
and ${maxExclusive} (exclusive)&lt;/message&gt;
 *               &lt;/field-validator&gt;
 *           &lt;/field&gt;
 *      &lt;/validators&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @author Rene Gielen
 *
 * @version $Id$
 */
// START SNIPPET: field-level-validator
public class DoubleRangeFieldValidator extends FieldValidatorSupport {
    
    String maxInclusive = null;
    String minInclusive = null;
    String minExclusive = null;
    String maxExclusive = null;

    Double maxInclusiveValue = null;
    Double minInclusiveValue = null;
    Double minExclusiveValue = null;
    Double maxExclusiveValue = null;

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Double value;
        try {
            Object obj = this.getFieldValue(fieldName, object);
            if (obj == null) {
                return;
            }
            value = Double.valueOf(obj.toString());
        } catch (NumberFormatException e) {
            return;
        }

        parseParameterValues();
        if ((maxInclusiveValue != null && value.compareTo(maxInclusiveValue) > 0) ||
                (minInclusiveValue != null && value.compareTo(minInclusiveValue) < 0) ||
                (maxExclusiveValue != null && value.compareTo(maxExclusiveValue) >= 0) ||
                (minExclusiveValue != null && value.compareTo(minExclusiveValue) <= 0)) {
            addFieldError(fieldName, object);
        }
    }

    private void parseParameterValues() {
        this.minInclusiveValue = parseDouble(minInclusive);
        this.maxInclusiveValue = parseDouble(maxInclusive);
        this.minExclusiveValue = parseDouble(minExclusive);
        this.maxExclusiveValue = parseDouble(maxExclusive);
    }

    private Double parseDouble (String value) {
        if (value != null) {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException e) {
                if (log.isWarnEnabled()) {
                    log.warn("DoubleRangeFieldValidator - [parseDouble]: Unable to parse given double parameter " + value);
                }
            }
        }
        return null;
    }

    public void setMaxInclusive(String maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public String getMaxInclusive() {
        return maxInclusive;
    }

    public void setMinInclusive(String minInclusive) {
        this.minInclusive = minInclusive;
    }

    public String getMinInclusive() {
        return minInclusive;
    }

    public String getMinExclusive() {
        return minExclusive;
    }

    public void setMinExclusive(String minExclusive) {
        this.minExclusive = minExclusive;
    }

    public String getMaxExclusive() {
        return maxExclusive;
    }

    public void setMaxExclusive(String maxExclusive) {
        this.maxExclusive = maxExclusive;
    }
}
// END SNIPPET: field-level-validator
