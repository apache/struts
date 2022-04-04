package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.validator.validators.ConditionalVisitorFieldValidator;
import com.opensymphony.xwork2.validator.validators.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.validators.DateRangeFieldValidator;
import com.opensymphony.xwork2.validator.validators.DoubleRangeFieldValidator;
import com.opensymphony.xwork2.validator.validators.EmailValidator;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;
import com.opensymphony.xwork2.validator.validators.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.validators.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.validators.RegexFieldValidator;
import com.opensymphony.xwork2.validator.validators.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.validators.RequiredStringValidator;
import com.opensymphony.xwork2.validator.validators.ShortRangeFieldValidator;
import com.opensymphony.xwork2.validator.validators.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.validators.URLValidator;
import com.opensymphony.xwork2.validator.validators.VisitorFieldValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Simple test to check if validation Annotations match given validator class
 */
public class AnnotationValidationConfigurationBuilderTest extends XWorkTestCase {

    public void testValidationAnnotation() throws Exception {
        // given
        AnnotationActionValidatorManager manager = createValidationManager(AnnotationValidationAction.class, Locale.US);

        // when
        List<Validator> validators = manager.getValidators(AnnotationValidationAction.class, null);

        // then
        assertEquals(validators.size(), 16);
        for (Validator validator : validators) {
            validate(validator);
        }
    }

    public void testValidationAnnotationExpParams() throws Exception {
        // given
        AnnotationActionValidatorManager manager = createValidationManager(AnnotationValidationExpAction.class, Locale.US);

        // when
        List<Validator> validators = manager.getValidators(AnnotationValidationExpAction.class, null);

        // then
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(new AnnotationValidationExpAction());

        assertEquals(validators.size(), 16);
        for (Validator validator : validators) {
            validator.setValueStack(valueStack);
            validate(validator);
        }
    }

    private void validate(Validator validator) throws Exception {
        if (validator.getValidatorType().equals("regex")) {
            validateRegexValidator((RegexFieldValidator) validator);
        } else if (validator.getValidatorType().equals("conditionalvisitor")) {
            validateConditionalFieldVisitorValidator((ConditionalVisitorFieldValidator) validator);
        } else if (validator.getValidatorType().equals("conversion")) {
            validateConversionFieldErrorVisitorValidator((ConversionErrorFieldValidator) validator);
        } else if (validator.getValidatorType().equals("myValidator")) {
            validateMyValidator((MyValidator) validator);
        } else if (validator.getValidatorType().equals("date")) {
            validateDateRangeFieldValidator((DateRangeFieldValidator) validator);
        } else if (validator.getValidatorType().equals("double")) {
            validateDoubleRangeFieldValidator((DoubleRangeFieldValidator) validator);
        } else if (validator.getValidatorType().equals("email")) {
            validateEmailValidator((EmailValidator) validator);
        } else if (validator.getValidatorType().equals("expression")) {
            validateExpressionValidator((ExpressionValidator) validator);
        } else if (validator.getValidatorType().equals("fieldexpression")) {
            validateFieldExpressionValidator((FieldExpressionValidator) validator);
        } else if (validator.getValidatorType().equals("int")) {
            validateIntRangeFieldValidator((IntRangeFieldValidator) validator);
        } else if (validator.getValidatorType().equals("required")) {
            validateRequiredFieldValidator((RequiredFieldValidator) validator);
        } else if (validator.getValidatorType().equals("requiredstring")) {
            validateRequiredStringValidator((RequiredStringValidator) validator);
        } else if (validator.getValidatorType().equals("short")) {
            validateShortRangeFieldValidator((ShortRangeFieldValidator) validator);
        } else if (validator.getValidatorType().equals("stringlength")) {
            validateStringLengthFieldValidator((StringLengthFieldValidator) validator);
        } else if (validator.getValidatorType().equals("url")) {
            validateUrlValidator((URLValidator) validator);
        } else if (validator.getValidatorType().equals("visitor")) {
            validateVisitorFieldValidator((VisitorFieldValidator) validator);
        }
    }

