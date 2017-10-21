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
package com.opensymphony.xwork2.validator;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This class serves 2 purpose :
 * <ul>
 * <li>
 * Parse the validation config file. (eg. MyAction-validation.xml, MyAction-actionAlias-validation.xml)
  * to return a List of ValidatorConfig encapsulating the validator information.
 * </li>
 * <li>
 * Parse the validator definition file, (eg. validators.xml) that defines the {@link Validator}s
 * registered with XWork.
 * </li>
 * </ul>
 *
 * @author Jason Carreira
 * @author James House
 * @author tm_jee ( tm_jee (at) yahoo.co.uk )
 * @author Rob Harrop
 * @author Rene Gielen
 *
 * @see com.opensymphony.xwork2.validator.ValidatorConfig
 */
public interface ValidatorFileParser {
    /**
     * Parse resource for a list of ValidatorConfig objects (configuring which validator(s) are
     * being applied to a particular field etc.)
     *
     * @param validatorFactory a validator factory
     * @param is input stream to the resource
     * @param resourceName file name of the resource
     * @return List list of ValidatorConfig
     */
    List<ValidatorConfig> parseActionValidatorConfigs(ValidatorFactory validatorFactory, InputStream is, String resourceName);

    /**
     * Parses validator definitions (register various validators with XWork).
     *
     * @param validators map of validators
     * @param is The input stream
     * @param resourceName The location of the input stream
     */
    void parseValidatorDefinitions(Map<String,String> validators, InputStream is, String resourceName);
}
