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
package org.apache.struts2.factory;

import org.apache.logging.log4j.Level;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.test.LogCapture;
import org.apache.struts2.validator.ValidatorConfig;
import org.apache.struts2.validator.ValidatorFactory;
import org.apache.struts2.validator.validators.LocalDateRangeFieldValidator;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultValidatorFactoryTest extends StrutsInternalTestCase {

    private ValidatorFactory validatorFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validatorFactory = container.getInstance(ValidatorFactory.class);
    }

    private LocalDateRangeFieldValidator localDateValidator(String min) {
        ValidatorConfig config = new ValidatorConfig.Builder("localdate")
                .addParam("fieldName", "birthday")
                .addParam("min", min)
                .build();
        return (LocalDateRangeFieldValidator) validatorFactory.getValidator(config);
    }

    public void testUnconvertibleParamIsLoggedAsWarningAndValidatorIsStillBuilt() {
        try (LogCapture logs = new LogCapture(DefaultValidatorFactory.class)) {
            LocalDateRangeFieldValidator validator = localDateValidator("01/02/2026");

            assertThat(validator.getMin()).isNull();
            assertThat(validator.getFieldName()).isEqualTo("birthday");
            assertThat(logs.messages()).hasSize(1);
            assertThat(logs.messages(Level.WARN)).singleElement().asString().contains(
                    LocalDateRangeFieldValidator.class.getName(), "[min]", "[01/02/2026]", "cannot be converted");
        }
    }

    public void testUnconvertibleParamIsLoggedAsErrorInDevMode() {
        initDispatcher(Map.of(StrutsConstants.STRUTS_DEVMODE, "true"));
        validatorFactory = container.getInstance(ValidatorFactory.class);

        try (LogCapture logs = new LogCapture(DefaultValidatorFactory.class)) {
            LocalDateRangeFieldValidator validator = localDateValidator("01/02/2026");

            assertThat(validator.getMin()).isNull();
            assertThat(logs.messages()).hasSize(1);
            assertThat(logs.messages(Level.ERROR)).singleElement().asString().contains("[min]", "[01/02/2026]");
        }
    }

    public void testConvertibleParamIsNotLogged() {
        try (LogCapture logs = new LogCapture(DefaultValidatorFactory.class)) {
            LocalDateRangeFieldValidator validator = localDateValidator("2026-02-01");

            assertThat(validator.getMin()).isEqualTo(LocalDate.of(2026, 2, 1));
            assertThat(logs.messages()).isEmpty();
        }
    }

    public void testParamWithoutPropertyIsNotLogged() {
        ValidatorConfig config = new ValidatorConfig.Builder("required")
                .addParam("fieldName", "name")
                .addParam("methodName", "execute")
                .build();

        try (LogCapture logs = new LogCapture(DefaultValidatorFactory.class)) {
            validatorFactory.getValidator(config);

            assertThat(logs.messages()).isEmpty();
        }
    }
}
