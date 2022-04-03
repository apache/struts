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

import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;

/**
 * <!-- START SNIPPET: javadoc -->
 * Field Validator that checks if the double specified is within a certain range.
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 *     <li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 *     <li>minInclusive - the minimum inclusive value in FloatValue format specified by Java language (if none is specified, it will not be checked) </li>
 *     <li>maxInclusive - the maximum inclusive value in FloatValue format specified by Java language (if none is specified, it will not be checked) </li>
 *     <li>minExclusive - the minimum exclusive value in FloatValue format specified by Java language (if none is specified, it will not be checked) </li>
 *     <li>maxExclusive - the maximum exclusive value in FloatValue format specified by Java language (if none is specified, it will not be checked) </li>
 *     <li>minInclusiveExpression - the minimum inclusive value specified as a OGNL expression (if none is specified, it will not be checked) </li>
 *     <li>maxInclusiveExpression - the maximum inclusive value specified as a OGNL expression (if none is specified, it will not be checked) </li>
 *     <li>minExclusiveExpression - the minimum exclusive value specified as a OGNL expression (if none is specified, it will not be checked) </li>
 *     <li>maxExclusiveExpression - the maximum exclusive value specified as a OGNL expression (if none is specified, it will not be checked) </li>
 * </ul>
 *
 * You can specify either minInclusive, maxInclusive, minExclusive and maxExclusive or minInclusiveExpression, maxInclusiveExpression,
 * minExclusiveExpression and maxExclusiveExpression as a OGNL expression, see example below. You can always try to mix params
 * but be aware that such behaviour was not tested.
 * <!-- END SNIPPET: parameters -->
 *
 *
 * <!-- START SNIPPET: parameters-warning -->
 * Do not use ${minInclusiveExpression}, ${maxInclusiveExpression}, ${minExclusiveExpressionExpression} and ${maxExclusive}
 * as an expression as this will turn into infinitive loop!
 * <!-- END SNIPPET: parameters-warning -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: examples -->
 * &lt;validators&gt;
 *     &lt;!-- Plain Validator Syntax --&gt;
 *         &lt;validator type="double"&gt;
 *         &lt;param name="fieldName"&gt;percentage&lt;/param&gt;
 *         &lt;param name="minInclusive"&gt;20.1&lt;/param&gt;
 *         &lt;param name="maxInclusive"&gt;50.1&lt;/param&gt;
 *         &lt;message&gt;Age needs to be between ${minInclusive} and ${maxInclusive} (inclusive)&lt;/message&gt;
 *     &lt;/validator&gt;
 *
 *     &lt;!-- Field Validator Syntax --&gt;
 *     &lt;field name="percentage"&gt;
 *         &lt;field-validator type="double"&gt;
 *             &lt;param name="minExclusive"&gt;0.123&lt;/param&gt;
 *             &lt;param name="maxExclusive"&gt;99.98&lt;/param&gt;
 *             &lt;message&gt;Percentage needs to be between ${minExclusive} and ${maxExclusive} (exclusive)&lt;/message&gt;
 *         &lt;/field-validator&gt;
 *     &lt;/field&gt;
 *
 *     &lt;!-- Field Validator Syntax with expression --&gt;
 *     &lt;field name="percentage"&gt;
 *         &lt;field-validator type="double"&gt;
 *             &lt;param name="minExclusiveExpression"&gt;${minExclusiveValue}&lt;/param&gt; &lt;!-- will be evaluated as: Double getMinExclusiveValue() --&gt;
 *             &lt;param name="maxExclusiveExpression"&gt;${maxExclusiveValue}&lt;/param&gt; &lt;!-- will be evaluated as: Double getMaxExclusiveValue() --&gt;
 *             &lt;message&gt;Percentage needs to be between ${minExclusive} and ${maxExclusive} (exclusive)&lt;/message&gt;
 *         &lt;/field-validator&gt;
 *     &lt;/field&gt;
 * &lt;/validators&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @author Rene Gielen
 */
public class DoubleRangeFieldValidator extends FieldValidatorSupport {

    private static final Logger LOG = LogManager.getLogger(DoubleRangeFieldValidator.class);

    private Double maxInclusive = null;
    private Double minInclusive = null;
    private Double minExclusive = null;
    private Double maxExclusive = null;

