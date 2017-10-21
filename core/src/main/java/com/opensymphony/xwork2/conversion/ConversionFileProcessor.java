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
package com.opensymphony.xwork2.conversion;

import java.util.Map;

/**
 * Used to process &lt;clazz&gt;-conversion.properties file to read defined Converters
 */
public interface ConversionFileProcessor {

    /**
     * Process conversion file to create mapping for key (property, type) and corresponding converter
     *
     * @param mapping keeps converters per given key
     * @param clazz class which should be converted by the converter
     * @param converterFilename to read converters from
     */
    void process(Map<String, Object> mapping, Class clazz, String converterFilename);

}
