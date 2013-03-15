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
import com.opensymphony.xwork2.validator.validators.RegexFieldValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple test to check if validation Annotations match given validator class
 */
public class AnnotationValidationConfigurationBuilderTest extends XWorkTestCase{

    public void testValidationAnnotation() throws Exception {
        // given
        AnnotationActionValidatorManager manager = createValidationManager(AnnotationValidationAction.class);

        // when
        List<Validator> validators = manager.getValidators(AnnotationValidationAction.class, null);

        // then
        assertEquals(validators.size(), 1);
        for (Validator validator : validators) {
            if (validator.getValidatorType().equals("regex")) {
                validateRegexValidator((RegexFieldValidator) validator);
            }
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

        assertEquals(validators.size(), 1);
        for (Validator validator : validators) {
            if (validator.getValidatorType().equals("regex")) {
                validator.setValueStack(valueStack);
                validateRegexValidator((RegexFieldValidator) validator);
            }
        }
    }

    private void validateRegexValidator(RegexFieldValidator validator) {
        assertEquals("foo", validator.getRegex());
        assertEquals("Foo doesn't match!", validator.getDefaultMessage());
        assertEquals("regex.key", validator.getMessageKey());
        assertEquals("bar", validator.getFieldName());
        assertEquals(true, validator.isShortCircuit());
        assertEquals(false, validator.isTrimed());
        assertEquals(false, validator.isCaseSensitive());
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

        return manager;
    }

}
