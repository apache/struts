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
package org.apache.struts2.components;

import org.apache.struts2.validator.Validator;
import org.apache.struts2.validator.validators.CreditCardValidator;
import org.apache.struts2.validator.validators.DoubleRangeFieldValidator;
import org.apache.struts2.validator.validators.EmailValidator;
import org.apache.struts2.validator.validators.RangeValidatorSupport;
import org.apache.struts2.validator.validators.RegexFieldValidator;
import org.apache.struts2.validator.validators.RequiredFieldValidator;
import org.apache.struts2.validator.validators.RequiredStringValidator;
import org.apache.struts2.validator.validators.StringLengthFieldValidator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default {@link HtmlConstraintProvider}.
 * <p>
 * Governed by one rule: never false-reject. A constraint is emitted only when the browser cannot
 * reject input the server would accept. In particular this implementation <em>never sets or changes
 * an input's {@code type}</em> — switching a field to {@code type="number"} would reject
 * {@code 1234,50}, which the framework's locale-aware conversion accepts in a comma-decimal locale,
 * and the browsers' {@code email}/{@code url} grammars differ from the framework's validators.
 * Range constraints are therefore emitted only on a control the developer already made numeric.
 *
 * @since 7.4.0
 */
public class StrutsHtmlConstraintProvider implements HtmlConstraintProvider {

    @Override
    public Map<String, String> constraintsFor(List<Validator> validators, HtmlControlType control, Object action) {
        Map<String, String> attributes = new LinkedHashMap<>();
        if (validators == null || validators.isEmpty() || control == null) {
            return attributes;
        }
        for (Validator validator : validators) {
            addConstraints(attributes, validator, control);
            addMessage(attributes, validator, action);
        }
        return attributes;
    }

    protected void addConstraints(Map<String, String> attributes, Validator validator, HtmlControlType control) {
        if (validator instanceof RequiredStringValidator) {
            addRequiredString(attributes, control);
        } else if (validator instanceof RequiredFieldValidator) {
            addRequiredField(attributes, control);
        } else if (validator instanceof StringLengthFieldValidator lengthValidator) {
            addLength(attributes, lengthValidator, control);
        } else if (validator instanceof RegexFieldValidator regexValidator) {
            addPattern(attributes, regexValidator, control);
        } else if (validator instanceof DoubleRangeFieldValidator doubleValidator) {
            addDoubleRange(attributes, doubleValidator, control);
        } else if (validator instanceof RangeValidatorSupport<?> rangeValidator) {
            addRange(attributes, rangeValidator, control);
        }
    }

    /**
     * {@code requiredstring} fails on null, empty and (by default) blank, so the browser's
     * {@code required} can only reject what the server would also reject. Safe on any text-entry control.
     */
    protected void addRequiredString(Map<String, String> attributes, HtmlControlType control) {
        if (!control.supportsLength()) {
            return;
        }
        attributes.put("required", "required");
    }

    /**
     * {@code required} fails only on null, an empty array or an empty collection. A control that submits
     * an empty string rather than omitting the parameter therefore passes server-side while the browser
     * blocks it — an empty text input, a select with an empty-valued header option, and an unticked
     * checkbox (CheckboxInterceptor substitutes "false") are all in that group. Only RADIO and FILE omit
     * the parameter entirely when empty, so only they agree with the browser.
     */
    protected void addRequiredField(Map<String, String> attributes, HtmlControlType control) {
        if (control != HtmlControlType.RADIO && control != HtmlControlType.FILE) {
            return;
        }
        attributes.put("required", "required");
    }

    protected void addLength(Map<String, String> attributes, StringLengthFieldValidator validator, HtmlControlType control) {
        // with trim=true the server measures the trimmed value, so a maxlength taken from it would
        // stop the user typing input the server would have accepted
        if (!control.supportsLength() || validator.isTrim()) {
            return;
        }
        if (validator.getMinLength() > -1) {
            attributes.put("minlength", String.valueOf(validator.getMinLength()));
        }
        if (validator.getMaxLength() > -1) {
            attributes.put("maxlength", String.valueOf(validator.getMaxLength()));
        }
    }

