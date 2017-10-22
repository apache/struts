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
package org.apache.struts.beanvalidation.validation.interceptor;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts.beanvalidation.validation.constant.ValidatorConstants;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * <p>
 * This is the central class for javax.validation (JSR-303) in a Struts2 setup : It bootstraps a
 * javax.validation.ValidationFactory and exposes it through the javax.validation.Validator interface. When
 * talking to an instance of this bean we will be talking to the default Validator of the underlying ValidatorFactory.
 * </p>
 * <p>
 * This is very convenient in that you don't have to perform yet another call on the factory, assuming that you will
 * almost always use the default Validator anyway. You need to pass provider class in order for this plugin to hook
 * itself to underlying validation Factory. Any of following Validation provider can be provided using
 * <code>struts.beanValidation.providerClass</code>
 * </p>
 *
 * <ul>
 * <li>Hibernate Validator - <code>org.hibernate.validator.HibernateValidator</code></li>
 * <li>Apache Bval - <code>org.apache.bval.jsr303.ApacheValidationProvider</code></li>
 * </ul>
 *
 */
public class DefaultBeanValidationManager implements BeanValidationManager {

    private static final Logger LOG = LogManager.getLogger(DefaultBeanValidationManager.class);

    protected Class providerClass;

    private ValidatorFactory validationFactory;

    @Inject
    public DefaultBeanValidationManager(
            @Inject(value = ValidatorConstants.PROVIDER_CLASS, required = false) String providerClassName,
            @Inject(value = ValidatorConstants.IGNORE_XMLCONFIGURAITION, required = false) String ignoreXMLConfiguration,
            @Inject(required = true) ObjectFactory objectFactory) {
        super();
        LOG.info("Initializing bean validation factory to get a validator");

        if (StringUtils.isNotBlank(providerClassName)) {
            try {
                this.providerClass = objectFactory.getClassInstance(providerClassName);
                LOG.info("{} validator found", this.providerClass.getName());
            } catch (ClassNotFoundException e) {
                LOG.error("Unable to find any bean validator implementation for class: {}", providerClassName);
                LOG.error("Unable to load bean validation provider class", e);
            }

        }
        if (this.providerClass == null) {
            LOG.warn("********** No bean validator class defined - Falling back to default provider **********");
        }

        Configuration configuration =
                this.providerClass != null
                        ? Validation.byProvider(this.providerClass).configure()
                        : Validation.byDefaultProvider().configure();
        if (BooleanUtils.toBoolean(ignoreXMLConfiguration)) {
            configuration.ignoreXmlConfiguration();
            LOG.info("XML configurations will be ignored by Validator, to enable XML based validation, set struts.beanValidation.ignoreXMLConfiguration to false.");
        }
        if (configuration != null) {
            this.validationFactory = configuration.buildValidatorFactory();
        }

    }

    /**
     * <p>
     * Method to return Validator instance.This will take in to account the provider class will try to create a
     * validation factory from given Validator. Validator will be returned based on the user preference.Validator will
     * be created based on the following cases.
     * </p>
     * <p>
     * In case user has specify explicitly and in a type safe fashion the expected provider, it will be used to create
     * validation factory and an instance of javax.validation.Validator will be returned.
     * </p>
     * <p>
     * In this case, the default validation provider resolver will be used to locate available providers. The chosen
     * provider is defined as followed:
     * </p>
     * <ul>
     * <li>if the XML configuration defines a provider, this provider is used</li>
     * <li>if the XML configuration does not define a provider or if no XML configuration is present the first provider
     * returned by the ValidationProviderResolver instance is used.</li>
     * </ul>
     *
     * @return validator instance
     */
    public Validator getValidator() {
        return this.validationFactory.getValidator();

    }
}
