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

import org.apache.struts2.ActionSupport;
import org.apache.struts2.validator.Validator;
import org.apache.struts2.validator.validators.CreditCardValidator;
import org.apache.struts2.validator.validators.DateRangeFieldValidator;
import org.apache.struts2.validator.validators.DoubleRangeFieldValidator;
import org.apache.struts2.validator.validators.EmailValidator;
import org.apache.struts2.validator.validators.IntRangeFieldValidator;
import org.apache.struts2.validator.validators.RangeValidatorSupport;
import org.apache.struts2.validator.validators.RegexFieldValidator;
import org.apache.struts2.validator.validators.RequiredFieldValidator;
import org.apache.struts2.validator.validators.RequiredStringValidator;
import org.apache.struts2.validator.validators.StringLengthFieldValidator;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StrutsHtmlConstraintProviderTest {

    private StrutsHtmlConstraintProvider provider;
    private Object action;

    @Before
    public void setUp() {
        provider = new StrutsHtmlConstraintProvider();
        action = new ActionSupport();
    }

    private Map<String, String> constraints(Validator validator, HtmlControlType control) {
        return constraints(validator, control, null);
    }

    private Map<String, String> constraints(Validator validator, HtmlControlType control, Object value) {
        return provider.constraintsFor(singletonList(validator), control, null, value);
    }

    @Test
    public void requiredStringEmitsRequiredEvenThoughServerIsStricter() {
        assertThat(constraints(new RequiredStringValidator(), HtmlControlType.TEXT))
            .containsEntry("required", "required");
    }

    @Test
    public void requiredStringEmitsRequiredOnTextarea() {
        assertThat(constraints(new RequiredStringValidator(), HtmlControlType.TEXTAREA))
            .containsEntry("required", "required");
    }

    @Test
    public void requiredFieldEmitsNothingOnATextControlBecauseEmptyStringWouldPassServerSide() {
        // an empty text input submits name="", which RequiredFieldValidator accepts (it only
        // rejects null / empty array / empty collection) — required here would false-reject
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void requiredFieldEmitsNothingOnACheckboxBecauseUncheckedSubstitutesFalse() {
        // CheckboxInterceptor substitutes "false" for an unticked box, so the field is never
        // null server-side and an unticked required checkbox would still pass validation
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.CHECKBOX)).isEmpty();
    }

    @Test
    public void requiredFieldEmitsNothingOnASelectBecauseAnEmptyOptionWouldPassServerSide() {
        // a select with an empty-valued header option submits "", which passes server-side
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.SELECT)).isEmpty();
    }

    @Test
    public void requiredFieldEmitsRequiredOnRadioBecauseNoSelectionOmitsTheParameter() {
        // an unselected radio group omits the parameter entirely, agreeing with the server
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.RADIO))
            .containsEntry("required", "required");
    }

    @Test
    public void requiredFieldEmitsRequiredOnFileBecauseNoSelectionOmitsTheParameter() {
        // an empty file input omits the parameter entirely, agreeing with the server
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.FILE))
            .containsEntry("required", "required");
    }

    @Test
    public void requiredFieldEmitsNothingOnRadioWhenTheBoundValueIsAlreadySet() {
        // a primitive int renders as 0, which is not in the list, so no radio is checked while the
        // server, seeing a non-null Integer, would accept the empty submit
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.RADIO, 0)).isEmpty();
    }

    @Test
    public void requiredFieldEmitsNothingOnFileWhenTheBoundValueIsAlreadySet() {
        // prepare() populating the file property from an existing entity is the ordinary edit flow
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.FILE, "existing.pdf")).isEmpty();
    }

    @Test
    public void requiredFieldEmitsRequiredOnRadioWhenTheBoundValueIsAnEmptyArray() {
        // RequiredFieldValidator fails an empty array, so the sides agree
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.RADIO, new String[0]))
            .containsEntry("required", "required");
    }

    @Test
    public void requiredStringIgnoresTheBoundValue() {
        // requiredstring judges the submitted value, which a text input always sends
        assertThat(constraints(new RequiredStringValidator(), HtmlControlType.TEXT, "prefilled"))
            .containsEntry("required", "required");
    }

    @Test
    public void stringLengthEmitsLengthsWhenNotTrimming() {
        StringLengthFieldValidator validator = new StringLengthFieldValidator();
        validator.setTrim(false);
        validator.setMinLength(3);
        validator.setMaxLength(10);

        assertThat(constraints(validator, HtmlControlType.TEXT))
            .containsEntry("minlength", "3")
            .containsEntry("maxlength", "10");
    }

    @Test
    public void stringLengthEmitsNothingWhenTrimming() {
        StringLengthFieldValidator validator = new StringLengthFieldValidator();
        validator.setTrim(true);
        validator.setMinLength(3);
        validator.setMaxLength(10);

        // the server measures the trimmed value, so maxlength here would stop the user
        // typing input the server would have accepted
        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void stringLengthEmitsNothingOnAControlWithoutLength() {
        StringLengthFieldValidator validator = new StringLengthFieldValidator();
        validator.setTrim(false);
        validator.setMaxLength(10);

        assertThat(constraints(validator, HtmlControlType.NUMBER)).isEmpty();
    }

    @Test
    public void stringLengthOmitsMinlengthWhenOnlyMaxLengthIsSet() {
        StringLengthFieldValidator validator = new StringLengthFieldValidator();
        validator.setTrim(false);
        validator.setMaxLength(10);

        Map<String, String> result = constraints(validator, HtmlControlType.TEXT);

        // minLength defaults to the -1 sentinel (unset), which must not become "minlength=-1"
        assertThat(result)
            .containsEntry("maxlength", "10")
            .doesNotContainKey("minlength");
    }

    @Test
    public void regexAloneEmitsPatternThatAlsoAcceptsBlankInput() {
        // RegexFieldValidator skips a value that trims to empty regardless of its trim param, so a
        // bare pattern would block whitespace-only input the server lets through
        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setRegex("[a-z]+");
        validator.setCaseSensitive(true);
        validator.setTrim(false);

        assertThat(constraints(validator, HtmlControlType.TEXT))
            .containsEntry("pattern", "(?:[a-z]+)|[\\x00-\\x20]*");
    }

    @Test
    public void regexWithTrimmingRequiredStringEmitsBarePattern() {
        // a trimming requiredstring rejects blank input server-side, so the two sides agree again
        RegexFieldValidator regex = new RegexFieldValidator();
        regex.setRegex("[a-z]+");
        regex.setCaseSensitive(true);
        regex.setTrim(false);

        assertThat(provider.constraintsFor(List.of(new RequiredStringValidator(), regex), HtmlControlType.TEXT, null, null))
            .containsEntry("pattern", "[a-z]+");
    }

    @Test
    public void regexWithRequiredStringListedAfterItStillEmitsBarePattern() {
        RegexFieldValidator regex = new RegexFieldValidator();
        regex.setRegex("[a-z]+");
        regex.setCaseSensitive(true);
        regex.setTrim(false);

        assertThat(provider.constraintsFor(List.of(regex, new RequiredStringValidator()), HtmlControlType.TEXT, null, null))
            .containsEntry("pattern", "[a-z]+");
    }

    @Test
    public void regexWithNonTrimmingRequiredStringKeepsTheBlankAlternative() {
        // requiredstring with trim=false accepts " " as non-empty, so blank input still reaches the
        // regex validator's unconditional skip
        RegexFieldValidator regex = new RegexFieldValidator();
        regex.setRegex("[a-z]+");
        regex.setCaseSensitive(true);
        regex.setTrim(false);
        RequiredStringValidator required = new RequiredStringValidator();
        required.setTrim(false);

        assertThat(provider.constraintsFor(List.of(required, regex), HtmlControlType.TEXT, null, null))
            .containsEntry("pattern", "(?:[a-z]+)|[\\x00-\\x20]*");
    }

    @Test
    public void regexEmitsNothingWhenCaseInsensitive() {
        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setRegex("[a-z]+");
        validator.setCaseSensitive(false);
        validator.setTrim(false);

        // HTML pattern accepts no flags, so a case-insensitive rule cannot be expressed
        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void regexEmitsNothingWhenNotPortable() {
        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setRegex("\\p{Alpha}+");
        validator.setCaseSensitive(true);
        validator.setTrim(false);

        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void regexEmitsNothingWhenTrimming() {
        // trim defaults to true: the server matches the trimmed value while pattern matches the
        // raw one, so "abc " would pass server-side and be blocked by the browser
        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setRegex("[a-z]+");
        validator.setCaseSensitive(true);

        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void creditCardValidatorNeverContributesAPatternConstraint() {
        // CreditCardValidator strips all whitespace before matching, so its regex cannot be
        // expressed as a browser pattern without also stripping whitespace client-side.
        // caseSensitive and trim are set explicitly here so this test actually reaches the
        // EmailValidator/CreditCardValidator exclusion in addPattern, rather than returning
        // earlier at the case-sensitivity guard (the constructor defaults caseSensitive to false).
        CreditCardValidator validator = new CreditCardValidator();
        validator.setCaseSensitive(true);
        validator.setTrim(false);

        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void intRangeEmitsBoundsOnlyOnANumericControl() {
        IntRangeFieldValidator validator = new IntRangeFieldValidator();
        validator.setMin(5);
        validator.setMax(50);

        assertThat(constraints(validator, HtmlControlType.NUMBER))
            .containsEntry("min", "5")
            .containsEntry("max", "50");
        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void doubleRangeEmitsInclusiveBoundsOnlyOnANumericControl() {
        // integral bounds here, deliberately: a fractional min is covered separately by
        // doubleRangeOmitsMinWhenItIsFractionalBecauseItWouldShiftTheStepBase, since it must NOT
        // emit min at all (it would shift the HTML step base off zero)
        DoubleRangeFieldValidator validator = new DoubleRangeFieldValidator();
        validator.setMinInclusive(6000.0);
        validator.setMaxInclusive(10000.1);

        assertThat(constraints(validator, HtmlControlType.NUMBER))
            .containsEntry("min", "6000.0")
            .containsEntry("max", "10000.1");
        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void doubleRangeOmitsMinWhenItIsFractionalBecauseItWouldShiftTheStepBase() {
        // min becomes the HTML step base, and the default step is 1: min="6000.1" would make the
        // browser reject 6002, which DoubleRangeFieldValidator accepts server-side. max does not
        // participate in the step base, so it is unaffected.
        DoubleRangeFieldValidator validator = new DoubleRangeFieldValidator();
        validator.setMinInclusive(6000.1);
        validator.setMaxInclusive(10000.1);

        Map<String, String> result = constraints(validator, HtmlControlType.NUMBER);

        assertThat(result)
            .containsEntry("max", "10000.1")
            .doesNotContainKey("min");
    }

    @Test
    public void doubleRangeEmitsMinWhenItIsIntegral() {
        DoubleRangeFieldValidator validator = new DoubleRangeFieldValidator();
        validator.setMinInclusive(6000.0);
        validator.setMaxInclusive(10000.0);

        assertThat(constraints(validator, HtmlControlType.NUMBER))
            .containsEntry("min", "6000.0")
            .containsEntry("max", "10000.0");
    }

    @Test
    public void dateRangeEmitsNothingBecauseTemporalFormattingIsDeferred() {
        DateRangeFieldValidator validator = new DateRangeFieldValidator();
        validator.setMin(new Date(0));
        validator.setMax(new Date(1_000_000L));

        assertThat(constraints(validator, HtmlControlType.DATE)).isEmpty();
    }

    @Test
    public void emailValidatorNeverContributesAConstraint() {
        // the browser's email grammar differs from EmailValidator's, so honouring it could reject
        // an address the server accepts. caseSensitive and trim are set explicitly here so this
        // test actually reaches addPattern's EmailValidator exclusion, rather than returning
        // earlier at the case-sensitivity guard or the isTrimed() guard (the constructor defaults
        // caseSensitive to false, and trim defaults to true).
        EmailValidator textControlValidator = new EmailValidator();
        textControlValidator.setCaseSensitive(true);
        textControlValidator.setTrim(false);
        assertThat(constraints(textControlValidator, HtmlControlType.TEXT)).isEmpty();

        EmailValidator emailControlValidator = new EmailValidator();
        emailControlValidator.setCaseSensitive(true);
        emailControlValidator.setTrim(false);
        assertThat(constraints(emailControlValidator, HtmlControlType.EMAIL)).isEmpty();
    }

    @Test
    public void unknownControlGetsNothing() {
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.UNSUPPORTED)).isEmpty();
    }

    @Test
    public void emptyInputIsHandled() {
        assertThat(provider.constraintsFor(null, HtmlControlType.TEXT, null, null)).isEmpty();
        assertThat(provider.constraintsFor(List.of(), HtmlControlType.TEXT, null, null)).isEmpty();
    }

    @Test
    public void messageIsNotEmittedOnAControlThatNeverSubmits() {
        // s:label and unknown controls resolve to UNSUPPORTED; a message there decorates an element the
        // browser never validates and no script has a submitted value to check it against
        Validator validator = mock(Validator.class);
        when(validator.getValidatorType()).thenReturn("requiredstring");
        when(validator.getMessage(action)).thenReturn("required");

        Map<String, String> result =
            provider.constraintsFor(singletonList(validator), HtmlControlType.UNSUPPORTED, action, null);

        assertThat(result).isEmpty();
    }

    @Test
    public void messageIsNotEmittedForAValidatorTypeThatCannotBeAnAttributeName() {
        // validator types are free-form in validators.xml; FreeMarker escapes the value, not the name
        Validator validator = mock(Validator.class);
        when(validator.getValidatorType()).thenReturn("my type=\"x\"");
        when(validator.getMessage(action)).thenReturn("nope");

        Map<String, String> result =
            provider.constraintsFor(singletonList(validator), HtmlControlType.TEXT, action, null);

        assertThat(result).isEmpty();
    }

    @Test
    public void messageIsEmittedForADottedValidatorType() {
        // legal both as a validators.xml type name and as a data-* attribute suffix
        Validator validator = mock(Validator.class);
        when(validator.getValidatorType()).thenReturn("acme.required");
        when(validator.getMessage(action)).thenReturn("needed");

        Map<String, String> result =
            provider.constraintsFor(singletonList(validator), HtmlControlType.TEXT, action, null);

        assertThat(result).containsEntry("data-msg-acme.required", "needed");
    }

    @Test
    public void rangeOmitsAMaxThatIsNotANumber() {
        // a date range on a control the developer declared numeric: min already fails the integral
        // guard, max must not fall through as Date.toString()
        DateRangeFieldValidator validator = new DateRangeFieldValidator();
        validator.setMin(new Date(0));
        validator.setMax(new Date(1_000_000L));

        assertThat(constraints(validator, HtmlControlType.NUMBER)).isEmpty();
    }

    @Test
    public void doubleRangeOmitsANonFiniteMax() {
        DoubleRangeFieldValidator nan = new DoubleRangeFieldValidator();
        nan.setMaxInclusive(Double.NaN);
        DoubleRangeFieldValidator infinite = new DoubleRangeFieldValidator();
        infinite.setMaxInclusive(Double.POSITIVE_INFINITY);

        assertThat(constraints(nan, HtmlControlType.NUMBER)).doesNotContainKey("max");
        assertThat(constraints(infinite, HtmlControlType.NUMBER)).doesNotContainKey("max");
    }

    @Test
    public void rangeOmitsAMinThatIsOnlyIntegralAfterRoundingThroughDouble() {
        // 1.0000000000000000001 collapses to 1.0 as a double but renders with its fraction, which
        // would shift the HTML step base exactly like a fractional min
        RangeValidatorSupport<BigDecimal> validator = new RangeValidatorSupport<>(BigDecimal.class) {
        };
        validator.setMin(new BigDecimal("1.0000000000000000001"));
        validator.setMax(new BigDecimal("10"));

        assertThat(constraints(validator, HtmlControlType.NUMBER))
            .doesNotContainKey("min")
            .containsEntry("max", "10");
    }

    @Test
    public void messageIsEmittedEvenForAValidatorThatContributesNoConstraint() {
        Validator validator = mock(Validator.class);
        when(validator.getValidatorType()).thenReturn("email");
        when(validator.getMessage(action)).thenReturn("not an email");

        Map<String, String> result =
            provider.constraintsFor(singletonList(validator), HtmlControlType.TEXT, action, null);

        assertThat(result).containsEntry("data-msg-email", "not an email");
    }
}
