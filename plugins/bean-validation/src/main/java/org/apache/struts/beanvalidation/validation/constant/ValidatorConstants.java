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
package org.apache.struts.beanvalidation.validation.constant;

/**
 * <p>Class consisting of various constant values being used within
 * bean validation plugin </p>
 *
 * <p>
 * These values can be overridden using struts.xml file by providing custom values.
 * </p>
 */
public final class ValidatorConstants {

    public static final String PROVIDER_CLASS = "struts.beanValidation.providerClass";
    public static final String IGNORE_XMLCONFIGURAITION = "struts.beanValidation.ignoreXMLConfiguration";
    public static final String CONVERT_MESSAGE_TO_UTF8 = "struts.beanValidation.convertMessageToUtf";
    public static final String CONVERT_MESSAGE_FROM = "struts.beanValidation.convertMessageFromEncoding";
    public static final String FIELD_SEPERATOR = ".";
    public static final String MODELDRIVEN_PREFIX = "model";
    public static final String EMPTY_SPACE = "";

}