    private void validateVisitorFieldValidator(VisitorFieldValidator validator) {
        assertEquals("foo", validator.getFieldName());
        assertEquals("visitorfield.key", validator.getMessageKey());
        assertEquals("Foo isn't valid!", validator.getDefaultMessage());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(false, validator.isAppendPrefix());
        assertEquals(true, validator.isShortCircuit());
    }

    private void validateUrlValidator(URLValidator validator) {
        assertEquals("foo", validator.getFieldName());
        assertEquals("Foo isn't a valid URL!", validator.getDefaultMessage());
        assertEquals("url.key", validator.getMessageKey());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(true, validator.isShortCircuit());
    }

    private void validateStringLengthFieldValidator(StringLengthFieldValidator validator) {
        assertEquals("foo", validator.getFieldName());
        assertEquals("stringlength.key", validator.getMessageKey());
        assertEquals("Foo is too long!", validator.getDefaultMessage());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(1, validator.getMinLength());
        assertEquals(10, validator.getMaxLength());
        assertEquals(true, validator.isShortCircuit());
        assertEquals(false, validator.isTrim());
    }

    private void validateShortRangeFieldValidator(ShortRangeFieldValidator validator) {
        assertEquals("foo", validator.getFieldName());
        assertEquals("Foo is out of range!", validator.getDefaultMessage());
        assertEquals("short.key", validator.getMessageKey());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(Short.valueOf("10"), validator.getMax());
        assertEquals(Short.valueOf("1"), validator.getMin());
        assertEquals(true, validator.isShortCircuit());
    }

    private void validateRequiredStringValidator(RequiredStringValidator validator) {
        assertEquals("foo", validator.getFieldName());
        assertEquals("requiredstring.key", validator.getMessageKey());
        assertEquals("Foo is required!", validator.getDefaultMessage());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(true, validator.isShortCircuit());
        assertEquals(false, validator.isTrim());
    }

    private void validateRequiredFieldValidator(RequiredFieldValidator validator) {
        assertEquals("foo", validator.getFieldName());
        assertEquals("Foo is required!", validator.getDefaultMessage());
        assertEquals("required.key", validator.getMessageKey());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(true, validator.isShortCircuit());
    }

    private void validateIntRangeFieldValidator(IntRangeFieldValidator validator) {
        assertEquals("foo", validator.getFieldName());
        assertEquals("int.key", validator.getMessageKey());
        assertEquals("Foo is out of range!", validator.getDefaultMessage());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(true, validator.isShortCircuit());
        assertEquals(Integer.valueOf(10), validator.getMax());
        assertEquals(Integer.valueOf(1), validator.getMin());
    }

    private void validateFieldExpressionValidator(FieldExpressionValidator validator) {
        assertEquals("foo", validator.getFieldName());
        assertEquals("It is not true!", validator.getDefaultMessage());
        assertEquals("fieldexpression.key", validator.getMessageKey());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals("true", validator.getExpression());
        assertEquals(true, validator.isShortCircuit());
    }

    private void validateExpressionValidator(ExpressionValidator validator) {
        assertEquals("expression.key", validator.getMessageKey());
        assertEquals("Is not true!", validator.getDefaultMessage());
        assertEquals("true", validator.getExpression());
        assertEquals(true, validator.isShortCircuit());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
    }

    private void validateEmailValidator(EmailValidator validator) {
        assertEquals("foo", validator.getFieldName());
        assertEquals(EmailValidator.EMAIL_ADDRESS_PATTERN, validator.getRegex());
        assertEquals("Foo isn't a valid e-mail!", validator.getDefaultMessage());
        assertEquals("email.key", validator.getMessageKey());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(true, validator.isShortCircuit());
        assertEquals(false, validator.isCaseSensitive());
        assertEquals(true, validator.isTrimed());
    }

    private void validateDoubleRangeFieldValidator(DoubleRangeFieldValidator validator) {
        assertEquals("foo", validator.getFieldName());
        assertEquals("double.key", validator.getMessageKey());
        assertEquals("Foo is out of range!", validator.getDefaultMessage());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(true, validator.isShortCircuit());
        assertEquals(1.4, validator.getMaxExclusive());
        assertEquals(1.2, validator.getMinExclusive());
        assertEquals(0.1, validator.getMaxInclusive());
        assertEquals(0.0, validator.getMinInclusive());
    }