    private String minInclusiveExpression;
    private String maxInclusiveExpression;
    private String minExclusiveExpression;
    private String maxExclusiveExpression;

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object obj = this.getFieldValue(fieldName, object);
        if (obj == null) {
            return;
        }

        Double maxInclusiveToUse = getMaxInclusive();
        Double minInclusiveToUse = getMinInclusive();
        Double maxExclusiveToUse = getMaxExclusive();
        Double minExclusiveToUse = getMinExclusive();

        if (obj.getClass().isArray()) {
            Object[] values = (Object[]) obj;
            validateCollection(maxInclusiveToUse, minInclusiveToUse, maxExclusiveToUse, minExclusiveToUse, Arrays.asList(values));
        } else if (Collection.class.isAssignableFrom(obj.getClass())) {
            Collection values = (Collection) obj;
            validateCollection(maxInclusiveToUse, minInclusiveToUse, maxExclusiveToUse, minExclusiveToUse, values);
        } else {
            validateValue(obj, maxInclusiveToUse, minInclusiveToUse, maxExclusiveToUse, minExclusiveToUse);
        }
    }

    protected void validateCollection(Double maxInclusiveToUse, Double minInclusiveToUse, Double maxExclusiveToUse, Double minExclusiveToUse, Collection values) {
        for (Object objValue : values) {
            validateValue(objValue, maxInclusiveToUse, minInclusiveToUse, maxExclusiveToUse, minExclusiveToUse);
        }
    }

    protected void validateValue(Object obj, Double maxInclusiveToUse, Double minInclusiveToUse, Double maxExclusiveToUse, Double minExclusiveToUse) {
        try {
            setCurrentValue(obj);
            Double value = Double.valueOf(obj.toString());
            if ((maxInclusiveToUse != null && value.compareTo(maxInclusiveToUse) > 0) ||
                    (minInclusiveToUse != null && value.compareTo(minInclusiveToUse) < 0) ||
                    (maxExclusiveToUse != null && value.compareTo(maxExclusiveToUse) >= 0) ||
                    (minExclusiveToUse != null && value.compareTo(minExclusiveToUse) <= 0)) {
                addFieldError(getFieldName(), value);
            }
        } catch (NumberFormatException e) {
            LOG.debug("Cannot validate value {} - not a Double", e);
        } finally {
            setCurrentValue(null);
        }
    }

    public void setMaxInclusive(Double maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public Double getMaxInclusive() {
        if (maxInclusive != null) {
            return maxInclusive;
        } else if (StringUtils.isNotEmpty(maxInclusiveExpression)) {
            return (Double) parse(maxInclusiveExpression, Double.class);
        }
        return maxInclusive;
    }

    public void setMinInclusive(Double minInclusive) {
        this.minInclusive = minInclusive;
    }

    public Double getMinInclusive() {
        if (minInclusive != null) {
            return minInclusive;
        } else if (StringUtils.isNotEmpty(minInclusiveExpression)) {
            return (Double) parse(minInclusiveExpression, Double.class);
        }
        return null;
    }

    public void setMinExclusive(Double minExclusive) {
        this.minExclusive = minExclusive;
    }

    public Double getMinExclusive() {
        if (minExclusive != null) {
            return minExclusive;
        } else if (StringUtils.isNotEmpty(minExclusiveExpression)) {
            return (Double) parse(minExclusiveExpression, Double.class);
        }
        return null;
    }

    public void setMaxExclusive(Double maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    public Double getMaxExclusive() {
        if (maxExclusive != null) {
            return maxExclusive;
        } else if (StringUtils.isNotEmpty(maxExclusiveExpression)) {
            return (Double) parse(maxExclusiveExpression, Double.class);
        }
        return null;
    }

    public void setMinInclusiveExpression(String minInclusiveExpression) {
        this.minInclusiveExpression = minInclusiveExpression;
    }

    public void setMaxInclusiveExpression(String maxInclusiveExpression) {
        this.maxInclusiveExpression = maxInclusiveExpression;
    }

    public void setMinExclusiveExpression(String minExclusiveExpression) {
        this.minExclusiveExpression = minExclusiveExpression;
    }

    public void setMaxExclusiveExpression(String maxExclusiveExpression) {
        this.maxExclusiveExpression = maxExclusiveExpression;
    }

}