    protected void addPattern(Map<String, String> attributes, RegexFieldValidator validator, HtmlControlType control) {
        // HTML pattern accepts no flags, so a case-insensitive rule cannot be expressed at all
        if (!control.supportsPattern() || !validator.isCaseSensitive()) {
            return;
        }
        // trim defaults to true, and the server matches the trimmed value while pattern matches the
        // raw one: "[a-z]+" would accept "abc " server-side and be blocked by the browser
        if (validator.isTrimed()) {
            return;
        }
        // Both extend RegexFieldValidator but do not match their regex against the raw value:
        // CreditCardValidator strips all whitespace first, and both carry grammars the browser
        // does not share. Neither is expressible as a pattern.
        if (validator instanceof EmailValidator || validator instanceof CreditCardValidator) {
            return;
        }
        String regex = validator.getRegex();
        if (EcmaScriptSafeRegex.isSafe(regex)) {
            attributes.put("pattern", regex);
        }
    }

    protected void addRange(Map<String, String> attributes, RangeValidatorSupport<?> validator, HtmlControlType control) {
        if (!isNumericRange(control)) {
            // Temporal controls support ranges too, but min/max there need per-control ISO
            // formatting (date -> yyyy-MM-dd, month -> yyyy-MM, week -> yyyy-'W'ww, time -> HH:mm).
            // Deliberately deferred; DateRangeFieldValidator therefore emits nothing for now.
            return;
        }
        // min is guarded by isIntegral; see the comment on that method. The shipped Integer/Short/Long
        // range validators always pass it, but a custom RangeValidatorSupport<Double> would not.
        Object min = validator.getMin();
        if (isIntegral(min)) {
            putIfPresent(attributes, "min", min);
        }
        putIfPresent(attributes, "max", validator.getMax());
    }

    protected void addDoubleRange(Map<String, String> attributes, DoubleRangeFieldValidator validator, HtmlControlType control) {
        if (!isNumericRange(control)) {
            // Temporal controls support ranges too, but min/max there need per-control ISO
            // formatting (date -> yyyy-MM-dd, month -> yyyy-MM, week -> yyyy-'W'ww, time -> HH:mm).
            // Deliberately deferred; DateRangeFieldValidator therefore emits nothing for now.
            return;
        }
        // exclusive bounds have no HTML equivalent; omitting them leaves the browser more
        // permissive than the server, which is the safe direction
        Double minInclusive = validator.getMinInclusive();
        if (isIntegral(minInclusive)) {
            putIfPresent(attributes, "min", minInclusive);
        }
        putIfPresent(attributes, "max", validator.getMaxInclusive());
    }

    private boolean isNumericRange(HtmlControlType control) {
        return control.supportsRange() && (control == HtmlControlType.NUMBER || control == HtmlControlType.RANGE);
    }

    /**
     * A fractional {@code min} moves the HTML step base off zero, and with the default {@code step="1"}
     * the browser then rejects whole numbers the server accepts. {@code max} does not participate in the
     * step base, so only {@code min} needs this guard.
     */
    private boolean isIntegral(Object value) {
        if (!(value instanceof java.lang.Number number)) {
            return false;
        }
        double asDouble = number.doubleValue();
        return !Double.isNaN(asDouble) && !Double.isInfinite(asDouble) && asDouble == Math.floor(asDouble);
    }

    protected void addMessage(Map<String, String> attributes, Validator validator, Object action) {
        if (action == null) {
            return;
        }
        String message = validator.getMessage(action);
        if (message != null && !message.isEmpty()) {
            attributes.put("data-msg-" + validator.getValidatorType(), message);
        }
    }

    private void putIfPresent(Map<String, String> attributes, String name, Object value) {
        if (value != null) {
            attributes.put(name, String.valueOf(value));
        }
    }
}