    private void validateDateRangeFieldValidator(DateRangeFieldValidator validator) throws ParseException {
        assertEquals("foo", validator.getFieldName());
        assertEquals("Foo isn't in range!", validator.getDefaultMessage());
        assertEquals("date.foo", validator.getMessageKey());
        assertEquals(true, validator.isShortCircuit());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(new SimpleDateFormat("yyyy").parse("2011"), validator.getMin());
        assertEquals(new SimpleDateFormat("yyyy").parse("2012"), validator.getMax());
    }

    private void validateMyValidator(MyValidator validator) {
        assertEquals("Foo is invalid!", validator.getDefaultMessage());
        assertEquals("foo", validator.getFieldName());
        assertEquals("foo.invalid", validator.getMessageKey());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
        assertEquals(true, validator.isShortCircuit());
        assertEquals(1, validator.getValue());
    }

    private void validateConversionFieldErrorVisitorValidator(ConversionErrorFieldValidator validator) {
        assertEquals("bar", validator.getFieldName());
        assertEquals("conversion.key", validator.getMessageKey());
        assertEquals("Foo conversion error!", validator.getDefaultMessage());
        assertEquals(true, validator.isRepopulateField());
        assertEquals(true, validator.isShortCircuit());
        assertTrue(Arrays.equals(new String[]{"one", "three"}, validator.getMessageParameters()));
    }

    private void validateConditionalFieldVisitorValidator(ConditionalVisitorFieldValidator validator) {
        assertEquals("foo+bar", validator.getExpression());
        assertEquals("some", validator.getContext());
        assertEquals("Foo doesn't match!", validator.getDefaultMessage());
        assertEquals("bar", validator.getFieldName());
        assertEquals(false, validator.isAppendPrefix());
        assertEquals(true, validator.isShortCircuit());
        assertEquals("conditional.key", validator.getMessageKey());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
    }

    private void validateRegexValidator(RegexFieldValidator validator) {
        assertEquals("foo", validator.getRegex());
        assertEquals("Foo doesn't match!", validator.getDefaultMessage());
        assertEquals("regex.key", validator.getMessageKey());
        assertEquals("bar", validator.getFieldName());
        assertEquals(true, validator.isShortCircuit());
        assertEquals(false, validator.isTrimed());
        assertEquals(false, validator.isCaseSensitive());
        assertTrue(Arrays.equals(new String[]{"one", "two", "three"}, validator.getMessageParameters()));
    }

    private AnnotationActionValidatorManager createValidationManager(final Class<? extends ActionSupport> actionClass, Locale locale) throws Exception {
        loadConfigurationProviders(new ConfigurationProvider() {
            public void destroy() {

            }

            public void init(Configuration configuration) throws ConfigurationException {
                configuration.addPackageConfig("default", new PackageConfig.Builder("default")
                        .addActionConfig("annotation", new ActionConfig.Builder("", "annotation", actionClass.getName()).build())
                        .build());
            }

            public boolean needsReload() {
                return false;
            }

            public void loadPackages() throws ConfigurationException {

            }

            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                builder.constant(XWorkConstants.DEV_MODE, true);
            }
        });

        // ActionContext is destroyed during rebuilding configuration
        ActionContext.getContext().setLocale(locale);

        ActionInvocation invocation = new DefaultActionInvocation(ActionContext.getContext().getContextMap(), true);
        container.inject(invocation);
        invocation.init(actionProxyFactory.createActionProxy("", "annotation", null, ActionContext.getContext().getContextMap()));

        AnnotationActionValidatorManager manager = new AnnotationActionValidatorManager();
        container.inject(manager);

        ValidatorFactory vf = container.getInstance(ValidatorFactory.class);
        vf.registerValidator("myValidator", MyValidator.class.getName());

        return manager;
    }

}
