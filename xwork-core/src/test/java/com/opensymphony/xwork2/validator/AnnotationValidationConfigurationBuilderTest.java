package com.opensymphony.xwork2.validator;

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
import com.opensymphony.xwork2.validator.validators.RegexFieldValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple test to check if validation Annotations match given validator class
 */
public class AnnotationValidationConfigurationBuilderTest extends XWorkTestCase {

    public void testValidationAnnotation() throws Exception {
        // given
        AnnotationActionValidatorManager manager = createValidationManager(AnnotationValidationAction.class);

        // when
        List<Validator> validators = manager.getValidators(AnnotationValidationAction.class, null);

        // then
        assertEquals(validators.size(), 4);
        for (Validator validator : validators) {
            validate(validator);
        }
    }

    public void testValidationAnnotationExpParams() throws Exception {
        // given
        AnnotationActionValidatorManager manager = createValidationManager(AnnotationValidationExpAction.class);

        // when
        List<Validator> validators = manager.getValidators(AnnotationValidationExpAction.class, null);

        // then
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(new AnnotationValidationExpAction());

        assertEquals(validators.size(), 4);
        for (Validator validator : validators) {
            validator.setValueStack(valueStack);
            validate(validator);
        }
    }

    private void validate(Validator validator) {
        if (validator.getValidatorType().equals("regex")) {
            validateRegexValidator((RegexFieldValidator) validator);
        } else if (validator.getValidatorType().equals("conditionalvisitor")) {
            validateConditionalFieldVisitorValidator((ConditionalVisitorFieldValidator) validator);
        } else if (validator.getValidatorType().equals("conversion")) {
            validateConversionFieldErrorVisitorValidator((ConversionErrorFieldValidator) validator);
        } else if (validator.getValidatorType().equals("myValidator")) {
            validateMyValidator((MyValidator) validator);
        }
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

    private AnnotationActionValidatorManager createValidationManager(final Class<? extends ActionSupport> actionClass) throws Exception {
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

        Map<String, Object> context = new HashMap<String, Object>();
        ActionInvocation invocation = new DefaultActionInvocation(context, true);
        container.inject(invocation);
        invocation.init(actionProxyFactory.createActionProxy("", "annotation", null, context));

        AnnotationActionValidatorManager manager = new AnnotationActionValidatorManager();
        container.inject(manager);

        ValidatorFactory vf = container.getInstance(ValidatorFactory.class);
        vf.registerValidator("myValidator", MyValidator.class.getName());

        return manager;
    }

}
